package yaak.util;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;

import yaak.core.YaakRuntimeException;

/**
* <p><code>Util</code> provides class-level convenience methods.</p>
* @author Jerry Smith
* @version $Id: Util.java 6 2005-06-08 17:00:15Z jsmith $
*/

public final class Util {
  private Util() {
  }

  /**
  * <p>Sleeps for the specified time--in seconds.</p>
  * @param seconds Number of seconds that the current thread
  * sleeps.
  */

  public static void sleepSeconds(long seconds) {
    try { Thread.sleep(seconds * 1000); }  catch (Exception e) {}
  }

  /**
  * <p>Sleeps for the specified time--in milliseconds.</p>
  * @param ms Number of milliseconds that the current
  * thread sleeps.
  */

  public static void sleepMilliseconds(long ms) {
    try { Thread.sleep(ms); }  catch (Exception e) {}
  }


  /**
  * <p>Determines the current host's fully qualified hostname.</p>
  * @return The fully qualified hostname.
  */

  public static String getHostname() {
    try {
      InetAddress ia = InetAddress.getLocalHost();
      return ia.getHostName();
    }
    catch (UnknownHostException e) {
      return null;
    }
  }

  /**
  * <p>Determines the current host's fully qualified hostname.</p>
  * @return The fully qualified hostname.
  */

  public static String getHostName() {
    return getHostname();
  }

  /**
  * <p>Determines whether or not the host exists (is locatable).</p>
  * @param hostname The name of the network host.
  * @return Whether or not the host is locatable.
  */

  public static boolean hostExists(String hostname) {
    return getIPAddress(hostname) != null;
  }

  /**
  * <p>Determines a host's IP address in string form.</p>
  * @param hostname The name of the network host.
  * @return The IP address of the network host.
  */

  public static String getIPAddress(String hostname) {
    String ipAddr = null;
    try {
      InetAddress ia = InetAddress.getByName(hostname);
      ipAddr = ia.getHostAddress();
    }
    catch (UnknownHostException e) {
    }
    return ipAddr;
  }

  /**
  * <p>Determines the system-dependent line-separator sequence.</p>
  * @return The line-separator character sequence.
  */

  public static String getLineSeparator() {
    return System.getProperty("line.separator");
  }

  /**
  * <p>Determines the first available port, beginning at the specified
  * port.  The search is abandoned after 500 increments.</p>
  * @param port The specified port.
  * @return The first available port.
  */

  public static int findAvailablePort(int port) {
    int limit = 500;
    Socket socket;
    while (limit > 0) {
      try {
        socket = new Socket("localhost", port);
        socket.close();
        limit--;
        port++;
        continue;
      }
      catch (Exception e) {
//        e.printStackTrace();
        return port;
      }
    }
    return -1;
  }

  /**
  * <p>Determines whether or not a RMI registry exists on the local host.</p>
  * @param port The specified port.
  * @return Whether or not the registry exist on the specified port.
  */

  public static boolean rmiRegistryExists(int port) {
    try {
//      java.rmi.registry.LocateRegistry.getRegistry(port);
      Naming.list("rmi://localhost:" + port);
      return true;
    }
    catch (MalformedURLException e) {
      throw new YaakRuntimeException("Malformed URL.");
    }
    catch (RemoteException e) {
      return false;
    }
  }

}
