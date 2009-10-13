package yaak.agent.communication;

/**
* <p><code>PointChannel</code> represents one-to-one communications.</p>
* @author Jerry Smith
* @version $Id: PointChannel.java 6 2005-06-08 17:00:15Z jsmith $
*/

public interface PointChannel {
  /**
  * <p>Sends a message (delivers it) to the registered channel listener.</p>
  * @param message The object that carries the pertinent data.
  * @throws CommunicationException Either the point-to-point channel
  * is not operational, or the receiving agent cannot be located.
  */

  void send(Message message) throws CommunicationException;

  /**
  * <p>Sends a payload (delivers it) to the registered channel listener.</p>
  * @param payload The object that carries the pertinent data.
  * @throws CommunicationException Either the point-to-point channel
  * is not operational, or the receiving agent cannot be located.
  */

  void send(Payload payload) throws CommunicationException;
}
