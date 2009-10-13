package yaak.test.agent;

import yaak.agent.Agent;
import yaak.agent.AgentContext;
import yaak.agent.AgentException;
import yaak.agent.AgentSystem;

import junit.framework.*;
import java.util.Map;

/**
* <p><code>TestAgentContextDecoration</code> tests agent-context
* decoration.</p>
* @author Jerry Smith
* @version $Id: TestAgentContextDecoration.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class TestAgentContextDecoration extends TestCase {
  private AgentContext context;
  private yaak.test.agent.MyTestAgent agent;

  public TestAgentContextDecoration(String name) {
    super(name);
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(TestAgentContextDecoration.class);
  }

  protected void setUp() {
    context = AgentSystem.createAgentContext("Jupiter");
    assertEquals("Jupiter", context.getContextName());
    try {
      agent = (yaak.test.agent.MyTestAgent) context.createAgent(
        yaak.test.agent.MyTestAgent.class, "MyTestAgent");
    }
    catch (AgentException e) {
      fail("Cannot create agent.");
    }
  }

  protected void tearDown() {
    // nothing
  }

  public void testDecoration() {
    String key1 = "DecorationOne";
    String key2 = "DecorationTwo";
    String value1 = "ValueforDecorationOne";
    String value2 = "ValueforDecorationTwo";
    context.putDecoration(key1, value1);
    context.putDecoration(key2, value2);
    assertEquals((String) context.getDecoration(key1), value1);
    assertEquals((String) context.getDecoration(key2), value2);
    assertTrue("There are two decorations.",
      ((Map) context.getDecorations()).size() == 2);
    agent.listDecorations();
  }
}
