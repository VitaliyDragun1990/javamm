
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
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ARITHMETIC_ADDITION;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ASSIGNMENT_ADDITION;

/**
 * {@linkplain BinaryExpressionCalculator Binary expression calculator}
 * implementation for binary addition operator
 *
 * @author Vitaliy Dragun
 *
 */
public final class AdditionBinaryExpressionCalculator extends AbstractArithmeticBinaryExpressionCalculator {

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
        if (areIntegers(value1, value2)) {
            return calculateForIntegers(value1, value2);
        } else if (areNumbers(value1, value2)) {
            return calculateForDoubles(value1, value2);
        } else if (eitherOneIsString(value1, value2)) {
            return calculateStringConcatenation(value1, value2);
        } else {
            throw createNotSupportedTypesError(value1, value2);
        }
    }

    @Override
    protected Integer calculateForIntegers(final Object value1, final Object value2) {
        return (Integer) value1 + (Integer) value2;
    }

    @Override
    protected Double calculateForDoubles(final Object value1, final Object value2) {
        return ((Number) value1).doubleValue() + ((Number) value2).doubleValue();
    }

    private String calculateStringConcatenation(final Object value1, final Object value2) {
        return String.valueOf(value1) + String.valueOf(value2);
    }

    private boolean eitherOneIsString(final Object value1, final Object value2) {
        return value1 instanceof String || value2 instanceof String;
    }
}
