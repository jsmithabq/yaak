package yaak.agent.communication;

import yaak.agent.AgentRuntimeException;

/**
* <p><code>CommunicationRuntimeException</code> signals unexpected
* communications-related exceptions.</p>
* @author Jerry Smith
* @version $Id: CommunicationRuntimeException.java 6 2005-06-08 17:00:15Z jsmith $
* @see java.lang.Exception
*/

public class CommunicationRuntimeException extends AgentRuntimeException {
  /**
  * <p>Instantiates an exception.</p>
  */

  public CommunicationRuntimeException() {
    super();
  }

  /**
  * <p>Instantiates an exception.</p>
  * @param str The specified exception string.
  */

  public CommunicationRuntimeException(String str) {
    super(str);
  }

  /**
  * <p>Instantiates an exception.</p>
  * @param throwable The nested exception.
  */

  public CommunicationRuntimeException(Throwable throwable) {
    super(throwable);
  }

  /**
  * <p>Instantiates an exception.</p>
  * @param str The specified exception string.
  * @param throwable The nested exception.
  */

  public CommunicationRuntimeException(String str, Throwable throwable) {
    super(str, throwable);
  }
}
