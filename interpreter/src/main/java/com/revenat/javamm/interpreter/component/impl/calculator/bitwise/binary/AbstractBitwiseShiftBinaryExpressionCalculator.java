
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

package com.revenat.javamm.interpreter.component.impl.calculator.bitwise.binary;

import com.revenat.javamm.code.fragment.operator.BinaryOperator;
import com.revenat.javamm.interpreter.component.impl.calculator.AbstractBinaryExpressionCalculator;

import static com.revenat.javamm.code.util.TypeUtils.confirmType;

/**
 * @author Vitaliy Dragun
 */
abstract class AbstractBitwiseShiftBinaryExpressionCalculator extends AbstractBinaryExpressionCalculator {

    protected AbstractBitwiseShiftBinaryExpressionCalculator(final BinaryOperator operator) {
        super(operator);
    }

    @Override
    protected Object calculate(final Object value1, final Object value2) {
        if (areIntegers(value1, value2)) {
            return calculateForIntegers(value1, value2);
        }
        throw createNotSupportedTypesError(value1, value2);
    }

    private boolean areIntegers(final Object value1, final Object value2) {
        return confirmType(Integer.class, value1, value2);
    }

    protected abstract Integer calculateForIntegers(Object value1, Object value2);
}
