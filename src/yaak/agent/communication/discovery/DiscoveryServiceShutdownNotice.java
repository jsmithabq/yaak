package yaak.agent.communication.discovery;

import yaak.util.Util;

/**
* <p><code>DiscoveryServiceShutdownNotice</code> sends a request to shutdown,
* which will be received by all active applications that use this discovery
* service.  Usage:</p>
  * <pre>
  *   java yaak.agent.communication.discovery.DiscoveryServiceShutdownNotice
  * </pre>
* @author Jerry Smith
* @version $Id: DiscoveryServiceShutdownNotice.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class DiscoveryServiceShutdownNotice {
  private DiscoveryServiceShutdownNotice() {
  }

  /**
  * <p>Not for public consumption.</p>
  * <p>Runs the program, broadcasting a shut-down notice:</p>
  * <pre>
  *   java yaak.agent.communication.discovery.DiscoveryServiceShutdownNotice
  * </pre>
  */

  public static void main(String[] args) {
    DiscoveryService ds = DiscoveryServiceImpl.getService();
    ds.start();
    Util.sleepSeconds(1);
    ds.shutdown();
  }
}
