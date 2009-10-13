package yaak.agent.communication;

/**
* <p><code>CommunicationChannel</code> represents one or more
* communication models.</p>
* @author Jerry Smith
* @version $Id: CommunicationChannel.java 6 2005-06-08 17:00:15Z jsmith $
*/

public interface CommunicationChannel extends BroadcastChannel, PointChannel {
  /**
  * <p>Registers the listener for this channel.</p>
  * @param channelListener The object that is registering to
  * receive communications.
  */

  void setChannelListener(ChannelListener channelListener);

  /**
  * <p>Gets the name for the channel.</p>
  * @return The channel name.
  */

  String getChannelName();
}
