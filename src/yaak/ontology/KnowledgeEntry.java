package yaak.ontology;

/**
* <p><code>KnowledgeEntry</code> provides (limited) metadata.</p>
* @author Jerry Smith
* @version $Id: KnowledgeEntry.java 6 2005-06-08 17:00:15Z jsmith $
*/

class KnowledgeEntry { // default privacy
  /**
  * <p>The property name in string form.</p>
  */

  String propertyName; // default privacy

  /**
  * <p>The property type suitable for reflective endeavors.  The type
  * for class instances can be obtained by <code>getClass()</code>,
  * but not for primitive data.  So, having this field/member is
  * convenient for consistent runtime analysis, and metadata-like
  * operations in general.</p>
  */

  Class propertyType; // default privacy
}
