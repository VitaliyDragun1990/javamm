
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

package com.revenat.javamm.interpreter.component.impl.calculator.arithmetic.binary;

import com.revenat.javamm.code.fragment.operator.BinaryOperator;
import com.revenat.javamm.interpreter.component.BinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.error.JavammLineRuntimeError;

import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ARITHMETIC_MODULUS;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ASSIGNMENT_MODULUS;

/**
 * {@linkplain BinaryExpressionCalculator Binary expression calculator}
 * implementation for binary modulus operator
 *
 * @author Vitaliy Dragun
 */
public final class ModulusBinaryExpressionCalculator extends AbstractArithmeticBinaryExpressionCalculator {

    private ModulusBinaryExpressionCalculator(final BinaryOperator operator) {
        super(operator);
    }

    public static ModulusBinaryExpressionCalculator createNormalCalculator() {
        return new ModulusBinaryExpressionCalculator(ARITHMETIC_MODULUS);
    }

    public static ModulusBinaryExpressionCalculator createAssignmentCalculator() {
        return new ModulusBinaryExpressionCalculator(ASSIGNMENT_MODULUS);
    }

    @Override
    protected Integer calculateForIntegers(final Object value1, final Object value2) {
        final Integer v1 = (Integer) value1;
        final Integer v2 = (Integer) value2;

        if (v2 == 0) {
            throw new JavammLineRuntimeError("/ by zero");
        }
        return v1 % v2;
    }

    @Override
    protected Double calculateForDoubles(final Object value1, final Object value2) {
        return ((Number) value1).doubleValue() % ((Number) value2).doubleValue();
    }
}
