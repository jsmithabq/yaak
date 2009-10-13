package yaak.agent.communication;

/**
* <p><code>CommunicationServer</code> handles point-to-point and
* publish/subscribe messaging.  This server provides dynamic channel
* management.</p>
* @author Jerry Smith
* @version $Id: CommunicationServer.java 6 2005-06-08 17:00:15Z jsmith $
*/

public interface CommunicationServer {
  /**
  * <p>Sets up and then retrieves a nonexistent,
  * <code>CommunicationChannel</code> object.  Applications should not
  * use this method directly.</p>
  * @param channelName The arbitrary channel name.
  * @param channelListener The (local) listener.
  * @return The object that implements the <code>CommunicationChannel</code>
  * interface.
  * @throws CommunicationException The channel already exists.
  */

  CommunicationChannel getChannelNew(String channelName,
    ChannelListener channelListener) throws CommunicationException;

  /**
  * <p>Retrieves an existing, or sets up and then retrieves a nonexistent,
  * <code>CommunicationChannel</code> object.  Applications should not
  * use this method directly.</p>
  * @param channelName The arbitrary channel name.
  * @param channelListener The (local) listener.
  * @return The object that implements the <code>CommunicationChannel</code>
  * interface.
  */

  CommunicationChannel getChannel(String channelName,
      ChannelListener channelListener);

  /**
  * <p>Retrieves an existing <code>CommunicationChannel</code> object.
  * Applications should not use this method directly.</p>
  * @param channelName The arbitrary channel name.
  * @return The object that implements the <code>CommunicationChannel</code>
  * interface.
  * @throws CommunicationRuntimeException The channel (currently)
  * does not exist.
  */

  CommunicationChannel getChannel(String channelName);

  /**
  * <p>Tests whether or not the channel already exists.</p>
  * @param channelName The arbitrary channel name.
  * @return Whether or not the channel exists.
  */

  boolean channelExists(String channelName);

  /**
  * <p>Logs the current list of channels.</p>
  */

  void logChannels();

  /**
  * <p>Closes down a channel.</p>
  * @param channelName The arbitrary channel name.
  */

  void close(String channelName);
}

