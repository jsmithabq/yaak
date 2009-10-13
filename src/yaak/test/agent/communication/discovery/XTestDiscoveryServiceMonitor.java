package yaak.test.agent.communication.discovery;

import yaak.agent.communication.discovery.DiscoveryListener;
import yaak.agent.communication.discovery.DiscoveryNotification;
import yaak.agent.communication.discovery.DiscoveryService;
import yaak.agent.communication.discovery.DiscoveryServiceFactory;
import yaak.util.Util;

/**
* <p><code>XTestDiscoveryServiceMonitor</code> starts a discovery
* service and displays each incoming message.</p>
* @author Jerry Smith
* @version $Id: XTestDiscoveryServiceMonitor.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class XTestDiscoveryServiceMonitor {
  public static void main(String[] args) {
    final DiscoveryService ds = DiscoveryServiceFactory.getService();
    ds.addDiscoveryListener(
      new DiscoveryListener() {
        public void handleNotification(DiscoveryNotification notification) {
          System.out.println(notification.getBroadcastInformation());
        }
      }
    );
    ds.start();
//    Util.sleepSeconds(1);
    System.out.println("Sending a test broadcast...");
    ds.sendBroadcast();
    System.out.println("Sending a test broadcast...done.");
    //
    // Receive a message in a separate thread:
    //
    new Thread(
      new Runnable() {
        public void run() {
          try {
            wait(10000);
          }
          catch (Exception e) {
            ds.sendBroadcast(Util.getHostname() +
              ": I take exception to all this waiting...");
          }
        }
      }
    ).start();
  }
}
