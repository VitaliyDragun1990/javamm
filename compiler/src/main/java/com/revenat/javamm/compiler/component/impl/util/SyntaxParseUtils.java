
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

import java.util.Iterator;
import java.util.List;

import static com.revenat.javamm.code.syntax.Delimiters.CLOSING_CURLY_BRACE;
import static com.revenat.javamm.code.syntax.Delimiters.CLOSING_PARENTHESIS;
import static com.revenat.javamm.code.syntax.Delimiters.CLOSING_SQUARE_BRACKET;
import static com.revenat.javamm.code.syntax.Delimiters.OPENING_CURLY_BRACE;
import static com.revenat.javamm.code.syntax.Delimiters.OPENING_PARENTHESIS;
import static com.revenat.javamm.code.syntax.Delimiters.OPENING_SQUARE_BRACKET;


/**
 * @author Vitaliy Dragun
 */
public final class SyntaxParseUtils {

    private static final TokenGroupSplitter TOKEN_GROUP_SPLITTER = new TokenGroupSplitter(",");

    private SyntaxParseUtils() {
    }

    public static List<String> getTokensBetweenBrackets(final String openingBracket,
                                                        final String closingBracket,
                                                        final SourceLine sourceLine,
                                                        final boolean allowEmptyResult) {
        return getTokensBetweenBrackets(
            openingBracket, closingBracket, sourceLine.getTokens(), sourceLine, allowEmptyResult
        );
    }

    public static List<String> getTokensBetweenBrackets(final String openingBracket,
                                                        final String closingBracket,
                                                        final List<String> tokens,
                                                        final SourceLine sourceLine,
                                                        final boolean allowEmptyResult) {
        return new SimpleTokensBetweenBracketsExtractor(openingBracket, closingBracket, allowEmptyResult, sourceLine)
            .extract(tokens);
    }

    public static List<String> getTokensUntilClosingBracketIsMet(final String openingBracket,
                                                                 final String closingBracket,
                                                                 final Iterator<String> tokens,
                                                                 final SourceLine sourceLine,
                                                                 final boolean allowEmptyResult) {
        return new AdvancedTokensBetweenBracketsExtractor(openingBracket, closingBracket)
            .extract(tokens, sourceLine, allowEmptyResult);
    }

    public static boolean isClosingBlockOperation(final SourceLine sourceLine) {
        return "}".equals(sourceLine.getFirst());
    }

    public static List<List<String>> groupTokensByComma(final List<String> tokens, final SourceLine sourceLine) {
        return TOKEN_GROUP_SPLITTER.split(tokens, sourceLine);
    }

    public static boolean isOpeningBracket(final String token) {
        return OPENING_CURLY_BRACE.equals(token) ||
            OPENING_PARENTHESIS.equals(token) ||
            OPENING_SQUARE_BRACKET.equals(token);
    }

    public static boolean isMatchingBrackets(final String tokenA, final String tokenB) {
        return isMatchingParentheses(tokenA, tokenB) ||
            isMatchingSquareBrackets(tokenA, tokenB) ||
            isMatchingCurlyBraces(tokenA, tokenB);
    }

    private static boolean isMatchingParentheses(final String tokenA, final String tokenB) {
        return OPENING_PARENTHESIS.equals(tokenA) && CLOSING_PARENTHESIS.equals(tokenB);
    }

    private static boolean isMatchingSquareBrackets(final String tokenA, final String tokenB) {
        return OPENING_SQUARE_BRACKET.equals(tokenA) && CLOSING_SQUARE_BRACKET.equals(tokenB);
    }

    private static boolean isMatchingCurlyBraces(final String tokenA, final String tokenB) {
        return OPENING_CURLY_BRACE.equals(tokenA) && CLOSING_CURLY_BRACE.equals(tokenB);
    }
}
