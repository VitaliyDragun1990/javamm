
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
import com.revenat.javamm.interpreter.component.impl.calculator.arithmetic.binary.MultiplicationBinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.error.JavammLineRuntimeError;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ARITHMETIC_MULTIPLICATION;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ASSIGNMENT_MULTIPLICATION;
import static com.revenat.javamm.code.util.TypeUtils.getType;
import static com.revenat.javamm.interpreter.test.helper.CustomAsserts.assertErrorMessageContains;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@DisplayName("a multiplication '*' binary expression calculator")
class MultiplicationBinaryExpressionCalculatorTest extends AbstractBinaryExpressionCalculatorTest {

    static Stream<Arguments> unsupportedPairsProvider() {
        return Stream.of(
            Arguments.arguments(1, true),
            Arguments.arguments(2.5, false),
            Arguments.arguments(10, " hello"),
            Arguments.arguments(10.5, " hello"),
            Arguments.arguments(10.5, null),
            Arguments.arguments(null, 10),
            Arguments.arguments("hello ", " world"),
            Arguments.arguments("hello ", null),
            Arguments.arguments("hello ", false)
        );
    }

    @Test
    @Order(1)
    void shouldSupportArithmeticMultiplicationOperator() {
        assertCalculatorSupportsOperator(MultiplicationBinaryExpressionCalculator.createNormalCalculator(), ARITHMETIC_MULTIPLICATION);
        assertCalculatorSupportsOperator(MultiplicationBinaryExpressionCalculator.createAssignmentCalculator(), ASSIGNMENT_MULTIPLICATION);
    }

    @Test
    @Order(2)
    void shouldCalculateIntegerMultiplication() {
        assertThat(calculate(2, 2), is(4));
    }

    @Test
    @Order(3)
    void shouldCalculateDoubleMultiplication() {
        assertThat(calculate(2.5, 2.5), is(6.25));
    }

    @Test
    @Order(4)
    void shouldCalculateIntegerAndDoubleMultiplicationAsDouble() {
        assertThat(calculate(2.5, 2), is(5.0));
        assertThat(calculate(2, 3.5), is(7.0));
    }

    @ParameterizedTest
    @MethodSource("unsupportedPairsProvider")
    @Order(5)
    void shouldFailToCalculateMultiplicationOfUnsupportedTypes(final Object value1, final Object value2) {
        final JavammLineRuntimeError e = assertThrows(JavammLineRuntimeError.class, () -> calculate(value1, value2));

        assertErrorMessageContains(e, "Operator '*' is not supported for types: %s and %s",
            getType(value1), getType(value2));
    }

    @Override
    protected BinaryExpressionCalculator createCalculatorUnderTest() {
        return MultiplicationBinaryExpressionCalculator.createNormalCalculator();
    }
}
