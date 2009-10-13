package yaak.agent.communication;

import java.io.Serializable;

/**
* <p><code>Payload</code> is the transport wrapper for any data (object)
* that's sent over a channel.  The name reflects the fact that the
* content has no further structure, that is, the payload is the entire
* message.</p>
* @author Jerry Smith
* @version $Id: Payload.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class Payload implements Serializable {
  private Serializable payload;
  private boolean broadcast = false;

  /**
  * <p>Creates and initializes the payload.</p>
  * @param payload The application-specific object that
  * constitutes the payload.
  */

  public Payload(Serializable payload) {
    this.payload = payload;
  }

  /**
  * <p>Retrieves the (packaged) object.</p>
  * @return The encapsulated payload object.
  */

  public Object getObject() {
    return (Object) payload;
  }

  /**
  * <p>Whether or not the message distribution is broadcast; if not,
  * then it's point-to-point.</p>
  * @return A broadcast communication channel, or not.
  */

  public boolean isBroadcast() {
    return broadcast;
  }

  /**
  * <p>Whether or not the message distribution is point-to-point; if not,
  * then it's broadcast.</p>
  * @return A point-to-point communication channel, or not.
  */

  public boolean isPointToPoint() {
    return !broadcast;
  }

  /**
  * <p>Whether or not the message distribution is point-to-point; if not,
  * then it's broadcast.</p>
  * @return A point-to-point communication channel, or not.
  */

  public boolean isP2P() {
    return !broadcast;
  }

  /**
  * <p>Sets the broadcast status.  Not for public consumption.  This
  * flag is set by the publish-related methods during communications,
  * overriding any application setting.</p>
  * @param broadcast Whether or not the payload is destined for
  * broadcast-style (as opposed to point-to-point) distribution.
  */

  public void setBroadcast(boolean broadcast) {
    this.broadcast = broadcast;
  }

  /**
  * <p>Sets the point-to-point status.  Not for public consumption.  This
  * flag is set by the send-related methods during communications,
  * overriding any application setting.</p>
  * @param p2p Whether or not the payload is destined for
  * point-to-point (as opposed to broadcast-style) distribution.
  */

  public void setPointToPoint(boolean p2p) {
    this.broadcast = !p2p;
  }
}
