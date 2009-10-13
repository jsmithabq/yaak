package yaak.agent.communication.discovery;

/**
* <p><code>DiscoveryListener</code> prescribes the minimal (most general)
* discovery-listener protocol.</p>
* @author Jerry Smith
* @version $Id: DiscoveryListener.java 6 2005-06-08 17:00:15Z jsmith $
*/

public interface DiscoveryListener {
  /**
  * <p>Implements the application-specific operations for a
  * discovery-related notification.</p>
  * @param notification The notification object originating within the
  * (local) discovery service that describes the most recent notification.
  */

  void handleNotification(DiscoveryNotification notification);
}
