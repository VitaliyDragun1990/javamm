
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

package com.revenat.javamm.interpreter.component.impl.calculator;

import com.revenat.javamm.code.component.ExpressionContext;
import com.revenat.javamm.code.fragment.Expression;
import com.revenat.javamm.code.fragment.operator.BinaryOperator;
import com.revenat.javamm.interpreter.component.BinaryExpressionCalculator;

/**
 * @author Vitaliy Dragun
 *
 */
public abstract class AbstractBinaryExpressionCalculator extends AbstractExpressionCalculator
        implements BinaryExpressionCalculator {

    protected AbstractBinaryExpressionCalculator(final BinaryOperator operator) {
        super(operator);
    }

    @Override
    public BinaryOperator getOperator() {
        return (BinaryOperator) super.getOperator();
    }

    @Override
    public Object calculate(final ExpressionContext expressionContext, final Expression expression1,
            final Expression expression2) {
        final Object value1 = expression1.getValue(expressionContext);
        final Object value2 = expression2.getValue(expressionContext);

        return calculate(value1, value2);
    }

    protected abstract Object calculate(Object value1, Object value2);
}
