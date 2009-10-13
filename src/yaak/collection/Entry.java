package yaak.collection;

import java.io.Serializable;

/**
* <p><code>Entry</code> is the base element, or node, within a
* container.  An <code>Entry</code> object could exist within a
* a list, set, pool, and so on.  <code>Entry</code> provides
* previous and next reference links to support simple doubly linked
* lists and other bidirectional data structures.</p>
* <p><em>Warning:  The current implementation is not thread-safe.</em>
* The user must manage access appropriately.</p>
* @author Jerry Smith
* @version $Id: Entry.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class Entry implements Serializable {
  Object object; // for now, default privacy for efficiency
  Entry previous; // for now, default privacy for efficiency
  Entry next; // for now, default privacy for efficiency

  /**
  * <p>Instantiates a node/element.</p>
  */

  public Entry() {
    clear();
  }

  /**
  * <p>Instantiates a node/element and sets the (data) object.</p>
  * @param object The encapsulated object.
  */

  public Entry(Object object) {
    this.object = object;
    previous = next = null;
  }

  /**
  * <p>Resets the (data) object and previous and next pointers.</p>
  */

  public void clear() {
    object = previous = next = null;
  }

  /**
  * <p>Retrieves the node's (data) object.</p>
  * @return The encapsulated (data) object.
  */

  public Object getObject() {
    return object;
  }

  /**
  * <p>Sets the node's (data) object.</p>
  * @param object The encapsulated (data) object.
  */

  public void setObject(Object object) {
    this.object = object;
  }

  /**
  * <p>Retrieves the node's "previous" link.</p>
  * @return The "previous" entry.
  */

  public Entry getPrevious() {
    return previous;
  }

  /**
  * <p>Sets the node's "previous" link.</p>
  * @param previous The "previous" link.
  */

  public void setPrevious(Entry previous) {
    this.previous = previous;
  }

  /**
  * <p>Retrieves the node's "next" link.</p>
  * @return The "next" entry.
  */

  public Entry getNext() {
    return next;
  }

  /**
  * <p>Sets the node's "next" link.</p>
  * @param next The "next" link.
  */

  public void setNext(Entry next) {
    this.next = next;
  }
}
