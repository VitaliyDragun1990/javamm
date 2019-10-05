
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
import com.revenat.javamm.compiler.component.ComplexLexemeValidator;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;

import java.util.List;

import static com.revenat.javamm.code.fragment.Parenthesis.CLOSING_PARENTHESIS;
import static com.revenat.javamm.code.fragment.Parenthesis.OPENING_PARENTHESIS;
import static com.revenat.javamm.code.util.TypeUtils.confirmType;

/**
 * @author Vitaliy Dragun
 *
 */
public class ComplexLexemeValidatorImpl implements ComplexLexemeValidator {

    @Override
    public void validate(final List<Lexeme> lexemes, final SourceLine sourceLine) {
        requireNotEmpty(lexemes);
        validateFirstLexemeNotBinaryOperator(lexemes, sourceLine);
        validateLastLexemeNotOperator(lexemes, sourceLine);

        validateLexemesPlacedInCorrectSequence(lexemes, sourceLine);
    }

    private void requireNotEmpty(final List<Lexeme> lexemes) {
        if (lexemes.isEmpty()) {
            throw new IllegalArgumentException("Found no lexemes to validate");
        }
    }

    private void validateFirstLexemeNotBinaryOperator(final List<Lexeme> lexemes, final SourceLine sourceLine) {
        final Lexeme firstLexeme = lexemes.get(0);
        if (isBinaryOperator(firstLexeme)) {
            throw syntaxError(sourceLine, "Expression can not start with binary operator: '%s'", firstLexeme);
        }
    }

    private void validateLastLexemeNotOperator(final List<Lexeme> lexemes, final SourceLine sourceLine) {
        final Lexeme lastLexeme = lexemes.get(lexemes.size() - 1);
        if (isOperator(lastLexeme)) {
            final Operator operator = (Operator) lastLexeme;
            throw syntaxError(sourceLine, "Expression can not end with %s operator: '%s'",
                    operator.getType(), operator);
        }
    }

    private void validateLexemesPlacedInCorrectSequence(final List<Lexeme> lexemes, final SourceLine sourceLine) {
        Lexeme expressionWithoutBinary = isExpression(lexemes.get(0)) ? lexemes.get(0) : null;
        for (int i = 0; i < lexemes.size() - 1; i++) {
            final Lexeme current = lexemes.get(i);
            final Lexeme next = lexemes.get(i + 1);

            expressionWithoutBinary = assertNoExpressionsWithoutBinary(next, expressionWithoutBinary, sourceLine);

            assertNoSequentialBinaryOperators(current, next, sourceLine);
            assertNoOperatorWithoutExpression(current, next, sourceLine);
            assertCorreclyPlacedParentheses(current, next, sourceLine);
        }
    }

    private Lexeme assertNoExpressionsWithoutBinary(final Lexeme lexemeToCheck,
                                                    final Lexeme expressionWithoutBinary,
                                                    final SourceLine sourceLine) {
        if (isExpression(lexemeToCheck) && expressionWithoutBinary != null) {
            throw syntaxError(sourceLine, "A binary operator is expected between expressions: '%s' and '%s'",
                    expressionWithoutBinary, lexemeToCheck);
        } else if (isExpression(lexemeToCheck)) {
            return lexemeToCheck;
        } else if (isBinaryOperator(lexemeToCheck)) {
            return null;
        } else {
            return expressionWithoutBinary;
        }
    }

    private void assertCorreclyPlacedParentheses(final Lexeme current,
                                                 final Lexeme next,
                                                 final SourceLine sourceLine) {
        if (isOpeningParenthesis(current) && isClosingParenthesis(next)) {
            throw syntaxError(sourceLine, "Parenthesis are incorrectly placed");
        }
        if (isClosingParenthesis(current) && isOpeningParenthesis(next)) {
            throw syntaxError(sourceLine, "Parenthesis are incorrectly placed");
        }
    }

    private void assertNoOperatorWithoutExpression(final Lexeme current,
                                                   final Lexeme next,
                                                   final SourceLine sourceLine) {
        if (isOpeningParenthesis(current) && isBinaryOperator(next)) {
            throw syntaxError(sourceLine, "An expression is expected for binary operator: '%s'", next);
        }
        if (isUnaryOperator(current) && isBinaryOperator(next)) {
            throw syntaxError(sourceLine, "An expression is expected for binary operator: '%s'", next);
        }
        if (isOperator(current) && isClosingParenthesis(next)) {
            throw syntaxError(sourceLine, "An expression is expected for %s operator: '%s'",
                    ((Operator) current).getType(), current);
        }
    }

    private void assertNoSequentialBinaryOperators(final Lexeme current,
                                                   final Lexeme next,
                                                   final SourceLine sourceLine) {
        if (isBinaryOperator(current) && isBinaryOperator(next)) {
            throw syntaxError(sourceLine, "An expression is expected between binary operators: '%s' and '%s'",
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

    private JavammLineSyntaxError syntaxError(final SourceLine sourceLine,
                                              final String msgTemplate,
                                              final Object... args) {
        return new JavammLineSyntaxError(sourceLine, msgTemplate, args);
    }
}
