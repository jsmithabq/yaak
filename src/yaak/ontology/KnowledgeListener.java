package yaak.ontology;

/**
* <p><code>KnowledgeListener</code> allows agents to register with an
* arena to share knowledge.</p>
* @author Jerry Smith
* @version $Id: KnowledgeListener.java 6 2005-06-08 17:00:15Z jsmith $
*/

public interface KnowledgeListener {
  /**
  * <p>Provides the knowledge.</p>
  * @return The object holding a summary of the exposed knowledge.
  */

  Knowledge getKnowledge();

  /**
  * <p>Identifies the knowledge type.</p>
  * @return The <code>Class</code> object identifying the knowledge.
  */

  Class getKnowledgeType();

  /**
  * <p>Identifies the knowledge type.</p>
  * @return The string identifying the knowledge's interface.
  */

  String getKnowledgeTypeAsString();
}
