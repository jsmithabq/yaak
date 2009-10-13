package yaak.ontology;

/**
* <p><code>Knowledge</code> is the base for all knowledge accessibility.
* An agent can implement any extension of this interface to quantify
* its willingness to deliver "portions" of its knowledge to another
* agent.</p>
* @author Jerry Smith
* @version $Id: Knowledge.java 6 2005-06-08 17:00:15Z jsmith $
*/

public interface Knowledge {
  /**
  * <p>Provides a list of knowledge properties.</p>
  * @return The list of properties in array format.
  */

  String[] getPropertyNames();

  /**
  * <p>Queries the data type for the specified property.  This method
  * is provided for convenience; an application could also use reflection
  * to determine an agent's (read) access methods.</p>
  * @param property The property.
  * @return The <code>class</code> instance representing the data type
  * for the specified knowledge attribute.
  */

  Class getType(String property);
}
