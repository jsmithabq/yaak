package yaak.test.agent.behavior;

import yaak.agent.AgentContext;
import yaak.agent.AgentException;
import yaak.agent.AgentSystem;

import junit.framework.*;

/**
* <p><code>TestActorBehaviorAct</code> tests behavior customization by
* <ul>
* <li>Extending the <code>Actor</code> base class.
* <li>Evaluating the result of the sense-reason-act cycle.
* </ul></p>
* @author Jerry Smith
* @version $Id: TestActorBehaviorAct.java 13 2006-02-12 18:30:51Z jsmith $
*/

public class TestActorBehaviorAct extends TestCase {
  private AgentContext context;
  private PrintingActor actor;

  public TestActorBehaviorAct(String name) {
    super(name);
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(TestActorBehaviorAct.class);
  }

  protected void setUp() {
    context = AgentSystem.createAgentContext("X");
    try {
      actor = (PrintingActor)
        context.createAgent(yaak.test.agent.behavior.PrintingActor.class);
    }
    catch (AgentException e) {
      fail("Cannot create agent.");
    }
  }

  protected void tearDown() {
    // nothing
  }

  public void testBehavior() {
    actor.run();
    assertEquals((String) actor.getActResult(), PrintingActor.TEST_STR);
  }
}
