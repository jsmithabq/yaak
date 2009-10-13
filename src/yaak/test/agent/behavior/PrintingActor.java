package yaak.test.agent.behavior;

import yaak.agent.behavior.Actor;

/**
* <p><code>PrintingActor</code> tests extension of the
* <code>Actor</code> base class, using the default sense and
* reason operations, but overriding the default act operation.</p>
* @author Jerry Smith
* @version $Id: PrintingActor.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class PrintingActor extends Actor {
  public static final String TEST_STR = "I'm only acting.";
  public Object act() {
    String str = TEST_STR;
    System.out.println(str);
    return str;
  }
}
