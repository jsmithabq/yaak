package yaak.agent;

/**
*<p><code>InconsistentStateException</code> reports violations in
* the state of an agent or its container.</p>
* @author Jerry Smith
* @version $Id: InconsistentStateException.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class InconsistentStateException extends AgentException {
  /**
  * <p>Instantiates an exception.</p>
  */

  public InconsistentStateException() {
    super();
  }

  /**
  * <p>Instantiates an exception.</p>
  * @param str The specified exception string.
  */

  public InconsistentStateException(String str) {
    super(str);
  }

  /**
  * <p>Instantiates an exception.</p>
  * @param throwable The nested exception.
  */

  public InconsistentStateException(Throwable throwable) {
    super(throwable);
  }

  /**
  * <p>Instantiates an exception.</p>
  * @param str The specified exception string.
  * @param throwable The nested exception.
  */

  public InconsistentStateException(String str, Throwable throwable) {
    super(str, throwable);
  }
}
