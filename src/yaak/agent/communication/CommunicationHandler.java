package yaak.agent.communication;

/**
* <p><code>CommunicationHandler</code> handles point-to-point and
* publish/subscribe messaging.</p>
* <p>This handler is local; that is, it is for intra-JVM communications.</p>
* @author Jerry Smith
* @version $Id: CommunicationHandler.java 6 2005-06-08 17:00:15Z jsmith $
*/

class CommunicationHandler implements CommunicationChannel { // default privacy
  private String channelName = "none";
  private ChannelListener channelListener;

  /**
  * <p>Not for public consumption.</p>
  */

  CommunicationHandler(String channelName) { // default privacy
    this.channelName = channelName;
  }

  /**
  * <p>Gets the name for the channel.</p>
  * @return The channel name.
  */

  public final String getChannelName() {
    return channelName;
  }

  /**
  * <p>Registers the listener for this channel.</p>
  * @param channelListener The object that is registering to
  * receive communications.
  */

  public final void setChannelListener(ChannelListener channelListener) {
    this.channelListener = channelListener;
  }

  /**
  * <p>Accepts a payload and then transports it (delivers it to the
  * registered channel listener).</p>
  * @param payload The object that carries the pertinent data.
  * @throws CommunicationException The receiving channel is not
  * operational.
  */

  public final void send(Payload payload) throws CommunicationException {
    if (channelListener == null) {
      throw new CommunicationException("The channel listener is null.");
    }
    channelListener.onDelivery(payload);
  }

  /**
  * <p>Accepts a message and then sends it on (delivers it) to the
  * registered channel listener.</p>
  * @param message The object that carries the pertinent data.
  * @throws CommunicationException The receiving channel is not
  * operational.
  */

  public void send(Message message) throws CommunicationException { 
    send(new Payload(message));
  }

  /**
  * <p>Accepts a payload and then publishes it to the channel.</p>
  * @param payload The object that carries the pertinent data.
  * @throws CommunicationException The receiving channel is not
  * operational.
  */

  public final void publish(Payload payload) throws CommunicationException {
    if (channelListener == null) {
      throw new CommunicationException("The channel listener is null.");
    }
    payload.setBroadcast(true);
    channelListener.onDelivery(payload);
  }

  /**
  * <p>Accepts a message and then publishes it to the channel.</p>
  * @param message The object that carries the pertinent data.
  * @throws CommunicationException The receiving channel is not
  * operational.
  */

  public void publish(Message message) throws CommunicationException {
    publish(new Payload(message));
  }
}
