
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

package com.revenat.javamm.interpreter.component.impl.calculator.bitwise;

import com.revenat.javamm.code.fragment.operator.BinaryOperator;
import com.revenat.javamm.interpreter.component.BinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.AbstractBinaryExpressionCalculator;

import static com.revenat.javamm.code.util.TypeUtils.confirmType;

/**
 * {@linkplain BinaryExpressionCalculator Binary expression calculator}
 * implementation for bitwise shift right zero fill ('>>>') operator
 *
 * @author Vitaliy Dragun
 *
 */
public class BitwiseShiftRightZeroFillBinaryExpressionCalculator extends AbstractBinaryExpressionCalculator {

    public BitwiseShiftRightZeroFillBinaryExpressionCalculator() {
        super(BinaryOperator.BITWISE_SHIFT_RIGHT_ZERO_FILL);
    }

    @Override
    protected Object calculate(final Object value1, final Object value2) {
        if (confirmType(Integer.class, value1, value2)) {
            return calculateRightShiftZeroFill(value1, value2);
        }
        throw createNotSupportedTypesError(value1, value2);
    }

    private int calculateRightShiftZeroFill(final Object value1, final Object value2) {
        return (Integer) value1 >>> (Integer) value2;
    }
}