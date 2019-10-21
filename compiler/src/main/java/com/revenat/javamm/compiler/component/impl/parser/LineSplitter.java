
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

package com.revenat.javamm.compiler.component.impl.parser;

import java.util.ArrayList;
import java.util.List;

import static com.revenat.javamm.code.syntax.SyntaxUtils.isSignificantDelimiter;

/**
 * Splits specified source line using appropriate delimiters
 *
 * @author Vitaliy Dragun
 *
 */
class LineSplitter {

    List<String> splitByDelimiters(final String line) {
        if (!line.isEmpty()) {
            final List<String> splittedByWhitespaces = splitByWhitespaceDelimiters(line);
            return splitBySignificantDelimiters(splittedByWhitespaces);
        }
        return List.of();
    }

    private List<String> splitByWhitespaceDelimiters(final String line) {
        return List.of(line.trim().split("\\s+"));
    }

    private List<String> splitBySignificantDelimiters(final List<String> tokens) {
        final List<String> result = new ArrayList<>();

        for (final String token : tokens) {
            if (!token.isEmpty()) {
                final int delimiterStartIndex = findDelimiterStartIndex(token);
                if (delimiterStartIndex != -1) {
                    final int delimiterEndIndex = findDelimiterEndIndex(token, delimiterStartIndex);
                    final String delimiterPart = token.substring(delimiterStartIndex, delimiterEndIndex);

                    result.addAll(splitBySignificantDelimiters(List.of(token.substring(0, delimiterStartIndex))));
                    result.addAll(parseDelimiters(delimiterPart));
                    result.addAll(splitBySignificantDelimiters(List.of(token.substring(delimiterEndIndex))));
                } else {
                    result.add(token);
                }
            }
        }

        return result;
    }

    private List<String> parseDelimiters(final String delimiterPart) {
        final List<String> delimiters = new ArrayList<>();

        parseDelimiters(delimiterPart, 0, delimiterPart.length(), delimiters);

        return delimiters;
    }

    private void parseDelimiters(final String delimiterPart, final int from, final int to, final List<String> result) {
        if (from >= to) {
            return;
        }

        final String delimiter = delimiterPart.substring(from, to);
        if (isSignificantDelimiter(delimiter)) {
            result.add(delimiter);
            parseDelimiters(delimiterPart, to, delimiterPart.length(), result);
        } else {
            parseDelimiters(delimiterPart, from, to - 1, result);
        }

    }

    private int findDelimiterStartIndex(final String token) {
        for (int i = 0; i < token.length(); i++) {
            if (isSignificantDelimiter(token.substring(i, i + 1))) {
                return i;
            }
        }
        return -1;
    }

    private int findDelimiterEndIndex(final String token, final int delimiterStartIndex) {
        if (delimiterStartIndex == -1) {
            return -1;
        }
        for (int i = delimiterStartIndex + 1; i < token.length(); i++) {
            if (!isSignificantDelimiter(token.substring(i, i + 1))) {
                return i;
            }
        }
        return token.length();
    }
}
