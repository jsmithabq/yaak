package yaak.agent.communication;

/**
* <p><code>MessageFilter</code> represents an object that evaluates
* whether or not the receiver wants the current message.</p>
* @author Jerry Smith
* @version $Id: MessageFilter.java 6 2005-06-08 17:00:15Z jsmith $
*/

public interface MessageFilter {
  /**
  * <p>The method executed against each message to determine
  * acceptance/delivery or rejection.</p>
  */

  boolean allow(Message message);
}
