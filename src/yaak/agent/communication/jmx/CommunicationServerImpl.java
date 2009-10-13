package yaak.agent.communication.jmx;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.management.MalformedObjectNameException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MBeanServerNotification;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

import com.sun.jdmk.ServiceName;
import com.sun.jdmk.comm.ClientNotificationHandler;
import com.sun.jdmk.comm.CommunicatorServer;
import com.sun.jdmk.comm.ConnectorAddress;
import com.sun.jdmk.comm.RemoteMBeanServer;
import com.sun.jdmk.comm.RmiConnectorAddress;
import com.sun.jdmk.comm.RmiConnectorClient;
import com.sun.jdmk.comm.RmiConnectorServer;

import yaak.agent.AgentRuntimeException;
import yaak.core.YaakSystem;
import yaak.util.Util;

import yaak.agent.communication.ChannelListener;
import yaak.agent.communication.CommunicationChannel;
import yaak.agent.communication.CommunicationException;
import yaak.agent.communication.CommunicationRuntimeException;
import yaak.agent.communication.CommunicationServer;
import yaak.agent.communication.CommunicationServerAdmin;
import yaak.agent.communication.Configuration;
import yaak.agent.communication.Message;
import yaak.agent.communication.Payload;

import yaak.agent.communication.discovery.DiscoveryService;
import yaak.agent.communication.discovery.DiscoveryServiceFactory;
import yaak.agent.communication.discovery.DiscoveryListener;
import yaak.agent.communication.discovery.DiscoveryNotification;

/**
* <p><code>CommunicationServerImpl</code> handles point-to-point and
* publish/subscribe messaging.  This server provides dynamic channel
* management.</p>
* @author Jerry Smith
* @version $Id: CommunicationServerImpl.java 6 2005-06-08 17:00:15Z jsmith $
*/

public final class CommunicationServerImpl implements
  CommunicationServer, CommunicationServerAdmin {
  private static final String RMI_CONNECTOR_NAME = "RmiConnectorService";
  private static final String CHANNEL_TAG = "channel";
  private static final String NAME_TAG = "name";
  private static final String DEFAULT_CHANNEL = "default";
  private static final String DOMAIN = "CommunicationServer:";
//  private static final String DOMAIN = ServiceNyaak.DOMAIN + ":";
  private static final String CHANNEL_NOTIFIER_BEAN_NAME =
    DOMAIN + NAME_TAG + "=" + "ChannelManagementNotifier";
  private Hashtable channelTable = new Hashtable();
  private Hashtable connectorTable = new Hashtable();
  private MBeanServer mBeanServer = null;
  private DiscoveryService discoveryService = null;
  private ChannelManagementNotifier channelNotifier = null;
  private RmiConnectorServer rmiConnector = null;
  private RmiConnectorAddress rmiConnectorAddress = null;
  private String rmiConnectorServiceName = null;
  private ObjectName delegateName = null;
  private ObjectName rmiConnectorObjectName = null;
  private ObjectName channelNotifierObjectName = null;
  private String domain = DOMAIN;
  private String mBeanServerId = null;
  private int localChannelCount = 0;
  private static CommunicationServer communicationServer = null;

  //
  // This class is the "JMX agent."
  //

  /**
  * <p>Retrieves a handle to the communication server.</p>
  * @return The communication server.
  */

  public static CommunicationServer getServer() {
    return getInstance();
  }

  /**
  * <p>Retrieves a handle to the communication server.</p>
  * @return The communication server.
  */

  public static CommunicationServer getInstance() {
    if (communicationServer == null) {
      communicationServer = new CommunicationServerImpl();
    }
    return communicationServer;
  }

  private CommunicationServerImpl() {
    initDistributedServices();
  }

  private void initDistributedServices() {
    mBeanServer = MBeanServerFactory.createMBeanServer();
    try {
      delegateName = new ObjectName(ServiceName.DELEGATE);
    }
    catch (MalformedObjectNameException e) {
      String msg = "Cannot create object name for MBean server delegate.";
      YaakSystem.getLogger().warning(msg);
      throw new AgentRuntimeException(msg, e);
    }
    discoverThisMBeanServerId();
    createLocalChannelNotifierBean();
    createRmiConnector();
    startRmiConnector();
    ConnectorInfo connectorInfo = new ConnectorInfo(
      ConnectorInfo.APP_REGISTRATION, mBeanServerId,
      rmiConnectorServiceName, Util.getHostname(),
      String.valueOf(Configuration.getRMIPort()));
    discoveryService = DiscoveryServiceFactory.getService();
    discoveryService.addDiscoveryListener(new LocalDiscoveryListener());
    discoveryService.start();
    Util.sleepSeconds(1);
    discoveryService.sendBroadcast(connectorInfo.getConnectorInfoAsString());
  }

  private void createRmiConnector() {
    int rmiPort = Configuration.getRMIPort();
    YaakSystem.getLogger().fine("Using RMI port: " + rmiPort + ".");
    rmiConnectorServiceName = NAME_TAG + "=" + RMI_CONNECTOR_NAME
      + "_" + new java.rmi.dgc.VMID()
    ;
    rmiConnector = new RmiConnectorServer(rmiPort, rmiConnectorServiceName);
    rmiConnectorObjectName = null;
    try {
      rmiConnectorObjectName =
        new ObjectName(domain + NAME_TAG + "=" + RMI_CONNECTOR_NAME);
      ObjectInstance rmiConnectorInstance =
        mBeanServer.registerMBean(rmiConnector, rmiConnectorObjectName);
    }
    catch (MalformedObjectNameException e) {
      String msg = "Cannot create object name for RMI connector.";
      YaakSystem.getLogger().warning(msg);
      throw new AgentRuntimeException(msg, e);
    }
    catch (Exception e) {
      String msg = "Cannot set up RMI connector.";
      YaakSystem.getLogger().warning(msg);
      throw new AgentRuntimeException(msg, e);
    }
    rmiConnectorAddress = new RmiConnectorAddress(rmiConnector.getHost(),
      rmiConnector.getPort(), rmiConnector.getServiceName());
  }

  private void startRmiConnector() {
    if (rmiConnector.getState() == CommunicatorServer.ONLINE) {
      return;
    }
    YaakSystem.getLogger().fine("RMI connector starting...");
    rmiConnector.start();
    while (rmiConnector.getState() == CommunicatorServer.STARTING) {
      Util.sleepSeconds(1);
    }
    YaakSystem.getLogger().fine("RMI connector started.");
    
  }

  private void stopRmiConnector() {
    if (rmiConnector.getState() == CommunicatorServer.OFFLINE) {
      return;
    }
    YaakSystem.getLogger().fine("RMI connector stopping...");
    try {
      rmiConnector.stop();
      while (rmiConnector.getState() == CommunicatorServer.STOPPING) {
        Util.sleepSeconds(1);
      }
    }
    catch (Exception e) {
      String msg = "Cannot stop RMI connector.";
      YaakSystem.getLogger().warning(msg);
      throw new AgentRuntimeException(msg, e);
    }
    YaakSystem.getLogger().fine("RMI connector stopped.");
  }

  private void discoverThisMBeanServerId() {
    try {
      mBeanServerId = (String) mBeanServer.invoke(
        delegateName, "getMBeanServerId",
        new Object[0], new String[0]);
    }
    catch (Exception e) {
      YaakSystem.getLogger().warning("Cannot discover server ID.");
    }
  }

  private RemoteMBeanServer addRemoteMBeanServer(String mBeanServerId,
      ConnectorAddress address) {
    if (mBeanServerId == null || mBeanServerId.length() == 0 ||
        address == null) {
      return null;
    }
    RemoteMBeanServer connectorClient =
      (RemoteMBeanServer) connectorTable.get(mBeanServerId);
    if (connectorClient != null) {
//      throw new AgentRuntimeException(
//       "Connector client already exists for: '" + mBeanServerId + "'.");
      return connectorClient;
    }
    connectorClient = new RmiConnectorClient();
    connectorClient.connect(address);
    connectorTable.put(mBeanServerId, connectorClient);
    YaakSystem.getLogger().fine(
      "Added connector client for communicator on server '" +
      mBeanServerId + "' with address '" + address + "'.");
    Util.sleepSeconds(1);
    try {
      connectorClient.addNotificationListener(channelNotifierObjectName,
        new RemoteChannelListener(), null, null);
    }
    catch (Exception e) {
      String msg = "Cannot add channel notifier via RMI connector client.";
      YaakSystem.getLogger().warning(msg);
      throw new AgentRuntimeException(msg, e);
    }
    connectorClient.setMode(ClientNotificationHandler.PUSH_MODE);
    return connectorClient;
  }

  private void removeRemoteMBeanServer(String mBeanServerId) {
    if (mBeanServerId == null || mBeanServerId.length() == 0) {
      return;
    }
    RemoteMBeanServer connectorClient =
      (RemoteMBeanServer) connectorTable.get(mBeanServerId);
    if (connectorClient == null) {
      throw new AgentRuntimeException(
       "No connector client exists for: '" + mBeanServerId + "'.");
    }
    connectorClient.disconnect();
    channelTable.remove(mBeanServerId);
    YaakSystem.getLogger().fine(
      "Removed connector client for communicator on server '" +
      mBeanServerId + "'.");
  }

  private void queryCommunicatorForBeansAndRegister(
      RemoteMBeanServer connectorClient) {
    if (connectorClient == null) {
      return;
    }
    Set mBeans = connectorClient.queryNames(null, null);
    YaakSystem.getLogger().fine("There are " + mBeans.size() + " beans.");
    Iterator iterator = mBeans.iterator();
    while (iterator.hasNext()) {
      ObjectName objectName = (ObjectName) iterator.next();
      YaakSystem.getLogger().fine("Name: '" + objectName + "'.");
      String channelName = objectName.getKeyProperty(CHANNEL_TAG);
      if (channelName != null) {
        registerHandlerBean(channelName, objectName, connectorClient);
      }
    }
  }

  /**
  * <p>Sets up and then retrieves a nonexistent
  * <code>CommunicationChannel</code> object.  Applications should not
  * use this method directly.</p>
  * @param channelName The arbitrary channel name.
  * @param channelListener The (local) listener.
  * @return The object that implements the <code>CommunicationChannel</code>
  * interface.
  * @throws CommunicationException The channel is already allocated.
  */

  synchronized public CommunicationChannel getChannelNew(String channelName,
      ChannelListener channelListener) throws CommunicationException
      {
    if (channelTable.get(channelName) != null) {
      throw new CommunicationException(
       "The specified channel is already in use: '" + channelName + "'.");
    }
    return getChannel(channelName, channelListener);
  }

  /**
  * <p>Retrieves an existing, or sets up and then retrieves a nonexistent,
  * <code>CommunicationChannel</code> object.  Applications should not
  * use this method directly.</p>
  * @param channelName The arbitrary channel name.
  * @param channelListener The (local) listener.
  * @return The object that implements the <code>CommunicationChannel</code>
  * interface.
  */

  public CommunicationChannel getChannel(String channelName,
      ChannelListener channelListener) {
    CommunicationHandlerDistributed handler =
      (CommunicationHandlerDistributed) channelTable.get(channelName);
    if (handler == null) {
      handler = new CommunicationHandlerDistributed(channelName);
      ObjectInstance mBean =
        createHandlerBean(CommunicationHandlerDistributedDynamicBean.class,
          domain, channelName, channelListener);
      ObjectName objectName = mBean.getObjectName();
      YaakSystem.getLogger().fine(
        "Created local bean for remote connection to channel: '" +
        channelName + "'.");
      handler.configureClientHandler(mBeanServer,
        CommunicationHandlerDistributedDynamicBean.class.getName(),
        objectName, null);
      channelTable.put(channelName, handler);
      localChannelCount++;
      sendChannelNotification(ChannelNotification.CHANNEL_CREATION,
        channelName, objectName);
    }
    return handler;
  }

  /**
  * <p>Retrieves an existing <code>CommunicationChannel</code> object.
  * Applications should not use this method directly.</p>
  * @param channelName The arbitrary channel name.
  * @return The object that implements the <code>CommunicationChannel</code>
  * interface.
  * @throws CommunicationRuntimeException The channel (currently)
  * does not exist.
  */

  public CommunicationChannel getChannel(String channelName) {
    CommunicationChannel handler =
      (CommunicationChannel) channelTable.get(channelName);
    if (handler == null) {
      throw new CommunicationRuntimeException(
        "The specified channel does not exist: '" + channelName + "'.");
    }
    return handler;
  }

  private ObjectInstance createHandlerBean(Class c,
      String objectNamePrefix, String channelName,
      ChannelListener channelListener) {
    return createHandlerBean(c.getName(), objectNamePrefix, channelName,
      channelListener);
  }

  private ObjectInstance createHandlerBean(String className,
      String objectNamePrefix, String channelName,
      ChannelListener channelListener) {
    try {
      Object params[] = {channelListener};
      String signature[] = {
        "yaak.agent.communication.ChannelListener"
      };
      ObjectName objectName = new ObjectName(
        objectNamePrefix +
//        "name=" + className +
        CHANNEL_TAG + "=" + channelName);
      return
        mBeanServer.createMBean(className, objectName, params, signature);
    }
    catch (MalformedObjectNameException e) {
      String msg = "Cannot create object name for bean: '" +
        className + "'.";
      YaakSystem.getLogger().warning(msg);
      throw new AgentRuntimeException(msg, e);
    }
    catch (Exception e) {
      String msg = "Cannot create bean for: '" + className + "'.";
      YaakSystem.getLogger().warning(msg);
      e.printStackTrace();
      throw new AgentRuntimeException(msg, e);
    }
  }

  private void createLocalChannelNotifierBean() {
    try {
      channelNotifier = new ChannelManagementNotifier();
      channelNotifierObjectName = new ObjectName(CHANNEL_NOTIFIER_BEAN_NAME);
      mBeanServer.registerMBean(channelNotifier, channelNotifierObjectName);
    }
    catch (MalformedObjectNameException e) {
      String msg = "Cannot create object name for channel notifier bean.";
      YaakSystem.getLogger().warning(msg);
      throw new AgentRuntimeException(msg, e);
    }
    catch (Exception e) {
      String msg = "Cannot create channel notifier bean.";
      YaakSystem.getLogger().warning(msg);
      e.printStackTrace();
      throw new AgentRuntimeException(msg, e);
    }
  }

  private void sendChannelNotification(String notifyType,
      String channelName, ObjectName objectName) {
    ChannelNotification notify =
      new ChannelNotification(notifyType, channelNotifier, 0);
    notify.setChannelName(channelName);
    notify.setMBeanServerId(mBeanServerId);
    notify.setObjectName(objectName);
    if (notifyType.equals(ChannelNotification.CHANNEL_CREATION)) {
      channelNotifier.sendCreationNotification(notify);
    }
    else if (notifyType.equals(ChannelNotification.CHANNEL_DELETION)) {
      channelNotifier.sendDeletionNotification(notify);
    }
  }

  private void registerHandlerBean(String channelName,
      ObjectName objectName, RemoteMBeanServer connectorClient) {
    CommunicationHandlerDistributed handler =
      (CommunicationHandlerDistributed) channelTable.get(channelName);
    if (handler == null) {
      handler = new CommunicationHandlerDistributed(channelName);
      handler.configureClientHandler(mBeanServer,
        CommunicationHandlerDistributedDynamicBean.class.getName(),
        objectName, connectorClient);
      channelTable.put(channelName, handler);
      YaakSystem.getLogger().fine("Registered channel: '" + channelName +
        "', object name '" + objectName + "', connector client '" +
        connectorClient + "'.");
    }
    else {
      YaakSystem.getLogger().fine("Channel '" + channelName +
        "' is already registered.");
    }
  }

  private void unregisterHandlerBean(String channelName,
      ObjectName objectName, RemoteMBeanServer connectorClient) {
    CommunicationHandlerDistributed handler =
      (CommunicationHandlerDistributed) channelTable.get(channelName);
    if (handler != null) {
      if (channelTable.remove(channelName) == null) {
        YaakSystem.getLogger().fine(
          "Internal error:  could not unregistered channel: '" + channelName +
          "', object name '" + objectName + "', connector client '" +
          connectorClient + "'.");
      }
      else {
        YaakSystem.getLogger().fine("Unregistered channel: '" + channelName +
          "', object name '" + objectName + "', connector client '" +
          connectorClient + "'.");
      }
    }
    else {
      YaakSystem.getLogger().fine("Channel '" + channelName +
        "' was not registered.");
    }
  }

  /**
  * <p>Tests whether or not the channel already exists.</p>
  * @param channelName The arbitrary channel name.
  * @return Whether or not the channel exists.
  */

  public boolean channelExists(String channelName) {
    return channelTable.get(channelName) != null;
  }

  /**
  * <p>Logs the current list of channels.</p>
  */

  public void logChannels() {
    YaakSystem.getLogger().fine("Current list of communication channels:");
    Enumeration channels = channelTable.keys();
    while (channels.hasMoreElements()) {
      YaakSystem.getLogger().fine((String) channels.nextElement());
    }
  }

  /**
  * <p>Shuts down all network resources.  The communication services
  * are activated automatically (on demand) whenever an application
  * configures itself for distributed communications.</p>
  * <p>This method gracefully closes down all distributed resources, as
  * appropriate, given the communication server's implementation.</p>
  * <p>If the server, depending on its implementation and API usage,
  * still has dependent threads, the application will have to use
  * <code>System.exit()</code> to close the application.
  */

  synchronized public void shutdown() {
    ConnectorInfo connectorInfo = new ConnectorInfo(
      ConnectorInfo.APP_UNREGISTRATION, mBeanServerId,
      rmiConnectorServiceName, Util.getHostname(),
      String.valueOf(Configuration.getRMIPort()));
    discoveryService.sendBroadcast(connectorInfo.getConnectorInfoAsString());
    Util.sleepSeconds(1);
    discoveryService.shutdown();
    Util.sleepSeconds(1);
    //
    // Close down local distributed bean supporting remote connections:
    //
    Enumeration channels = channelTable.keys();
    while (channels.hasMoreElements()) {
      close((String) channels.nextElement());
    }
    Util.sleepSeconds(1);
    //
    // Close down local connector server:
    //
    stopRmiConnector();
    //
    // Close down connector clients to remote beans:
    //
    Enumeration connectors = connectorTable.keys();
    while (connectors.hasMoreElements()) {
      removeRemoteMBeanServer((String) connectors.nextElement());
    }
  }

  /**
  * <p>Closes down a channel.</p>
  * @param channelName The arbitrary channel name.
  */

  synchronized public void close(String channelName) {
    CommunicationHandlerDistributed handler = 
      (CommunicationHandlerDistributed) channelTable.get(channelName);
    if (handler == null) {
      throw new AgentRuntimeException("Cannot close channel; '" +
        channelName + "' does not exist.");
    }
    ObjectName objectName = handler.close();
    if (objectName == null) {
      return;
    }
    channelTable.remove(channelName);
    localChannelCount--;
    sendChannelNotification(ChannelNotification.CHANNEL_DELETION,
      channelName, objectName);
  }

  //
  // This functionality depends on the delimiter being unique among
  // the symbolic names appended together to form the broadcast
  // information.  Note, however, that the value of the broadcast
  // is subject to change, depending on the JMX reference implementation.
  //

  class ConnectorInfo {
    static final String DELIMITER = ";";
    static final String APP_REGISTRATION = "AppRegistration";
    static final String APP_UNREGISTRATION = "AppUnregistration";
    String type;
    String mBeanServerId;
    String rmiConnectorServiceName;
    String hostname;
    String port;

    ConnectorInfo(String type, String mBeanServerId,
        String rmiConnectorServiceName, String hostname, String port) {
      this.type = type;
      this.mBeanServerId = mBeanServerId;
      this.rmiConnectorServiceName = rmiConnectorServiceName;
      this.hostname = hostname;
      this.port = port;
    }

    ConnectorInfo(String info) {
      StringTokenizer st = new StringTokenizer(info, DELIMITER);
      type = st.nextToken();
      mBeanServerId = st.nextToken();
      rmiConnectorServiceName = st.nextToken();
      hostname = st.nextToken();
      port = st.nextToken();
    }

    String getConnectorInfoAsString() {
      return type + DELIMITER + mBeanServerId + DELIMITER +
        rmiConnectorServiceName + DELIMITER + hostname + DELIMITER + port;
    }

    String getType() {
      return type;
    }

    boolean isRegistration() {
      return type.equals(APP_REGISTRATION);
    }

    boolean isUnregistration() {
      return type.equals(APP_UNREGISTRATION);
    }

    String getMBeanServerId() {
      return mBeanServerId;
    }

    String getRmiConnectorServiceName() {
      return rmiConnectorServiceName;
    }

    String getHostname() {
      return hostname;
    }

    String getPort() {
      return port;
    }
  }

  class LocalDiscoveryListener implements DiscoveryListener {
    public void handleNotification(DiscoveryNotification notification) {
      String broadcastInfo = notification.getBroadcastInformation();
      YaakSystem.getLogger().fine(broadcastInfo);
      ConnectorInfo connectorInfo = new ConnectorInfo(broadcastInfo);
      if (connectorInfo.getMBeanServerId().equals(mBeanServerId)) {
        YaakSystem.getLogger().fine("Ignoring response from self...");
        return; // ignore self
      }
      if (connectorInfo.isRegistration()) {
        YaakSystem.getLogger().fine("Handle application activation...");
        int port = -1;
        try {
          port = Integer.parseInt(connectorInfo.getPort());
        }
        catch (Exception e) {
          throw new AgentRuntimeException(
            "Cannot parse port number for newly discovered application.");
        }
        String remoteConnectorName =
          connectorInfo.getRmiConnectorServiceName();
        String remoteMBeanServerId = connectorInfo.getMBeanServerId();
        boolean isNewRemoteApplication =
          connectorTable.get(remoteMBeanServerId) == null;
        RmiConnectorAddress address = new RmiConnectorAddress(
          connectorInfo.getHostname(), port, remoteConnectorName);
        YaakSystem.getLogger().fine("Remote connector name: '" +
          remoteConnectorName + "' address: '" + address + "'.");
        RemoteMBeanServer connectorClient =
          addRemoteMBeanServer(remoteMBeanServerId, address);
        queryCommunicatorForBeansAndRegister(connectorClient);
        //
        // Now, reciprocate:
        //
        if (isNewRemoteApplication) {
          ConnectorInfo returnConnectorInfo = new ConnectorInfo(
            ConnectorInfo.APP_REGISTRATION, mBeanServerId,
            rmiConnectorServiceName, Util.getHostname(),
            String.valueOf(Configuration.getRMIPort()));
          discoveryService.sendBroadcast(
            returnConnectorInfo.getConnectorInfoAsString());
        }
      }
      else if (connectorInfo.isUnregistration()) {
        YaakSystem.getLogger().fine("Handle application deactivation...");
        removeRemoteMBeanServer(connectorInfo.getMBeanServerId());
      }
      else {
        YaakSystem.getLogger().fine("Unknown connector notification type...");
      }
    }
  }

  class RemoteChannelListener implements NotificationListener {
    public void handleNotification(Notification notification,
        Object handback) {
      ChannelNotification channelNotify = (ChannelNotification) notification;
      String type = channelNotify.getType();
//      YaakSystem.getLogger().fine("!!!!!Channel notification with type: '" +
//        type + "'.");
      try {
        if (type.equals(ChannelNotification.CHANNEL_CREATION)) {
          YaakSystem.getLogger().fine("Handle channel creation...");
          String mBeanServerId = channelNotify.getMBeanServerId();
          RemoteMBeanServer connectorClient =
            (RemoteMBeanServer) connectorTable.get(mBeanServerId);
          if (connectorClient == null) {
            throw new AgentRuntimeException(
              "Cannot look up connector client for registering channel bean.");
          }
          registerHandlerBean(channelNotify.getChannelName(),
            channelNotify.getObjectName(), connectorClient);
        }
        else if (type.equals(ChannelNotification.CHANNEL_DELETION)) {
          YaakSystem.getLogger().fine("Handle channel deletion...");
          String mBeanServerId = channelNotify.getMBeanServerId();
          RemoteMBeanServer connectorClient =
            (RemoteMBeanServer) connectorTable.get(mBeanServerId);
          if (connectorClient == null) {
            throw new AgentRuntimeException(
              "Cannot look up connector client for unregistering channel bean.");
          }
          unregisterHandlerBean(channelNotify.getChannelName(),
            channelNotify.getObjectName(), connectorClient);
        }
        else {
          YaakSystem.getLogger().fine(
            "Unhandled channel notification for type '" + type + "'.");
        }
      }
      catch (Exception e) {
        YaakSystem.getLogger().fine("Internal error, with exception:");
        e.printStackTrace();
      }
    }
  }
}
