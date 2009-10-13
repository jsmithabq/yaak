package yaak.agent.communication;

import yaak.agent.AgentException;

/**
* <p><code>CommunicationException</code> is the base exception class
* for communications-related exceptions.</p>
* @author Jerry Smith
* @version $Id: CommunicationException.java 6 2005-06-08 17:00:15Z jsmith $
* @see java.lang.Exception
*/

public class CommunicationException extends AgentException {
  /**
  * <p>Instantiates an exception.</p>
  */

  public CommunicationException() {
    super();
  }

  /**
  * <p>Instantiates an exception.</p>
  * @param str The specified exception string.
  */

  public CommunicationException(String str) {
    super(str);
  }

  /**
  * <p>Instantiates an exception.</p>
  * @param throwable The nested exception.
  */

  public CommunicationException(Throwable throwable) {
    super(throwable);
  }

  /**
  * <p>Instantiates an exception.</p>
  * @param str The specified exception string.
  * @param throwable The nested exception.
  */

  public CommunicationException(String str, Throwable throwable) {
    super(str, throwable);
  }
}
