package yaak.agent.communication.discovery;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Vector;

import yaak.core.YaakException;
import yaak.core.YaakSystem;
import yaak.util.Util;

/**
* <p><code>DiscoveryServiceImpl</code> provides a very lightweight service
* for discovering active remote services.</p>
* @author Jerry Smith
* @version $Id: DiscoveryServiceImpl.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class DiscoveryServiceImpl implements DiscoveryService {
  private static final String BROADCAST_PREFIX = "Broadcast from host: ";
  private static final String DEFAULT_BROADCAST_INFORMATION =
    BROADCAST_PREFIX + Util.getHostname();
  private static final int DEFAULT_BROADCAST_TTL = 16;
  private static final int DEFAULT_BROADCAST_BUFFER_SIZE = 32768;
  private static final String SHUTDOWN = "shutdown";
  private Vector listeners = new Vector();
  private String group = Configuration.getDiscoveryGroup();
  private int port = Configuration.getDiscoveryPort();
  private int broadcastTTL = DEFAULT_BROADCAST_TTL;
  private int broadcastBufferSize = DEFAULT_BROADCAST_BUFFER_SIZE;
  private byte[] inBuffer = new byte[DEFAULT_BROADCAST_BUFFER_SIZE]; // shared
  private MulticastSocket broadcastSocket, inSocket; // shared
  private InetAddress iaGroup; // shared
  private String broadcastInformation = DEFAULT_BROADCAST_INFORMATION;
  private Thread thread = null;
  private boolean running = false;
  private Object lock = new Object();

  /**
  * <p>Runs a discovery service.</p>
  *
  * <p>There is one, optional, command-line argument:</p>
  * <pre>
  * java yaak.agent.communication.jmx.DiscoveryServiceImpl
  * java yaak.agent.communication.jmx.DiscoveryServiceImpl shutdown
  * </pre>
  * <p>The first usage starts a discovery service; the second usage
  * broadcasts a shut-down message to all running discovery services.</p>
  */

  public static void main(String[] args) {
    DiscoveryService ds = DiscoveryServiceFactory.getService();
    ds.start();
    if (args.length == 1) {
      if (args[0].equals(SHUTDOWN)) {
        ds.shutdown();
      }
    }
  }

  /**
  * <p>Retrieves a discovery service object.</p>
  * @return The discovery service.
  */

  protected static DiscoveryService getService() {
    return getInstance();
  }

  /**
  * <p>Retrieves a discovery service object.</p>
  * @return The discovery service.
  */

  protected static DiscoveryService getInstance() {
    return new DiscoveryServiceImpl();
  }

  private DiscoveryServiceImpl() {
    initDiscoveryServices();
  }

  private void initDiscoveryServices() {
    try {
      iaGroup = InetAddress.getByName(group);
      broadcastSocket = new MulticastSocket(port);
      setBroadcastTTL(broadcastTTL);
      broadcastSocket.joinGroup(iaGroup);
      inSocket = new MulticastSocket(port);
      inSocket.joinGroup(iaGroup);
      Util.sleepSeconds(1);
//      sendBroadcast(broadcastInformation);
    }
    catch (Exception e) {
      YaakSystem.getLogger().warning("Cannot initiate discovery operations: ");
      e.printStackTrace();
    }
  }

  private DatagramPacket createBroadcastPacket(String data) {
    return createBroadcastPacket(data.getBytes());
  }

  private DatagramPacket createBroadcastPacket(byte[] data) {
    return new DatagramPacket(data, data.length, iaGroup, port);
  }

  /**
  * <p>Starts services for this discovery service.</p>
  */

  public void start() {
    if (running) {
      return;
    }
    synchronized (lock) {
      running = true;
    }
    thread = new Thread(new BroadcastListener());
    thread.start();
  }

  /**
  * <p>Shuts down services for this discovery service.</p>
  */

  public void shutdown() {
    try {
      broadcastSocket.send(createBroadcastPacket(SHUTDOWN));
    }
    catch (Exception e) {
      YaakSystem.getLogger().warning("Cannot request shutdown:");
      e.printStackTrace();
    }
  }

  private void handleShutdown() {
    synchronized (lock) {
      running = false;
    }
    try {
      broadcastSocket.leaveGroup(iaGroup);
      inSocket.leaveGroup(iaGroup);
    }
    catch (Exception e) {
      YaakSystem.getLogger().warning("Cannot leave discovery multiicast group:");
      e.printStackTrace();
    }
    broadcastSocket.close();
    inSocket.close();
    thread = null;
  }

  /**
  * <p>Sets the default broadcast information.</p>
  * @param information The broadcast information.
  */

  public void setBroadcastInformation(String information) {
    if (information == null || information.length() == 0) {
      throw new IllegalArgumentException(
        "Broadcast information cannot be null.");
    }
    synchronized (lock) {
      broadcastInformation = information;
    }
  }

  /**
  * <p>Gets the broadcast TTL.</p>
  * @return The broadcast ttl.
  */

  public int getBroadcastTTL() {
    return broadcastTTL;
  }

  /**
  * <p>Sets the broadcast TTL.</p>
  * @param ttl The broadcast ttl.
  */

  public void setBroadcastTTL(int ttl) {
    //
    // No big-brother type checking:
    //
    synchronized (lock) {
      broadcastTTL = ttl;
      try {
        broadcastSocket.setTimeToLive(broadcastTTL);
      }
      catch (Exception e) {
        YaakSystem.getLogger().warning("Cannot set TTL:");
        e.printStackTrace();
      }
    }
  }

  /**
  * <p>Gets the broadcast buffer size (incoming data only).</p>
  * @return The broadcast buffer size.
  */

  public int getBroadcastBufferSize() {
    return broadcastBufferSize;
  }

  /**
  * <p>Sets the broadcast buffer size (incoming data only).</p>
  * @param size The buffer size.
  */

  public void setBroadcastBufferSize(int size) {
    //
    // No big-brother type checking:
    //
    synchronized (lock) {
      broadcastBufferSize = size;
      inBuffer = new byte[broadcastBufferSize];
    }
  }

  /**
  * <p>Sends the default broadcast information.</p>
  */

  public void sendBroadcast() {
    sendBroadcast(broadcastInformation);
  }

  /**
  * <p>Sends the specified broadcast information.</p>
  * @param information The information.
  */

  public void sendBroadcast(String information) {
    if (information == null || information.length() == 0) {
      throw new IllegalArgumentException(
        "Broadcast information cannot be null.");
    }
    try {
      broadcastSocket.send(createBroadcastPacket(information));
    }
    catch (Exception e) {
      YaakSystem.getLogger().warning("Cannot broadcast requested packet:");
      e.printStackTrace();
    }
  }

  /**
  * <p>Sends the specified broadcast information.</p>
  * @param information The information.
  */

  public void sendBroadcast(byte[] information) {
    if (information == null || information.length == 0) {
      throw new IllegalArgumentException(
        "Broadcast information cannot be null.");
    }
    try {
      broadcastSocket.send(createBroadcastPacket(information));
    }
    catch (Exception e) {
      YaakSystem.getLogger().warning("Cannot broadcast requested packet:");
      e.printStackTrace();
    }
  }

  private String getIncomingData(DatagramPacket packet) {
    if (packet == null) {
      return null;
    }
    return new String(packet.getData(), 0, packet.getLength());
  }

  /**
  * <p>Registers a listener for discovery notifications.</p>
  * @param listener The discovery listener.
  */

  public final void addDiscoveryListener(DiscoveryListener listener) {
    synchronized (listeners) {
      listeners.add(listener);
    }
  }

  /**
  * <p>Unregisters a listener for discovery notifications.</p>
  * @param listener The discovery listener.
  */

  synchronized public final void removeDiscoveryListener(
      DiscoveryListener listener) {
    synchronized (listeners) {
      listeners.remove(listener);
    }
  }

  /**
  * <p>Delivers the incoming broadcast as a notification to all registered
  * recipients.</p>
  * @param notification The object that carries the pertinent data.
  */

  protected final void onBroadcast(DiscoveryNotification notification) {
    Vector v;
    synchronized (this) {
      v = (Vector) listeners.clone();
    }
    for (int i = 0; i < v.size(); i++) {
      DiscoveryListener listener = (DiscoveryListener) v.elementAt(i);
      listener.handleNotification(notification);
    }
  }

  class BroadcastListener implements Runnable { // default privacy
    public void run() {
      DatagramPacket packet = null;
      while (running) {
        try {
          packet = new DatagramPacket(inBuffer, inBuffer.length);
          inSocket.receive(packet);
        }
        catch (Exception e) {
          YaakSystem.getLogger().warning("Cannot receive discovery packet:");
          e.printStackTrace();
        }
        String data = getIncomingData(packet);
        YaakSystem.getLogger().finer("Received data:");
        YaakSystem.getLogger().finer(data);
        if (data != null) {
          if (data.equals(SHUTDOWN)) {
            handleShutdown();
          }
          else {
            onBroadcast(
              new DiscoveryNotification(data, null)
            );
          }
        }
      }
    }
  }
}
