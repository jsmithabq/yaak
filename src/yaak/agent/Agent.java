package yaak.agent;

import java.io.Serializable;
import java.util.Map;

import yaak.agent.behavior.AgentBehavior;
import yaak.agent.communication.AgentAddress;
import yaak.agent.communication.CommunicationException;
import yaak.agent.communication.Message;
import yaak.agent.communication.MessageFilter;
import yaak.agent.communication.MessageListener;
import yaak.core.YaakSystem;
import yaak.util.StringUtil;

/**
* <p><code>Agent</code> is the base agent, that is, the common agent
* data type that every agent must extend in order to participate in
* the agent world.</p>
* @author Jerry Smith
* @version $Id: Agent.java 11 2006-01-28 16:14:45Z jsmith $
*/

abstract public class Agent implements Runnable, Serializable {
  private static final String DEFAULT_AGENT_NAME = "anonymous";
  private String agentName = DEFAULT_AGENT_NAME;
  private AgentID id;
  protected AgentBehavior agentBehavior;
  transient private PersonalAgentContext context;

  /**
  * <p>Not for public consumption.</p>
  * <p><code>Agent</code> is never instantiated directly.</p>
  */

  protected Agent() {
  }

  final void setPersonalAgentContext(PersonalAgentContext context)
      { // default privacy
    this.context = context;
  }

  /**
  * <p>Provides access to the agent context's decorations, which can be
  * used for whiteboard-like, collaborative operations among agents.</p>
  * @return The decorations.
  */

  protected final Map getContextDecorations() {
    return context.getDecorations();
  }

  final void setAgentID(AgentID id) { // default privacy
    this.id = id;
  }

  /**
  * <p>Provides access to the agent's ID.</p>
  * @return The agent ID.
  */

  public final AgentID getAgentID() {
    return id;
  }

  /**
  * <p>Sets the agent's logical name.</p>
  * @param agentName The agent's name.
  */

  synchronized public final void setAgentName(String agentName) {
    //
    // Policy:  protect against null or empty string:
    //
//    this.agentName = StringUtil.checkValue(agentName, DEFAULT_AGENT_NAME);
    this.agentName =
      StringUtil.checkValue(agentName, getAgentID().toString());
  }

  /**
  * <p>Provides access to the agent's name.</p>
  * @return The agent's name.
  */

  public final String getAgentName() {
    return agentName;
  }

  /**
  * <p>Provides access to the name (only) for the agent's context.</p>
  * @return The agent context's name.
  */

  public String getContextName() {
    return context.getContextName();
  }

  /**
  * <p>Provides access to the agent's (own/self) address.</p>
  * @return The agent's address.
  */

  public final String getAgentSelfAddressAsString() {
    return agentName + "@" + getContextName();
  }

  /**
  * <p>Provides access to the agent's (own/self) address.</p>
  * @return The agent's address.
  */

  public final AgentAddress getAgentSelfAddress() {
    return new AgentAddress(agentName + "@" + getContextName());
  }

  /**
  * <p>Sets the agent's behavior (operations) object.</p>
  * @param agentBehavior The agent's configured behavior,
  * <code>null</code> by default.
  * @see yaak.agent.Agent#execute
  * @see yaak.agent.behavior.AgentBehavior
  */

  synchronized public final void setAgentBehavior(AgentBehavior agentBehavior) {
    this.agentBehavior = agentBehavior;
  }

  /**
  * <p>Provides access to the agent's behavior.</p>
  * @return The agent's behavior.
  */

  public final AgentBehavior getAgentBehavior() {
    return agentBehavior;
  }

  /**
  * <p>Executes agent behavior/operations.  This method can be invoked
  * directly or indirectly under the control of a thread.</p>
  */

  public final void run() {
    onExecute();
    execute();
    onFinish();
  }

  /**
  * <p>Publishes a message to the specified address, usually a
  * context.  Currently, <em>for broadcast messaging</em>, addresses
  * are interpreted as follows:</p>
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
  * <p>Note that broadcast messages are delivered asynchronously only,
  * and only to registered agents.</p>
  * @param address The address.
  * @param message The message.
  * @throws CommunicationException Cannot send the message
  * due to an address specification error, etc.
  */

  public final void publishMessage(String address, Message message)
      throws CommunicationException {
    context.getMessageExecutive().publishMessage(
      new AgentAddress(agentName, context.getContextName()),
      new AgentAddress(address),
      message);
  }

  /**
  * <p>Sends a message to the specified agent.</p>
  * @param recipient The recipient's address.
  * @param message The message.
  * @throws CommunicationException Cannot send the message
  * due to a recipient specification error, etc.
  */

  public final void sendMessage(AgentAddress recipient, Message message)
      throws CommunicationException {
    context.getMessageExecutive().sendMessage(
      new AgentAddress(agentName, context.getContextName()),
      recipient,
      message);
  }

  /**
  * <p>Sends a message to the specified agent.</p>
  * @param recipient The recipient's address in
  * <code>"agentname@podname"</code> format.
  * @param message The message.
  * @throws CommunicationException Cannot send the message
  * due to a recipient specification error, etc.
  */

  public final void sendMessage(String recipient, Message message)
      throws CommunicationException {
    context.getMessageExecutive().sendMessage(
      new AgentAddress(agentName, context.getContextName()),
      new AgentAddress(recipient),
      message);
  }

  /**
  * <p>Receives a message from the specified agent.  This method is
  * synchronous:  (1) invoke this method and the agent waits (via
  * the method call), and then (2) upon delivery of the message,
  * process the message according to application-defined convention.</p>
  * @param sender The sender's address in
  * <code>"agentname@podname"</code> format.
  * @return The object that carries the pertinent data.
  * @throws CommunicationException Cannot receive the message
  * due to ..., etc.
  */

  public final Message receiveMessage(String sender)
      throws CommunicationException {
    return receiveMessage(new AgentAddress(sender));
  }

  /**
  * <p>Receives a message from the specified agent.  This method is
  * synchronous:  (1) invoke this method and the agent waits (via
  * the method call), and then (2) upon delivery of the message,
  * process the message according to application-defined convention.</p>
  * @param sender The sender's address.
  * @return The object that carries the pertinent
  * data.
  * @throws CommunicationException Cannot receive the message
  * due to ..., etc.
  */

  public final Message receiveMessage(AgentAddress sender)
      throws CommunicationException {
    return context.getMessageExecutive().receive(sender);
  }

  /**
  * <p>Registers an object that listens for messages.  This method
  * initializes an agent for asynchronous message delivery/reception.
  * This registration is not restricted to a particular source (agent).</p>
  * @param listener The agent's delegated listener.
  */

  public final void registerForMessages(MessageListener listener) {
    try {
      context.getMessageExecutive().registerForMessages(agentName, listener);
    }
    catch (AgentException e) {
      YaakSystem.getLogger().warning(
        "cannot register due to internal error, possibly agent name: ");
      e.printStackTrace();
    }
  }

  /**
  * <p>Unregisters an agent for asynchronous message delivery/reception.</p>
  */

  public final void unregisterForMessages() {
    try {
      context.getMessageExecutive().unregisterForMessages(agentName);
    }
    catch (AgentException e) {
      YaakSystem.getLogger().warning(
        "cannot unregister due to internal error, possibly agent name: ");
      e.printStackTrace();
    }
  }

  /**
  * <p>Registers a message-filtering object.  This registration
  * is not restricted to a particular source (agent).</p>
  * @param filter The agent's message filter.
  */

  public final void registerFilterForMessages(MessageFilter filter) {
    try {
      context.getMessageExecutive().registerFilterForMessages(
        agentName, filter);
    }
    catch (AgentException e) {
      YaakSystem.getLogger().warning(
        "cannot register filter due to internal error, possibly agent name: ");
      e.printStackTrace();
    }
  }

  /**
  * <p>Unregisters the message-filtering object for this agent.</p>
  */

  public final void unregisterFilterForMessages() {
    try {
      context.getMessageExecutive().unregisterFilterForMessages(agentName);
    }
    catch (AgentException e) {
      YaakSystem.getLogger().warning(
      "cannot unregister filter due to internal error, possibly agent name: ");
      e.printStackTrace();
    }
  }

  /**
  * <p>Performs agent-specific operation(s).  This method is executed
  * automatically when an agent's <code>run()</code> method is invoked.
  * There are two approaches to associating executable "behavior" with
  * an agent:</p>
  * <ol>
  * <li>Specialize {@link yaak.agent.Agent Agent} simply to override this
  * method for agent-specific operation(s), which, by definition, "uses up"
  * the single inheritance possibility in order to provide custom behavior.
  * <pre>
  *   ...
  *   class SpecialAgent extends Agent {
  *     public void execute() {
  *       // perform behavior (operations)
  *     }
  *   }
  *   ...
  *   Agent agent = agentContext.createAgent(SpecialAgent.class);
  *   ...
  *   new DefaultExecutor().execute(agent);
  *   ...
  * </pre>
  * <li>Set the agent behavior for any agent, which (the default)
  * <code>execute()</code> attempts to execute.
  * <pre>
  *   ...
  *   class SpecialBehavior implements AgentBehavior {
  *     public void execute() {
  *       // perform behavior (operations)
  *     }
  *   }
  *   ...
  *   Agent agent = agentContext.createAgent(AnyAgent.class);
  *   ...
  *   agent.setAgentBehavior(new SpecialBehavior());
  *   ...
  *   new DefaultExecutor().execute(agent);
  *   ...
  * </pre>
  * </ol>
  * <p>The latter is the default, in the sense that
  * {@link yaak.agent.Agent#execute Agent.execute()} attempts to invoke
  * {@link yaak.agent.behavior.AgentBehavior#execute AgentBehavior.execute()}
  * if the agent behavior is not <code>null</code>.</p>
  * <p>In both code segments above, the behavior is executed via the
  * following technique:</p>
  * <pre>
  *   new DefaultExecutor().execute(agent);
  * </pre>
  * <p>That is, the agent's operations are executed indirectly (with respect
  * to the thread context) by handing the agent off to other functionality
  * that establishes an execution context.  It's possible, of course, to
  * use any direct or indirect execution context, e.g.,</p>
  * <pre>
  *   // directly, within the current execution context:
  *   agent.run();
  *   // indirectly, using 'Thread' to establish an execution context:
  *   new Thread(agent).start();
  *   // indirectly, using a dedicated executor:
  *   new DefaultExecutor().execute(agent);
  *   // indirectly, using a thread-pool scheduler:
  *   scheduler.add(actor);
  * </pre>
  */

  protected void execute() {
    if (agentBehavior != null) {
      agentBehavior.execute();
    }
  }

  /**
  * <p>Application-defined operation(s) executed immediately before
  * an agent begins executing.</p>
  */

  protected void onExecute() {
  }

  /**
  * <p>Application-defined operation(s) executed immediately after
  * an agent has finished executing.  This method is rather meaningless
  * if (the overriding) <code>execute()</code> method launches a
  * thread and handles its "work" within that thread.</p>
  */

  protected void onFinish() {
  }

  /**
  * <p>Releases any internally held resources.</p>
  */

  void dispose() { // default privacy
//    System.out.println("dispose() in 'Agent'...");
  }

  /**
  * <p>Application-defined operation(s) executed when an agent
  * is created.  This method is executed immediately after the agent
  * is installed into the agent context and initialized.  To provide
  * a custom creation operation, simply override this method.  The
  * argument to this method is specified during agent creation:</p>
  * <pre>
  *   Agent agent = context.createAgent("MyAgent", someObject, "007");
  * </pre>
  */

  protected void onCreation(Object args) {
  }

  /**
  * <p>Application-defined operations executed when an agent is removed
  * from its agent context.  This method is executed immediately before
  * the agent's connection(s) with its agent context is severed.</p>
  */

  protected void onDisposing() {
  }

  /**
  * <p>Provides a representative summary of the agent's state.</p>
  * @return The state summary as a string.
  */

  public String toString() {
    return "[Agent ID: " + id + ", name: " + agentName + "]";
  }
}
