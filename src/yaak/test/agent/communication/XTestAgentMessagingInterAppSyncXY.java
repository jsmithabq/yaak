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
* <p><code>XTestAgentMessagingInterAppSyncXY</code> tests synchronized
* agent communication across agent contexts<em>and</em> across
* applications.</p>
* <p>This test application (by design) throws an exception if the resource
* <code>yaak.agent.communication.isDistributed</code> evaluates to
* <code>false</code>.</p>
* @author Jerry Smith
* @version $Id: XTestAgentMessagingInterAppSyncXY.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class XTestAgentMessagingInterAppSyncXY {
  private AgentContext x, y;
  private Agent agentX, agentY;
  private Thread thread;

  public XTestAgentMessagingInterAppSyncXY() {
    initServices();
  }

  public static void main(String[] args) {
    XTestAgentMessagingInterAppSyncXY appXY =
      new XTestAgentMessagingInterAppSyncXY();
    appXY.testLocalMessaging();
    Util.sleepSeconds(1);
    appXY.testRemoteMessaging();
    Util.sleepSeconds(2);
    appXY.close();
  }

  private void initServices() {
    x = AgentSystem.createAgentContext("X");
    y = AgentSystem.createAgentContext("Y");
    try {
      agentX = x.createAgent("yaak.test.agent.MyTestAgent", "AgentX");
      agentY = y.createAgent("yaak.test.agent.MyTestAgent", "AgentY");
    }
    catch (AgentException e) {
      System.out.println("Cannot create agents:");
      e.printStackTrace();
    }
    thread = new Thread(
      new Runnable() {
        public void run() {
          try {
            Message msg = agentY.receiveMessage(agentX.getAgentSelfAddress());
            YaakSystem.getLogger().info(agentY.getAgentSelfAddressAsString() +
              " received a message from " +
              msg.getSource().stringFormatAddress() + ": ");
            YaakSystem.getLogger().info("" + msg.getObject());
          }
          catch (CommunicationException e) {
            System.out.println("Cannot receive message.");
            e.printStackTrace();
          }
        }
      }
    );
  }

  public void testLocalMessaging() {
    thread.start();
    Util.sleepSeconds(1); // must wait for thread to start...
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
      agentX.sendMessage("AgentZ@Z",
        new Message("Hello, AgentZ.  Have you been waiting long?"));
    }
    catch (CommunicationException e) {
      System.out.println("Exception sending message:");
      e.printStackTrace();
    }
  }

  public void close() {
    x.dispose();
    y.dispose();
    AgentSystem.shutdownCommunicationServer();
  }
}
