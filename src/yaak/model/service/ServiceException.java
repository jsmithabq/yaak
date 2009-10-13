package yaak.model.service;

import yaak.agent.AgentRuntimeException;

/**
*<p><code>ServiceException</code> reports service failures.</p>
* @author Jerry Smith
* @version $Id: ServiceException.java 13 2006-02-12 18:30:51Z jsmith $
*/

public class ServiceException extends AgentRuntimeException {
  /**
  * <p>Instantiates an exception.</p>
  */

  public ServiceException() {
    super();
  }

  /**
  * <p>Instantiates an exception.</p>
  * @param str The specified exception string.
  */

  public ServiceException(String str) {
    super(str);
  }

  /**
  * <p>Instantiates an exception.</p>
  * @param throwable The nested exception.
  */

  public ServiceException(Throwable throwable) {
    super(throwable);
  }

  /**
  * <p>Instantiates an exception.</p>
  * @param str The specified exception string.
  * @param throwable The nested exception.
  */

  public ServiceException(String str, Throwable throwable) {
    super(str, throwable);
  }
}
