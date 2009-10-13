package yaak.agent.communication;

import java.util.Vector;

import yaak.agent.Agent;
import yaak.agent.AgentException;
import yaak.core.YaakSystem;

/**
* <p><code>PrivateChannelAdapter</code> provides "closed-loop" communication
* channels.  Applications can register arbitrary (bundled) listeners and
* then deliver an object (any payload) to all listeners.  Generally,
* communication channels have a single registered listener; this adapter,
* however, encapsulates dynamic channel creation, along with a dynamically
* changable set of listeners.</p>
* <p>The adapter automatically, and dynamically, manages the logical
* channel, which is specified by an arbitrary symbolic name.</p>
* @author Jerry Smith
* @version $Id: PrivateChannelAdapter.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class PrivateChannelAdapter implements ChannelListener {
  private Vector listeners = new Vector();
  private CommunicationChannel channel;

  /**
  * <p>Establishes an adapter for the specified logical channel.  The
  * channel is allocated automatically and dynamically.</p>
  * @param channelName The arbitrary, logical channel name.
  */

  public PrivateChannelAdapter(String channelName) {
    channel = new CommunicationHandler(channelName);
    channel.setChannelListener(this);
  }

  /**
  * <p>Registers a listener for this channel.</p>
  * @param listener The channel listener.
  */

  synchronized public final void addChannelListener(
      ChannelListener listener) {
    listeners.add(listener);
  }

  /**
  * <p>Unregisters a listener for this channel.</p>
  * @param listener The channel listener.
  */

  synchronized public final void removeChannelListener(
      ChannelListener listener) {
    listeners.remove(listener);
  }

  /**
  * <p>Publishes (sends) an object to this adapter's registered recipients.
  * By definition, the communication is one-to-many in nature--from this
  * adapter to each registered listener.</p>
  * @param payload The object that carries the pertinent data.
  */

  public final void publish(Payload payload) throws CommunicationException {
    channel.publish(payload);
  }

  /**
  * <p>Sends (publishes) an object to this adapter's registered recipients.
  * By definition, the communication is one-to-many in nature--from this
  * adapter to each registered listener.</p>
  * @param payload The object that carries the pertinent data.
  */

  public final void send(Payload payload) throws CommunicationException {
    channel.send(payload);
  }

  /**
  * <p>Delivers the payload to this adapter's registered recipients.
  * This method handles incoming deliveries from the communication server
  * and is not intended for external/public use.</p>
  * @param payload The object that carries the pertinent data.
  */

  public final void onDelivery(Payload payload) {
    Object o = payload.getObject();
    Vector v;
    synchronized (this) {
      v = (Vector) listeners.clone();
    }
    for (int i = 0; i < v.size(); i++) {
      ChannelListener listener = (ChannelListener) v.elementAt(i);
      listener.onDelivery(payload);
    }
  }
}
