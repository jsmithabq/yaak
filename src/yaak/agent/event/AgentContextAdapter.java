package yaak.agent.event;

/**
* <p><code>AgentContextAdapter</code> provides an empty, or place-holder,
* implementation of the <code>AgentContextListener</code> interface.</p>
* @author Jerry Smith
* @version $Id: AgentContextAdapter.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class AgentContextAdapter implements AgentContextListener {
  /**
  * <p>Instantiates the adapter.</p>
  */

  public AgentContextAdapter() {
  }

  /**
  * <p>Invoked each time an agent is created in the local context.</p>
  * @param event The event object.
  */

  public void agentCreated(AgentContextEvent event) {
  }

  /**
  * <p>Invoked each time an agent is disposed of in the local context.</p>
  * @param event The event object.
  */

  public void agentDisposed(AgentContextEvent event) {
  }
}
