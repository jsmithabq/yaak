package yaak.model.event;

/**
* <p><code>SimActionAdapter</code> provides an empty, or place-holder,
* implementation of the <code>SimActionListener</code> interface.</p>
* @author Jerry Smith
* @version $Id: SimActionAdapter.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class SimActionAdapter implements SimActionListener {
  /**
  * <p>Instantiates the adapter.</p>
  */

  public SimActionAdapter() {
  }

  /**
  * <p>Invoked each time the simulation application processes an action
  * on behalf of the domain model.</p>
  * @param event The event object.
  */

  public void actionPerformed(SimActionEvent event) {
  }
}
