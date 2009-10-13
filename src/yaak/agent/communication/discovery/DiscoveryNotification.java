package yaak.agent.communication.discovery;

/**
* <p><code>DiscoveryNotification</code> communicates (to a local
* observer/listener) the broadcast information for the base
* discovery-listener protocol.</p>
* @author Jerry Smith
* @version $Id: DiscoveryNotification.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class DiscoveryNotification {
  private String broadcastInformation; // can be anything
  private Object broadcastObject; // can be anything

  /**
  * <p>Instantiates a notification object.</p>
  * <p>Not for public consumption.</p>
  */

  protected DiscoveryNotification() {
  }

  /**
  * <p>Instantiates a notification object.</p>
  * <p>Not for public consumption.</p>
  * @param broadcastInformation The appropriate broadcast-related information.
  */

  protected DiscoveryNotification(String broadcastInformation) {
    this(broadcastInformation, null);
  }

  /**
  * <p>Instantiates a notification object.</p>
  * <p>Not for public consumption.</p>
  * @param broadcastInformation The appropriate broadcast-related information.
  * @param broadcastObject The appropriate broadcast-related object.
  */

  protected DiscoveryNotification(String broadcastInformation,
      Object broadcastObject) {
    this.broadcastInformation = broadcastInformation;
    this.broadcastObject = broadcastObject;
  }

  /**
  * <p>Sets the broadcast-related information (for notifications).</p>
  * @param broadcastInformation The appropriate broadcast-related information.
  */

  protected void setBroadcastInformation(String broadcastInformation) {
    this.broadcastInformation = broadcastInformation;
  }

  /**
  * <p>Sets the broadcast-related object (for notifications).</p>
  * @param broadcastObject The appropriate broadcast-related object.
  */

  protected void setBroadcastObject(Object broadcastObject) {
    this.broadcastObject = broadcastObject;
  }

  /**
  * <p>Gets the encapsulated (textual) broadcast information.</p>
  * @return The broadcast information, if any.
  */

  public String getBroadcastInformation() {
    return broadcastInformation;
  }

  /**
  * <p>Gets the encapsulated broadcast-related object.</p>
  * @return The broadcast object, if any.
  */

  public Object getBroadcastObject() {
    return broadcastObject;
  }
}
