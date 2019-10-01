
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

package com.revenat.javamm.compiler.component.impl.expression.builder;

import com.revenat.javamm.code.fragment.Lexeme;
import com.revenat.javamm.code.fragment.Operator;
import com.revenat.javamm.code.fragment.Parenthesis;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.expression.ComplexExpression;
import com.revenat.javamm.code.fragment.expression.PostfixNotationExpression;
import com.revenat.javamm.code.fragment.operator.BinaryOperator;
import com.revenat.javamm.code.fragment.operator.UnaryOperator;
import com.revenat.javamm.compiler.component.ComplexExpressionBuilder;
import com.revenat.javamm.compiler.component.OperatorPrecedenceResolver;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

import static com.revenat.javamm.code.util.TypeUtils.confirmType;

/**
 * Responsible for building {@linkplain ComplexExpression complex expressions}
 * using {@code Postfix notation} representation.
 *
 * Infix -> Postfix
 *  7 - 2 * 3 -> 7 2 3 * -
 *
 * @author Vitaliy Dragun
 *
 */
public class PostfixNotationComplexExpressionBuilder implements ComplexExpressionBuilder {
    private final OperatorPrecedenceResolver precedenceResolver;

    public PostfixNotationComplexExpressionBuilder(final OperatorPrecedenceResolver operatorPrecedenceResolver) {
        this.precedenceResolver = operatorPrecedenceResolver;
    }

    @Override
    public ComplexExpression build(final List<Lexeme> lexemes, final SourceLine sourceLine) {
        return new PostfixNotationExpression(representInPostifxNotation(lexemes, sourceLine), lexemesAsString(lexemes));
    }

    private List<Lexeme> representInPostifxNotation(final List<Lexeme> lexemes, final SourceLine sourceLine) {
        final List<Lexeme> result = new ArrayList<>();
        final Deque<Lexeme> stack = new ArrayDeque<>();

        for (final Lexeme lexeme : lexemes) {
            processLexeme(lexeme, result, stack, sourceLine);
        }

        popStackOperatorsIntoResult(result, stack, sourceLine);

        return List.copyOf(result);
    }

    private void popStackOperatorsIntoResult(final List<Lexeme> result,
                                             final Deque<Lexeme> stack,
                                             final SourceLine sourceLine) {
        while (!stack.isEmpty()) {
            final Lexeme top = stack.pop();

            if (isNotOperator(top)) {
                throw new JavammLineSyntaxError(sourceLine, "Missing )");
            }

            result.add(top);
        }
    }

    private void processLexeme(final Lexeme lexeme,
                               final List<Lexeme> result,
                               final Deque<Lexeme> stack,
                               final SourceLine sourceLine) {
        if (isOperator(lexeme)) {
            processOperator((Operator) lexeme, result, stack);
        } else if (isParenthesis(lexeme)) {
            processParenthesis((Parenthesis) lexeme, result, stack, sourceLine);
        } else {
            processExpression(lexeme, result);
        }
    }

    private void processOperator(final Operator operator,
                                 final List<Lexeme> result,
                                 final Deque<Lexeme> stack) {
        while (!stack.isEmpty()) {
            final Lexeme top = stack.peek();

            if (isOperatorWithHigherOrEqualPrecedence(top, operator)) {
                result.add(top);
                stack.pop();
            } else {
                break;
            }
        }
        stack.push(operator);
    }

    private void processParenthesis(final Parenthesis parenthesis,
                                    final List<Lexeme> result,
                                    final Deque<Lexeme> stack,
                                    final SourceLine sourceLine) {
        if (isOpeningParenthesis(parenthesis)) {
            stack.push(parenthesis);
        } else {
            popStackContentUntilOpeningParenthesis(result, stack, sourceLine);
        }
    }

    private void popStackContentUntilOpeningParenthesis(final List<Lexeme> result,
                                                        final Deque<Lexeme> stack,
                                                        final SourceLine sourceLine) {
        while (!stack.isEmpty()) {
            final Lexeme top = stack.pop();

            if (isOpeningParenthesis(top)) {
                return;
            } else {
                result.add(top);
            }
        }

        throw new JavammLineSyntaxError(sourceLine, "Missing (");
    }

    private boolean isOperatorWithHigherOrEqualPrecedence(final Lexeme lexeme, final Operator operator) {
        return !(areBothUnary(lexeme, operator) ||
                isBinaryWithLowerPrecedence(lexeme, operator) ||
                isNotOperator(lexeme));
    }

    private boolean isParenthesis(final Lexeme lexeme) {
        return confirmType(Parenthesis.class, lexeme);
    }

    private boolean isOperator(final Lexeme lexeme) {
        return confirmType(Operator.class, lexeme);
    }

    private boolean isOpeningParenthesis(final Lexeme lexeme) {
        return Parenthesis.OPENING_PARENTHESIS == lexeme;
    }

    private void processExpression(final Lexeme lexeme, final List<Lexeme> result) {
        result.add(lexeme);
    }

    private boolean isNotOperator(final Lexeme lexeme) {
        return !isOperator(lexeme);
    }

    private boolean isBinaryWithLowerPrecedence(final Lexeme first, final Operator second) {
        return isBinary(first) && hasLowerPrecedence((Operator) first, second);
    }

    private boolean hasLowerPrecedence(final Operator first, final Operator second) {
        return precedenceResolver.hasLowerPrecedence(first, second);
    }

    private boolean isBinary(final Lexeme top) {
        return confirmType(BinaryOperator.class, top);
    }

    private boolean areBothUnary(final Lexeme first, final Lexeme second) {
        return confirmType(UnaryOperator.class, first, second);
    }

    private String lexemesAsString(final List<Lexeme> lexemes) {
        return lexemes.stream()
                .map(Object::toString)
                .collect(Collectors.joining(" "));
    }
}
