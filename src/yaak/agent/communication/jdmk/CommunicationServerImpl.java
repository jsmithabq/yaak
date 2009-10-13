package yaak.agent.communication.jdmk;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
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
import com.sun.jdmk.comm.HtmlAdaptorServer;
import com.sun.jdmk.comm.ConnectorAddress;
import com.sun.jdmk.comm.RemoteMBeanServer;
import com.sun.jdmk.comm.RmiConnectorClient;
import com.sun.jdmk.comm.RmiConnectorServer;
import com.sun.jdmk.discovery.DiscoveryClient;
import com.sun.jdmk.discovery.DiscoveryMonitor;
import com.sun.jdmk.discovery.DiscoveryResponder;
import com.sun.jdmk.discovery.DiscoveryResponderNotification;
import com.sun.jdmk.discovery.DiscoveryResponse;

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
//  private static final String DOMAIN = ServiceName.DOMAIN + ":";
  private static final String CHANNEL_NOTIFIER_BEAN_NAME =
    DOMAIN + NAME_TAG + "=" + "ChannelManagementNotifier";
  private Hashtable channelTable = new Hashtable();
  private Hashtable connectorTable = new Hashtable();
  private MBeanServer mBeanServer = null;
  private ChannelManagementNotifier channelNotifier = null;
  private HtmlAdaptorServer htmlAdaptor = null;
  private RmiConnectorServer rmiConnector = null;
  private DiscoveryClient discoveryClient = null;
  private DiscoveryMonitor discoveryMonitor = null;
  private DiscoveryResponder discoveryResponder = null;
  private ObjectName delegateName = null;
  private ObjectName htmlAdaptorObjectName = null;
  private ObjectName rmiConnectorObjectName = null;
  private ObjectName discoveryClientObjectName = null;
  private ObjectName discoveryMonitorObjectName = null;
  private ObjectName discoveryResponderObjectName = null;
  private ObjectName channelNotifierObjectName = null;
  private DiscoveryMonitorNotification notificationListener = null;
  private String domain = DOMAIN;
  private String mBeanServerId = null;
  private int localChannelCount = 0;
  private boolean useHtmlAdaptor = false;
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
    String useAdaptor = (String) System.getProperty("htmlAdaptor");
    YaakSystem.getLogger().fine("System property 'htmlAdaptor' == '" +
      useAdaptor + "'.");
    useHtmlAdaptor = new Boolean(useAdaptor).booleanValue();
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
    createLocalNotifierBean();
    createHtmlAdaptor();
    startHtmlAdaptor();
    createRmiConnector();
    startRmiConnector();
    createDiscoveryMonitor();
    startDiscoveryMonitor();
    addDiscoveryMonitorListener();
    createDiscoveryClient();
    startDiscoveryClient();
    createDiscoveryResponder();
    startDiscoveryResponder();
    processResponders(discoveryClient.findCommunicators());
  }

  private void createHtmlAdaptor() {
    if (!useHtmlAdaptor) {
      return;
    }
    int htmlPort = Configuration.getHTMLPort();
    YaakSystem.getLogger().fine("Using HTML port: " + htmlPort + ".");
    htmlAdaptor = new HtmlAdaptorServer(htmlPort);
    htmlAdaptorObjectName = null;
    try {
      htmlAdaptorObjectName =
        new ObjectName(domain + NAME_TAG + "=HtmlAdaptor");
      ObjectInstance htmlAdaptorInstance =
        mBeanServer.registerMBean(htmlAdaptor, htmlAdaptorObjectName);
    }
    catch (MalformedObjectNameException e) {
      String msg = "Cannot create object name for HTML adaptor.";
      YaakSystem.getLogger().warning(msg);
      throw new AgentRuntimeException(msg, e);
    }
    catch (Exception e) {
      String msg = "Cannot set up HTML adaptor.";
      YaakSystem.getLogger().warning(msg);
      throw new AgentRuntimeException(msg, e);
    }
  }

  private void createRmiConnector() {
    int rmiPort = Configuration.getRMIPort();
    YaakSystem.getLogger().fine("Using RMI port: " + rmiPort + ".");
    String rmiConnectorServiceName = NAME_TAG + "=" + RMI_CONNECTOR_NAME +
      "_" + new java.rmi.dgc.VMID();
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
  }

  private void createDiscoveryMonitor() {
    discoveryMonitor = new DiscoveryMonitor();
    discoveryMonitorObjectName = null;
    try {
      discoveryMonitorObjectName =
        new ObjectName(domain + NAME_TAG + "=DiscoveryMonitor");
      ObjectInstance discoveryMonitorObjectInstance =
        mBeanServer.registerMBean(discoveryMonitor,
          discoveryMonitorObjectName);
    }
    catch (MalformedObjectNameException e) {
      String msg = "Cannot create object name for discovery monitor.";
      YaakSystem.getLogger().warning(msg);
      throw new AgentRuntimeException(msg, e);
    }
    catch (Exception e) {
      String msg = "Cannot set up discovery monitor.";
      YaakSystem.getLogger().warning(msg);
      throw new AgentRuntimeException(msg, e);
    }
  }

  private void createDiscoveryClient() {
    discoveryClient = new DiscoveryClient();
    discoveryClientObjectName = null;
    try {
      discoveryClientObjectName =
        new ObjectName(domain + NAME_TAG + "=DiscoveryClient");
      ObjectInstance discoveryClientObjectInstance =
        mBeanServer.registerMBean(discoveryClient, discoveryClientObjectName);
    }
    catch (MalformedObjectNameException e) {
      String msg = "Cannot create object name for discovery client.";
      YaakSystem.getLogger().warning(msg);
      throw new AgentRuntimeException(msg, e);
    }
    catch (Exception e) {
      String msg = "Cannot set up discovery client.";
      YaakSystem.getLogger().warning(msg);
      throw new AgentRuntimeException(msg, e);
    }
  }

  private void createDiscoveryResponder() {
    discoveryResponder = new DiscoveryResponder();
    discoveryResponderObjectName = null;
    try {
      discoveryResponderObjectName =
        new ObjectName(domain + NAME_TAG + "=DiscoveryResponder");
      ObjectInstance discoveryResponderObjectInstance =
        mBeanServer.registerMBean(discoveryResponder,
          discoveryResponderObjectName);
    }
    catch (MalformedObjectNameException e) {
      String msg = "Cannot create object name for discovery responder.";
      YaakSystem.getLogger().warning(msg);
      throw new AgentRuntimeException(msg, e);
    }
    catch (Exception e) {
      String msg = "Cannot set up discovery responder.";
      YaakSystem.getLogger().warning(msg);
      throw new AgentRuntimeException(msg, e);
    }
  }

  private void addDiscoveryMonitorListener() {
    try {
      mBeanServer.addNotificationListener(discoveryMonitorObjectName,
        (notificationListener = new DiscoveryMonitorNotification()),
        null, null);
    }
    catch (Exception e) {
      String msg = "Cannot add discovery notification monitor.";
      YaakSystem.getLogger().warning(msg);
      throw new AgentRuntimeException(msg, e);
    }
  }

  private void startHtmlAdaptor() {
    if (!useHtmlAdaptor) {
      return;
    }
    if (htmlAdaptor.getState() == CommunicatorServer.ONLINE) {
      return;
    }
    YaakSystem.getLogger().fine("HTML adaptor starting...");
    htmlAdaptor.start();
    while (htmlAdaptor.getState() == CommunicatorServer.STARTING) {
      Util.sleepSeconds(1);
    }
    YaakSystem.getLogger().fine("HTML adaptor started.");
  }

  private void stopHtmlAdaptor() {
    if (!useHtmlAdaptor) {
      return;
    }
    if (htmlAdaptor.getState() == CommunicatorServer.OFFLINE) {
      return;
    }
    YaakSystem.getLogger().fine("HTML adaptor stopping...");
    try {
      htmlAdaptor.stop();
      while (htmlAdaptor.getState() == CommunicatorServer.STOPPING) {
        Util.sleepSeconds(1);
      }
    }
    catch (Exception e) {
      String msg = "Cannot stop HTML adaptor.";
      YaakSystem.getLogger().warning(msg);
      throw new AgentRuntimeException(msg, e);
    }
    YaakSystem.getLogger().fine("HTML adaptor stopped.");
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

  private void startDiscoveryMonitor() {
    if (discoveryMonitor.getState().intValue() ==
        DiscoveryMonitor.ONLINE) {
      return;
    }
    YaakSystem.getLogger().fine("Monitor starting...");
    try {
      discoveryMonitor.start();
      while (discoveryMonitor.getState().intValue() !=
          DiscoveryMonitor.ONLINE) {
        Util.sleepSeconds(1);
      }
    }
    catch (Exception e) {
      String msg = "Cannot start discovery monitor.";
      YaakSystem.getLogger().warning(msg);
      throw new AgentRuntimeException(msg, e);
    }
    YaakSystem.getLogger().fine("Monitor started.");
  }

  private void stopDiscoveryMonitor() {
    if (discoveryMonitor.getState().intValue() ==
        DiscoveryMonitor.OFFLINE) {
      return;
    }
    YaakSystem.getLogger().fine("Monitor stopping...");
    try {
      discoveryMonitor.stop();
      while (discoveryMonitor.getState().intValue() ==
          DiscoveryMonitor.STOPPING) {
        Util.sleepSeconds(1);
      }
    }
    catch (Exception e) {
      String msg = "Cannot stop discovery monitor.";
      YaakSystem.getLogger().warning(msg);
      throw new AgentRuntimeException(msg, e);
    }
    YaakSystem.getLogger().fine("Monitor stopped.");
  }

  private void startDiscoveryClient() {
    if (discoveryClient.getState().intValue() ==
        DiscoveryClient.ONLINE) {
      return;
    }
    YaakSystem.getLogger().fine("Client starting...");
    try {
      discoveryClient.start();
      while (discoveryClient.getState().intValue() !=
          DiscoveryClient.ONLINE) {
        Util.sleepSeconds(1);
      }
    }
    catch (Exception e) {
      String msg = "Cannot start discovery client.";
      YaakSystem.getLogger().warning(msg);
      throw new AgentRuntimeException(msg, e);
    }
    YaakSystem.getLogger().fine("Client started.");
  }

  private void stopDiscoveryClient() {
    if (discoveryClient.getState().intValue() ==
        DiscoveryClient.OFFLINE) {
      return;
    }
    YaakSystem.getLogger().fine("Client stopping...");
    try {
      discoveryClient.stop();
      while (discoveryClient.getState().intValue() ==
          DiscoveryClient.STOPPING) {
        Util.sleepSeconds(1);
      }
    }
    catch (Exception e) {
      String msg = "Cannot stop discovery client.";
      YaakSystem.getLogger().warning(msg);
      throw new AgentRuntimeException(msg, e);
    }
    YaakSystem.getLogger().fine("Client stopped.");
  }

  private void startDiscoveryResponder() {
    if (discoveryResponder.getState().intValue() ==
        DiscoveryResponder.ONLINE) {
      return;
    }
    YaakSystem.getLogger().fine("Responder starting...");
    try {
      discoveryResponder.start();
      while (discoveryResponder.getState().intValue() ==
          DiscoveryResponder.STARTING) {
        Util.sleepSeconds(1);
      }
    }
    catch (Exception e) {
      String msg = "Cannot start discovery responder.";
      YaakSystem.getLogger().warning(msg);
      throw new AgentRuntimeException(msg, e);
    }
    YaakSystem.getLogger().fine("Responder started.");
  }

  private void stopDiscoveryResponder() {
    if (discoveryResponder.getState().intValue() ==
        DiscoveryResponder.OFFLINE) {
      return;
    }
    YaakSystem.getLogger().fine("Responder stopping...");
    try {
      discoveryResponder.stop();
      while (discoveryResponder.getState().intValue() ==
          DiscoveryResponder.STOPPING) {
        Util.sleepSeconds(1);
      }
    }
    catch (Exception e) {
      String msg = "Cannot stop discovery responder.";
      YaakSystem.getLogger().warning(msg);
      throw new AgentRuntimeException(msg, e);
    }
    YaakSystem.getLogger().fine("Responder stopped.");
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

  //
  // To register all remote beans:
  //   a. Monitor responders, and for each responder
  //   b. Find all communicators, and for each communicator
  //   c. Find all RMI connectors, and for each RMI connector
  //   d. Find all MBeans with a 'channel' property, and for each such bean
  //   e. Register a connection to that bean as a channel in the channel table
  //

  private void processResponders(Vector responses) {
    if (responses == null || responses.size() == 0) {
      return;
    }
    try {
      YaakSystem.getLogger().fine("At start-up, discovered " +
        responses.size() + " response(s):");
      for (Enumeration e = responses.elements(); e.hasMoreElements(); ) {
        processResponder((DiscoveryResponse) e.nextElement());
      }
    }
    catch (Exception e) {
      YaakSystem.getLogger().warning("Cannot discover responses.");
      e.printStackTrace();
    }
  }

  private void processResponder(DiscoveryResponse response) {
    if (response == null) {
      return;
    }
    if (response.getMBeanServerId().equals(mBeanServerId)) {
      YaakSystem.getLogger().fine("Ignoring response from self...");
      return; // ignore self
    }
    Hashtable communicators = response.getObjectList();
    YaakSystem.getLogger().fine("There are " + communicators.size() +
      " communicator(s).");
    Enumeration keys = communicators.keys();
    while (keys.hasMoreElements()) {
      ObjectName objectName = (ObjectName) keys.nextElement();
      String name = objectName.getKeyProperty(NYaak_TAG);
      if (!name.equals(RMI_CONNECTOR_NYaak)) {
        continue;
      }
      try {
        ConnectorAddress address =
          (ConnectorAddress) communicators.get(objectName);
        YaakSystem.getLogger().fine("Communicator name: '" + objectName +
          "' address: '" + address + "'.");
        RemoteMBeanServer connectorClient =
          addRemoteMBeanServer(response.getMBeanServerId(), address);
        queryCommunicatorForBeansAndRegister(connectorClient);
      }
      catch (Exception ex) {
        //
        // Swallow any exceptions from any bogus/lingering connectors,
        // or from HTML adaptors.
        //
        ex.printStackTrace();
      }
    }
  }

  private void cleanupResponder(DiscoveryResponse response) {
    if (response == null) {
      return;
    }
    if (response.getMBeanServerId().equals(mBeanServerId)) {
      YaakSystem.getLogger().fine("Ignoring response from self...");
      return; // ignore self
    }
    Hashtable communicators = response.getObjectList();
    YaakSystem.getLogger().fine("There are " + communicators.size() +
      " communicator(s).");
    Enumeration keys = communicators.keys();
    while (keys.hasMoreElements()) {
      ObjectName objectName = (ObjectName) keys.nextElement();
      String name = objectName.getKeyProperty(NAME_TAG);
      if (!name.equals(RMI_CONNECTOR_NAME)) {
        continue;
      }
      removeRemoteMBeanServer(response.getMBeanServerId());
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
      String msg = "Cannot add bean notifier via RMI connector client.";
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

  private void createLocalNotifierBean() {
    try {
      channelNotifier = new ChannelManagementNotifier();
      channelNotifierObjectName = new ObjectName(CHANNEL_NOTIFIER_BEAN_NAME);
      mBeanServer.registerMBean(channelNotifier, channelNotifierObjectName);
    }
    catch (MalformedObjectNameException e) {
      String msg = "Cannot create object name for notifier bean.";
      YaakSystem.getLogger().warning(msg);
      throw new AgentRuntimeException(msg, e);
    }
    catch (Exception e) {
      String msg = "Cannot create notifier bean.";
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

  //
  // Most of these operations should be unnecessary...
  //

  synchronized public void shutdown() {
//    if (!Configuration.isDistributed()) {
//      return;
//    }
    //
    // Close down local distributed bean supporting remote connections:
    //
    Enumeration channels = channelTable.keys();
    while (channels.hasMoreElements()) {
      close((String) channels.nextElement());
    }
    Util.sleepSeconds(1);
    //
    // Close down local "servers":
    //
    stopHtmlAdaptor();
    stopRmiConnector();
    stopDiscoveryClient();
    stopDiscoveryResponder();
    stopDiscoveryMonitor();
    //
    // Close down connector clients to remote beans:
    //
    Enumeration connectors = connectorTable.keys();
    while (connectors.hasMoreElements()) { // handle each client
      removeRemoteMBeanServer((String) connectors.nextElement());
    }
/**************************************************************************
    try {
      mBeanServer.removeNotificationListener(discoveryMonitorObjectName,
        notificationListener);
    }
    catch (Exception e) {
      YaakSystem.getLogger().warning("Cannot remove notification listeners.");
    }
    try {
      if (useHtmlAdaptor) {
        mBeanServer.unregisterMBean(htmlAdaptorObjectName);
      }
      mBeanServer.unregisterMBean(rmiConnectorObjectName);
      mBeanServer.unregisterMBean(discoveryClientObjectName);
      mBeanServer.unregisterMBean(discoveryMonitorObjectName);
      mBeanServer.unregisterMBean(discoveryResponderObjectName);
      mBeanServer.unregisterMBean(channelNotifierObjectName);
//      mBeanServer.unregisterMBean(delegateName);
      MBeanServerFactory.releaseMBeanServer(mBeanServer);
    }
    catch (Exception e) {
      YaakSystem.getLogger().warning(
        "Cannot unregister one or more service beans.");
    }
    Set mBeans = mBeanServer.queryNames(null, null);
    YaakSystem.getLogger().fine("There are " + mBeans.size() +
      " beans remaining in the server:");
    Iterator iterator = mBeans.iterator();
    while (iterator.hasNext()) {
      ObjectName objectName = (ObjectName) iterator.next();
      YaakSystem.getLogger().fine("Name: '" + objectName + "'.");
    }
**************************************************************************/
  }

  /**
  * <p>Closes down a channel.</p>
  * @param channelName The arbitrary channel name.
  */

  synchronized public void close(String channelName) {
//    if (!Configuration.isDistributed()) {
//      return;
//    }
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

  class DiscoveryMonitorNotification implements NotificationListener {
    public void handleNotification(Notification notification, Object handback) {
      DiscoveryResponderNotification drNotify =
        (DiscoveryResponderNotification) notification;
      YaakSystem.getLogger().fine(
        "Received discovery notification from host '" +
        drNotify.getEventInfo().getHost() + "'.");
/****
      YaakSystem.getLogger().fine("Responder state: " +
        ((drNotify.getState().intValue() == DiscoveryMonitor.ONLINE) ?
          "ONLINE" : "OFFLINE") + ".");
****/
      if (drNotify.getState().intValue() == DiscoveryMonitor.ONLINE) {
        processResponder((DiscoveryResponse) drNotify.getEventInfo());
      }
      else {
        cleanupResponder((DiscoveryResponse) drNotify.getEventInfo());
      }
//      logChannels();
    }
  }

  class RemoteChannelListener implements NotificationListener {
    public void handleNotification(Notification notification,
        Object handback) {
      ChannelNotification channelNotify = (ChannelNotification) notification;
      String type = channelNotify.getType();
//      YaakSystem.getLogger().fine("!!!!!Bean notification with type: '" +
//        type + "'.");
      try {
        if (type.equals(ChannelNotification.CHANNEL_CREATION)) {
          YaakSystem.getLogger().fine("Handle bean creation...");
          String mBeanServerId = channelNotify.getMBeanServerId();
          RemoteMBeanServer connectorClient =
            (RemoteMBeanServer) connectorTable.get(mBeanServerId);
          if (connectorClient == null) {
            throw new AgentRuntimeException(
              "Cannot look up connector client for registering bean.");
          }
          registerHandlerBean(channelNotify.getChannelName(),
            channelNotify.getObjectName(), connectorClient);
        }
        else if (type.equals(ChannelNotification.CHANNEL_DELETION)) {
          YaakSystem.getLogger().fine("Handle bean deletion...");
          String mBeanServerId = channelNotify.getMBeanServerId();
          RemoteMBeanServer connectorClient =
            (RemoteMBeanServer) connectorTable.get(mBeanServerId);
          if (connectorClient == null) {
            throw new AgentRuntimeException(
              "Cannot look up connector client for unregistering bean.");
          }
          unregisterHandlerBean(channelNotify.getChannelName(),
            channelNotify.getObjectName(), connectorClient);
        }
        else {
          YaakSystem.getLogger().fine(
            "Unhandled bean notification for type '" + type + "'.");
        }
      }
      catch (Exception e) {
        YaakSystem.getLogger().fine("Internal error, with exception:");
        e.printStackTrace();
      }
    }
  }
}
