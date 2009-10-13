package yaak.ontology;

import yaak.agent.AgentRuntimeException;

/**
*<p><code>KnowledgeException</code> reports knowledge access failures.</p>
* @author Jerry Smith
* @version $Id: KnowledgeException.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class KnowledgeException extends AgentRuntimeException {
  /**
  * <p>Instantiates an exception.</p>
  */

  public KnowledgeException() {
    super();
  }

  /**
  * <p>Instantiates an exception.</p>
  * @param str The specified exception string.
  */

  public KnowledgeException(String str) {
    super(str);
  }

  /**
  * <p>Instantiates an exception.</p>
  * @param throwable The nested exception.
  */

  public KnowledgeException(Throwable throwable) {
    super(throwable);
  }

  /**
  * <p>Instantiates an exception.</p>
  * @param str The specified exception string.
  * @param throwable The nested exception.
  */

  public KnowledgeException(String str, Throwable throwable) {
    super(str, throwable);
  }
}
