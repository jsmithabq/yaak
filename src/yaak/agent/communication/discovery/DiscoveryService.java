package yaak.agent.communication.discovery;

/**
* <p>This <code>DiscoveryService</code> interface represents a
* server instance that provides basic discovery services.</p>
* @author Jerry Smith
* @version $Id: DiscoveryService.java 6 2005-06-08 17:00:15Z jsmith $
*/

public interface DiscoveryService {
  /**
  * <p>Starts services for this discovery service.</p>
  */

  void start();

  /**
  * <p>Shuts down services for this discovery service.</p>
  */

  void shutdown();

  /**
  * <p>Sets the default broadcast information.</p>
  * @param information The broadcast information.
  */

  void setBroadcastInformation(String information);

  /**
  * <p>Gets the broadcast TTL.</p>
  * @return The broadcast ttl.
  */

  int getBroadcastTTL();

  /**
  * <p>Sets the broadcast TTL.</p>
  * @param ttl The broadcast ttl.
  */

  void setBroadcastTTL(int ttl);

  /**
  * <p>Gets the broadcast buffer size.</p>
  * @return The broadcast buffer size.
  */

  int getBroadcastBufferSize();

  /**
  * <p>Sets the broadcast buffer size.</p>
  * @param size The buffer size.
  */

  void setBroadcastBufferSize(int size);

  /**
  * <p>Sends the default broadcast information.</p>
  */

  public void sendBroadcast();

  /**
  * <p>Sends the specified broadcast information.</p>
  * @param information The information.
  */

  void sendBroadcast(String information);

  /**
  * <p>Registers a listener for discovery notifications.</p>
  * @param listener The discovery listener.
  */

  void addDiscoveryListener(DiscoveryListener listener);

  /**
  * <p>Unregisters a listener for discovery notifications.</p>
  * @param listener The discovery listener.
  */

  void removeDiscoveryListener(DiscoveryListener listener);
}
