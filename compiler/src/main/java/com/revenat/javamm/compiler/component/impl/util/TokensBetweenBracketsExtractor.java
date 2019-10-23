
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

import java.util.List;

/**
 * @author Vitaliy Dragun
 *
 */
class TokensBetweenBracketsExtractor {

    private static final String MISSING_BRACKET_TEMPLATE = "Missing %s";

    private final String openingBracket;

    private final String closingBracket;

    private final SourceLine sourceLine;

    private final boolean allowEmptyResult;

    TokensBetweenBracketsExtractor(final String openingBracket,
                                   final String closingBracket,
                                   final boolean allowEmptyResult,
                                   final SourceLine sourceLine) {
        this.openingBracket = openingBracket;
        this.closingBracket = closingBracket;
        this.allowEmptyResult = allowEmptyResult;
        this.sourceLine = sourceLine;
    }

    List<String> extract(final List<String> tokens) {
        final int openingBracketPosition = getOpeningBracketPosition(tokens);
        final int closingBracketsPosition = getClosingBracketPosition(tokens);

        validateBracketsRelativePosition(openingBracketPosition, closingBracketsPosition);

        return getTokensBetweenBrackets(tokens, openingBracketPosition, closingBracketsPosition);

    }

    private int getOpeningBracketPosition(final List<String> tokens) {
        final int openingBracketsPosition = tokens.indexOf(openingBracket);

        if (openingBracketsPosition == -1) {
            throw syntaxError(MISSING_BRACKET_TEMPLATE, openingBracket);
        }

        return openingBracketsPosition;
    }

    private int getClosingBracketPosition(final List<String> tokens) {
        final int closingBracketsPosition = tokens.lastIndexOf(closingBracket);

        if (closingBracketsPosition == -1) {
            throw syntaxError(MISSING_BRACKET_TEMPLATE, closingBracket);
        }

        return closingBracketsPosition;
    }

    private void validateBracketsRelativePosition(final int openingBracketPosition, final int closingBracketsPosition) {
        if (openingBracketPosition > closingBracketsPosition) {
            throw syntaxError("Expected %s before %s", openingBracket, closingBracket);
        }
    }

    private List<String> getTokensBetweenBrackets(final List<String> tokens,
                                                  final int openingBracketPosition,
                                                  final int closingBracketsPosition) {
        final List<String> tokensBetweenBrackets = tokens.subList(openingBracketPosition + 1, closingBracketsPosition);

        validateSize(tokensBetweenBrackets);

        return List.copyOf(tokensBetweenBrackets);
    }

    private void validateSize(final List<String> tokensBetweenBrackets) {
        if (!allowEmptyResult && tokensBetweenBrackets.isEmpty()) {
            throw syntaxError("An expression is expected between %s and %s",
                    openingBracket, closingBracket);
        }
    }

    private JavammLineSyntaxError syntaxError(final String message,
                                              final Object...args) {
        return new JavammLineSyntaxError(sourceLine, message, args);
    }
}
