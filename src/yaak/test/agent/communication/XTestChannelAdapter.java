package yaak.test.agent.communication;

import yaak.agent.communication.CommunicationException;
import yaak.agent.communication.ChannelListener;
import yaak.agent.communication.Payload;
import yaak.agent.communication.PrivateChannelAdapter;
import yaak.core.YaakSystem;

/**
* <p><code>XTestChannelAdapter</code> tests use of the channel
* adapter functionality.</p>
* @author Jerry Smith
* @version $Id: XTestChannelAdapter.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class XTestChannelAdapter {
  public static void main(String[] args) {
    PrivateChannelAdapter adapterA = new PrivateChannelAdapter("A");
    PrivateChannelAdapter adapterB = new PrivateChannelAdapter("B");
    adapterA.addChannelListener(new ChannelListener() {
      public void onDelivery(Payload payload) {
        System.out.println("Adapter A-1 received a delivery:");
        System.out.println("" + payload.getObject());
      }
    });
    adapterA.addChannelListener(new ChannelListener() {
      public void onDelivery(Payload payload) {
        System.out.println("Adapter A-2 received a delivery:");
        System.out.println("" + payload.getObject());
      }
    });
    adapterB.addChannelListener(new ChannelListener() {
      public void onDelivery(Payload payload) {
        System.out.println("Adapter B-1 received a delivery:");
        System.out.println("" + payload.getObject());
      }
    });
    adapterB.addChannelListener(new ChannelListener() {
      public void onDelivery(Payload payload) {
        System.out.println("Adapter B-2 received a delivery:");
        System.out.println("" + payload.getObject());
      }
    });
    Payload payloadA = new Payload("The heart of the matter.");
    Payload payloadB = new Payload("The cream of the crop.");
    try {
      adapterA.publish(payloadA);
      adapterB.publish(payloadB);
    }
    catch (CommunicationException e) {
      YaakSystem.getLogger().fine("Could not publish data.");
    }
  }
}
