
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
import com.revenat.javamm.interpreter.component.impl.calculator.AbstractBinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.error.JavammLineRuntimeError;

import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ARITHMETIC_DIVISION;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ASSIGNMENT_DIVISION;
import static com.revenat.javamm.code.util.TypeUtils.confirmType;

/**
 * {@linkplain BinaryExpressionCalculator Binary expression calculator}
 * implementation for binary division operator
 *
 * @author Vitaliy Dragun
 *
 */
public final class DivisionBinaryExpressionCalculator extends AbstractBinaryExpressionCalculator {

    private DivisionBinaryExpressionCalculator(final BinaryOperator operator) {
        super(operator);
    }

    public static DivisionBinaryExpressionCalculator createNormalCalculator() {
        return new DivisionBinaryExpressionCalculator(ARITHMETIC_DIVISION);
    }

    public static DivisionBinaryExpressionCalculator createAssignmentCalculator() {
        return new DivisionBinaryExpressionCalculator(ASSIGNMENT_DIVISION);
    }

    @Override
    protected Object calculate(final Object value1, final Object value2) {
        if (confirmType(Integer.class, value1, value2)) {
            return calculateIntegersDivision(value1, value2);
        } else if (confirmType(Number.class, value1, value2)) {
            return calculateDoubleDivision(value1, value2);
        }
        throw createNotSupportedTypesError(value1, value2);
    }

    private double calculateDoubleDivision(final Object value1, final Object value2) {
        return ((Number) value1).doubleValue() / ((Number) value2).doubleValue();
    }

    private int calculateIntegersDivision(final Object value1, final Object value2) {
        final Integer v1 = (Integer) value1;
        final Integer v2 = (Integer) value2;

        if (v2 == 0) {
            throw new JavammLineRuntimeError("/ by zero");
        }
        return v1 / v2;
    }
}
