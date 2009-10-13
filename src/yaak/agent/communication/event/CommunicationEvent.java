package yaak.agent.communication.event;

import yaak.agent.event.AgentEvent;

/**
* <p><code>CommunicationEvent</code> provides infomation for objects that
* register as <code>CommunicationListener</code> objects.</p>
* @author Jerry Smith
* @version $Id: CommunicationEvent.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class CommunicationEvent extends AgentEvent {
  /**
  * <p>Indicates a received communication.</p>
  */

  public static final int COMM_RECEIVED = 1;

  /**
  * <p>Indicates a sent communication.</p>
  */

  public static final int COMM_SENT= 2;

  /**
  * <p>Passed to objects that register as communication listeners.
  * @param eventID The type of event, for example, an
  * <code>COMM_RECEIVED</code> operation.
  * @param source The source object for the event.
  */

  public CommunicationEvent(int eventID, Object source) {
    super(source, eventID);
  }
}
