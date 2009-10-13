package yaak.agent;

/**
*<p><code>DuplicateNameException</code> reports duplicate agent name
* violations.</p>
* @author Jerry Smith
* @version $Id: DuplicateNameException.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class DuplicateNameException extends AgentException {
  /**
  * <p>Instantiates an exception.</p>
  */

  public DuplicateNameException() {
    super();
  }

  /**
  * <p>Instantiates an exception.</p>
  * @param str The specified exception string.
  */

  public DuplicateNameException(String str) {
    super(str);
  }

  /**
  * <p>Instantiates an exception.</p>
  * @param throwable The nested exception.
  */

  public DuplicateNameException(Throwable throwable) {
    super(throwable);
  }

  /**
  * <p>Instantiates an exception.</p>
  * @param str The specified exception string.
  * @param throwable The nested exception.
  */

  public DuplicateNameException(String str, Throwable throwable) {
    super(str, throwable);
  }
}
