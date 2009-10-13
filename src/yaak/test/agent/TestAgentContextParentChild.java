package yaak.test.agent;

import yaak.agent.Agent;
import yaak.agent.AgentID;
import yaak.agent.AgentContext;
import yaak.agent.AgentException;
import yaak.agent.AgentSystem;

import junit.framework.*;

/**
* <p><code>TestAgentContextParentChild</code> tests the agent context's
* support for nested contexts.</p>
* @author Jerry Smith
* @version $Id: TestAgentContextParentChild.java 13 2006-02-12 18:30:51Z jsmith $
*/

public class TestAgentContextParentChild extends TestCase {
  public TestAgentContextParentChild(String name) {
    super(name);
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(TestAgentContextParentChild.class);
  }

  public void testContexts() {
    AgentContext parent = null, child = null;
    Agent agent1, agent2;
    parent = AgentSystem.createAgentContext("Parent");
    assertEquals("Parent", parent.getContextName());
    try {
      //
      // Create an instance of an agent, setting the agent's symbolic name:
      //
      agent1 = parent.createAgent("yaak.test.agent.MyTestAgent", "Agent1");
      assertEquals("Agent1", agent1.getAgentName());
    }
    catch (AgentException e) {
      fail("Cannot create agent within the parent context.");
    }
    try {
      //
      // Create another agent context:
      //
      child = AgentSystem.createAgentContext(parent, "Child");
    }
    catch (AgentException e) {
      fail("Cannot create the child context.");
    }
    AgentID[] ids = parent.getAgentIDs();
    //
    // Parent contains child context (an agent), plus one "plain" agent:
    //
    assertTrue("There are two agents in the parent.", ids.length == 2);
    try {
      //
      // Create another agent, setting the name separately:
      //
      agent2 = child.createAgent("yaak.test.agent.MyTestAgent");
      agent2.setAgentName("Agent2");
      assertEquals("Agent2", agent2.getAgentName());
    }
    catch (AgentException e) {
      fail("Cannot create agent within the child context.");
    }
    ids = child.getAgentIDs();
    //
    // Child contains one agent:
    //
    assertTrue("There is one agent in the child.", ids.length == 1);
//    child.dispose(); // automatically disposed of via parent disposal
//    parent.dispose();
  }
}
