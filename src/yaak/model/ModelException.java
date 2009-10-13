package yaak.model;

import yaak.core.YaakException;

/**
* <p><code>ModelException</code> is the base exception class for
* model-related exceptions.</p>
* @author Jerry Smith
* @version $Id: ModelException.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class ModelException extends YaakException {
  /**
  * <p>Instantiates an exception.</p>
  */

  public ModelException() {
    super();
  }

  /**
  * <p>Instantiates an exception.</p>
  * @param str The specified exception string.
  */

  public ModelException(String str) {
    super(str);
  }

  /**
  * <p>Instantiates an exception.</p>
  * @param throwable The nested exception.
  */

  public ModelException(Throwable throwable) {
    super(throwable);
  }

  /**
  * <p>Instantiates an exception.</p>
  * @param str The specified exception string.
  * @param throwable The nested exception.
  */

  public ModelException(String str, Throwable throwable) {
    super(str, throwable);
  }
}
