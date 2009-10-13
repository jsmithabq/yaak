package yaak.agent.communication.discovery;

/**
* <p><code>DiscoveryServiceFactory</code> creates discovery services.</p>
* @author Jerry Smith
* @version $Id: DiscoveryServiceFactory.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class DiscoveryServiceFactory {
  private DiscoveryServiceFactory() {
  }

  /**
  * <p>Retrieves a discovery service object.</p>
  * @return The discovery service.
  */

  public static DiscoveryService getService() {
    return DiscoveryServiceImpl.getService();
  }

  /**
  * <p>Retrieves a discovery service object.</p>
  * @return The discovery service.
  */

  public static DiscoveryService getInstance() {
    return DiscoveryServiceImpl.getInstance();
  }
}
