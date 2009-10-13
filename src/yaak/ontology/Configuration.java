package yaak.ontology;

import yaak.core.YaakException;
import yaak.core.YaakSystem;

/**
* <p><code>Configuration</code> manages ontology configuration
* data.  This class provides a temporary configuration solution
* and is under construction.</p>
* @author Jerry Smith
* @version $Id: Configuration.java 6 2005-06-08 17:00:15Z jsmith $
*/

public final class Configuration {
  private static final String ONTOLOGY_MODEL = "yaak.ontology.model";
  private static final String DEFAULT_ONTOLOGY = "performatives";
  private static String model = DEFAULT_ONTOLOGY;

  static {
    try {
       model= YaakSystem.getProperty(ONTOLOGY_MODEL, DEFAULT_ONTOLOGY);
    }
    catch (YaakException e) {
      YaakSystem.getLogger().warning("cannot load properties: ");
      e.printStackTrace();
    }
  }

  private Configuration() {
  }

  /**
  * <p>Gets the ontology model.</p>
  * @return The model's symbolic name.
  */

  public static String getModel() {
    return model;
  }
}
