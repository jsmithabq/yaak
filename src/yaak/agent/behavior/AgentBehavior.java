package yaak.agent.behavior;

/**
* <p><code>AgentBehavior</code> prescribes a simple contract for executing
* arbitrary operations.</p>
* @author Jerry Smith
* @version $Id: AgentBehavior.java 6 2005-06-08 17:00:15Z jsmith $
* @see yaak.agent.Agent#execute
*/

public interface AgentBehavior {
  /**
  * <p>The behavior/operations to execute.</p>
  */

  void execute();
}
