package yaak.agent.message.event;

/**
* <p><code>MessageAdapter</code> provides an empty, or place-holder,
* implementation of the <code>MessageListener</code> interface.</p>
* @author Jerry Smith
* @version $Id: MessageAdapter.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class MessageAdapter implements MessageListener {
  /**
  * <p>Instantiates the adapter.</p>
  */

  public MessageAdapter() {
  }

  /**
  * <p>Invoked each time a message is sent.</p>
  * @param event The event object.
  */

  public void messageSent(MessageEvent event) {
  }

  /**
  * <p>Invoked each time a message is received.</p>
  * @param event The event object.
  */

  public void messageReceived(MessageEvent event) {
  }
}
