
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

package com.revenat.javamm.interpreter.component.impl.calculator.arithmetic.unary;

import com.revenat.javamm.code.fragment.operator.UnaryOperator;
import com.revenat.javamm.interpreter.component.UnaryExpressionCalculator;

/**
 * {@linkplain UnaryExpressionCalculator Unary expression calculator}
 * implementation for 'arithmetical unary increment' ({@code ++}) operator
 *
 * @author Vitaliy Dragun
 */
public class IncrementUnaryExpressionCalculator extends AbstractArithmeticUnaryExpressionCalculator {

    public IncrementUnaryExpressionCalculator() {
        super(UnaryOperator.INCREMENT);
    }

    @Override
    protected Integer calculateForInteger(final Object value) {
        int i = (Integer) value;
        return ++i;
    }

    @Override
    protected Double calculateForDouble(final Object value) {
        double d = (Double) value;
        return ++d;
    }
}
