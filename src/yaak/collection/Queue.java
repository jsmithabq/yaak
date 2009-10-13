package yaak.collection;

/**
* <p><code>Queue</code> provides a simple FIFO organization for
* an underlying object pool.</p>
* @author Jerry Smith
* @version $Id: Queue.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class Queue extends ObjectPool {
  /**
  * <p>Instantiates a queue.</p>
  */

  public Queue() {
    super();
  }

  /**
  * <p>Adds an element/object to the queue.</p>
  * @param object The object.
  */

  public void add(Object object) {
    insertLast(object);
  }

  /**
  * <p>Removes the first (leading, oldest) element from the queue.</p>
  * @return The first element.
  */

  public Object remove() {
    Entry entry = getFirst();
    if (entry == null) {
      return null;
    }
    Object object = entry.getObject();
    deleteFirst();
    return object;
  }

  /**
  * <p>Removes the first (leading, oldest) element from the queue
  * with verification.  That is, if the first element on the
  * queue matched the specified object (equality of reference
  * pointers), the removal proceeds; otherwise, removal is
  * abandoned.</p>
  * @param object The verification object.
  * @return The first element.
  */

  public Object removeSafe(Object object) {
    Entry entry = getFirst();
    if (entry == null) {
      return null;
    }
    Object candidate = entry.getObject();
    if (candidate == object) {
      deleteFirst();
      return object;
    }
    else {
     return null;
    }
  }

  /**
  * <p>Retrieves (in a read-only manner) the first element.</p>
  * @return The first element.
  */

  public Object peekFirst() {
    Entry entry = getFirst();
    return (entry == null) ? null : entry.getObject();
  }

  /**
  * <p>Retrieves (in a read-only manner) the first element.</p>
  * @return The first element.
  */

  public Object peekNext() {
    Entry entry = getFirst();
    return (entry == null) ? null : entry.getObject();
  }

  /**
  * <p>Retrieves the number of elements in the queue.</p>
  * @return The element count.
  */

  public int size() {
    return getNumberEntries();
  }
}
