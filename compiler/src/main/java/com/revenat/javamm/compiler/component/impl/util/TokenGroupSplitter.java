
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

package com.revenat.javamm.compiler.component.impl.util;

import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import static com.revenat.javamm.compiler.component.impl.util.SyntaxParseUtils.isMatchingBrackets;
import static com.revenat.javamm.compiler.component.impl.util.SyntaxParseUtils.isOpeningBracket;
import static java.util.Collections.unmodifiableList;

/**
 * Splits tokens into groups using specified delimiter
 *
 * @author Vitaliy Dragun
 */
class TokenGroupSplitter {

    private final String delimiter;

    TokenGroupSplitter(final String delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * Splits specified {@code tokens} into groups of tokens separated by delimiter.
     */
    List<List<String>> split(final List<String> tokens, final SourceLine sourceLine) {
        return tokens.isEmpty() ? unmodifiableList(new ArrayList<>()) : splitTokensIntoGroups(tokens, sourceLine);
    }

    private List<List<String>> splitTokensIntoGroups(final List<String> tokens, final SourceLine sourceLine) {
        final List<List<String>> groups = new ArrayList<>();

        List<String> group = new ArrayList<>();
        final ListIterator<String> iter = tokens.listIterator();
        while (iter.hasNext()) {
            final String token = iter.next();

            if (isOpeningBracket(token)) {
                group.add(token);
                addToGroupUntilMatchingBracketFound(iter, group, token);
            } else if (isDelimiter(token)) {
                groups.add(unmodifiableList(requireNotEmpty(group, sourceLine)));
                group = new ArrayList<>();
            } else {
                group.add(token);
            }
        }
        groups.add(unmodifiableList(requireNotEmpty(group, sourceLine)));

        return unmodifiableList(groups);
    }

    private boolean isDelimiter(final String token) {
        return delimiter.equals(token);
    }

    private void addToGroupUntilMatchingBracketFound(final ListIterator<String> tokens,
                                                     final List<String> group,
                                                     final String openingBracket) {
        while (tokens.hasNext()) {
            final String token = tokens.next();
            group.add(token);

            if (openingBracket.equals(token)) {
                addToGroupUntilMatchingBracketFound(tokens, group, token);
            } else if (isMatchingBrackets(openingBracket, token)) {
                break;
            }
        }
    }

    private List<String> requireNotEmpty(final List<String> group, final SourceLine sourceLine) {
        if (group.isEmpty()) {
            throw missingValueOrRedundantDelimiterError(sourceLine);
        }
        return group;
    }

    private JavammLineSyntaxError missingValueOrRedundantDelimiterError(final SourceLine sourceLine) {
        return new JavammLineSyntaxError(sourceLine, "Missing value or redundant '%s'", delimiter);
    }
}
