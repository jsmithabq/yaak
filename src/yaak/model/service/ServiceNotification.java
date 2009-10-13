package yaak.model.service;

import yaak.agent.Agent;

/**
* <p><code>ServiceNotification</code> communicates the required service
* request.</p>
* @author Jerry Smith
* @version $Id: ServiceNotification.java 13 2006-02-12 18:30:51Z jsmith $
*/

public class ServiceNotification {
  private Agent requestingAgent;

  /**
  * <p>Instantiates a notification object.</p>
  * <p>Not for public consumption.</p>
  */

  protected ServiceNotification(Agent requestingAgent) {
    this.requestingAgent = requestingAgent;
  }

  /**
  * <p>Gets the requesting agent.</p>
  * @return The requesting agent.
  */

  public Agent getRequestingAgent() {
    return requestingAgent;
  }
}
