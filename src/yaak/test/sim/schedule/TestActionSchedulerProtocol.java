package yaak.test.sim.schedule;

import yaak.core.YaakSystem;
import yaak.sim.schedule.ActionScheduler;

import junit.framework.*;

/**
* <p><code>TestActionSchedulerProtocol</code> tests the protocol,
* in particular, the guard against indirect invocation of run().</p>
* @author Jerry Smith
* @version $Id: TestActionSchedulerProtocol.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class TestActionSchedulerProtocol extends TestCase {
  static {
    yaak.core.YaakSystem.initFramework();
  }

  public TestActionSchedulerProtocol(String name) {
    super(name);
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(TestActionSchedulerProtocol.class);
  }

  protected void setUp() {
  }

  protected void tearDown() {
    // nothing
  }

  public void testUnsupportedOperationException() {
    try {
      ActionScheduler scheduler = new ActionScheduler();
      new Thread(scheduler).start();
    }
    catch (UnsupportedOperationException e) {
      YaakSystem.getLogger().warning(
        "successfully validated suppression of run().");
      e.printStackTrace();
    }
  }
}
