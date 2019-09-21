
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
import static org.junit.jupiter.api.Assertions.*;

import com.revenat.javamm.code.fragment.operator.BinaryOperator;
import com.revenat.javamm.interpreter.component.BinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.arithmetic.ModulusBinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.error.JavammLineRuntimeError;

import java.util.stream.Stream;

import static com.revenat.javamm.code.util.TypeUtils.getType;
import static com.revenat.javamm.interpreter.test.helper.CustomAsserts.assertErrorMessageContains;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("a modulus binary expression calculator")
class ModulusBinaryExpressionCalculatorTest extends AbstractBinaryExpressionmCalculatorTest {

    @Test
    @Order(1)
    void shouldSupportArithmeticModulusOperator() {
        assertThat(calculator.getOperator(), is(BinaryOperator.ARITHMETIC_MODULUS));
    }

    @Test
    @Order(2)
    void shouldCalculateModulusForIntegers() {
        assertThat(calculate(5, 2), is(1));
        assertThat(calculate(2, 5), is(2));
        assertThat(calculate(4, 2), is(0));
    }

    @Test
    @Order(3)
    void shouldCalculateModulusForDoubles() {
        assertThat(calculate(3.5, 3.0), is(0.5));
        assertThat(calculate(3.5, 7.5), is(3.5));
        assertThat(calculate(10.5, 5.25), is(0.0));
    }

    @Test
    @Order(4)
    void shouldCalculateModulusForIntegerAndDoubleAsDouble() {
        assertThat(calculate(4, 2.0), is(0.0));
        assertThat(calculate(5.2, 3), is(2.2));
        assertThat(calculate(5, 7.5), is(5.0));
    }

    @Test
    @Order(5)
    void shouldFailToCalculateModulusForIntegerAndZero() {
        final JavammLineRuntimeError e = assertThrows(JavammLineRuntimeError.class,  () -> calculate(4, 0));

        assertErrorMessageContains(e, "/ by zero");
    }

    @Test
    @Order(6)
    void shouldCalculateModulusForDoubleAndZeroAsNaN() {
        assertThat(calculate(3.5, 0), is(Double.NaN));
        assertThat(calculate(3, 0.0), is(Double.NaN));
    }

    @ParameterizedTest
    @MethodSource("unsupportedPairsProvider")
    @Order(7)
    void shouldFailToCalculateModulusForTypesOtherThanIntegersOrDoubles(final Object value1, final Object value2) {
        final JavammLineRuntimeError e = assertThrows(JavammLineRuntimeError.class, () -> calculate(value1, value2));

        assertErrorMessageContains(e, "Operator '%%' is not supported for types: %s and %s",
                getType(value1), getType(value2));
    }

    static Stream<Arguments> unsupportedPairsProvider() {
        return Stream.of(
                Arguments.arguments(1, false),
                Arguments.arguments(10.5, true),
                Arguments.arguments(10, null),
                Arguments.arguments(5.5, " hello"),
                Arguments.arguments(null, " hello"),
                Arguments.arguments(null, false),
                Arguments.arguments(true, " hello")
                );
    }

    @Override
    protected BinaryExpressionCalculator createCalculatorUnderTest() {
        return new ModulusBinaryExpressionCalculator();
    }
}
