package yaak.agent;

import java.lang.reflect.Method;

import yaak.agent.communication.CommunicationServerAdmin;
import yaak.agent.communication.CommunicationServer;
import yaak.agent.communication.Configuration;
import yaak.core.YaakSystem;
import yaak.util.Util;

/**
* <p><code>AgentSystem</code> provides top-level access to agent services,
* for example, creating an agent context.</p>
* <p>See {@link yaak.agent.AgentContext AgentContext} for usable services.</p>
* @author Jerry Smith
* @version $Id: AgentSystem.java 6 2005-06-08 17:00:15Z jsmith $
* @see yaak.agent.AgentContext
*/

public class AgentSystem {
  private static long uniqueID = System.currentTimeMillis();
  private static CommunicationServerAdmin communicationServer = null;

  static {
    if (Configuration.isDistributed()) {
      String classname = Configuration.getCommunicationServerClassname();
      try {
        Class c = Class.forName(classname);
        Method method = c.getDeclaredMethod("getServer", new Class[0]);
        communicationServer =
          (CommunicationServerAdmin) method.invoke(null, null);
      }
      catch (ClassNotFoundException e) {
        YaakSystem.getLogger().warning(
          "Cannot access or start communication server: '" +
          classname + "'.");
        YaakSystem.getLogger().warning(
          "Are all implementation-related files available?");
        throw new AgentRuntimeException(
          "Cannot access/start communication server:", e);
      }
      catch (Exception e) { // just quit -- cannot know what's wrong here
        YaakSystem.getLogger().warning(
          "Cannot initialize communication server: '" +
          classname + "'.");
        YaakSystem.getLogger().warning(
          "Are all implementation-related files available?");
        throw new AgentRuntimeException(
          "Cannot initialize communication server:", e);
      }
    }
  }

  //
  // Currently, 'AgentSystem' does not support instantiation.
  //

  private AgentSystem() {
  }

  /**
  * <p>Provides a system-unique ID.</p>
  * @return The next ID.
  */

  synchronized static long getNextSystemUniqueID() { // default privacy
    return uniqueID++;
  }

  /**
  * <p>Serves up an agent context/place.  Applications create agents
  * (specializations of <code>Agent</code>), not by instantiating an
  * <code>Agent</code> specialization, but by requesting an agent from
  * an agent context; applications obtain an agent context by requesting
  * it from <code>AgentSystem</code>:</p>
  * <pre>
  *   ...
  *   AgentContext ac = AgentSystem.createAgentContext();
  *   ac.createAgent("Hello", null); // 'Hello' extends 'Agent'
  *   ...
  * </pre>
  * @return The object that implements the <code>AgentContext</code> interface.
  * @see yaak.agent.AgentContext
  */

  public static AgentContext createAgentContext() {
    return AgentContainer.getInstance();
  }

  /**
  * <p>Serves up an agent context/place.  Applications create agents
  * (specializations of <code>Agent</code>), not by instantiating an
  * <code>Agent</code> specialization, but by requesting an agent from
  * an agent context; applications obtain an agent context by requesting
  * it from <code>AgentSystem</code>:</p>
  * <pre>
  *   ...
  *   AgentContext ac = AgentSystem.createAgentContext("MyAgentContext");
  *   ac.createAgent("Hello", null); // 'Hello' extends 'Agent'
  *   ...
  * </pre>
  * @param contextName The external/symbolic name for the context.
  * @return The object that implements the <code>AgentContext</code> interface.
  * @see yaak.agent.AgentContext
  */

  public static AgentContext createAgentContext(String contextName) {
    return AgentContainer.getInstance(contextName);
  }

  /**
  * <p>Serves up an agent context/place.  Applications create agents
  * (specializations of <code>Agent</code>), not by instantiating an
  * <code>Agent</code> specialization, but by requesting an agent from
  * an agent context; applications obtain an agent context by requesting
  * it from <code>AgentSystem</code>:</p>
  * <pre>
  *   ...
  *   AgentContext ac = AgentSystem.createAgentContext(parentContext);
  *   ac.createAgent("Hello", null); // 'Hello' extends 'Agent'
  *   ...
  * </pre>
  * @param parent The (existing) parent agent context.
  * @return The object that implements the <code>AgentContext</code> interface.
  * @throws AgentException Unable to create the agent context
  * within the parent context.
  * @see yaak.agent.AgentContext
  */

  public static AgentContext createAgentContext(AgentContext parent)
      throws AgentException {
    return (AgentContext) parent.createAgent(parent.getClass());
  }

  /**
  * <p>Serves up an agent context/place.  Applications create agents
  * (specializations of <code>Agent</code>), not by instantiating an
  * <code>Agent</code> specialization, but by requesting an agent from
  * an agent context; applications obtain an agent context by requesting
  * it from <code>AgentSystem</code>:</p>
  * <pre>
  *   ...
  *   AgentContext ac =
  *     AgentSystem.createAgentContext(parentContext, contextName);
  *   ac.createAgent("Hello", null); // 'Hello' extends 'Agent'
  *   ...
  * </pre>
  * @param parent The (existing) parent agent context.
  * @param contextName The external/symbolic name for the context.
  * @return The object that implements the <code>AgentContext</code> interface.
  * @throws AgentException Unable to create the agent context
  * within the parent context.
  * @see yaak.agent.AgentContext
  */

  public static AgentContext createAgentContext(AgentContext parent,
      String contextName) throws AgentException {
//    return (AgentContext) parent.createAgent(parent.getClass(), contextName);
    AgentContainer child =
      (AgentContainer) parent.createAgent(yaak.agent.AgentContainer.class);
    child.setContextName(contextName);
    return child;
  }

  /**
  * <p>Retrieves a handle to the communication server.  The communication
  * server is activated automatically (on demand), but only if the
  * application is configured for distributed agent contexts, based on
  * the resource <code>yaak.agent.communication.isDistributed</code>.</p>
  */

  public static CommunicationServerAdmin getCommunicationServer() {
    if (!Configuration.isDistributed()) {
      throw new AgentRuntimeException(
        "Currently, distributed agent services are not activated.");
    }
    //
    // Just in case someone tries to catch the start-up exception...
    //
    if (communicationServer == null) {
      throw new AgentRuntimeException(
        "Internal error: cannot get communication server.");
    }
    return communicationServer;
  }

  /**
  * <p>Shuts down gracefully all global resources exposed by the
  * communication server.  The communication server is activated
  * automatically (on demand), but only if the application is
  * configured for distributed agent contexts, based on the resource
  * <code>yaak.agent.communication.isDistributed</code>.</p>
  */

  public static void shutdownCommunicationServer() {
    if (!Configuration.isDistributed()) {
      return;
    }
    //
    // Just in case someone tries to catch start-up exception...
    //
    if (communicationServer == null) {
//      throw new AgentRuntimeException(
//        "Internal error: cannot get communication server.");
      return; // just return quietly
    }
    communicationServer.shutdown();
  }

  /**
  * <p>Sleeps for an arbitrary number of seconds.  This method
  * is provided as a convenience; it simply calls
  * {@link yaak.util.Util#sleepSeconds Util.sleepSeconds()}.</p>
  * @param seconds The number of seconds to hold this thread "at idle."
  */

  public static void sleepSeconds(long seconds) {
    Util.sleepSeconds(seconds);
  }
}
