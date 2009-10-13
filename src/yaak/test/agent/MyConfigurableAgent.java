package yaak.test.agent;

import yaak.agent.ConfigurableAgent;

/**
* <p><code>MyConfigurableAgent</code> tests extension of the
* <code>Agent</code> base class.</p>
* @author Jerry Smith
* @version $Id: MyConfigurableAgent.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class MyConfigurableAgent extends ConfigurableAgent {
  private boolean v;
  private int w;
  private float x;
  private double y;
  private String z = "none";

  public boolean getV() {
    return v;
  }

  public int getW() {
    return w;
  }

  public float getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public String getZ() {
    return z;
  }
}
