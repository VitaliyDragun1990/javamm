
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

package com.revenat.javamm.compiler.component.impl.parser.custom;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.revenat.javamm.code.syntax.SyntaxUtils.isSignificantDelimiter;

/**
 * @author Vitaliy Dragun
 *
 */
class SignificantDelimiterParser {

    List<String> parseIfPresent(final String line) {
        if (isSignificantDelimiterPresent(line)) {
            return process(line);
        }
        return List.of(line);
    }

    private List<String> process(final String line) {
        final int from = findDelimiterStartIndex(line);
        final int to = findDelimiterEndIndex(line, from);
        final String delimiterPart = line.substring(from, to);
        return Stream
                .of(Stream.of(line.substring(0, from)),
                    parse(delimiterPart).stream(),
                    parseIfPresent(line.substring(to)).stream())
                .flatMap(Function.identity())
                .filter(t -> !t.isEmpty())
                .collect(Collectors.toList());
    }

    private List<String> parse(final String delimiters) {
        final List<String> result = new ArrayList<>();

        parseDelimiters(delimiters, 0, delimiters.length(), result);

        return result;
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

    private boolean isSignificantDelimiterPresent(final String line) {
        return findDelimiterStartIndex(line) != -1;
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
        for (int i = delimiterStartIndex + 1; i < token.length(); i++) {
            if (!isSignificantDelimiter(token.substring(i, i + 1))) {
                return i;
            }
        }
        return token.length();
    }
}
