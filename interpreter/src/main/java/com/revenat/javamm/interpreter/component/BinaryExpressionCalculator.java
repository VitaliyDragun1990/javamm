
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

package com.revenat.javamm.interpreter.component;

import com.revenat.javamm.code.component.ExpressionContext;
import com.revenat.javamm.code.fragment.Expression;
import com.revenat.javamm.code.fragment.expression.BinaryExpression;
import com.revenat.javamm.code.fragment.operator.BinaryOperator;
import com.revenat.javamm.interpreter.error.JavammRuntimeError;

/**
 * Calculates {@linkplain BinaryExpression binary expressions}
 *
 * @author Vitaliy Dragun
 *
 */
public interface BinaryExpressionCalculator extends ExpressionCalculator {

    /**
     * Returns {@linkplain BinaryOperator binary operator} that expression
     * calculator can handle
     */
    @Override
    BinaryOperator getOperator();

    /**
     * Calculates binary expression using
     * {@linkplain BinaryExpressionCalculator#getOperator() operator} and provided
     * {@linkplain Expression operand1} and {@linkplain Expression operand2}
     *
     * @throws JavammRuntimeError if
     *                            {@linkplain BinaryExpressionCalculator#getOperator()
     *                            implied operator} supported by {@code this} binary
     *                            calculator does not support specified operands
     */
    Object calculate(ExpressionContext expressionContext, Expression operand1, Expression operand2);
}
