
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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.revenat.javamm.interpreter.component.BinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.bitwise.binary.BitwiseOrBinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.error.JavammLineRuntimeError;

import java.util.stream.Stream;

import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ASSIGNMENT_BITWISE_OR;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.BITWISE_OR;
import static com.revenat.javamm.code.util.TypeUtils.getType;
import static com.revenat.javamm.interpreter.test.helper.CustomAsserts.assertErrorMessageContains;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("a bitwise '|' binary expression calculator")
class BitwiseOrBinaryExpressionCalculatorTest extends AbstractBinaryExpressionmCalculatorTest {

    @Test
    @Order(1)
    void shouldSupportBitwiseOrOperator() {
        assertCalculatorSupportsOperator(BitwiseOrBinaryExpressionCalculator.createNormalCalculator(), BITWISE_OR);
        assertCalculatorSupportsOperator(BitwiseOrBinaryExpressionCalculator.createAssignmentCalculator(), ASSIGNMENT_BITWISE_OR);
    }

    @Test
    @Order(2)
    void shouldCalculateBitwiseOrOperationForIntegers() {
        assertThat(calculate(4, 2), is(6)); // 0100 | 0010
        assertThat(calculate(-5, 1), is(-5)); // 0101 | 0001
    }

    @Test
    @Order(3)
    void shouldCalculateEagerLogicalOrOperationForBooleans() {
        assertThat(calculate(true, true), is(true));
        assertThat(calculate(false, true), is(true));
        assertThat(calculate(true, false), is(true));
        assertThat(calculate(false, false), is(false));
    }

    @ParameterizedTest
    @MethodSource("unsupportedPairsProvider")
    @Order(4)
    void shouldFailToCalculateForUnsupportedTypes(final Object value1, final Object value2) {
        final JavammLineRuntimeError e = assertThrows(JavammLineRuntimeError.class, () -> calculate(value1, value2));

        assertErrorMessageContains(e, "Operator '|' is not supported for types: %s and %s",
                getType(value1), getType(value2));
    }

    static Stream<Arguments> unsupportedPairsProvider() {
        return Stream.of(
                Arguments.arguments(1.2, 1),
                Arguments.arguments(1.2, 5.2),
                Arguments.arguments(null, 1),
                Arguments.arguments(null, false),
                Arguments.arguments(false, 10),
                Arguments.arguments(" hello", " world"),
                Arguments.arguments(" hello", 10)
                );
    }

    @Override
    protected BinaryExpressionCalculator createCalculatorUnderTest() {
        return BitwiseOrBinaryExpressionCalculator.createNormalCalculator();
    }
}
