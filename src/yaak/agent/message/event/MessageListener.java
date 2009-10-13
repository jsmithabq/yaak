package yaak.agent.message.event;

import java.util.EventListener;

/**
* <p><code>MessageListener</code> provides notifications services for
* significant messaging operations:  sending and receiving.</p>
* @author Jerry Smith
* @version $Id: MessageListener.java 6 2005-06-08 17:00:15Z jsmith $
*/

public interface MessageListener extends EventListener {
  /**
  * <p>Invoked each time a message is sent.</p>
  * @param event The event object.
  */

  public void messageSent(MessageEvent event);

  /**
  * <p>Invoked each time a message is received.</p>
  * @param event The event object.
  */

  public void messageReceived(MessageEvent event);
}
