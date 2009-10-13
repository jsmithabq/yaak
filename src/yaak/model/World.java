package yaak.model;

import yaak.agent.AgentContext;
import yaak.agent.AgentException;
import yaak.agent.AgentSystem;
import yaak.model.service.ServiceArena;
import yaak.model.service.ServiceArenaFactory;
import yaak.util.StringUtil;

/**
* <p><code>World</code> implements the domain-specific environment.
* It provides the domain representation/state for the
* {@link yaak.model.ModelExecutive ModelExecutive}, which handles
* the world-model, action-level interpretation of the domain operations.</p>
* @author Jerry Smith
* @version $Id: World.java 13 2006-02-12 18:30:51Z jsmith $
*/

public class World {
  private static final String defaultArenaName = "WorldServiceArena";
  private static final String defaultContextName = "WorldAgentContext";
  private ServiceArena serviceArena = null; // potentially remains null
  private AgentContext agentContext = null; // potentially remains null

  /**
  * <p>Not for public consumption.</p>
  */

  protected World() {
  }

  /**
  * <p>Initialize services, including a default service arena.</p>
  * @return The service arena.
  */

  public ServiceArena initServices() {
    return initServices(defaultContextName, defaultArenaName);
  }

  /**
  * <p>Initialize services, including default agent context and
  * service arena.</p>
  * @param agentContextName The symbolic name for the context.
  * @param serviceArenaName The symbolic name for the arena.
  * @return The service arena.
  */

  public ServiceArena initServices(String agentContextName,
      String serviceArenaName) {
    agentContext = AgentSystem.createAgentContext(
      StringUtil.checkValue(agentContextName, defaultContextName));
    serviceArena = ServiceArenaFactory.getInstance(
      StringUtil.checkValue(serviceArenaName, defaultArenaName));
    return serviceArena;
  }

  /**
  * <p>Retrieves the agent context.</p>
  * @return The agent context, or <code>null</code> if services are not
  * available/initialized.
  */

  public AgentContext getAgentContext() {
    return agentContext;
  }

  /**
  * <p>Retrieves the service arena.</p>
  * @return The service arena, or <code>null</code> if services are not
  * available/initialized.
  */

  public ServiceArena getServiceArena() {
    return serviceArena;
  }

  /**
  * <p>Activates the world environment.</p>
  */

  public void activate() {
    serviceArena.start();
  }

  /**
  * <p>Deactivates the world environment.</p>
  */

  public void deactivate() {
    serviceArena.shutdown();
    serviceArena.dispose();
    agentContext.dispose();
  }
}
