package yaak.agent.communication.jdmk;

/**
* <p><code>ChannelManagementNotifierMBean</code> is an MBean for
* inter-application channel management.</p>
* @author Jerry Smith
* @version $Id: ChannelManagementNotifierMBean.java 6 2005-06-08 17:00:15Z jsmith $
*/

public interface ChannelManagementNotifierMBean {
  /**
  * <p>Sends a pass-through notification.</p>
  */

  void sendNotification();
}
