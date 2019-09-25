
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

package com.revenat.javamm.interpreter.component.impl.calculator.logical;

import com.revenat.javamm.code.component.ExpressionContext;
import com.revenat.javamm.code.fragment.Expression;
import com.revenat.javamm.code.fragment.operator.BinaryOperator;
import com.revenat.javamm.interpreter.component.BinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.AbstractBinaryExpressionCalculator;

import static com.revenat.javamm.code.util.TypeUtils.confirmType;

/**
 * {@linkplain BinaryExpressionCalculator Binary expression calculator}
 * implementation for 'logical and' ({@code &&}) operator
 *
 * @author Vitaliy Dragun
 *
 */
public class LogicalAndBinaryExpressionCalculator extends AbstractBinaryExpressionCalculator {

    public LogicalAndBinaryExpressionCalculator() {
        super(BinaryOperator.LOGICAL_AND);
    }

    @Override
    public Object calculate(final ExpressionContext expressionContext, final Expression expression1,
            final Expression expression2) {
        final Object operand1 = expression1.getValue(expressionContext);

        if (isFalsy(operand1)) {
            return false;
        } else {
            return calculate(operand1, expression2.getValue(expressionContext));
        }
    }

    @Override
    protected Object calculate(final Object value1, final Object value2) {
        if (confirmType(Boolean.class, value1, value2)) {
            return (Boolean) value1 && (Boolean) value2;
        } else {
            throw createNotSupportedTypesError(value1, value2);
        }
    }

    private boolean isFalsy(final Object operand) {
        return operand instanceof Boolean && !(Boolean) operand;
    }
}
