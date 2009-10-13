package yaak.test.collection;

import java.util.Enumeration;
import yaak.collection.ObjectPool;

public class XTestObjectPool {
  public static void main(String[] args) {
    ObjectPool op = new ObjectPool();
    op.insertFirst("first1 object");
    op.insertFirst("first2 object");
    op.setCurrent(op.insertFirst("first3 object"));
    op.insertFirst("first4 object");
    op.insertFirst("first5 object");
    op.insertCurrent("current1 object");
    op.insertCurrent("current2 object");
    op.insertCurrent("current3 object");
    op.insertCurrent("current4 object");
    op.insertCurrent("current5 object");
    op.insertLast("last1 object");
    op.insertLast("last2 object");
    op.insertLast("last3 object");
    op.insertLast("last4 object");
    op.insertLast("last5 object");
    op.printObjects();
    System.out.println("Number entries: " + op.getNumberEntries());
/*
    Enumeration e = op.elements();
    while (e.hasMoreElements()) {
      System.out.println(e.nextElement());
    }
*/
    op.deleteCurrent();
    op.deleteLast();
    op.deleteFirst();
    op.printObjects();
    System.out.println("Number entries: " + op.getNumberEntries());
  }
}
