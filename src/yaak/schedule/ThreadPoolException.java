package yaak.schedule;

import yaak.core.YaakException;

/**
* <p><code>ThreadPoolException</code> extends <code>YaakException</code>,
* primarily as a tagging/typing exception.</p>
* @author Jerry Smith
* @version $Id: ThreadPoolException.java 6 2005-06-08 17:00:15Z jsmith $
* @see yaak.core.YaakException
*/

public class ThreadPoolException extends YaakException {
  /**
  * <p>Instantiates an exception.</p>
  */

  public ThreadPoolException() {
    super();
  }

  /**
  * <p>Instantiates an exception.</p>
  * @param str The specified exception string.
  */

  public ThreadPoolException(String str) {
    super(str);
  }

  /**
  * <p>Instantiates an exception.</p>
  * @param throwable The nested exception.
  */

  public ThreadPoolException(Throwable throwable) {
    super(throwable);
  }

  /**
  * <p>Instantiates an exception.</p>
  * @param str The specified exception string.
  * @param throwable The nested exception.
  */

  public ThreadPoolException(String str, Throwable throwable) {
    super(str, throwable);
  }
}
