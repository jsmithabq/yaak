package yaak.agent.communication.event;

import java.util.EventListener;

/**
* <p><code>CommunicationListener</code> provides notifications services for
* significant communication operations:  sending and receiving.</p>
* @author Jerry Smith
* @version $Id: CommunicationListener.java 6 2005-06-08 17:00:15Z jsmith $
*/

public interface CommunicationListener extends EventListener {
  /**
  * <p>Invoked each time the pod sends a communication.</p>
  * @param event The event object.
  */

  public void messageSent(CommunicationEvent event);

  /**
  * <p>Invoked each time the pod receives a communication.</p>
  * @param event The event object.
  */

  public void messageReceived(CommunicationEvent event);
}
