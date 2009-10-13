package yaak.util;

/**
* <p><code>StringUtil</code> provides class-level convenience methods for
* strings.</p>
* @author Jerry Smith
* @version $Id: StringUtil.java 6 2005-06-08 17:00:15Z jsmith $
*/

public final class StringUtil {
  private StringUtil() {
  }

  /**
  * <p>Accepts a primary string and an alternate/default string.  If the
  * first/primary string is not null and has positive length,
  * <code>checkValue()</code> simply returns it; otherwise, it returns the
  * alternate/default string.</p>
  * @param str The primary string.
  * @param alternateStr The alternate/default/back-up/substitution
  * string.
  * @return The primary string if it has content,
  * otherwise the alternate string.
  */

  public static String checkValue(String str, String alternateStr) {
    return !(str == null || str.length() == 0) ?
      str : alternateStr;
  }

  /**
  * <p>Tests a string to determine if it has non-zero length, guarding
  * against null values as well.</p>
  * @param str The string.
  * @return Whether or not the string is both
  * non-null and has length greater than zero.
  */

  public static boolean hasValue(String str) {
    return !(str == null || str.length() == 0);
  }

  /**
  * <p>Accepts a primary string and two substitution-related strings.  It
  * replaces every occurrence of the first substitution string with the
  * second throughout the primary string.</p>
  * @param src The primary string.
  * @param oldStr The string that will be replaced.
  * @param newStr The replacing string.
  * @return The modified primary string.
  */

  public static String replace(String src, String oldStr, String newStr) {
    StringBuffer result = new StringBuffer(src.length() + 50);
    int startIndex = 0;
    int endIndex = src.indexOf(oldStr);
    while(endIndex != -1) {
      result.append(src.substring(startIndex,endIndex));
      result.append(newStr);
      startIndex = endIndex + oldStr.length();
      endIndex = src.indexOf(oldStr,startIndex);
    }
    result.append(src.substring(startIndex,src.length()));
    return result.toString();
  }

  /**
  * <p>Removes the specified substring from the primary string.</p>
  * @param data The primary string.
  * @param subData The substring, all occurences of which are
  * removed from the primary string.
  * @return The modified primary string.
  */

  public static String stripSubstring(String data, String subData) {
    int dataLen, bufferOffset, dataOffset, offset;
    char[] buffer = new char[data.length()];
    dataLen = data.length();
    dataOffset = 0;
    bufferOffset = 0;
    offset = searchForwardInsensitive(data, subData, dataOffset);
    while (offset != -1) {
      data.getChars(dataOffset, offset, buffer, bufferOffset);
      bufferOffset += offset - dataOffset;
      dataOffset = offset + subData.length();
      offset = searchForwardInsensitive(data, subData, dataOffset);
    }
    data.getChars(dataOffset, dataLen, buffer, bufferOffset);
    bufferOffset += dataLen - dataOffset;
    return new String(buffer, 0, bufferOffset);
  }

  /**
  * <p>Searches forward in the primary string beginning at the specified
  * offset for the first occurrence of the search string.</p>
  * @param str The primary string.
  * @param searchText The search pattern.
  * @param offset The offset in the primary string where the
  * search begins.
  * @return The zero-based offset where the matching
  * search pattern begins, or -1 if the pattern is not found.
  */

  public static int searchForwardInsensitive(String str, String searchText,
      int offset) {
    int strLen = str.length();
    int searchLen = searchText.length();
    int beginMatchOffset = offset;

    if (strLen == 0 || searchLen == 0) {
      return -1;
    }
    char[] buffer = new char[strLen];
    str.getChars(0, strLen, buffer, 0);
    char[] pattern = new char[searchLen];
    searchText.getChars(0, searchLen, pattern, 0);
    for (int next = 0, i = offset; i < strLen; i++)
      if (getUpper(buffer[i]) == getUpper(pattern[next])) {
        beginMatchOffset = i;
        while (next < searchLen &&
            getUpper(buffer[i++]) == getUpper(pattern[next++])) {
          ; // scan...
        }
        if (next == searchLen &&
            getUpper(buffer[--i]) == getUpper(pattern[--next])) {
          return beginMatchOffset;
        }
        i = beginMatchOffset;
        next = 0;
      }
    return -1;
  }

  private static char getUpper(char c) {
    return Character.isLowerCase(c) ? Character.toUpperCase(c) : c;
  }
}
