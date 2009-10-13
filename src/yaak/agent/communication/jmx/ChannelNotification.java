package yaak.agent.communication.jmx;

import javax.management.ObjectName;
import javax.management.Notification;

/**
* <p><code>ChannelNotification</code> adds channel-related information
* to a notification.</p>
* @author Jerry Smith
* @version $Id: ChannelNotification.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class ChannelNotification extends Notification {
  /**
  * <p>Indicates that a channel has been created.</p>
  */
  public static final String CHANNEL_CREATION = "ChannelCreated";
  /**
  * <p>Indicates that a channel has been deleted.</p>
  */
  public static final String CHANNEL_DELETION = "ChannelDeleted";
  /**
  * <p>Indicates that a channel has been exercised (for testing only).</p>
  */
  public static final String CHANNEL_EXERCISE = "ChannelExercise";

  private String channelName;
  private String mBeanServerId;
  private ObjectName objectName;

  /**
  * <p>Creates a notification object.</p>
  * @param type The channel notification type.
  * @param source The object creating the notification.
  * @param sequenceNumber The sequence number -- unused in this
  * implementation.
  */

  public ChannelNotification(String type, Object source, long sequenceNumber) {
    super(type, source, sequenceNumber);
  }

  /**
  * <p>Gets the channel name.</p>
  * @return The channel name.
  */

  public String getChannelName() {
    return channelName;
  }

  void setChannelName(String channelName) { // default privacy
    this.channelName = channelName;
  }

  /**
  * <p>Gets the object name for the channel managed bean.</p>
  * @return The object name.
  */

  public ObjectName getObjectName() {
    return objectName;
  }

  void setObjectName(ObjectName objectName) { // default privacy
    this.objectName = objectName;
  }

  /**
  * <p>Gets the MBean server ID for the origin environment.</p>
  * @return The servre ID.
  */

  public String getMBeanServerId() {
    return mBeanServerId;
  }

  void setMBeanServerId(String mBeanServerId) { // default privacy
    this.mBeanServerId = mBeanServerId;
  }
}
