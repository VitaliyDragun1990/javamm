
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

import com.revenat.javamm.code.fragment.operator.BinaryOperator;
import com.revenat.javamm.interpreter.component.BinaryExpressionCalculator;

/**
 * {@linkplain BinaryExpressionCalculator Binary expression calculator}
 * implementation for arithmetic multiplication operator
 *
 * @author Vitaliy Dragun
 *
 */
public class MultiplicationBinaryExpressionCalculator extends AbstractArithmeticBinaryExpressionCalculator {

    public MultiplicationBinaryExpressionCalculator() {
        super(BinaryOperator.ARITHMETIC_MULTIPLICATION);
    }

    @Override
    protected Object calculate(final Object value1, final Object value2) {
        if (areIntegers(value1, value2)) {
            return calculateIntegerMultiplication(value1, value2);
        } else if (areDoubles(value1, value2) || areNumbers(value1, value2)) {
            return calculateDoubleMultiplication(value1, value2);
        }

        throw createNotSupportedTypesError(value1, value2);
    }

    private double calculateDoubleMultiplication(final Object value1, final Object value2) {
        return ((Number) value1).doubleValue() * ((Number) value2).doubleValue();
    }

    private int calculateIntegerMultiplication(final Object value1, final Object value2) {
        return (Integer) value1 * (Integer) value2;
    }
}
