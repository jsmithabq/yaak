/**********************************************************************
*  Copyright (c) 1991 - 2000 Jerry Smith.
*
*  This software is provided for demonstration purposes only.  As
*  freely-distributed, modifiable source code, this software carries
*  absolutely no warranty.  Jerry Smith disclaims all warranties for
*  this software, including any implied warranties of merchantability
*  and fitness, and shall not be liable for damages of any type
*  resulting from its use.
*  Permission to use, copy, modify, and distribute this source code
*  for any purpose and without fee is hereby granted, provided that
*  the above copyright and this permission notice appear in all
*  copies and supporting documentation, and provided that Jerry Smith
*  not be named in advertising or publicity pertaining to the
*  redistribution of this software without specific, written prior
*  permission.
**********************************************************************/

package yaak.util;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
* <p><code>StringDelimiter</code> separates a string into (string)
* tokens based on the presence of a multicharacter delimiter.
* <code>StringDelimiter</code> is an alternative to
* <code>java.util.StringTokenizer</code>.</p>
* @author Jerry Smith
* @version $Id: StringDelimiter.java 6 2005-06-08 17:00:15Z jsmith $
*/

public class StringDelimiter {
  private String string;
  private String delimiter;
  private int cursor, delimiterLen;

  /**
  * <p>Instantiates a <code>StringDelimiter</code> object and initializes
  * both the string (to be parsed) and the delimiter sequence for the
  * parsing operations.</p>
  * @param string The string that's parsed into tokens according
  * to the specified delimiter.
  * @param delimiter The delimiter string for parsing the
  * specified string into tokens.
  */

  public StringDelimiter(String string, String delimiter) {
    this.string = new String(string);
    this.delimiter = new String(delimiter);
    delimiterLen = delimiter.length();
    initialize();
  }

  /**
  * <p>The number of tokens in the string, based on the delimiter.</p>
  * @return The token count.
  */

  public int totalTokens() {
    int oldCursor = cursor;
    cursor = 0;
    int total = countTokens();
    cursor = oldCursor;
    return total;
  }

  /**
  * <p>The number of tokens remaining, based on the delimiter.</p>
  * @return The tokens remaining between the current
  * cursor (parsing marker) and the end of the string.
  */

  public int countTokens() {
    if (!stateOK()) {
      return 0;
    }
    int count = 0, i = string.indexOf(delimiter, cursor), j = 0;
    while (i != -1) {
      j = i;
      i = string.indexOf(delimiter, i + delimiterLen);
      count++;
    }
    if (j < string.length() && !string.substring(j).equals(delimiter)) {
      count++;
    }
    return count;
  }

  /**
  * <p>The end-of-parse-string test.</p>
  * @return Whether or not more tokens remain to
  * be parsed.
  */

  public boolean hasMoreTokens() {
    if (!stateOK()) {
      return false;
    }
    return string.indexOf(delimiter, cursor) != -1 ||
      cursor < string.length();
  }

  /**
  * <p>The end-of-parse-string test.</p>
  * @return Whether or not more tokens remain to
  * be parsed.
  */

  public boolean hasMoreElements() {
    return hasMoreTokens();
  }

  /**
  * <p>The next token, given the current parsing cursor, and the
  * configured (initialized) delimiter.</p>
  * @return The next token.
  * @throws NoSuchElementException The string is null or empty.
  */
 
  public String nextToken() {
    if (!stateOK()) {
      throw new NoSuchElementException(
        "StringDelimiter.nextToken(): null or empty string.");
    }
    int i = string.indexOf(delimiter, cursor);
    if (i == -1) {
      if (cursor < string.length()) {
        String token = string.substring(cursor);
        cursor = string.length();
        return token;
      }
      else {
        throw new NoSuchElementException(
          "StringDelimiter.nextToken(): no more tokens.");
      }
    }
    else {
      String token = string.substring(cursor, i);
      cursor = i + delimiterLen;
      return token;
    }
  }

  /**
  * <p>The next token, given the current parsing cursor, and the
  * newly configured delimiter.</p>
  * @param delimiter The new delimiter, which replaces
  * the delimiter specified during instantiation.
  * @return The next token.
  * @throws NoSuchElementException The string is null or empty.
  */
 
  public String nextToken(String delimiter) {
    if (!stateOK()) {
      throw new NoSuchElementException(
      "StringDelimiter.nextToken(): null or empty string or delimiter.");
    }
    this.delimiter = delimiter;
    delimiterLen = delimiter.length();
    return nextToken();
  }

  /**
  * <p>The next token, given the current parsing cursor, and the
  * configured (initialized) delimiter.</p>
  * @return The next token, as an <code>Object</code>.
  */
 
  public Object nextElement() {
    return (Object) nextToken();
  }

  /**
  * <p>Reset the parsing cursor to the beginning of the string.  Note:
  * This operation does <em>not</em> reset the delimiter sequence.</p>
  */
 
  protected void reset() {
    initialize();
  }

  private boolean stateOK() {
    return !(string == null || delimiter == null ||
      string.length() == 0 || delimiter.length() == 0 ||
      string.equals(delimiter));
  }

  private void initialize() {
    cursor = 0;
  }

/**************************************************************************
  public static void main(String args[]) {
    String test_str = "Java is a<L>great language.";
    StringDelimiter sd = new StringDelimiter(test_str, "<L>");
//    StringDelimiter sd = new StringDelimiter(test_str, "\n");
    StringTokenizer st = new StringTokenizer(test_str, "\n");
    System.out.println("The number of sd tokens is: " + sd.countTokens());
    while (sd.hasMoreTokens())
      System.out.println(sd.nextToken());
    System.out.println("The number of st tokens is: " + st.countTokens());
    while (st.hasMoreTokens())
      System.out.println(st.nextToken());
    System.out.println("THE END");
  }
**************************************************************************/
}

