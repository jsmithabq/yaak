package yaak.agent.communication;

/**
* <p>Not for public consumption.</p>
* <p>The current communication layer does not support named channels,
* but retains a channel holder for future growth if deemed necessary.</p>
* @author Jerry Smith
* @version $Id: ContextRegistration.java 6 2005-06-08 17:00:15Z jsmith $
*/

final class ContextRegistration extends ContextAnnouncement { // default privacy
  private String contextName;
  private String channelName;
  private CommunicationChannel communicationChannel;

  ContextRegistration(String contextName, String channelName) {
    this.contextName = contextName;
    this.channelName = channelName;
  }

  String getContextName() {
    return contextName;
  }

/***********************************************
  String getCommunicationChannelName() {
    return channelName;
  }
***********************************************/

  CommunicationChannel getCommunicationChannel() {
    return communicationChannel;
  }

  void setCommunicationChannel(CommunicationChannel communicationChannel) {
    this.communicationChannel = communicationChannel;
  }

  /**
  * <p>Provides a representative summary of the registration
  * object.</p>
  * @return The state summary as a string.
  */

  public String toString() {
    return
      "[contextName = " + contextName + "], " +
/*      "[communicationChannel = " + channelName +*/
      "]";
  }
}
