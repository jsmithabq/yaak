package yaak.agent.communication.jmx;

import javax.management.MBeanNotificationInfo;
import javax.management.NotificationBroadcasterSupport;
import javax.management.Notification;

import yaak.core.YaakSystem;

/**
* <p><code>ChannelManagementNotifier</code> is an MBean for
* inter-application channel management.</p>
* @author Jerry Smith
* @version $Id: ChannelManagementNotifier.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class ChannelManagementNotifier extends NotificationBroadcasterSupport
    implements ChannelManagementNotifierMBean {

  /**    
  * <p>Instantiates a channel notifier.</p>
  */

  public ChannelManagementNotifier() {
    super();
  }

  /**    
  * <p>Provides notification-related information.</p>
  * @return The notification information.
  */

  public MBeanNotificationInfo[] getNotificationInfo() {
    MBeanNotificationInfo[] notifyInfoArray  = new MBeanNotificationInfo[1];
    String[] notifyTypes = {
      ChannelNotification.CHANNEL_CREATION,
      ChannelNotification.CHANNEL_DELETION,
      ChannelNotification.CHANNEL_EXERCISE
    };
    notifyInfoArray[0] = new MBeanNotificationInfo(notifyTypes,
      "javax.management.Notification",
      "Channel notifications sent by ChannelManagementNotifier");
    return notifyInfoArray;
  }

  /**
  * <p>Sends a pass-through notification.</p>
  */

  public void sendNotification() {
    sendNotification(
      new ChannelNotification(ChannelNotification.CHANNEL_EXERCISE, this, 0));
  }

  void sendCreationNotification(ChannelNotification notify) { // default privacy
    sendNotification(notify);
  }

  void sendDeletionNotification(ChannelNotification notify) { // default privacy
    sendNotification(notify);
  }
}
