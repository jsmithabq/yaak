package yaak.agent;

import yaak.core.YaakException;

/**
* <p><code>AgentException</code> extends
* {@link yaak.core.YaakException YaakException},
* as a base for reporting agent-related exceptions.</p>
* @author Jerry Smith
* @version $Id: AgentException.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class AgentException extends YaakException {
  /**
  * <p>Instantiates an exception.</p>
  */

  public AgentException() {
    super();
  }

  /**
  * <p>Instantiates an exception.</p>
  * @param str The specified exception string.
  */

  public AgentException(String str) {
    super(str);
  }

  /**
  * <p>Instantiates an exception.</p>
  * @param throwable The nested exception.
  */

  public AgentException(Throwable throwable) {
    super(throwable);
  }

  /**
  * <p>Instantiates an exception.</p>
  * @param str The specified exception string.
  * @param throwable The nested exception.
  */

  public AgentException(String str, Throwable throwable) {
    super(str, throwable);
  }
}
