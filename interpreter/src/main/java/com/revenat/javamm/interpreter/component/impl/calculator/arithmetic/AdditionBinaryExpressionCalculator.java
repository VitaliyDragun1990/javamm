
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

import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ARITHMETIC_ADDITION;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ASSIGNMENT_ADDITION;
import static com.revenat.javamm.code.util.TypeUtils.confirmType;

/**
 * {@linkplain BinaryExpressionCalculator Binary expression calculator}
 * implementation for binary addition operator
 *
 * @author Vitaliy Dragun
 *
 */
public final class AdditionBinaryExpressionCalculator extends AbstractBinaryExpressionCalculator {

    private AdditionBinaryExpressionCalculator(final BinaryOperator operator) {
        super(operator);
    }

    public static AdditionBinaryExpressionCalculator createNormalCalculator() {
        return new AdditionBinaryExpressionCalculator(ARITHMETIC_ADDITION);
    }

    public static AdditionBinaryExpressionCalculator createAssignmentCalculator() {
        return new AdditionBinaryExpressionCalculator(ASSIGNMENT_ADDITION);
    }

    @Override
    protected Object calculate(final Object value1, final Object value2) {
        Object result = null;

        if (confirmType(Integer.class, value1, value2)) {
            result = calculateIntegerAddition(value1, value2);
        } else if (confirmType(Double.class, value1, value2)) {
            result = calculateDoubleAddition(value1, value2);
        } else if (confirmType(Number.class, value1, value2)) {
            result = ((Number) value1).doubleValue() + ((Number) value2).doubleValue();
        } else if (eitherOneIsString(value1, value2)) {
            result = calculateStringConcatenation(value1, value2);
        } else {
            throw createNotSupportedTypesError(value1, value2);
        }
        return result;
    }

    private String calculateStringConcatenation(final Object value1, final Object value2) {
        return String.valueOf(value1) + String.valueOf(value2);
    }

    private double calculateDoubleAddition(final Object value1, final Object value2) {
        return (Double) value1 + (Double) value2;
    }

    private boolean eitherOneIsString(final Object value1, final Object value2) {
        return value1 instanceof String || value2 instanceof String;
    }

    private int calculateIntegerAddition(final Object value1, final Object value2) {
        return (Integer) value1 + (Integer) value2;
    }
}
