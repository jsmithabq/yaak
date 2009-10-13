package yaak.agent;

import java.util.List;
import java.util.Map;
import yaak.agent.event.AgentContextListener;
import yaak.agent.communication.MessageFilter;

/**
* <p><code>AgentContext</code> provides an agent "context" (place,
* environment, depending on your preferred terminology) accessible via
* its service methods.</p>
* <p>An application can create multiple agent contexts; each context having
* an application-specific, logical name, for example, an application with
* two contexts, one named <code>jupiter</code> and the other named
* <code>saturn</code>.</p>
* <p>Agent contexts support context-managed, point-to-point (P2P) and
* broadcast communication.  That is, an inter-agent message from
* <code>"mary@jupiter"</code> to <code>"carlos@saturn"</code> passes
* from <code>mary</code> to <code>jupiter</code> to <code>saturn</code>
* to <code>carlos</code>.  This communication structure facilitates
* efficient message filtering.</p>
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
* <p>Note that Yaak supports automatic, dynamic, inter-context and
* distributed, inter-context communication.  That is, two agents in
* different applications (distributed application components) can
* communicate if they know the respective context names.</p>
* <p>Note that the distributed communication is seamless.  There is no
* need to configure and/or start up external communication servers.
* That is, if configured for distributed operations, Yaak automatically
* launches a lightweight communication server upon application start-up,
* one per application (automatic communication server replication).  To
* enable distributed operations, add the following setting to the Yaak
* configuration file:</p>
* <pre>
* yaak.agent.communication.isDistributed=true
* </pre>
* <p>Without this setting, communication operations are accomplished by
* a "local communication server" via method calls (inter-context,
* <i>intra</i>-application messaging).  When distributed communication
* is enabled, Yaak optimizes local communication (inter-context,
* <i>intra</i>-application messaging) using the local communication server;
* Yaak uses the network-oriented communication server only for distributed
* communications (inter-context, <i>inter</i>-application messaging).</p>
* @author Jerry Smith
* @version $Id: AgentContext.java 12 2006-02-06 23:02:23Z jsmith $
*/

public interface AgentContext {
  /**
  * <p>The state of this variable is only a guideline, because there are
  * an unlimited number of potenial channel negotiations with sibling
  * containers.</p>
  * @return Whether or not the initialization is complete.
  */

  boolean isInitialized();

  /**
  * <p>The logical name for the agent context.</p>
  * @return The context name.
  */

  String getContextName();

  /**
  * <p>Disposes of this agent context, which simply disposes of each
  * agent in this context and closes any communication-related
  * resources.</p>
  */

  void dispose();

  /**
  * <p>Creates and initializes an agent.</p>
  * @param c The Class instance representing the agent class.
  * @return The agent.
  * @throws AgentException Unable to create the agent.
  */

  Agent createAgent(Class c) throws AgentException;

  /**
  * <p>Creates and initializes an agent.</p>
  * @param c The Class instance representing the agent class.
  * @param agentName The application-specific, symbolic name for the agent.
  * @return The agent.
  * @throws AgentException Unable to create the agent.
  */

  Agent createAgent(Class c, String agentName) throws AgentException;

  /**
  * <p>Creates and initializes an agent.</p>
  * @param classname The string specification for the agent classname.
  * @return The agent.
  * @throws AgentException Unable to create the agent.
  */

  Agent createAgent(String classname) throws AgentException;

  /**
  * <p>Creates and initializes an agent.</p>
  * @param classname The string specification for the agent classname.
  * @param agentName The application-specific, symbolic name for the agent.
  * @return The agent.
  * @throws AgentException Unable to create the agent.
  */

  Agent createAgent(String classname, String agentName)
      throws AgentException;

  /**
  * <p>Creates and initializes an agent.</p>
  * @param classname The string specification for the agent classname.
  * @param args The initialization object(s) to pass to
  * <code>onCreation()</code>.
  * @return The agent.
  * @throws AgentException Unable to create the agent.
  */

//  Agent createAgent(String classname, Object args) throws AgentException;

  /**
  * <p>Creates and initializes an agent.</p>
  * @param classname The string specification for the agent classname.
  * @param args The initialization object(s) to pass to
  * <code>onCreation()</code>.
  * @param agentName The application-specific, symbolic name for the agent.
  * @return The agent.
  * @throws AgentException Unable to create the agent.
  */

  Agent createAgent(String classname, Object args,
      String agentName) throws AgentException;

  /**
  * <p>Creates and initializes an agent.</p>
  * @param c The Class instance representing the agent class.
  * @param args The initialization object(s) to pass to
  * <code>onCreation()</code>.
  * @return The agent.
  * @throws AgentException Unable to create the agent.
  */

//  Agent createAgent(Class c, Object args) throws AgentException;

  /**
  * <p>Creates and initializes an agent.</p>
  * @param c The Class instance representing the agent class.
  * @param args The initialization object(s) to pass to
  * <code>onCreation()</code>.
  * @param agentName The application-specific, symbolic name for the agent.
  * @return The agent.
  * @throws AgentException Unable to create the agent.
  */

  Agent createAgent(Class c, Object args, String agentName)
      throws AgentException;

  /**
  * <p>Disposes of an agent (removes it from the agent context).</p>
  * @param id The reference ID for the agent.
  * @throws AgentException Unable to locate the agent for disposal.
  */

  void disposeAgent(AgentID id) throws AgentException;

  /**
  * <p>Disposes of an agent (removes it from the agent context).</p>
  * @param agent The agent.
  * @throws AgentException The agent reference is null or invalid.
  */

  void disposeAgent(Agent agent) throws AgentException;

  /**
  * <p>Registers a listener/observer for context-related events.</p>
  * @param listener The to-be-registered listener object.
  */

  void addAgentContextListener(AgentContextListener listener);

  /**
  * <p>Unregisters a listener/observer for context-related events.</p>
  * @param listener The (registered) listener to unregister.
  */

  void removeAgentContextListener(AgentContextListener listener);

  /**
  * <p>Returns a snapshot-like list of IDs, as an array, independent
  * of the dynamically changing context.  The elements are
  * {@link yaak.agent.AgentID AgentID} references.</p>
  * @return The array of agent IDs.
  */

  AgentID[] getAgentIDs();

  /**
  * <p>Returns a snapshot-like list of IDs, as a {@link java.util.List List},
  * independent of the dynamically changing context.  The elements
  * are {@link yaak.agent.AgentID AgentID} references.</p>
  * @return The list of agent IDs.
  */

  List getAgentIDList();

  /**
  * <p>Returns a snapshot-like list of agents, as an array, independent
  * of the dynamically changing context.  The elements are
  * {@link yaak.agent.Agent Agent} references.</p>
  * @return The array of agents.
  */

  Agent[] getAgents();

  /**
  * <p>Returns a snapshot-like list of agents, as a
  * {@link java.util.List List}, independent of the dynamically
  * changing context.  The elements are {@link yaak.agent.Agent Agent}
  * references.</p>
  * @return The list of agents.
  */

  List getAgentList();

  /**
  * <p>Provides look-up operations for the agent's name.</p>
  * @return The agent's name.
  * @throws AgentException Unable to locate the agent.
  */

  String getAgentNameByID(AgentID id) throws AgentException;

  /**
  * <p>Provides context-level storage for shared data (context
  * decorations).</p>
  * @param key The key for the indexed decoration.
  * @param object The to-be-stored decoration.
  */

  void putDecoration(Object key, Object object);

  /**
  * <p>Provides look-up services for context-level decorations.</p>
  * @param key The key for the indexed decoration.
  * @return The decoration.
  */

  Object getDecoration(Object key);

  /**
  * <p>Makes the encapsulated, shared decorations available for direct
  * access.</p>
  */

  Map getDecorations();

  /**
  * <p>Clears all context-level decorations.</p>
  */

  void clearDecorations();

  /**
  * <p>Registers the <em>default</em> message filter for
  * <em>agent-directed</em> messages.</p>
  * @param filter The message filter.
  */

  void registerDefaultMessageFilter(MessageFilter filter);

  /**
  * <p>Unregisters the <em>default</em> message filter for
  * <em>agent-directed</em> messages.</p>
  */

  void unregisterDefaultMessageFilter();
}
