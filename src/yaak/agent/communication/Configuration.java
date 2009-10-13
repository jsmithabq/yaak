package yaak.agent.communication;

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
  private static final String CHANNEL_NAME = "default";
  private static final String AGENT_COMMUNICATION_DISTRIBUTED =
    "yaak.agent.communication.isDistributed";
  private static final String AGENT_COMMUNICATION_SERVER =
    "yaak.agent.communication.server";
  private static final String AGENT_COMMUNICATION_HTML_PORT =
    "yaak.agent.communication.htmlPort";
  private static final String AGENT_COMMUNICATION_RMI_PORT =
    "yaak.agent.communication.rmiPort";
  private static final String DEFAULT_PREFIX = "";
  private static final String DISTRIBUTED = "false";
  private static final String COMMUNICATION_SERVER =
    "yaak.agent.communication.jmx.CommunicationServerImpl";
  private static final String HTML_PORT = "7082";
  private static final String RMI_PORT = "8082";

  private static String channelPrefix = DEFAULT_PREFIX;
  private static boolean distributed = new Boolean(DISTRIBUTED).booleanValue();
  private static String communicationServerClassname = COMMUNICATION_SERVER;
  private static int htmlPort = new Integer(HTML_PORT).intValue();
  private static int rmiPort = new Integer(RMI_PORT).intValue();

  static {
    try {
      distributed =
        new Boolean(
          YaakSystem.getProperty(AGENT_COMMUNICATION_DISTRIBUTED, DISTRIBUTED)
        ).booleanValue();
      communicationServerClassname =
        YaakSystem.getProperty(AGENT_COMMUNICATION_SERVER, COMMUNICATION_SERVER);
      htmlPort =
        new Integer(
          YaakSystem.getProperty(AGENT_COMMUNICATION_HTML_PORT, HTML_PORT)
        ).intValue();
      rmiPort =
        new Integer(
          YaakSystem.getProperty(AGENT_COMMUNICATION_RMI_PORT, RMI_PORT)
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
  * <p>Whether or not communication channels are distributed.</p>
  * @return Whether or not channels are distributed.
  */

  public static boolean isDistributed() {
    return distributed;
  }

  /**
  * <p>Whether or not communication channels are distributed.</p>
  * @return Whether or not channels are distributed.
  */

  public static boolean getDistributed() {
    return distributed;
  }

  /**
  * <p>Returns the name of the communication server implementation class.</p>
  * @return The fully qualified classname.
  */

  public static String getCommunicationServerClassname() {
    return communicationServerClassname;
  }

  /**
  * <p>Gets the HTML port.</p>
  * @return The HTML port.
  */

  public static int getHTMLPort() {
    return htmlPort;
  }

  /**
  * <p>Gets the RMI port.</p>
  * @return The RMI port.
  */

  public static int getRMIPort() {
    return rmiPort;
  }
}
