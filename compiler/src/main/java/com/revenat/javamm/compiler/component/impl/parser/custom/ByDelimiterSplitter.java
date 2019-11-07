
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
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Splits specified source line using appropriate delimiters
 *
 * @author Vitaliy Dragun
 *
 */
class ByDelimiterSplitter {

    private final SignificantDelimiterParser delimiterParser = new SignificantDelimiterParser();

    List<String> splitByDelimiters(final String line) {
        return splitBySignificantDelimiters(splitByWhitespaceDelimiters(line));
    }

    private List<String> splitByWhitespaceDelimiters(final String line) {
        return Arrays.stream(line.trim().split("\\s+"))
                .filter(l -> !l.isEmpty())
                .collect(toList());
    }

    private List<String> splitBySignificantDelimiters(final List<String> tokens) {
        final List<String> result = new ArrayList<>();

        for (final String token : tokens) {
            result.addAll(delimiterParser.parseIfPresent(token));
        }

        return result;
    }
}
