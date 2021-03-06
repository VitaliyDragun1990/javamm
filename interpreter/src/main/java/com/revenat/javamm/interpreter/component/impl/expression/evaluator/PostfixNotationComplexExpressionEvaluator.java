
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

package com.revenat.javamm.interpreter.component.impl.expression.evaluator;

import com.revenat.javamm.code.fragment.Expression;
import com.revenat.javamm.code.fragment.Lexeme;
import com.revenat.javamm.code.fragment.UpdatableExpression;
import com.revenat.javamm.code.fragment.expression.ConstantExpression;
import com.revenat.javamm.code.fragment.expression.PostfixNotationComplexExpression;
import com.revenat.javamm.code.fragment.operator.BinaryOperator;
import com.revenat.javamm.code.fragment.operator.UnaryOperator;
import com.revenat.javamm.interpreter.component.CalculatorFacade;
import com.revenat.javamm.interpreter.component.ExpressionEvaluator;

import java.util.ArrayDeque;
import java.util.Deque;

import static com.revenat.javamm.code.util.LexemeUtils.isBinaryOperator;
import static com.revenat.javamm.code.util.LexemeUtils.isUnaryOperator;

/**
 * Responsible for evaluation {@linkplain PostfixNotationComplexExpression postfix
 * notation expressions}
 *
 * @author Vitaliy Dragun
 */
public class PostfixNotationComplexExpressionEvaluator extends AbstractExpressionEvaluator
    implements ExpressionEvaluator<PostfixNotationComplexExpression> {

    private final CalculatorFacade calculator;

    public PostfixNotationComplexExpressionEvaluator(final CalculatorFacade calculator) {
        this.calculator = calculator;
    }

    @Override
    public Class<PostfixNotationComplexExpression> getExpressionClass() {
        return PostfixNotationComplexExpression.class;
    }

    @Override
    public Object evaluate(final PostfixNotationComplexExpression expression) {
        final Deque<Expression> resultStack = new ArrayDeque<>();

        for (final Lexeme lexeme : expression.getLexemes()) {
            processLexeme(resultStack, lexeme);
        }

        return evaluateResult(resultStack);
    }

    private void processLexeme(final Deque<Expression> stack, final Lexeme lexeme) {
        if (isBinaryOperator(lexeme)) {
            calculateBinaryOperationAndPutResultInStack((BinaryOperator) lexeme, stack);
        } else if (isUnaryOperator(lexeme)) {
            calculateUnaryOperationAndPutResultInStack((UnaryOperator) lexeme, stack);
        } else {
            putInStack((Expression) lexeme, stack);
        }
    }

    private void calculateBinaryOperationAndPutResultInStack(final BinaryOperator operator,
                                                             final Deque<Expression> stack) {
        final Expression secondOperand = stack.pop();
        final Expression firstOperand = stack.pop();
        final Object result = calculate(firstOperand, operator, secondOperand);

        if (operator.isAssignment()) {
            updateOperandValue(firstOperand, result);
        }

        stack.push(toExpression(result));
    }

    private void updateOperandValue(final Expression operandToUpdate, final Object newValue) {
        ((UpdatableExpression) operandToUpdate).setValue(getExpressionContext(), newValue);
    }

    private void calculateUnaryOperationAndPutResultInStack(final UnaryOperator operator,
                                                            final Deque<Expression> stack) {
        final Expression operand = stack.pop();
        final Object result = calculate(operand, operator);
        stack.push(toExpression(result));
    }

    private void putInStack(final Expression expression, final Deque<Expression> stack) {
        stack.push(expression);
    }

    private Object evaluateResult(final Deque<Expression> resultStack) {
        return resultStack.pop().getValue(getExpressionContext());
    }

    private Object calculate(final Expression operand, final UnaryOperator operator) {
        return calculator.calculate(getExpressionContext(), operator, operand);
    }

    private Object calculate(final Expression firstOperand,
                             final BinaryOperator operator,
                             final Expression secondOperand) {
        return calculator.calculate(getExpressionContext(), firstOperand, operator, secondOperand);
    }

    private ConstantExpression toExpression(final Object result) {
        return ConstantExpression.valueOf(result);
    }
}
