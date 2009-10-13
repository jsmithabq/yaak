package yaak.agent.communication;

import java.util.Hashtable;

import yaak.agent.Agent;
import yaak.agent.AgentContainer;
import yaak.agent.AgentException;
import yaak.agent.AgentSystem;
import yaak.core.YaakSystem;

/**
* <p><code>Executive</code> provides core communications functionality.
* These services support both broadcast connection-oriented channel
* communications.</p>
* <p>The communication services also support "ordered and agent-specific
* receivership."  That is, agents can register for asynchronous messages
* without regard for the sending agent, which constitutes the most general,
* lowest-order message registration.  In addition, agents can invoke
* <code>receive()</code> dynamically for synchronous message reception
* from a specified agent.  Incoming messages, for which there is a named,
* (waiting) receiving agent, are directed to the waiting agent, completing
* its synchronous method invocation.</p>
* <p>The communication layer does not support (multiple) named channels
* per agent context.  That is, the agent context name maps directly to the
* channel name for the communication channel that's established dynamically
* for that agent context.  In other words, every agent context has a
* dedicated communication channel, and all inter-context, inter-agent
* communications are passed from an agent to its context to the target
* context to the target agent.</p>
* <p>Having a single dedicated communication channel per context is good,
* because it reduces the "weight" (baggage) of each context.  The objective
* is for an agent context to be lightweight, so that the developer can
* organize agents in contexts, much like a scientific programmer would
* organize data in arrays.  If the performance implication for a context
* is minimal, the creative developer is free to use agent contexts in a
* variety of ways--whatever works well for the application domain.</p>
* <p>In particular, multiple communication channels, routers, radios with
* multiple channels, and so on are easily represented by building
* systems of agent contexts.  Note that Yaak does not restrict the
* developer in any way from nesting agent contexts; however, the effects
* that you can achieve by <i>nesting</i> contexts are often more
* easily accomplished by simple linear organizations of contexts, using
* a manager context to <i>organize</i> subordinate contexts.</p>
* @author Jerry Smith
* @version $Id: Executive.java 6 2005-06-08 17:00:15Z jsmith $
*/

//
// Notes:
//
// In this context, synchronization is NOT required on the read-level
// access methods.
//
//

public class Executive {
  //
  // Intra-JVM, cross-context registration:
  //
  private static Hashtable contextRegistrationTable = new Hashtable();
  private AgentContainer container;
  private Hashtable messageListenerTable = new Hashtable();
  private Hashtable receiveListenerTable = new Hashtable();
  private Hashtable messageFilterTable = new Hashtable();
  private MessageFilter defaultFilter = null;
  private CommunicationChannel communicationChannel = null;
  private CommunicationServer communicationServer = null;
  private ChannelListener contextCommunicationListener =
    new LocalContextChannelListener();
  private boolean initialized = false;

  /**
  * <p>Not for public consumption.</p>
  */

  protected Executive() {
    this(null);
  }

  /**
  * <p>Not for public consumption.</p>
  * @param container The container for which this executive
  * provides services.
  */

  protected Executive(AgentContainer container) {
    this.container = container;
    init();
  }

  /**
  * <p>Not for public consumption.</p>
  * @param container The container for which this executive provides services.
  */

  protected final void setAgentContainer(AgentContainer container) {
    this.container = container;
  }

  private void init() {
    String localName = "local." + container.getContextName();
    //
    // Use the current configuration:
    //
    if (Configuration.isDistributed()) {
      communicationServer = (CommunicationServer)
        AgentSystem.getCommunicationServer();
    }
    ContextRegistration cr = new ContextRegistration(
      container.getContextName(), localName);
    communicationChannel = new CommunicationHandler(localName);
    cr.setCommunicationChannel(communicationChannel);
    if (contextRegistrationTable.put(container.getContextName(), cr) != null) {
      throw new IllegalArgumentException("Context name '" +
        container.getContextName() + "' already in use (local).");
    }
    YaakSystem.getLogger().fine(
      "added context registration: '" + container.getContextName() + "'.");
    communicationChannel.setChannelListener(contextCommunicationListener);
    if (Configuration.isDistributed()) {
      try {
        communicationServer.getChannelNew(container.getContextName(),
          contextCommunicationListener);
      }
      catch (CommunicationException e) {
        throw new IllegalArgumentException("Context name '" +
        container.getContextName() + "' already in use (remote).");
      }
    }
    initialized = true;
  }

  /**
  * <p>Frees communication-related resources for this executive.  Note
  * that freeing global resources applies to all dependent contexts.</p>
  */

  public final void close() {
    contextRegistrationTable.remove(container.getContextName()); // ???
    YaakSystem.getLogger().fine("removed context registration: '" +
      container.getContextName() + "'.");
    if (Configuration.isDistributed()) {
      communicationServer.close(container.getContextName());
    }
  }

  /**
  * <p>The state of this variable is only a guideline, because there are
  * an unlimited number of potenial channel negotiations with sibling
  * containers.</p>
  * @return Whether or not the initialization is complete.
  */

  public final boolean isInitialized() {
    return initialized;
  }

  /**
  * <p>Gets the executive's communication channel.</p>
  * @return The communication channel.
  */

  public final CommunicationChannel getCommunicationChannel() {
    return communicationChannel;
  }

  /**
  * <p>Receives a message from the specified agent.</p>
  * @param sendingAddress The agent/context address of the sending agent.
  * @return The object that carries the pertinent data.
  * @throws CommunicationException Either the receiving channel is not
  * operational, or another agent is waiting for a message from the
  * sending agent.
  */

  public final Message
  receive(AgentAddress sendingAddress) throws CommunicationException {
    Envelope envelope = new Envelope(sendingAddress, null);
    String receiveStr = sendingAddress.stringFormatAddress();
    if (receiveListenerTable.get(receiveStr) != null) {
      throw new CommunicationException(
      "An agent is already waiting for a message from the specified sender.");
    }
    receiveListenerTable.put(receiveStr, envelope);
    YaakSystem.getLogger().fine(
      "entering wait state for message from: '" + receiveStr + "'.");
    try {
      synchronized (envelope) {
        envelope.wait();
      }
    }
    catch (InterruptedException e) {
      YaakSystem.getLogger().warning(
        "interrupt while waiting to receive envelope: ");
      e.printStackTrace();
    }
    return envelope.message;
  }

  /**
  * <p>Looks up the communication channel for a particular agent/context.
  * Currently, <em>for broadcast messaging</em>, addresses are
  * interpreted as follows:</p>
  * <ul>
  * <li><code>"mary@jupiter"</code> is <code>"mary@jupiter"</code>
  * <li><code>"@jupiter"</code> is <code>"[all-agents]@jupiter"</code>
  * <li><code>"mary@"</code> is <code>"[all-agents]@[local-context]"</code>
  * <li><code>"@"</code> is <code>"[all-agents]@[local-context]"</code>
  * <li><code>""</code> is <code>"[all-agents]@[local-context]"</code>
  * </ul>
  * <p>where <code>[all-agents]</code> is all agents in the context
  * and <code>[local-context]</code> is the context in which the agent
  * resides.</p>
  * @param address The agent/context address.
  * @return The <code>CommunicationChannel</code> instance, which
  * provides broadcast-over-channel and connection-oriented services.
  */

  synchronized public final
  CommunicationChannel lookupCommunicationChannel(AgentAddress address) {
    String contextName = address.getContextName();
    if (contextName == null) {
      contextName = container.getContextName(); // assume local context
    }
    ContextRegistration cr =
      (ContextRegistration) contextRegistrationTable.get(contextName);
    if (cr == null) { // not local
      if (Configuration.isDistributed()) {
        if (!communicationServer.channelExists(contextName)) {
          YaakSystem.getLogger().fine(
            "No channel exists during look-up for: '" + contextName + "'.");
          return null;
        }
        return communicationServer.getChannel(contextName);
      }
      else {
        return null;
      }
    }
    else {
      return cr.getCommunicationChannel();
    }
  }

  /**
  * <p>Registers the message listener for <em>agent-directed</em>
  * messages.</p>
  * @param agentName The agent name.
  * @param listener The message listener.
  * @throws AgentException The agent name is null, zero-length,
  * or cannot be managed internally for an unspecified reason.
  */

  synchronized public final void registerMessageListener(String agentName,
      MessageListener listener) throws AgentException {
    if (agentName == null || agentName.length() == 0) {
      throw new AgentException("Agent name is null or zero-length.");
    }
    messageListenerTable.put(agentName, listener);
  }

  /**
  * <p>Unregisters the message listener for <em>agent-directed</em>
  * messages.</p>
  * @param agentName The agent name.
  * @throws AgentException The agent name is null, zero-length,
  * or cannot be managed internally for an unspecified reason.
  */

  synchronized public final void unregisterMessageListener(String agentName)
      throws AgentException {
    if (agentName == null || agentName.length() == 0) {
      throw new AgentException("Agent name is null or zero-length.");
    }
    messageListenerTable.remove(agentName);
  }

  /**
  * <p>Registers the <em>default</em> message filter for
  * <em>agent-directed</em> messages.</p>
  * @param filter The message filter.
  */

  synchronized public final void registerDefaultMessageFilter(
      MessageFilter filter) {
    defaultFilter = filter;
  }

  /**
  * <p>Unregisters the <em>default</em> message filter for
  * <em>agent-directed</em> messages.</p>
  */

  synchronized public final void unregisterDefaultMessageFilter() {
    defaultFilter = null;
  }

  /**
  * <p>Registers the message filter for <em>agent-directed</em>
  * messages.</p>
  * @param agentName The agent name.
  * @param filter The message filter.
  * @throws AgentException The agent name is null, zero-length,
  * or cannot be managed internally for an unspecified reason.
  */

  synchronized public final void registerMessageFilter(String agentName,
      MessageFilter filter) throws AgentException {
    if (agentName == null || agentName.length() == 0) {
      throw new AgentException("Agent name is null or zero-length.");
    }
    messageFilterTable.put(agentName, filter);
  }

  /**
  * <p>Unregisters the message filter for <em>agent-directed</em>
  * messages.</p>
  * @param agentName The agent name.
  * @throws AgentException The agent name is null, zero-length,
  * or cannot be managed internally for an unspecified reason.
  */

  synchronized public final void unregisterMessageFilter(String agentName)
      throws AgentException {
    if (agentName == null || agentName.length() == 0) {
      throw new AgentException("Agent name is null or zero-length.");
    }
    messageFilterTable.remove(agentName);
  }

  private void deliverMessage(String agentName, Message msg) {
    YaakSystem.getLogger().fine(
      "evaluate message delivery for: '" + agentName + "'.");
    MessageListener listener =
      (MessageListener) messageListenerTable.get(agentName);
    if (listener != null) {
      synchronized (this) {
        MessageFilter filter =
          (MessageFilter) messageFilterTable.get(agentName);
        if (filter != null) {
          if (filter.allow(msg)) {
            YaakSystem.getLogger().fine(
              "message allowed by agent filter: '" + agentName + "'.");
            listener.onMessage(msg);
          }
          else {
            YaakSystem.getLogger().fine(
              "message rejected by agent filter: '" + agentName + "'.");
          }
        }
        else if (defaultFilter != null) {
          if (defaultFilter.allow(msg)) {
            YaakSystem.getLogger().fine(
              "message allowed by default filter: '" + agentName + "'.");
            listener.onMessage(msg);
          }
          else {
            YaakSystem.getLogger().fine(
              "message rejected by default filter: '" + agentName + "'.");
          }
        }
        else {
          YaakSystem.getLogger().fine(
            "message delivered unfiltered: '" + agentName + "'.");
          listener.onMessage(msg);
        }
      }
    }
  }

  //
  // Encapsulates/holds incoming asynchronous and synchronous messages:
  //

  private class Envelope {
    AgentAddress sender;
    Message message;

    Envelope(AgentAddress sender, Message message) {
      this.sender = sender;
      this.message = message;
    }
  }

  //
  // Handles incoming messages (message reception):
  //

  private class LocalContextChannelListener implements ChannelListener {
    synchronized public void onDelivery(Payload payload) {
      Object o = payload.getObject();
      YaakSystem.getLogger().fine("received payload (local): " + o);
      if (o instanceof Message) {
        Message msg = (Message) o;
        AgentAddress source = msg.getSource();
        String sourceAsStr = source.stringFormatAddress();
        if (payload.isBroadcast()) {
          Agent[] agents = container.getAgents();
          for (int i = 0; i < agents.length; i++) {
            deliverMessage(agents[i].getAgentName(), msg);
          }
        }
        else { // P2P
          AgentAddress target = msg.getTarget();
          String localAgentName = target.getAgentName();
          YaakSystem.getLogger().fine(
            "message arrived for: '" + localAgentName + "'.");
          Envelope envelope =
            (Envelope) receiveListenerTable.get(sourceAsStr);
          if (envelope != null) {
            YaakSystem.getLogger().fine(
              "message received for waiting agent: '" + localAgentName + "'.");
            receiveListenerTable.remove(sourceAsStr); // one reception only
            envelope.message = msg;
            synchronized (envelope) {
              envelope.notify();
            }
          }
          else {
            deliverMessage(localAgentName, msg);
          }
        }
      }
      else {
        YaakSystem.getLogger().fine("unknown payload (local)...");
      }
    }
  }
}
