package yaak.test.agent;

import junit.framework.*;

/**
* <p><code>AgentTests</code> defines the agent miniframework test suite.</p>
* @author Jerry Smith
* @version $Id: AgentTests.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class AgentTests {
  public static void main (String[] args) {
    junit.textui.TestRunner.run(suite());
  }

  public static Test suite() {
    TestSuite suite = new TestSuite("Agent Tests");
    suite.addTest(
      new TestSuite(yaak.test.agent.TestAgentContextAgent.class));
    suite.addTest(
      new TestSuite(yaak.test.agent.TestAgentContextAgentID.class));
    return suite;
  }
}
