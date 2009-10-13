package yaak.agent.behavior;

/**
* <p><code>ActorBehavior</code> prescribes a simple contract for executing
* arbitrary operations.</p>
* @author Jerry Smith
* @version $Id: ActorBehavior.java 6 2005-06-08 17:00:15Z jsmith $
* @see yaak.agent.behavior.Actor#execute
*/

public interface ActorBehavior extends AgentBehavior {
  /**
  * <p>Executes a custom sense-reason-act execution cycle (protocol).
  * The {@link yaak.agent.behavior.Actor Actor}'s
  * {@link yaak.agent.behavior.Actor#sense sense()},
  * {@link yaak.agent.behavior.Actor#reason reason()}, and
  * {@link yaak.agent.behavior.Actor#act act()} methods should
  * implements the sense-reason-act process under the control
  * of this execution protocol.</p>
  * @param actor The actor associated with this behavior object.
  * @return Any application-specific object produced (returned)
  * by the actor's {@link yaak.agent.behavior.Actor#act act()} method.
  */

  Object execute(Actor actor);
}
