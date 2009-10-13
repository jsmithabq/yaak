package yaak.test.agent.behavior;

import yaak.agent.behavior.Actor;

/**
* <p><code>SensitiveThinkingActor</code> tests extension of the
* <code>Actor</code> base class, adding sensing, reasoning, and
* act operation.</p>
* @author Jerry Smith
* @version $Id: SensitiveThinkingActor.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class SensitiveThinkingActor extends Actor {
  public static final String SENSE_RESULT =
    "I sense, therefore I'm aware;";
  public static final String REASON_RESULT =
    "I think, therefore I have flair;";
  public static final String ACT_RESULT =
    "But when I act, they all stare.";
  public static final String TEST_RESULT =
    SENSE_RESULT + REASON_RESULT + ACT_RESULT;
  private String str = "";

  public boolean sense() {
    str += SENSE_RESULT;
    return true; // success
  }

  public boolean reason() {
    str += REASON_RESULT;
    return true; // success
  }

  public Object act() {
    str += ACT_RESULT;
    System.out.println(SENSE_RESULT);
    System.out.println(REASON_RESULT);
    System.out.println(ACT_RESULT);
    return str;
  }
}
