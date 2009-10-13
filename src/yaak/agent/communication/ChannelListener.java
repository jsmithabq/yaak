package yaak.agent.communication;

/**
* <p><code>ChannelListener</code> represents an object that monitors
* data (object) transport (its arrival) over a channel.</p>
* @author Jerry Smith
* @version $Id: ChannelListener.java 6 2005-06-08 17:00:15Z jsmith $
*/

public interface ChannelListener {
  /**
  * <p>Executed upon arrival of each transported object.</p>
  * @param payload The object that encapsulates the transported object.
  */

   void onDelivery(Payload payload);
}
