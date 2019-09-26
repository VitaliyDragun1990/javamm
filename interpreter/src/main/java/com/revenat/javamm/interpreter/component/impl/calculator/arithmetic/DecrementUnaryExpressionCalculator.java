
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

package com.revenat.javamm.interpreter.component.impl.calculator.arithmetic;

import com.revenat.javamm.code.fragment.operator.UnaryOperator;
import com.revenat.javamm.interpreter.component.UnaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.AbstractUnaryExpressionCalculator;

import static com.revenat.javamm.code.util.TypeUtils.confirmType;

/**
 * {@linkplain UnaryExpressionCalculator Unary expression calculator}
 * implementation for 'arithmetical unary decrement' ({@code --}) operator
 *
 * @author Vitaliy Dragun
 *
 */
public class DecrementUnaryExpressionCalculator extends AbstractUnaryExpressionCalculator {

    public DecrementUnaryExpressionCalculator() {
        super(UnaryOperator.DECREMENT);
    }

    @Override
    protected Object calculate(final Object value) {
        if (confirmType(Integer.class, value)) {
            return decrementIntegerValue(value);
        } else if (confirmType(Double.class, value)) {
            return decrementDoubleValue(value);
        }
        throw createNotSupportedTypesError(value);
    }

    private Double decrementDoubleValue(final Object value) {
        double d = (Double) value;
        return --d;
    }

    private Integer decrementIntegerValue(final Object value) {
        int i = (Integer) value;
        return --i;
    }
}
