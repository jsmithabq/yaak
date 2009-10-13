package yaak.agent.communication.discovery;

import yaak.core.YaakException;
import yaak.core.YaakSystem;

/**
* <p><code>Configuration</code> manages communications configuration
* data.  This class provides a temporary configuration solution and is
* under construction.</p>
* @author Jerry Smith
* @version $Id: Configuration.java 6 2005-06-08 17:00:15Z jsmith $
*/

public final class Configuration {
  private static final String AGENT_COMMUNICATION_DISCOVERY_GROUP =
    "yaak.agent.communication.discoveryGroup";
  private static final String AGENT_COMMUNICATION_DISCOVERY_PORT =
    "yaak.agent.communication.discoveryPort";
  private static final String DISCOVERY_GROUP = "231.231.231.231";
  private static final String DISCOVERY_PORT = "9231";

  private static String discoveryGroup = DISCOVERY_GROUP;
  private static int discoveryPort =
    new Integer(DISCOVERY_PORT).intValue();

  static {
    try {
      discoveryGroup =
        YaakSystem.getProperty(AGENT_COMMUNICATION_DISCOVERY_GROUP,
          DISCOVERY_GROUP);
      discoveryPort =
        new Integer(
          YaakSystem.getProperty(AGENT_COMMUNICATION_DISCOVERY_PORT,
          DISCOVERY_PORT)
        ).intValue();
    }
    catch (YaakException e) {
      YaakSystem.getLogger().warning("cannot load properties: ");
      e.printStackTrace();
    }
  }

  private Configuration() {
  }

  /**
  * <p>Gets the discovery-service multicast group.</p>
  * @return The discovery-service multicast group.
  */

  public static String getDiscoveryGroup() {
    return discoveryGroup;
  }

  /**
  * <p>Gets the discovery-service multicast port.</p>
  * @return The discovery-service multicast port.
  */

  public static int getDiscoveryPort() {
    return discoveryPort;
  }
}
