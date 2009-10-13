package yaak.test.agent.communication;

import yaak.agent.Agent;
import yaak.agent.AgentException;
import yaak.agent.AgentContext;
import yaak.agent.AgentSystem;
import yaak.agent.communication.AgentAddress;
import yaak.agent.communication.CommunicationException;
import yaak.agent.communication.Message;
import yaak.agent.communication.MessageListener;
import yaak.core.YaakSystem;
import yaak.util.Util;

import java.util.Date;

/**
* <p><code>XTestAgentMessagingInterAppPerformanceB</code> tests asychronous
* agent communication across agent applications.</p>
* @author Jerry Smith
* @version $Id: XTestAgentMessagingInterAppPerformanceB.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class XTestAgentMessagingInterAppPerformanceB {
  private static AgentContext b;
  private static int receiveCount = 0;
  private static Agent receiverAgent;

  public static void main(String[] args) {
    b = AgentSystem.createAgentContext("B");
    try {
      System.out.println("+++++Creating receiver agent...");
      receiverAgent = b.createAgent(
        "yaak.test.agent.MyTestAgent", "Receiver");
      receiverAgent.registerForMessages(new MessageListener() {
        long beginTime, endTime;

        public void onMessage(Message msg) {
          if (receiveCount == 0) {
            beginTime = System.currentTimeMillis();
            System.out.println("+++++Begin receiving messages: " + new Date());
          }
          if (((String) msg.getObject()).equals("stop")) {
            endTime = System.currentTimeMillis();
            System.out.println(
              "+++++Finish receiving messages: " + new Date());
            System.out.println(
              "+++++Received " + receiveCount + " message(s).");
            System.out.println("+++++Elapsed time: " +
              (endTime - beginTime) / 1000 + " second(s).");
              //
              // It's not nice to "pull the plug" when a remote
              // app is sending messages, but it's important for
              // testing purposes...
              //
//            System.exit(0);
          }
          receiveCount++;
        }
      });
    }
    catch (AgentException e) {
      YaakSystem.getLogger().fine("Cannot create agent.");
      e.printStackTrace();
    }
    waitAround();
  }

  private static void waitAround() {
    while (true) {
      try { Thread.sleep(1000); } catch (InterruptedException e) {}
    }
  }
}
