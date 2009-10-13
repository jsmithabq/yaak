package yaak.test.agent.communication;

import yaak.agent.Agent;
import yaak.agent.AgentException;
import yaak.agent.AgentContext;
import yaak.agent.AgentSystem;
import yaak.agent.communication.AgentAddress;
import yaak.agent.communication.CommunicationException;
import yaak.agent.communication.Message;
import yaak.agent.communication.MessageListener;
import yaak.agent.communication.StringMessage;
import yaak.core.YaakSystem;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;

/**
* <p><code>XTestAgentChat</code> tests asynchronous, distributed messaging via
* a simple network chat application.</p>
* @author Jerry Smith
* @version $Id: XTestAgentChat.java 10 2006-01-23 20:32:10Z jsmith $
*/

public class XTestAgentChat extends Frame {
  private static String contextName = "Jupiter";
  private static String agentName = "Joe";
  private Button send;
  private TextField recipient;
  private TextArea receiveMessage, sendMessage;
  private Agent agent;
  private AgentContext context;

  public static void main(String[] args) {
    if (!processArguments(args)) {
      return;
    }
    new XTestAgentChat(agentName + "@" + contextName);
  }

  XTestAgentChat(String title) {
    super(title);
    initUI();
    
    context = AgentSystem.createAgentContext(contextName);
    try {
      agent = context.createAgent(
        yaak.test.agent.MyTestAgent.class, agentName);
      agent.registerForMessages(new MessageListener() {
        public void onMessage(Message msg) {
          receiveMessage.setText(((StringMessage) msg).getText());
        }
      });
    }
    catch (AgentException e) {
      YaakSystem.getLogger().fine("Cannot create agent.");
      e.printStackTrace();
    }
    ActionListener sendIt = new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        try {
          String target = recipient.getText();
          if (target.length() > 0) {
            agent.sendMessage(new AgentAddress(target),
              new StringMessage(sendMessage.getText()));
          }
        }
        catch (CommunicationException e) {
          YaakSystem.getLogger().fine("Cannot send message.");
          e.printStackTrace();
        }
      }
    };
    send.addActionListener(sendIt);
    recipient.addActionListener(sendIt);
  }

  private void initUI(){
    setLayout(new BorderLayout(5, 5));
    Panel outerPanel = new InsetsPanel();
    add(outerPanel, BorderLayout.CENTER);
    GridLayout grid = new GridLayout(2, 1, 5, 5);
    outerPanel.setLayout(grid);
    Panel receiveMessagePanel = new InsetsPanel();
    receiveMessagePanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
    receiveMessagePanel.add(new Label("Receive:"));
    receiveMessagePanel.add(receiveMessage = new TextArea(5, 30));
    receiveMessage.setEditable(false);
    outerPanel.add(receiveMessagePanel);
    Panel sendMessagePanel = new InsetsPanel();
    sendMessagePanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
    sendMessagePanel.add(new Label("Send:"));
    sendMessagePanel.add(sendMessage = new TextArea(5, 30));
    outerPanel.add(sendMessagePanel);
    Panel sendPanel = new InsetsPanel();
    sendPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
    sendPanel.add(send = new Button("Send"));
    sendPanel.add(new Label("Recipient:"));
    sendPanel.add(recipient = new TextField(25));
    add(sendPanel, BorderLayout.SOUTH);

    pack();
    setSize(getPreferredSize());
    setVisible(true);

    addWindowListener(
      new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          context.dispose();
          System.exit(0);
        }
      }
    );
  }

  private static boolean processArguments(String[] args) {
    if (args.length > 0 && args[0].equals("-help")) {
      System.out.println("Usage:  " +
        "yaak.test.agent.communication.XTestAgentChat " +
        "[<agentName>] [<contextName>]");
      return false;
    }
    if (args.length > 0) {
      agentName = args[0];
    }
    if (args.length > 1) {
      contextName = args[1];
    }
    return true;
  }

  private class InsetsPanel extends Panel {
    public Insets getInsets() {
      return new Insets(5, 5, 5, 5);
    }
  }
}
