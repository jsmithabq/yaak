package yaak.test.agent;

import yaak.agent.AgentContext;
import yaak.agent.AgentException;
import yaak.agent.AgentSystem;

import junit.framework.*;

/**
* <p><code>TestAgentSpecialization</code> tests extension of the
* <code>Agent</code> base class.</p>
* @author Jerry Smith
* @version $Id: TestAgentSpecialization.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class TestAgentSpecialization extends TestCase {
  private AgentContext context;
  private yaak.test.agent.MyTestAgent agent;
  private String initString = "Initialization string.";

  public TestAgentSpecialization(String name) {
    super(name);
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(TestAgentSpecialization.class);
  }

  protected void setUp() {
    context = AgentSystem.createAgentContext("X");
    try {
      //
      // Specialize the base agent class, setting the agent's
      // symbolic name:
      //
      agent = (yaak.test.agent.MyTestAgent) context.createAgent(
        yaak.test.agent.MyTestAgent.class, initString, "MyTestAgent");
      assertEquals("MyTestAgent", agent.getAgentName());
    }
    catch (AgentException e) {
      fail("Cannot create test agent.");
    }
  }

  protected void tearDown() {
    // nothing
  }

  public void testInitialization() {
    assertEquals("The initialization state has been set properly.",
      agent.getTestString(), initString);
  }
}
