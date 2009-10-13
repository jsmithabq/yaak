package yaak.agent.communication;

/**
* <p><code>BroadcastChannel</code> represents one-to-many communications.</p>
* @author Jerry Smith
* @version $Id: BroadcastChannel.java 6 2005-06-08 17:00:15Z jsmith $
*/

public interface BroadcastChannel {
  /**
  * <p>Publishes a message to the channel.</p>
  * @param message The object that carries the pertinent data.
  * @throws CommunicationException The sending channel is not
  * operational.
  */

  void publish(Message message) throws CommunicationException;

  /**
  * <p>Publishes a payload to the channel.</p>
  * @param payload The object that carries the pertinent data.
  * @throws CommunicationException The sending channel is not
  * operational.
  */

  void publish(Payload payload) throws CommunicationException;
}
