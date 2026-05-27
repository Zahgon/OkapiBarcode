/*
 * Copyright 2018 Daniel Gredler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.org.okapibarcode.util;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import java.nio.charset.StandardCharsets;
import uk.org.okapibarcode.backend.OkapiInputException;

/**
 * String utility class.
 *
 * @author Daniel Gredler
 */
public final class Strings {

    private Strings() {
        // utility class
    }

    /**
     * Replaces raw values with special placeholders, where applicable.
     *
     * @param s the string to add placeholders to
     * @return the specified string, with placeholders added
     * @see <a href="http://www.zint.org.uk/Manual.aspx?type=p&page=4">Zint placeholders</a>
     * @see #unescape(String, boolean)
     */
    public static String escape(String s) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Replaces any special placeholders with their raw values (not including FNC values).
     *
     * @param s the string to check for placeholders
     * @param lenient whether or not to be lenient with unrecognized escape sequences
     * @return the specified string, with placeholders replaced
     * @see <a href="http://www.zint.org.uk/Manual.aspx?type=p&page=4">Zint placeholders</a>
     * @see #escape(String)
     */
    public static String unescape(String s, boolean lenient) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    private static boolean isHex(char c) {
        return (c >= '0' && c <= '9') || (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f');
    }

    /**
     * Deletes the last line in the specified string, assuming that it contains a trailing new line character.
     *
     * @param s the string to modify
     * @return the modified string
     */
    public static StringBuilder deleteLastLine(StringBuilder s) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Appends the specific integer to the specified string, in binary format, padded to the specified number of digits.
     *
     * @param s the string to append to
     * @param value the value to append, in binary format
     * @param digits the number of digits to pad to
     */
    public static void binaryAppend(StringBuilder s, int value, int digits) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Replaces non-ASCII and non-printable characters with their Unicode-escaped equivalent.
     *
     * @param s the input string
     * @return the input string, with non-ASCII and non-printable characters replaced
     */
    public static String toPrintableAscii(String s) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Counts the number of occurrences of the specified substring within the specified string.
     *
     * @param s the string to search within
     * @param substring the substring to search for
     * @return the number of occurrences of the specified substring within the specified string
     */
    public static int count(String s, String substring) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }
}
