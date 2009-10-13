package yaak.agent.communication;

/**
* <p><code>MessageListener</code> represents an object that monitors
* message transport (its arrival) over a channel.</p>
* @author Jerry Smith
* @version $Id: MessageListener.java 6 2005-06-08 17:00:15Z jsmith $
*/

public interface MessageListener {
  /**
  * <p>Executed upon arrival of each message.</p>
  * @param message The object that carries a message.
  */

  void onMessage(Message message);
}
