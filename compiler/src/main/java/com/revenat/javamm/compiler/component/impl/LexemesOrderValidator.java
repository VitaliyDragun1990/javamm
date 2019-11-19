
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

import com.revenat.javamm.code.fragment.Lexeme;
import com.revenat.javamm.code.fragment.Operator;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.compiler.component.OperatorPrecedenceResolver;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;

import java.util.List;

import static com.revenat.javamm.code.util.LexemeUtils.isBinaryAssignmentOperator;
import static com.revenat.javamm.code.util.LexemeUtils.isBinaryOperator;
import static com.revenat.javamm.code.util.LexemeUtils.isClosingParenthesis;
import static com.revenat.javamm.code.util.LexemeUtils.isExpression;
import static com.revenat.javamm.code.util.LexemeUtils.isOpeningParenthesis;
import static com.revenat.javamm.code.util.LexemeUtils.isOperator;
import static com.revenat.javamm.code.util.LexemeUtils.isUnaryOperator;
import static com.revenat.javamm.compiler.component.impl.util.SyntaxValidationUtils.requireVariableExpression;

/**
 * Validates that specified lexemes are in valid order
 *
 * @author Vitaliy Dragun
 *
 */
final class LexemesOrderValidator {

    private final OperatorPrecedenceResolver operatorPrecedenceResolver;

    private final List<Lexeme> lexemes;

    private final SourceLine sourceLine;

    private Lexeme expressionWithoutBinary;

    private Lexeme lastCheckedExpression;

    private Lexeme lastValidatedLexeme;

    private LexemesOrderValidator(final OperatorPrecedenceResolver operatorPrecedenceResolver,
                                  final List<Lexeme> lexemes,
                                  final SourceLine sourceLine) {
        this.operatorPrecedenceResolver = operatorPrecedenceResolver;
        this.lexemes = lexemes;
        this.sourceLine = sourceLine;
        this.expressionWithoutBinary = null;
        this.lastCheckedExpression = null;
        this.lastValidatedLexeme = null;
    }

    static LexemesOrderValidator validator(final OperatorPrecedenceResolver operatorPrecedenceResolver,
                                           final List<Lexeme> lexemes,
                                           final SourceLine sourceLine) {
        return new LexemesOrderValidator(operatorPrecedenceResolver, lexemes, sourceLine);
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
        expressionWithoutBinary = isExpression(firstLexeme) ? firstLexeme : expressionWithoutBinary;

        for (int i = 0; i < lexemes.size() - 1; i++) {
            final Lexeme current = lexemes.get(i);
            final Lexeme next = lexemes.get(i + 1);

            assertNoExpressionsWithoutBinaryInBetween(next);

            assertNoSequentialBinaryOperators(current, next);
            assertNoOperatorWithoutExpression(current, next);
            assertCorrectlyPlacedParentheses(current, next);
            requireVariableExpressionForBinaryAssignmentOperator(current, next);

            lastValidatedLexeme = current;
        }
    }

    private void requireVariableExpressionForBinaryAssignmentOperator(final Lexeme current, final Lexeme next) {
        lastCheckedExpression = isExpression(current) ? current : lastCheckedExpression;

        if (isBinaryAssignmentOperator(next) && isPreviousLexemeNotOperatorWithHigherPrecedence(next)) {
            requireVariableExpression(lastCheckedExpression, (Operator) next, sourceLine);
        } else if (isBinaryAssignmentOperator(next)) {
            throw syntaxError("A variable expression is expected for binary operator: '%s'", next);
        }
    }

    private boolean isPreviousLexemeNotOperatorWithHigherPrecedence(final Lexeme operator) {
        return !isPreviousLexemeIsOperatorWithHigherPrecedence(operator);
    }

    private boolean isPreviousLexemeIsOperatorWithHigherPrecedence(final Lexeme operator) {
        return lastValidatedLexeme != null &&
                isOperator(lastValidatedLexeme) &&
                operatorPrecedenceResolver.hasLowerPrecedence((Operator) operator, (Operator) lastValidatedLexeme);
    }

    private void assertNoExpressionsWithoutBinaryInBetween(final Lexeme lexemeToCheck) {
        if (isExpression(lexemeToCheck) && expressionWithoutBinary != null) {
            throw syntaxError("A binary operator is expected between expressions: '%s' and '%s'",
                    expressionWithoutBinary, lexemeToCheck);
        } else if (isExpression(lexemeToCheck)) {
            expressionWithoutBinary = lexemeToCheck;
        } else if (isBinaryOperator(lexemeToCheck)) {
            expressionWithoutBinary = null;
        }
    }

    private void assertCorrectlyPlacedParentheses(final Lexeme current,
                                                  final Lexeme next) {
        if (isOpeningParenthesis(current) && isClosingParenthesis(next)) {
            throw syntaxError("Parentheses are incorrectly placed");
        }
        if (isClosingParenthesis(current) && isOpeningParenthesis(next)) {
            throw syntaxError("Parentheses are incorrectly placed");
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

    private JavammLineSyntaxError syntaxError(final String msgTemplate,
                                              final Object... args) {
        return new JavammLineSyntaxError(sourceLine, msgTemplate, args);
    }
}
