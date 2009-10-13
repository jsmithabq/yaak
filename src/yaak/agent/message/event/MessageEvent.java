package yaak.agent.message.event;

import yaak.agent.event.AgentEvent;

/**
* <p><code>MessageEvent</code> provides infomation for objects that
* register as <code>MessageListener</code> objects.</p>
* @author Jerry Smith
* @version $Id: MessageEvent.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class MessageEvent extends AgentEvent {
  /**
  * <p>Indicates a received message.</p>
  */

  public static final int MESSAGE_RECEIVED = 1;

  /**
  * <p>Indicates a sent message.</p>
  */

  public static final int MESSAGE_SENT= 2;

  /**
  * <p>Passed to objects that register as message listeners.
  * @param eventID The type of event, for example, a
  * <code>MESSAGE_RECEIVED</code> operation.
  * @param source The source object for the event.
  */

  public MessageEvent(int eventID, Object source) {
    super(source, eventID);
  }
}
