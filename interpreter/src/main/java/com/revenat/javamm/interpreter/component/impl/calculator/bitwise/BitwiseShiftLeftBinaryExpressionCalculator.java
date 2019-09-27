
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

import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ASSIGNMENT_BITWISE_SHIFT_LEFT;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.BITWISE_SHIFT_LEFT;
import static com.revenat.javamm.code.util.TypeUtils.confirmType;

/**
 * {@linkplain BinaryExpressionCalculator Binary expression calculator}
 * implementation for binary bitwise shift left ('<<') operator
 *
 * @author Vitaliy Dragun
 *
 */
public final class BitwiseShiftLeftBinaryExpressionCalculator extends AbstractBinaryExpressionCalculator {

    private BitwiseShiftLeftBinaryExpressionCalculator(final BinaryOperator operator) {
        super(operator);
    }

    public static BitwiseShiftLeftBinaryExpressionCalculator createNormalCalculator() {
        return new BitwiseShiftLeftBinaryExpressionCalculator(BITWISE_SHIFT_LEFT);
    }

    public static BitwiseShiftLeftBinaryExpressionCalculator createAssignmentCalculator() {
        return new BitwiseShiftLeftBinaryExpressionCalculator(ASSIGNMENT_BITWISE_SHIFT_LEFT);
    }

    @Override
    protected Object calculate(final Object value1, final Object value2) {
        if (confirmType(Integer.class, value1, value2)) {
            return calculateBitwiseShiftLeft(value1, value2);
        }
        throw createNotSupportedTypesError(value1, value2);
    }

    private int calculateBitwiseShiftLeft(final Object value1, final Object value2) {
        return (Integer) value1 << (Integer) value2;
    }
}
