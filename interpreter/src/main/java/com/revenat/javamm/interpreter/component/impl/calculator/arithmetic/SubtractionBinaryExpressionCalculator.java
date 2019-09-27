
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
import com.revenat.javamm.code.util.TypeUtils;
import com.revenat.javamm.interpreter.component.BinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.AbstractBinaryExpressionCalculator;

import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ARITHMETIC_SUBTRACTION;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ASSIGNMENT_SUBTRACTION;

/**
 * {@linkplain BinaryExpressionCalculator Binary expression calculator}
 * implementation for binary subtraction operator
 *
 * @author Vitaliy Dragun
 *
 */
public final class SubtractionBinaryExpressionCalculator extends AbstractBinaryExpressionCalculator {

    private SubtractionBinaryExpressionCalculator(final BinaryOperator operator) {
        super(operator);
    }

    public static SubtractionBinaryExpressionCalculator createNormalCalculator() {
        return new SubtractionBinaryExpressionCalculator(ARITHMETIC_SUBTRACTION);
    }

    public static SubtractionBinaryExpressionCalculator createAssignmentCalculator() {
        return new SubtractionBinaryExpressionCalculator(ASSIGNMENT_SUBTRACTION);
    }

    @Override
    protected Object calculate(final Object value1, final Object value2) {
        if (TypeUtils.confirmType(Integer.class, value1, value2)) {
            return calculateSubtractionForIntegers(value1, value2);
        } else if (TypeUtils.confirmType(Number.class, value1, value2)) {
            return calculateSubtractionForDoubles(value1, value2);
        }
        throw createNotSupportedTypesError(value1, value2);
    }

    private int calculateSubtractionForIntegers(final Object value1, final Object value2) {
        return (Integer) value1 - (Integer) value2;
    }

    private double calculateSubtractionForDoubles(final Object value1, final Object value2) {
        return ((Number) value1).doubleValue() - ((Number) value2).doubleValue();
    }
}
