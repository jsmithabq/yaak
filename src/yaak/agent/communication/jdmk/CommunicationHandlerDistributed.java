package yaak.agent.communication.jdmk;

import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

import com.sun.jdmk.comm.CommunicationException;
import com.sun.jdmk.comm.ConnectorAddress;
import com.sun.jdmk.comm.RemoteMBeanServer;

import yaak.agent.AgentRuntimeException;
import yaak.core.YaakSystem;

import yaak.agent.communication.ChannelListener;
import yaak.agent.communication.CommunicationChannel;
import yaak.agent.communication.CommunicationRuntimeException;
import yaak.agent.communication.Message;
import yaak.agent.communication.Payload;

/**
* <p><code>CommunicationHandlerDistributed</code> handles point-to-point and
* publish/subscribe messaging.</p>
* <p>This handler dynamically invokes remote operations of a remote MBean
* instance representing a remote communication channel.</p>
* @author Jerry Smith
* @version $Id: CommunicationHandlerDistributed.java 6 2005-06-08 17:00:15Z jsmith $
*/

//
// Currently, the critical resources (instance variables) are not
// protected w.r.t. thread safety.  Because this class in not public
// (only used within the communication server implementation),
// this policy is acceptable...
//

final class CommunicationHandlerDistributed implements CommunicationChannel
    { // default privacy
  private String channelName = "none";
  private ChannelListener channelListener;
  private RemoteMBeanServer connectorClient;
  private MBeanServer mBeanServer = null;
  private ObjectName mBeanObjectName;
  
  /**
  * <p>Not for public consumption.</p>
  */

  CommunicationHandlerDistributed(String channelName) { // default privacy
    this.channelName = channelName;
  }

  /**
  * <p>Gets the name for the channel.</p>
  * @return The channel name.
  */

  public String getChannelName() {
    return channelName;
  }

  /**
  * <p>Sets the name for the channel.</p>
  * @param channelName The channel name.
  */

  void setChannelName(String channelName) { // default privacy
    this.channelName = channelName;
  }

  /**
  * <p>Registers the listener for this channel.</p>
  * @param channelListener The object that is registering to
  * receive communications.
  */

  public void setChannelListener(ChannelListener channelListener) {
    this.channelListener = channelListener;
  }

  /**
  * <p>Sets the managed bean for this channel.</p>
  * @param mBeanServer The MBean server.
  * @param className The classname for the bean implementation class.
  * @param mBeanObjectName The managed bean by name.
  * @param address The connector address.
  */

  void configureClientHandler(MBeanServer mBeanServer,
      String className, ObjectName mBeanObjectName,
      RemoteMBeanServer connectorClient) { // default privacy
    this.mBeanServer = mBeanServer;
    this.mBeanObjectName = mBeanObjectName;
//    if (connectorClient == null) { // for now, don't configure client for local bean
//      return;
//    }
    this.connectorClient = connectorClient;
  }

  /**
  * <p>Accepts a payload and then transports it (delivers it to the
  * registered channel listener).</p>
  * @param payload The object that carries the pertinent data.
  * @throws CommunicationRuntimeException The receiving channel has a
  * communication transport failure.
  * @throws CommunicationException The receiving channel is not
  * operational due to an unknown condition.
  */

  public void send(Payload payload) throws CommunicationException {
    Object[] params = { payload };
    String[] signature = { "yaak.agent.communication.Payload" };
    try {
      connectorClient.invoke(mBeanObjectName, "send", params, signature);
    }
    catch (com.sun.jdmk.comm.CommunicationException e) {
      String msg = "The receiving channel is \"out to lunch.\"";
      YaakSystem.getLogger().info(msg);
      throw new CommunicationRuntimeException(msg, e);
    }
    catch (Exception e) {
      throw new CommunicationException(e);
    }
  }

  /**
  * <p>Accepts a message and then sends it on (delivers it) to the
  * registered channel listener.</p>
  * @param message The object that carries the pertinent data.
  * @throws CommunicationRuntimeException The receiving channel has a
  * communication transport failure.
  * @throws CommunicationException The receiving channel is not
  * operational due to an unknown condition.
  */

  public void send(Message message) throws CommunicationException { 
    send(new Payload(message));
  }

  /**
  * <p>Accepts a payload and then publishes it to the channel.</p>
  * @param payload The object that carries the pertinent data.
  * @throws CommunicationRuntimeException The receiving channel has a
  * communication transport failure.
  * @throws CommunicationException The receiving channel is not
  * operational due to an unknown condition.
  */

  public void publish(Payload payload) throws CommunicationException {
//    payload.setBroadcast(true);
    Object[] params = { payload };
    String[] signature = { "yaak.agent.communication.Payload" };
    try {
      connectorClient.invoke(mBeanObjectName, "publish", params, signature);
    }
    catch (com.sun.jdmk.comm.CommunicationException e) {
      String msg = "The receiving channel is \"out to lunch.\"";
      YaakSystem.getLogger().info(msg);
      throw new CommunicationRuntimeException(msg, e);
    }
    catch (Exception e) {
      throw new CommunicationException(e);
    }
  }

  /**
  * <p>Accepts a message and then publishes it to the channel.</p>
  * @param message The object that carries the pertinent data.
  * @throws CommunicationRuntimeException The receiving channel has a
  * communication transport failure.
  * @throws CommunicationException The receiving channel is not
  * operational due to an unknown condition.
  */

  public void publish(Message message) throws CommunicationException {
    publish(new Payload(message));
  }

  ObjectName close() { // default privacy
    if (connectorClient == null) { // channel for a local context
      try {
        mBeanServer.unregisterMBean(mBeanObjectName);
        YaakSystem.getLogger().fine("Managed bean: '" +
          mBeanObjectName + "' has been unregistered.");
        return mBeanObjectName;
      }
      catch (Exception e) {
        YaakSystem.getLogger().warning("Cannot unregister managed bean: '" +
          mBeanObjectName + "'.");
      }
    }
    return null;
  }
}
