package yaak.collection;

import yaak.core.YaakSystem;

import java.io.Serializable;
import java.util.Enumeration;

/**
* <p><code>ObjectPool</code> manages a pool of objects.  It supports
* an element cursor for tracking the current object.</p>
* <p>The underlying organization for the pool is a doubly linked list.
* This structure is "highly synchronized."  Thus, its performance
* under certain concurrency conditions will be inferior to other
* implementations, for example, fail-fast implementations.</p>
* @author Jerry Smith
* @version $Id: ObjectPool.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class ObjectPool implements Serializable {
  private int numEntries;

  /**
  * The pointer to the first entry.
  */

  protected Entry first;

  /**
  * The pointer to the last entry.
  */

  protected Entry last;

  /**
  * The pointer to the current entry.
  */

  protected Entry current;

  /**
  * <p>Instantiates an object pool.</p>
  */

  public ObjectPool() {
    numEntries = 0;
    first = last = current = null;
  }

  /**
  * <p>Retrieves the number of elements in the pool.</p>
  * @return The element count.
  */

  synchronized public int getNumberEntries() {
    return numEntries;
  }

  /**
  * <p>Retrieves (in a read-only manner) the first entry.</p>
  * @return The first entry.
  */

  synchronized public Entry getFirst() {
    return first;
  }

  /**
  * <p>Retrieves (in a read-only manner) the last entry.</p>
  * @return The last entry.
  */

  synchronized public Entry getLast() {
    return last;
  }

  /**
  * <p>Retrieves (in a read-only manner) the pool's "previous" entry,
  * relative to the specified entry.</p>
  * @param entry The reference entry.
  * @return The "previous" entry.
  */

  synchronized public Entry getPrevious(Entry entry) {
    return entry.getPrevious();
  }

  /**
  * <p>Retrieves (in a read-only manner) the pool's "next" entry,
  * relative to the specified entry.</p>
  * @param entry The reference entry.
  * @return The "next" entry.
  */

  synchronized public Entry getNext(Entry entry) {
    return entry.getNext();
  }

  /**
  * <p>Retrieves (in a read-only manner) the pool's <em>i</em>th entry
  * (zero-based indexing).</p>
  * @param position The reference entry.
  * @return The "<em>i</em>th" entry.
  */

  synchronized public Entry getEntryByPosition(int position) {
    Entry entry = first;
    if (position < 0 || position >= numEntries) {
      return null;
    }
    while (entry != null && position-- > 0) {
      entry = entry.next;
    }
    return entry;
  }

  /**
  * <p>Retrieves (in a read-only manner) the pool's current entry,
  * based on the position of the current-entry pointer.</p>
  * @return The current entry.
  */

  synchronized public Entry getCurrent() {
    return current;
  }

  /**
  * <p>Sets the pool's current-entry pointer (cursor) to the
  * specified entry.</p>
  * @param entry The object pool entry receiving
  * the current-entry pointer.
  * @return The (newly set) current entry.
  */

  synchronized public Entry setCurrent(Entry entry) {
    return current = entry;
  }

  /**
  * <p>Retrieves a list of the current entries' objects as an
  * enumeration.</p>
  * @return The enumeration of objects
  * from the pool.
  */

  synchronized Enumeration getList() {
    return new List();
  }

  /**
  * <p>Retrieves a list of the current entries' objects as an
  * enumeration.</p>
  * @return The enumeration of objects
  * from the pool.
  */

  synchronized public Enumeration elements() {
    return new List();
  }

  /**
  * <p>Retrieves the pool's current object, based on the
  * current-entry pointer.</p>
  * @return The (data) object stored within
  * the current entry/node.
  */

  synchronized public Object getCurrentObject() {
    if (current == null) {
      return null;
    }
    return current.object;
  }

  /**
  * <p>Sets the (data) object for the entry, based on the
  * current-entry pointer.</p>
  * @param object The object to store at the current entry.
  * @return The (data) object stored within
  * the current entry/node.
  */

  synchronized public Object setCurrentObject(Object object) {
    if (current == null) {
      return null;
    }
    current.setObject(object);
    return current.getObject();
  }

  /**
  * <p>Retrieves the (data) object for the specified entry.</p>
  * @param entry The entry of interest in the pool.
  * @return The (data) object stored within
  * the specified entry/node.
  */

  synchronized public Object getEntryObject(Entry entry) {
    return (entry == null) ? null : entry.getObject();
  }

  /**
  * <p>Retrieves the (data) object for the <em>i</em>th entry
  * (zero-based indexing).</p>
  * @param position The index/position of the entry
  * in the pool.
  * @return The (data) object stored within
  * the specified entry/node.
  */

  synchronized public Object getEntryObjectByPosition(int position) {
    Entry entry = getEntryByPosition(position);
    return (entry == null) ? null : entry.getObject();
  }

  /**
  * <p>Retrieves the (data) object for each entry, from first to last,
  * and "prints" it to standard output.</p>
  */

  public void printObjects() {
    Entry e = first;
    while (e != null) {
      System.out.println(e.getObject());
      e = e.getNext();
    }
  }

  /**
  * <p>Inserts the (data) object into the pool in the first/head
  * position.</p>
  * @param object The object to store in the (new)
  * first entry.
  * @return The (new) first entry/node.
  */

  synchronized public Entry insertFirst(Object object) {
    if (object == null) {
      return null;
    }
    numEntries++;
    Entry entry = new Entry(object);
    entry.setNext(first);
    if (first == null) {
      first = last = current = entry;
    }
    else {
      first.setPrevious(entry);
    }
    return first = entry;
  }

  /**
  * <p>Inserts the (data) object into the pool in the last/tail
  * position.</p>
  * @param object The object to store in the (new)
  * last entry.
  * @return The (new) last entry/node.
  */

  synchronized public Entry insertLast(Object object) {
    if (object == null) {
      return null;
    }
    numEntries++;
    Entry entry = new Entry(object);
    entry.setPrevious(last);
    if (last == null) {
      first = last = current = entry;
    }
    else {
      last.setNext(entry);
    }
    return last = entry;
  }

  /**
  * <p>Inserts the (data) object into the pool before the
  * current entry.</p>
  * @param object The object to store in the (new)
  * current entry.
  * @return The (new) current entry/node.
  */

  synchronized public Entry insertCurrent(Object object) {
    if (object == null) {
      return null;
    }
    numEntries++;
    Entry entry = new Entry(object);
    if (current == null) {
      first = last = current = entry;
      if (numEntries != 1) {
        YaakSystem.getLogger().severe("integrity error.");
      }
      return entry;
    }
    else {
      Entry previousEntry = current.getPrevious();
      if (previousEntry != null) {
        previousEntry.setNext(entry);
      }
      current.setPrevious(entry);
      entry.setNext(current);
      entry.setPrevious(previousEntry);
      return entry;
    }
  }

  /**
  * <p>Deletes the first entry in the pool.</p>
  * @return The (new) first entry/node.
  */

  synchronized public Entry deleteFirst() {
    if (first == null) {
      return null;
    }
    numEntries--;
    Entry entry = first.getNext();
    if (entry != null) {
      entry.setPrevious(null);
      if (current == first) {
        current = entry;
      }
    }
    else {
      last = current = null;
    }
    first.clear();
    return first = entry;
  }

  /**
  * <p>Deletes the last entry in the pool.</p>
  * @return The (new) last entry/node.
  */

  synchronized public Entry deleteLast() {
    if (last == null) {
      return null;
    }
    numEntries--;
    Entry entry = last.getPrevious();
    if (entry != null) {
      entry.setNext(null);
      if (current == last) {
        current = entry;
      }
    }
    else {
      first = current = null;
    }
    last.clear();
    return last = entry;
  }

  /**
  * <p>Deletes the current entry in the pool.</p>
  * @return The (new) current entry/node.
  */

  synchronized public Entry deleteCurrent() {
    if (current == null) {
      return null;
    }
    if (current == first) {
      return deleteFirst();
    }
    else if (current == last) {
      return deleteLast();
    }
    else {
      numEntries--;
      Entry previousEntry, nextEntry;
      previousEntry = current.getPrevious();
      nextEntry = current.getNext();
      previousEntry.setNext(nextEntry);
      nextEntry.setPrevious(previousEntry);
      return nextEntry;
    }
  }

  class List implements Enumeration {
    Object[] list = null;
    int nextEnumerationOffset = -1;

    List() {
      list = new Object[numEntries];
      nextEnumerationOffset = 0;
      Entry next = first;
      for (int i = 0; i < numEntries; i++) {
        list[i] = next.getObject();
        next = next.getNext();
      }
    }

    public boolean hasMoreElements() {
      return (nextEnumerationOffset < numEntries);
    }

    public Object nextElement() {
      return list[nextEnumerationOffset++];
    }
  }
}
