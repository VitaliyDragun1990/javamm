
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

package com.revenat.javamm.compiler.component.impl;

import com.revenat.javamm.code.fragment.Expression;
import com.revenat.javamm.code.fragment.Lexeme;
import com.revenat.javamm.code.fragment.Operator;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.operator.BinaryOperator;
import com.revenat.javamm.code.fragment.operator.UnaryOperator;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;

import java.util.List;

import static com.revenat.javamm.code.fragment.Parenthesis.CLOSING_PARENTHESIS;
import static com.revenat.javamm.code.fragment.Parenthesis.OPENING_PARENTHESIS;
import static com.revenat.javamm.code.util.TypeUtils.confirmType;

/**
 * Validates that specified lexemes are in valid order
 *
 * @author Vitaliy Dragun
 *
 */
final class LexemesOrderValidator {

    private final List<Lexeme> lexemes;

    private final SourceLine sourceLine;

    private Lexeme firstExprassionWithoutBinaryOperator;

    private LexemesOrderValidator(final List<Lexeme> lexemes,
                          final SourceLine sourceLine) {
        this.lexemes = lexemes;
        this.sourceLine = sourceLine;
        this.firstExprassionWithoutBinaryOperator = null;
    }

    static LexemesOrderValidator validator(final List<Lexeme> lexemes, final SourceLine sourceLine) {
        return new LexemesOrderValidator(lexemes, sourceLine);
    }

    public void validate() {
        validateFirstLexemeNotBinaryOperator();
        validateLastLexemeNotOperator();

        validateLexemesOrder();
    }

    private void validateFirstLexemeNotBinaryOperator() {
        final Lexeme firstLexeme = lexemes.get(0);
        if (isBinaryOperator(firstLexeme)) {
            throw syntaxError("Expression can not start with binary operator: '%s'", firstLexeme);
        }
    }

    private void validateLastLexemeNotOperator() {
        final Lexeme lastLexeme = lexemes.get(lexemes.size() - 1);
        if (isOperator(lastLexeme)) {
            final Operator operator = (Operator) lastLexeme;
            throw syntaxError("Expression can not end with %s operator: '%s'",
                    operator.getType(), operator);
        }
    }

    private void validateLexemesOrder() {
        final Lexeme firstLexeme = lexemes.get(0);
        firstExprassionWithoutBinaryOperator = isExpression(firstLexeme) ? firstLexeme : null;

        for (int i = 0; i < lexemes.size() - 1; i++) {
            final Lexeme current = lexemes.get(i);
            final Lexeme next = lexemes.get(i + 1);

            assertNoExpressionsWithoutBinary(next);

            assertNoSequentialBinaryOperators(current, next);
            assertNoOperatorWithoutExpression(current, next);
            assertCorreclyPlacedParentheses(current, next);
        }
    }

    private void assertNoExpressionsWithoutBinary(final Lexeme lexemeToCheck) {
        if (isExpression(lexemeToCheck) && firstExprassionWithoutBinaryOperator != null) {
            throw syntaxError("A binary operator is expected between expressions: '%s' and '%s'",
                    firstExprassionWithoutBinaryOperator, lexemeToCheck);
        } else if (isExpression(lexemeToCheck)) {
            firstExprassionWithoutBinaryOperator = lexemeToCheck;
        } else if (isBinaryOperator(lexemeToCheck)) {
            firstExprassionWithoutBinaryOperator = null;
        }
    }

    private void assertCorreclyPlacedParentheses(final Lexeme current,
                                                 final Lexeme next) {
        if (isOpeningParenthesis(current) && isClosingParenthesis(next)) {
            throw syntaxError("Parenthesis are incorrectly placed");
        }
        if (isClosingParenthesis(current) && isOpeningParenthesis(next)) {
            throw syntaxError("Parenthesis are incorrectly placed");
        }
    }

    private void assertNoOperatorWithoutExpression(final Lexeme current,
                                                   final Lexeme next) {
        if (isOpeningParenthesis(current) && isBinaryOperator(next)) {
            throw syntaxError("An expression is expected for binary operator: '%s'", next);
        }
        if (isUnaryOperator(current) && isBinaryOperator(next)) {
            throw syntaxError("An expression is expected for binary operator: '%s'", next);
        }
        if (isOperator(current) && isClosingParenthesis(next)) {
            throw syntaxError("An expression is expected for %s operator: '%s'",
                    ((Operator) current).getType(), current);
        }
    }

    private void assertNoSequentialBinaryOperators(final Lexeme current,
                                                   final Lexeme next) {
        if (isBinaryOperator(current) && isBinaryOperator(next)) {
            throw syntaxError("An expression is expected between binary operators: '%s' and '%s'",
                    current, next);
        }
    }

    private boolean isExpression(final Lexeme lexeme) {
        return confirmType(Expression.class, lexeme);
    }

    private boolean isOperator(final Lexeme lexeme) {
        return confirmType(Operator.class, lexeme);
    }

    private boolean isBinaryOperator(final Lexeme lexeme) {
        return confirmType(BinaryOperator.class, lexeme);
    }

    private boolean isUnaryOperator(final Lexeme lexeme) {
        return confirmType(UnaryOperator.class, lexeme);
    }

    private boolean isOpeningParenthesis(final Lexeme lexeme) {
        return lexeme == OPENING_PARENTHESIS;
    }

    private boolean isClosingParenthesis(final Lexeme lexeme) {
        return lexeme == CLOSING_PARENTHESIS;
    }

    private JavammLineSyntaxError syntaxError(final String msgTemplate,
                                              final Object... args) {
        return new JavammLineSyntaxError(sourceLine, msgTemplate, args);
    }
}
