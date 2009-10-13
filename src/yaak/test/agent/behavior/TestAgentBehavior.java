package yaak.test.agent.behavior;

import yaak.agent.Agent;
import yaak.agent.AgentContext;
import yaak.agent.AgentException;
import yaak.agent.AgentSystem;
import yaak.agent.behavior.AgentBehavior;
import yaak.concurrent.DefaultExecutor;

import junit.framework.*;

/**
* <p><code>TestAgentBehavior</code> tests behavior customization by
* <ul>
* <li>Extending the <code>Agent</code> base class.
* <li>Setting the agent behavior object.
* </ul></p>
* @author Jerry Smith
* @version $Id: TestAgentBehavior.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class TestAgentBehavior extends TestCase {
  private AgentContext context;
  private Agent agent1, agent2;

  public TestAgentBehavior(String name) {
    super(name);
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(TestAgentBehavior.class);
  }

  protected void setUp() {
    context = AgentSystem.createAgentContext("X");
    try {
      agent1 = context.createAgent(yaak.test.agent.MyTestAgent.class);
      agent2 = context.createAgent(yaak.test.agent.AnyAgent.class);
    }
    catch (AgentException e) {
      fail("Cannot create agents.");
    }
    agent2.setAgentBehavior(new AgentBehavior() {
      public void execute() {
        System.out.println("Perform assigned behavior.");
      }
    });
  }

  protected void tearDown() {
    // nothing
  }

  public void testBehavior() {
//    agent1.run();
//    agent2.run();
    new DefaultExecutor().execute(agent1);
    new DefaultExecutor().execute(agent2);
  }
}
