
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

package com.revenat.javamm.interpreter.component.impl.calculator;

import com.revenat.javamm.interpreter.component.BinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.bitwise.binary.BitwiseShiftRightBinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.error.JavammLineRuntimeError;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ASSIGNMENT_BITWISE_SHIFT_RIGHT;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.BITWISE_SHIFT_RIGHT;
import static com.revenat.javamm.code.util.TypeUtils.getType;
import static com.revenat.javamm.interpreter.test.helper.CustomAsserts.assertErrorMessageContains;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("a bitwise shift right >> binary expression calculator")
class BitwiseShiftRightBinaryExpressionCalculatorTest extends AbstractBinaryExpressionCalculatorTest {

    static Stream<Arguments> unsupportedPairsProvider() {
        return Stream.of(
            Arguments.arguments(1.2, 1),
            Arguments.arguments(1.2, 5.2),
            Arguments.arguments(null, 1),
            Arguments.arguments(null, false),
            Arguments.arguments(false, 10),
            Arguments.arguments(" hello", " world"),
            Arguments.arguments(" hello", 10),
            Arguments.arguments(true, false),
            Arguments.arguments(10, false),
            Arguments.arguments(false, 10),
            Arguments.arguments(false, " hello")
        );
    }

    @Test
    @Order(1)
    void shouldSupportBitwiseShiftRightOperator() {
        assertCalculatorSupportsOperator(BitwiseShiftRightBinaryExpressionCalculator.createNormalCalculator(), BITWISE_SHIFT_RIGHT);
        assertCalculatorSupportsOperator(BitwiseShiftRightBinaryExpressionCalculator.createAssignmentCalculator(), ASSIGNMENT_BITWISE_SHIFT_RIGHT);
    }

    @Test
    @Order(2)
    void shouldCalculateBitwiseShiftRightOperationForIntegers() {
        assertThat(calculate(-100, 2), is(-25));
        assertThat(calculate(4, 1), is(2));
        assertThat(calculate(4, -1), is(0));
        assertThat(calculate(100, -1), is(0));
        assertThat(calculate(-100, -1), is(-1));
        assertThat(calculate(-4, -2), is(-1));
    }

    @ParameterizedTest
    @MethodSource("unsupportedPairsProvider")
    @Order(3)
    void shouldFailToCalculateForUnsupportedTypes(final Object value1, final Object value2) {
        final JavammLineRuntimeError e = assertThrows(JavammLineRuntimeError.class, () -> calculate(value1, value2));

        assertErrorMessageContains(e, "Operator '>>' is not supported for types: %s and %s",
            getType(value1), getType(value2));
    }

    @Override
    protected BinaryExpressionCalculator createCalculatorUnderTest() {
        return BitwiseShiftRightBinaryExpressionCalculator.createNormalCalculator();
    }
}
