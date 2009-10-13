package yaak.agent.event;

import java.util.EventListener;

/**
* <p><code>AgentContextListener</code> provides notifications services for
* significant events in an agent context:  creation and disposal.</p>
* @author Jerry Smith
* @version $Id: AgentContextListener.java 6 2005-06-08 17:00:15Z jsmith $
*/

public interface AgentContextListener extends EventListener {
  /**
  * <p>Invoked each time the context creates an agent.</p>
  * @param event The event object.
  */

  public void agentCreated(AgentContextEvent event);

  /**
  * <p>Invoked each time the context disposes of an agent.</p>
  * @param event The event object.
  */

  public void agentDisposed(AgentContextEvent event);
}
