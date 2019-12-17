
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
import com.revenat.javamm.interpreter.component.impl.calculator.arithmetic.binary.DivisionBinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.error.JavammLineRuntimeError;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ARITHMETIC_DIVISION;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ASSIGNMENT_DIVISION;
import static com.revenat.javamm.code.util.TypeUtils.getType;
import static com.revenat.javamm.interpreter.test.helper.CustomAsserts.assertErrorMessageContains;
import static java.lang.Double.POSITIVE_INFINITY;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("a division '/' binary expression calculator")
class DivisionBinaryExpressionCalculatorTest extends AbstractBinaryExpressionCalculatorTest {

    static Stream<Arguments> unsupportedPairsProvider() {
        return Stream.of(
            Arguments.arguments(1, true),
            Arguments.arguments(1.5, false),
            Arguments.arguments(1.5, " hello"),
            Arguments.arguments(1.5, null),
            Arguments.arguments(1, " hello"),
            Arguments.arguments(1, null),
            Arguments.arguments(true, null),
            Arguments.arguments("hello ", null)
        );
    }

    @Test
    @Order(1)
    void shouldSupportArithmeticDivisionOperator() {
        assertCalculatorSupportsOperator(DivisionBinaryExpressionCalculator.createNormalCalculator(), ARITHMETIC_DIVISION);
        assertCalculatorSupportsOperator(DivisionBinaryExpressionCalculator.createAssignmentCalculator(), ASSIGNMENT_DIVISION);
    }

    @Test
    @Order(2)
    void shouldCalculateIntegerDivision() {
        assertThat(calculate(4, 2), is(2));
        assertThat(calculate(-4, 2), is(-2));
    }

    @Test
    @Order(3)
    void shouldCalculateDoubleDivision() {
        assertThat(calculate(5.0, 2.5), is(2.0));
        assertThat(calculate(-10.5, 2.0), is(-5.25));
    }

    @Test
    @Order(4)
    void shouldCalculateIntegerAndDoubleDivisionAsDouble() {
        assertThat(calculate(5, 2.5), is(2.0));
        assertThat(calculate(8.5, 2), is(4.25));
    }

    @Test
    @Order(5)
    void shouldFailIfDivideIntegerByZero() {
        final JavammLineRuntimeError e = assertThrows(JavammLineRuntimeError.class, () -> calculate(5, 0));

        assertErrorMessageContains(e, "/ by zero");
    }

    @Test
    @Order(6)
    void shouldCalculateDoubleDivisionByZeroAsPositiveInfinity() {
        assertThat(calculate(3.5, 0), is(POSITIVE_INFINITY));
        assertThat(calculate(3.5, 0.0), is(POSITIVE_INFINITY));
        assertThat(calculate(3, 0.0), is(POSITIVE_INFINITY));
    }

    @ParameterizedTest
    @MethodSource("unsupportedPairsProvider")
    @Order(7)
    void shouldFailToCalculateDivisionOfUnsupportedTypes(final Object value1, final Object value2) {
        final JavammLineRuntimeError e = assertThrows(JavammLineRuntimeError.class, () -> calculate(value1, value2));

        assertErrorMessageContains(e, "Operator '/' is not supported for types: %s and %s",
            getType(value1), getType(value2));
    }

    @Override
    protected BinaryExpressionCalculator createCalculatorUnderTest() {
        return DivisionBinaryExpressionCalculator.createNormalCalculator();
    }
}
