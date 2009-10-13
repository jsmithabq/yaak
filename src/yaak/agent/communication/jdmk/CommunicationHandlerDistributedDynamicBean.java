package yaak.agent.communication.jdmk;

import java.lang.reflect.Constructor;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.RuntimeOperationsException;

import javax.management.ReflectionException;

import yaak.agent.AgentRuntimeException;

import yaak.agent.communication.ChannelListener;
import yaak.agent.communication.CommunicationChannel;
import yaak.agent.communication.CommunicationException;
import yaak.agent.communication.Message;
import yaak.agent.communication.Payload;

/**
* <p><code>CommunicationHandlerDistributedDynamicBean</code> is the (local)
* dynamic JMX MBean representing a communication channel in the local
* application.  It provides no JMX-configurable attributes.</p>
* <p>This handler is <em>not</em> invoked locally; that is, it is for
* inter-JVM communications.  A local instance of this handler is invoked by
* a remote instance of <code>CommunicationHandlerDistributed</code>.</p>
* @author Jerry Smith
* @version $Id: CommunicationHandlerDistributedDynamicBean.java 6 2005-06-08 17:00:15Z jsmith $
*/

public final class CommunicationHandlerDistributedDynamicBean
    implements DynamicMBean {
  private ChannelListener channelListener;

  /**
  * <p>Not for public consumption.  This constructor must be public, due to
  * the JMX API protocol.</p>
  */

  public CommunicationHandlerDistributedDynamicBean() {
    this(null);
  }

  /**
  * <p>Not for public consumption.  This constructor must be public, due to
  * the JMX API protocol.</p>
  * @param channelListener The (local) listener that receives the incoming
  * agent messages.
  */

  public CommunicationHandlerDistributedDynamicBean(ChannelListener channelListener)
      {
    this.channelListener = channelListener;
  }

  /**
  * <p>Not for public consumption.  This method is disabled.</p>
  * @throws AgentRuntimeException This method is not applicable.
  */

  public Object getAttribute(String attribute) throws
      AttributeNotFoundException, MBeanException, ReflectionException {
    throw new AgentRuntimeException("This method is not applicable.");
  }

  /**
  * <p>Not for public consumption.  This method is disabled.</p>
  * @throws AgentRuntimeException This method is not applicable.
  */

  public AttributeList getAttributes(String[] attributes) {
    throw new AgentRuntimeException("This method is not applicable.");
  }

  /**
  * <p>Not for public consumption.  This method is disabled.</p>
  * @throws AgentRuntimeException This method is not applicable.
  */

  public void setAttribute(Attribute attribute) throws
      AttributeNotFoundException, InvalidAttributeValueException,
      MBeanException, ReflectionException {
    throw new AgentRuntimeException("This method is not applicable.");
  }

  /**
  * <p>Not for public consumption.  This method is disabled.</p>
  * @throws AgentRuntimeException This method is not applicable.
  */

  public AttributeList setAttributes(AttributeList attributes) {
    throw new AgentRuntimeException("This method is not applicable.");
  }

  /**
  * <p>Invokes a public method, given argument and parameter lists.</p>
  * @param operation The method name.
  * @param params The arguments for the method invocation.
  * @param signature The signature for looking up the possibly overloaded
  * method.
  * @return A <code>null</code> reference (not applicable).
  * @throws MBeanException Any JMX-propagated exception.
  * @throws ReflectionException Any JMX-propagated exception.
  */

  public Object invoke(String operation, Object params[],
      String signature[]) throws MBeanException, ReflectionException {
    if (operation == null) {
      throw new RuntimeOperationsException(
        new IllegalArgumentException("Operation cannot be null."),
          "Illegal operation: 'invoke(null)'.");
    }
    else if (params == null || params.length != 1) {
      throw new RuntimeOperationsException(
        new IllegalArgumentException("There must be exactly one argument."),
          "There must be exactly one argument.");
    }
    else if (signature == null || signature.length != 1) {
      throw new RuntimeOperationsException(
        new IllegalArgumentException(
          "There must be exactly one argument specifying the parameter type."),
          "There must be exactly one argument specifying the parameter type.");
    }
    else if (operation.equals("send")) {
      if (signature[0].equals("yaak.agent.communication.Payload")) {
        try {
          send((Payload) params[0]);
        }
        catch (CommunicationException e) {
          throw new MBeanException(e);
        }
      }
    }
    else if (operation.equals("send")) {
      if (signature[0].equals("yaak.agent.communication.Message")) {
        try {
          send((Message) params[0]);
        }
        catch (CommunicationException e) {
          throw new MBeanException(e);
        }
      }
    }
    else if (operation.equals("publish")) {
      if (signature[0].equals("yaak.agent.communication.Payload")) {
        try {
          publish((Payload) params[0]);
        }
        catch (CommunicationException e) {
          throw new MBeanException(e);
        }
      }
    }
    else if (operation.equals("publish")) {
      if (signature[0].equals("yaak.agent.communication.Message")) {
        try {
          publish((Message) params[0]);
        }
        catch (CommunicationException e) {
          throw new MBeanException(e);
        }
      }
    }
    else { 
      throw new ReflectionException(
        new NoSuchMethodException(operation), 
        "For class: '" + getClass().getName() + "' operation: '" +
        operation + "' is invalid.");
	}
    return null;
  }

  /**
  * <p>Not for public consumption.</p>
  * @return The MBean information object.
  */

  public MBeanInfo getMBeanInfo() {
//    MBeanAttributeInfo[] dAttributes = new MBeanAttributeInfo[1];
    MBeanConstructorInfo[] dConstructors = new MBeanConstructorInfo[2];
    MBeanOperationInfo[] dOperations = new MBeanOperationInfo[4];
/*
    dAttributes[0] = new MBeanAttributeInfo("ChannelListener",
      "yaak.agent.communication.ChannelListener",
      "The channel listener.",
      false,
      true,
      false);
*/

    MBeanParameterInfo[] const1Param = null;
    dConstructors[0] = new MBeanConstructorInfo(
      "CommunicationHandlerDistributedDynamicBean",
      "Instantiates a handler without setting the channel listener.",
      const1Param);
    MBeanParameterInfo[] const2Param = new MBeanParameterInfo[1];
    const2Param[0] = new MBeanParameterInfo("channelListener",
      "yaak.agent.communication.ChannelListener",
      "The channel listener.");
    dConstructors[1] = new MBeanConstructorInfo(
      "CommunicationHandlerDistributedDynamicBean",
      "Instantiates a handler and sets the channel listener.",
      const2Param);

    MBeanParameterInfo[] op1Param = new MBeanParameterInfo[1];
    op1Param[0] = new MBeanParameterInfo("payload",
      "yaak.agent.communication.Payload",
      "The raw communications payload.");
    dOperations[0] = new MBeanOperationInfo("send",
      "Send a communication payload.",
      op1Param,
      "void",
      MBeanOperationInfo.UNKNOWN);

    MBeanParameterInfo[] op2Param = new MBeanParameterInfo[1];
    op2Param[0] = new MBeanParameterInfo("message",
      "yaak.agent.communication.Message",
      "The message.");
    dOperations[1] = new MBeanOperationInfo("send",
      "Send a communication message.",
      op2Param,
      "void",
      MBeanOperationInfo.UNKNOWN);

    MBeanParameterInfo[] op3Param = new MBeanParameterInfo[1];
    op3Param[0] = new MBeanParameterInfo("payload",
      "yaak.agent.communication.Payload",
      "The raw communications payload.");
    dOperations[2] = new MBeanOperationInfo("publish",
      "Publish a communication payload.",
      op3Param,
      "void",
      MBeanOperationInfo.UNKNOWN);

    MBeanParameterInfo[] op4Param = new MBeanParameterInfo[1];
    op4Param[0] = new MBeanParameterInfo("message",
      "yaak.agent.communication.Message",
      "The message.");
    dOperations[3] = new MBeanOperationInfo("publish",
      "Publish a communication message.",
      op4Param,
      "void",
      MBeanOperationInfo.UNKNOWN);

    return new MBeanInfo(getClass().getName(),
      "A dynamic (MBean) handler for distributed communication.",
      new MBeanAttributeInfo[0], dConstructors, dOperations,
      new MBeanNotificationInfo[0]);
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
  * <p>Accepts a payload and then transports it (delivers it to the
  * registered channel listener).</p>
  * @param payload The object that carries the pertinent data.
  * @throws CommunicationException The receiving channel is not
  * operational.
  */

  public void send(Payload payload) throws CommunicationException {
    if (channelListener == null) {
      throw new CommunicationException("The channel listener is null.");
    }
    payload.setPointToPoint(true);
    channelListener.onDelivery(payload);
  }

  /**
  * <p>Accepts a message and then sends it on (delivers it) to the
  * registered channel listener.</p>
  * @param message The object that carries the pertinent data.
  * @throws CommunicationException The receiving channel is not
  * operational.
  */

  public void send(Message message) throws CommunicationException { 
    send(new Payload(message));
  }

  /**
  * <p>Accepts a payload and then publishes it to the channel.</p>
  * @param payload The object that carries the pertinent data.
  * @throws CommunicationException The receiving channel is not
  * operational.
  */

  public void publish(Payload payload) throws CommunicationException {
    if (channelListener == null) {
      throw new CommunicationException("The channel listener is null.");
    }
    payload.setBroadcast(true);
    channelListener.onDelivery(payload);
  }

  /**
  * <p>Accepts a message and then publishes it to the channel.</p>
  * @param message The object that carries the pertinent data.
  * @throws CommunicationException The receiving channel is not
  * operational.
  */

  public void publish(Message message) throws CommunicationException {
    publish(new Payload(message));
  }
}
