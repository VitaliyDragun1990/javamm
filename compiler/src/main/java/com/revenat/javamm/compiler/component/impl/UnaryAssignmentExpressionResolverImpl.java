
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
import com.revenat.javamm.code.fragment.Parenthesis;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.expression.UnaryPostfixAssignmentExpression;
import com.revenat.javamm.code.fragment.expression.UnaryPrefixAssignmentExpression;
import com.revenat.javamm.code.fragment.expression.VariableExpression;
import com.revenat.javamm.code.fragment.operator.UnaryOperator;
import com.revenat.javamm.compiler.component.UnaryAssignmentExpressionResolver;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import static com.revenat.javamm.code.fragment.operator.UnaryOperator.DECREMENT;
import static com.revenat.javamm.code.fragment.operator.UnaryOperator.INCREMENT;
import static com.revenat.javamm.code.util.TypeUtils.confirmType;
import static com.revenat.javamm.compiler.component.impl.util.SyntaxValidationUtils.requireVariableExpression;

/**
 * @author Vitaliy Dragun
 *
 */
public class UnaryAssignmentExpressionResolverImpl implements UnaryAssignmentExpressionResolver {
    private static final Set<UnaryOperator> UNARY_ASSIGNMENT_OPERATORS = Set.of(
            INCREMENT,
            DECREMENT
    );

    @Override
    public List<Lexeme> resolve(final List<Lexeme> lexemes, final SourceLine sourceLine) {
        final List<Lexeme> result = new ArrayList<>();

        for (final ListIterator<Lexeme> original = lexemes.listIterator(); original.hasNext();) {
            processNextLexemeFrom(original, sourceLine, result);
        }

        return result;
    }

    private void processNextLexemeFrom(final ListIterator<Lexeme> lexemes,
                                       final SourceLine sourceLine,
                                       final List<Lexeme> result) {
        final Lexeme current = lexemes.next();

        if (lexemes.hasNext()) {
            processLexemePair(current, lexemes, result, sourceLine);
        } else if (isUnaryAssignmentOperator(current)) {
            throw syntaxError(sourceLine, "An argument is expected for unary operator: '%s'", current);

        } else {
            result.add(current);
        }
    }

    private void processLexemePair(final Lexeme current,
                                   final ListIterator<Lexeme> lexemes,
                                   final List<Lexeme> result,
                                   final SourceLine sourceLine) {
        final Lexeme next = lexemes.next();

        if (isUnaryAssignmentOperatorBeforeExpression(current, next)) {
            processUnaryAssignmentOperatorBeforeExpression((UnaryOperator) current, next, result, sourceLine);

        } else if (isExpressionBeforeUnaryAssignmentOperator(current, next)) {
            processExpressionBeforeUnaryAssignmentOperator(current, (UnaryOperator) next, result, sourceLine);

        } else if (isUnaryAssignmentOperatorBeforeOpeningParenthesis(current, next)) {
            processUnaryAssignmentOperatorBeforeOpeningParenthesis((UnaryOperator) current,
                                                                   next,
                                                                   lexemes,
                                                                   result,
                                                                   sourceLine);

        } else if (isExpressionBeforeClosingParenthesis(current, next)) {
            processExpressionBeforeClosingParenthesis(current, next, lexemes, result, sourceLine);

        } else {
            lexemes.previous();
            result.add(current);
        }
    }

    private void processUnaryAssignmentOperatorBeforeExpression(final UnaryOperator operator,
                                                                final Lexeme expression,
                                                                final List<Lexeme> result,
                                                                final SourceLine sourceLine) {
        final VariableExpression operand = requireVariableExpression(expression, operator, sourceLine);
        result.add(new UnaryPrefixAssignmentExpression(operand, operator));
    }

    private void processExpressionBeforeUnaryAssignmentOperator(final Lexeme expression,
                                                                final UnaryOperator operator,
                                                                final List<Lexeme> result,
                                                                final SourceLine sourceLine) {
        final VariableExpression operand = requireVariableExpression(expression, operator, sourceLine);
        result.add(new UnaryPostfixAssignmentExpression(operand, operator));
    }

    private void processUnaryAssignmentOperatorBeforeOpeningParenthesis(final UnaryOperator operator,
                                                                        final Lexeme parenthesis,
                                                                        final ListIterator<Lexeme> lexemes,
                                                                        final List<Lexeme> result,
                                                                        final SourceLine sourceLine) {
        Lexeme next = parenthesis;
        while (isOpeningParenthesis(next)) {
            result.add(next);
            next = lexemes.next();
        }

        final Lexeme agr = next;
        next = lexemes.next();

        if (isExpressionBeforeClosingParenthesis(agr, next)) {
            final VariableExpression operand = requireVariableExpression(agr, operator, sourceLine);
            result.add(new UnaryPrefixAssignmentExpression(operand, operator));
            lexemes.previous();

        } else {
            throw syntaxError(sourceLine, "Invalid argument for '%s' operator", operator);
        }
    }

    private void processExpressionBeforeClosingParenthesis(final Lexeme expression,
                                                           final Lexeme parenthesis,
                                                           final ListIterator<Lexeme> lexemes,
                                                           final List<Lexeme> result,
                                                           final SourceLine sourceLine) {
        final int expressionPosition = result.size();
        result.add(parenthesis);
        Lexeme next = lexemes.next();

        while (lexemes.hasNext() && isClosingParenthesis(next)) {
            result.add(next);
            next = lexemes.next();
        }

        if (isUnaryAssignmentOperator(next) && isOpeningParenthesis(result.get(expressionPosition - 1))) {
            final UnaryOperator operator = (UnaryOperator) next;
            final VariableExpression operand = requireVariableExpression(expression, operator, sourceLine);
            result.add(expressionPosition, new UnaryPostfixAssignmentExpression(operand, operator));

        } else if (isUnaryAssignmentOperator(next)) {
            throw syntaxError(sourceLine, "Invalid argument for '%s' operator", next);

        } else {
            result.add(expressionPosition, expression);
        }
    }

    private boolean isExpressionBeforeClosingParenthesis(final Lexeme current, final Lexeme next) {
        return isExpression(current) && isClosingParenthesis(next);
    }

    private boolean isUnaryAssignmentOperatorBeforeOpeningParenthesis(final Lexeme current, final Lexeme next) {
        return isUnaryAssignmentOperator(current) && isOpeningParenthesis(next);
    }

    private boolean isExpressionBeforeUnaryAssignmentOperator(final Lexeme current, final Lexeme next) {
        return isExpression(current) && isUnaryAssignmentOperator(next);
    }

    private boolean isUnaryAssignmentOperatorBeforeExpression(final Lexeme current, final Lexeme next) {
        return isUnaryAssignmentOperator(current) && isExpression(next);
    }

    private boolean isClosingParenthesis(final Lexeme next) {
        return next == Parenthesis.CLOSING_PARENTHESIS;
    }

    private boolean isOpeningParenthesis(final Lexeme next) {
        return next == Parenthesis.OPENING_PARENTHESIS;
    }

    private boolean isExpression(final Lexeme lexeme) {
        return confirmType(Expression.class, lexeme);
    }

    private boolean isUnaryAssignmentOperator(final Lexeme lexeme) {
        return UNARY_ASSIGNMENT_OPERATORS.contains(lexeme);
    }

    private JavammLineSyntaxError syntaxError(final SourceLine sourceLine, final String message, final Object... args) {
        return new JavammLineSyntaxError(sourceLine, message, args);
    }
}
