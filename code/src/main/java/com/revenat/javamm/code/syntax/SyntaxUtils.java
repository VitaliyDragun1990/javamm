
/*
 * Copyright (c) 2019. http://devonline.academy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.revenat.javamm.code.syntax;

import java.util.Set;
import java.util.stream.Stream;

import static com.revenat.javamm.code.syntax.Delimiters.IGNORED_DELIMITERS;
import static com.revenat.javamm.code.syntax.Delimiters.SIGNIFICANT_TOKEN_DELIMITERS;
import static com.revenat.javamm.code.syntax.Delimiters.STRING_DELIMITERS;

import static java.util.stream.Collectors.toUnmodifiableSet;

/**
 * Contains utility functions for syntax verification
 *
 * @author Vitaliy Dragun
 *
 */
public final class SyntaxUtils {

    private static final Set<Character> LATIN_CHARS_ONLY = Stream
            .concat(
                    Stream.iterate('a', ch -> ch != 'z' + 1, ch -> (char) (ch + 1)),
                    Stream.iterate('A', ch -> ch != 'Z' + 1, ch -> (char) (ch + 1))
            ).collect(toUnmodifiableSet());

    private static final Set<Character> DIGITS_ONLY =
            "0123456789".chars()
            .mapToObj(ch -> (char) ch)
            .collect(toUnmodifiableSet());

    private SyntaxUtils() {
    }

    public static boolean isValidSyntaxToken(final String token) {
        if (isAssumedStringLiteral(token)) {
            return true;
        } else if (isSignificantDelimiter(token)) {
            return true;
        } else {
            return verifyOnlyContainsAllowedSymbols(token);
        }
    }

    private static boolean isAssumedStringLiteral(final String token) {
        return STRING_DELIMITERS.contains(token.charAt(0));
    }

    public static boolean isSignificantDelimiter(final String token) {
        return SIGNIFICANT_TOKEN_DELIMITERS.contains(token);
    }

    public static String trimAllWhitespaceCharacters(final String line) {
        String result = line;
        while (containsLeadingIgnoredDelimiter(result) || containsTrailingIgnoredDelimiter(result)) {
            result = trimIgnoredDelimiters(result);
        }
        return result;
    }

    private static boolean containsLeadingIgnoredDelimiter(final String line) {
        return !line.isEmpty() && IGNORED_DELIMITERS.contains(line.charAt(0));
    }

    private static boolean containsTrailingIgnoredDelimiter(final String line) {
        return !line.isEmpty() && IGNORED_DELIMITERS.contains(line.charAt(line.length() - 1));
    }

    private static String trimIgnoredDelimiters(String line) {
        return line
            .trim()
            .replaceAll("(^\\h*)|(\\h*$)", "");
    }

    private static boolean verifyOnlyContainsAllowedSymbols(final String token) {
        return token.codePoints()
                .mapToObj(c -> (char) c)
                .allMatch(ch -> isLatinChar(ch) || isDigit(ch) || ch == '.');
    }

    public static boolean isLatinChar(final char ch) {
        return LATIN_CHARS_ONLY.contains(ch);
    }

    private static boolean isDigit(final char ch) {
        return DIGITS_ONLY.contains(ch);
    }
}
