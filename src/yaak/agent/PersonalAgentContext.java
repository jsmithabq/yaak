package yaak.agent;

import yaak.agent.message.Executive;

import java.util.Map;

/**
* <p><code>PersonalAgentContext</code> makes selected context-related
* resources available to an agent.  This interface is not publicly
* available!</p>
* @author Jerry Smith
* @version $Id: PersonalAgentContext.java 12 2006-02-06 23:02:23Z jsmith $
*/

interface PersonalAgentContext {
  /**
  * <p>Provides access to the message executive resource.</p>
  * @return The messaging executive.
  */

  Executive getMessageExecutive();

  /**
  * <p>The logical name for the agent context.</p>
  * @return The context name.
  */

  String getContextName();

  //
  // Policy issue?  Is it acceptable to provide global access to the
  // decorations?  That is, should agents be able to use it as a
  // collective whiteboard?
  //

  /**
  * <p>Provides context-level storage for shared data (context
  * decorations).</p>
  * @param key The key for the indexed decoration.
  * @param object The to-be-stored decoration.
  */

  void putDecoration(Object key, Object object);

  /**
  * <p>Provides look-up services for context-level decorations.</p>
  * @param key The key for the indexed decoration.
  * @return The decoration.
  */

  Object getDecoration(Object key);

  /**
  * <p>Makes the encapsulated, shared decorations available for direct
  * access.</p>
  */

  Map getDecorations();

  /**
  * <p>Clears all context-level decorations.</p>
  */

  void clearDecorations();
}
