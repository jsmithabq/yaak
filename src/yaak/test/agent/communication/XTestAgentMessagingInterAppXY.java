package yaak.test.agent.communication;

import yaak.agent.Agent;
import yaak.agent.AgentContext;
import yaak.agent.AgentException;
import yaak.agent.AgentSystem;
import yaak.agent.communication.CommunicationException;
import yaak.agent.communication.Message;
import yaak.agent.communication.MessageListener;
import yaak.core.YaakSystem;
import yaak.util.Util;

/**
* <p><code>XTestAgentMessagingInterAppXY</code> tests agent communication
* across agent contexts.</p>
* <p>This test application (by design) throws an exception if the resource
* <code>yaak.agent.communication.isDistributed</code> evaluates to
* <code>false</code>.</p>
* @author Jerry Smith
* @version $Id: XTestAgentMessagingInterAppXY.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class XTestAgentMessagingInterAppXY {
  private AgentContext x, y, x1;
  private Agent agentX, agentY, agentX1;

  public XTestAgentMessagingInterAppXY() {
    initServices();
  }

  public static void main(String[] args) {
    XTestAgentMessagingInterAppXY appXY = new XTestAgentMessagingInterAppXY();
    appXY.testLocalMessaging();
    Util.sleepSeconds(5);
    appXY.testRemoteMessaging();
    Util.sleepSeconds(5);
    appXY.addMoreServices();
    Util.sleepSeconds(5);
    appXY.testMoreRemoteMessaging();
    Util.sleepSeconds(5);
    appXY.close();
  }

  private void initServices() {
    x = AgentSystem.createAgentContext("X");
    y = AgentSystem.createAgentContext("Y");
    try {
      agentX = x.createAgent("yaak.test.agent.MyTestAgent", "AgentX");
      agentY = y.createAgent("yaak.test.agent.MyTestAgent", "AgentY");
      agentY.registerForMessages(new MessageListener() {
        public void onMessage(Message message) {
          System.out.println(agentY.getAgentSelfAddressAsString() +
            " received a message from " +
            message.getSource().stringFormatAddress() + ": ");
          System.out.println("" + message.getObject());
        }
      });
    }
    catch (AgentException e) {
      System.out.println("Cannot create agents for X and Y:");
      e.printStackTrace();
    }
  }

  private void addMoreServices() {
    x1 = AgentSystem.createAgentContext("X1");
    try {
      agentX1 = x1.createAgent("yaak.test.agent.MyTestAgent", "AgentX1");
    }
    catch (AgentException e) {
      System.out.println("Cannot create agent for X1:");
      e.printStackTrace();
    }
  }

  public void testLocalMessaging() {
    try {
      agentX.sendMessage("AgentY@Y", new Message("Hello, AgentY."));
    }
    catch (CommunicationException e) {
      System.out.println("Exception sending message:");
      e.printStackTrace();
    }
  }

  public void testRemoteMessaging() {
    try {
      agentX.sendMessage("AgentZ@Z", new Message("Hello, AgentZ."));
    }
    catch (CommunicationException e) {
      System.out.println("Exception sending message:");
      e.printStackTrace();
    }
  }

  public void testMoreRemoteMessaging() {
    try {
      agentX1.sendMessage("AgentZ@Z", new Message("Hello, AgentZ."));
    }
    catch (CommunicationException e) {
      System.out.println("Exception sending message:");
      e.printStackTrace();
    }
  }

  public void close() {
    x.dispose();
    y.dispose();
    x1.dispose();
    AgentSystem.shutdownCommunicationServer();
  }

  private void waitAroundIndefinitely() {
    while (true) {
      try { Thread.sleep(4000); } catch (InterruptedException e) {}
    }
  }
}
