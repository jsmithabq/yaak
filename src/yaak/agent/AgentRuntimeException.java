package yaak.agent;

import yaak.core.YaakRuntimeException;

/**
* <p><code>AgentRuntimeException</code> extends
* {@link yaak.core.YaakRuntimeException YaakRuntimeException},
* as a base for reporting unchecked, agent-related exceptions.</p>
* @author Jerry Smith
* @version $Id: AgentRuntimeException.java 12 2006-02-06 23:02:23Z jsmith $
*/

public class AgentRuntimeException extends YaakRuntimeException {
  /**
  * <p>Instantiates an exception.</p>
  */

  public AgentRuntimeException() {
    super();
  }

  /**
  * <p>Instantiates an exception.</p>
  * @param str The specified exception string.
  */

  public AgentRuntimeException(String str) {
    super(str);
  }

  /**
  * <p>Instantiates an exception.</p>
  * @param throwable The nested exception.
  */

  public AgentRuntimeException(Throwable throwable) {
    super(throwable);
  }

  /**
  * <p>Instantiates an exception.</p>
  * @param str The specified exception string.
  * @param throwable The nested exception.
  */

  public AgentRuntimeException(String str, Throwable throwable) {
    super(str, throwable);
  }
}
