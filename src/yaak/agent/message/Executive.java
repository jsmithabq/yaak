package yaak.agent.message;

import yaak.agent.AgentContainer;
import yaak.agent.AgentException;
import yaak.agent.communication.AgentAddress;
import yaak.agent.communication.CommunicationChannel;
import yaak.agent.communication.ChannelListener;
import yaak.agent.communication.CommunicationException;
import yaak.agent.communication.Message;
import yaak.agent.communication.MessageFilter;
import yaak.agent.communication.MessageListener;

/**
* <p><code>Executive</code> provides core messaging functionality.  These
* services support both broadcast and connection-oriented messages.</p>
* @author Jerry Smith
* @version $Id: Executive.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class Executive {
  private AgentContainer container;

  /**
  * <p>Not for public consumption.</p>
  */

  protected Executive() {
    this(null);
  }

  /**
  * <p>Not for public consumption.</p>
  * @param container The container for which this
  * executive provides services.
  */

  protected Executive(AgentContainer container) {
    this.container = container;
    init();
  }

  /**
  * <p>Not for public consumption.</p>
  * @param container The container for which this executive provides
  * services.
  */

  protected final void setAgentContainer(AgentContainer container) {
    this.container = container;
  }

  private void init() {
  }

  /**
  * <p>Gets the executive's communication channel.</p>
  * @return The communication channel.
  */

  public final CommunicationChannel getCommunicationChannel() {
    return container.getCommunicationExecutive().getCommunicationChannel();
  }

  /**
  * <p>Publishes a message to the specified address, usually a context.</p>
  * @param source The (logical) address of the source agent.
  * @param target The (logical) address of the target agent.
  * @param message The object that carries the pertinent data.
  * @throws CommunicationException Either the sending channel is not
  * operational, or the receiving agent cannot be located.  Is the
  * communication server running?
  */

  public final void publishMessage(AgentAddress source, AgentAddress target,
      Message message) throws CommunicationException {
    message.setSource(source);
    message.setTarget(target);
    CommunicationChannel commChannel =
      container.getCommunicationExecutive().lookupCommunicationChannel(target);
    if (commChannel == null) {
      throw new CommunicationException(
        "No communication channel: cannot broadcast message to context '" +
        target.getContextName() + "'.");
    }
    commChannel.publish(message);
  }

  /**
  * <p>Sends a message to the specified agent.</p>
  * @param source The (logical) address of the source agent.
  * @param target The (logical) address of the target agent.
  * @param message The object that carries the pertinent data.
  * @throws CommunicationException Either the sending channel is not
  * operational, or the receiving agent cannot be located.  Is the
  * communication server running?
  */

  public final void sendMessage(AgentAddress source, AgentAddress target,
      Message message) throws CommunicationException {
    message.setSource(source);
    message.setTarget(target);
    CommunicationChannel commChannel =
      container.getCommunicationExecutive().lookupCommunicationChannel(target);
    if (commChannel == null) {
      throw new CommunicationException(
        "No communication channel: cannot send message to context '" +
        target.getContextName() + "'.");
    }
    commChannel.send(message);
  }

  /**
  * <p>Receives a message from the specified agent.</p>
  * @param source The agent/pod address of the sending agent.
  * @return The object that carries the pertinent data.
  * @throws CommunicationException Either the receiving channel
  * is not operational, the receiver is already waiting on a message, or
  * the sending agent cannot be located.
  */

  public final Message receive(AgentAddress source)
      throws CommunicationException {
    Message message = container.getCommunicationExecutive().receive(source);
    // For now, pass straight through???
    return message;
  }

  /**
  * <p>Registers the message listener for <em>agent-directed</em>
  * messages.</p>
  * @param agentName The symbolic agent name.
  * @param listener The object for receiving messages.
  * @throws AgentException The agent name is null, zero-length,
  * or cannot be managed internally for an unspecified reason.
  */

  public final void registerForMessages(String agentName,
      MessageListener listener) throws AgentException {
    yaak.agent.communication.Executive cExec =
      container.getCommunicationExecutive();
    cExec.registerMessageListener(agentName, listener);
  }

  /**
  * <p>Unregisters the message listener for <em>agent-directed</em>
  * messages.</p>
  * @param agentName The symbolic agent name.
  * @throws AgentException The agent name is null, zero-length,
  * or cannot be managed internally for an unspecified reason.
  */

  public final void unregisterForMessages(String agentName)
      throws AgentException {
    yaak.agent.communication.Executive cExec =
      container.getCommunicationExecutive();
    cExec.unregisterMessageListener(agentName);
  }

  /**
  * <p>Registers the message filter for <em>agent-directed</em>
  * messages.</p>
  * @param agentName The symbolic agent name.
  * @param filter The object for filtering messages (restricting
  * message delivery).
  * @throws AgentException The agent name is null, zero-length,
  * or cannot be managed internally for an unspecified reason.
  */

  public final void registerFilterForMessages(String agentName,
      MessageFilter filter) throws AgentException {
    yaak.agent.communication.Executive cExec =
      container.getCommunicationExecutive();
    cExec.registerMessageFilter(agentName, filter);
  }

  /**
  * <p>Unregisters the message filter for <em>agent-directed</em>
  * messages.</p>
  * @param agentName The symbolic agent name.
  * @throws AgentException The agent name is null, zero-length,
  * or cannot be managed internally for an unspecified reason.
  */

  public final void unregisterFilterForMessages(String agentName)
      throws AgentException {
    yaak.agent.communication.Executive cExec =
      container.getCommunicationExecutive();
    cExec.unregisterMessageFilter(agentName);
  }
}
