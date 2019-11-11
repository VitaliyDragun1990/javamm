
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

import com.revenat.javamm.code.fragment.Lexeme;
import com.revenat.javamm.code.fragment.Operator;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.expression.VariableExpression;
import com.revenat.javamm.code.syntax.Delimiters;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;

import static com.revenat.javamm.code.syntax.Delimiters.CLOSING_PARENTHESIS;
import static com.revenat.javamm.code.syntax.Delimiters.OPENING_CURLY_BRACE;
import static com.revenat.javamm.code.syntax.Delimiters.OPENING_PARENTHESIS;
import static com.revenat.javamm.code.syntax.Keywords.ALL_KEYWORDS;
import static com.revenat.javamm.code.util.TypeUtils.confirmType;

import static java.lang.Character.isLetter;

/**
 * @author Vitaliy Dragun
 *
 */
public final class SyntaxValidationUtils {

    public static final int MAX_ALLOWED_PARAMETER_COUNT = 5;

    private SyntaxValidationUtils() {
    }

    public static void validateThatLineEndsWithOpeningCurlyBrace(final SourceLine sourceLine) {
        if (!OPENING_CURLY_BRACE.equals(sourceLine.getLast())) {
            throw new JavammLineSyntaxError(sourceLine, "'%s' expected at the end of the line", OPENING_CURLY_BRACE);
        }
    }

    public static void validateThatLineContainsClosingCurlyBraceOnly(final SourceLine sourceLine) {
        if (!assertContainsOnly(sourceLine, Delimiters.CLOSING_CURLY_BRACE)) {
            throw new JavammLineSyntaxError(sourceLine, "'%s' expected only", Delimiters.CLOSING_CURLY_BRACE);
        }
    }

    public static void validateThatLineContainsOpeningCurlyBraceOnly(final SourceLine sourceLine) {
        if (!assertContainsOnly(sourceLine, OPENING_CURLY_BRACE)) {
            throw new JavammLineSyntaxError(sourceLine, "'%s' expected only", OPENING_CURLY_BRACE);
        }
    }

    public static void validateOpeningParenthesisAfterTokenInPosition(final SourceLine sourceLine,
                                                                      final String token,
                                                                      final int tokenPosition) {
        if (!OPENING_PARENTHESIS.equals(sourceLine.getToken(tokenPosition + 1))) {
            throw new JavammLineSyntaxError(sourceLine, "'%s' expected after '%s'", OPENING_PARENTHESIS, token);
        }
    }

    public static void validateClosingParenthesisBeforeOpeningCurlyBrace(final SourceLine sourceLine) {
        final String lastButOneToken = sourceLine.getToken(sourceLine.getTokenCount() - 2);
        if (!CLOSING_PARENTHESIS.equals(lastButOneToken)) {
            throw new JavammLineSyntaxError(sourceLine, "'%s' expected before '%s'",
                    CLOSING_PARENTHESIS, OPENING_CURLY_BRACE);
        }
    }

    public static void validateTokenRightBeforeOpeningCurlyBrace(final String expectedToken,
                                                                 final SourceLine sourceLine) {
        final String lastButOneToken = sourceLine.getToken(sourceLine.getTokenCount() - 2);
        if (!expectedToken.equals(lastButOneToken)) {
            throw new JavammLineSyntaxError(sourceLine, "'%s' expected before '%s'",
                    expectedToken, OPENING_CURLY_BRACE);
        }
    }

    public static void validateOneTokenAfterAnotherOne(final String expectedToken,
                                                       final String anotherToken,
                                                       final int anotherTokenPosition,
                                                       final SourceLine sourceLine) {
        if (!expectedToken.equals(sourceLine.getToken(anotherTokenPosition + 1))) {
            throw new JavammLineSyntaxError(sourceLine, "'%s' expected after '%s'", expectedToken, anotherToken);
        }
    }

    /**
     * Validates that first character of the provided {@code name} argument is a
     * letter.
     *
     * @throws JavammLineSyntaxError if validation fails
     */
    public static void validateThatFirstCharacterIsLetter(final LanguageFeature feature, final String name,
            final SourceLine sourceLine) {
        if (!isLetter(name.charAt(0))) {
            throw new JavammLineSyntaxError(sourceLine, "The %s name must start with letter: '%s'", feature.getName(),
                    name);
        }
    }

    /**
     * Validates that provided {@code name} argument is not a reserved keyword
     *
     * @throws JavammLineSyntaxError if validation fails
     */
    public static void validateThatNameIsNotKeyword(final LanguageFeature feature, final String name,
            final SourceLine sourceLine) {
        if (ALL_KEYWORDS.contains(name)) {
            throw new JavammLineSyntaxError(sourceLine, "The keyword '%s' can not be used as a %s name", name,
                    feature.getName());
        }
    }

    public static VariableExpression requireVariableExpression(final Lexeme lexeme,
                                                               final Operator operator,
                                                               final SourceLine sourceLine) {
        if (confirmType(VariableExpression.class, lexeme)) {
            return (VariableExpression) lexeme;
        } else {
            throw new JavammLineSyntaxError(sourceLine, "A variable expression is expected for %s operator: '%s'",
                    operator.getType(), operator);
        }
    }

    private static boolean assertContainsOnly(final SourceLine sourceLine, final String expectedToken) {
        return sourceLine.getTokenCount() == 1 && expectedToken.equals(sourceLine.getFirst());
    }

    public enum LanguageFeature {

        VARIABLE,

        FUNCTION;

        public String getName() {
            return name().toLowerCase();
        }
    }
}
