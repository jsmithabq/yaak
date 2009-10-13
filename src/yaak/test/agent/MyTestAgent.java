package yaak.test.agent;

import yaak.agent.Agent;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
* <p><code>MyTestAgent</code> tests extension of the
* <code>Agent</code> base class.</p>
* @author Jerry Smith
* @version $Id: MyTestAgent.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class MyTestAgent extends Agent {
  private String testString = "nothing";

  public void onCreation(Object args) {
    this.testString = (String) args;
  }

  public String getTestString() {
    return testString;
  }

  public void execute() {
    System.out.println("Perform 'MyTestAgent' agent operations.");
  }

  public void listDecorations() {
    Map decorations = getContextDecorations();
    Collection values = decorations.values();
    Iterator iteratador = values.iterator();
    System.out.println("The decoration values are:");
    while (iteratador.hasNext()) {
      System.out.println(iteratador.next());
    }
  }
}
