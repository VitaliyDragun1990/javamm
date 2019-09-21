
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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.revenat.javamm.code.fragment.operator.BinaryOperator;
import com.revenat.javamm.interpreter.component.impl.calculator.arithmetic.SubtractionBinaryExpressionCalculator;
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

@DisplayName("a subtraction binary expression calculator")
class SubtractionBinaryExpressionCalculatorTest extends AbstractBinaryExpressionmCalculatorTest {


    @Test
    @Order(1)
    void shouldSupportArithmeticalSubtractionOperator() {
        assertThat(calculator.getOperator(), is(BinaryOperator.ARITHMETIC_SUBTRACTION));
    }

    @Test
    @Order(2)
    void shouldCalculateIntegerSubtraction() {
        final Object result = calculate(10, 5);

        assertThat(result, equalTo(5));
    }

    @Test
    @Order(3)
    void shouldCalculateDoubleSubtraction() {
        final Object result = calculate(10.0, 2.0);

        assertThat(result, equalTo(8.0));
    }

    @Test
    @Order(4)
    void shouldPresentResultAsDoubleIfCalculateIntegerAndDouble() {
        final Object result = calculate(5, 7.0);

        assertThat(result, equalTo(-2.0));
    }

    @ParameterizedTest
    @MethodSource("unsubtractedPairsProvider")
    @Order(5)
    void shouldFailToCalculateNotNumberSubtraction(final Object value1, final Object value2) {
        final JavammLineRuntimeError e = assertThrows(
                JavammLineRuntimeError.class,
                () -> calculate(value1, value2));

        assertErrorMessageContains(e, "Operator '-' is not supported for types: %s and %s", getType(value1), getType(value2));
    }

    static Stream<Arguments> unsubtractedPairsProvider() {
        return Stream.of(
                Arguments.arguments("hello", " word"),
                Arguments.arguments("hello", true),
                Arguments.arguments(false, true),
                Arguments.arguments(10, true),
                Arguments.arguments(10.5, false),
                Arguments.arguments(null, null),
                Arguments.arguments(null, true),
                Arguments.arguments(null, "null"),
                Arguments.arguments(null, 0),
                Arguments.arguments(null, 10.0)
        );
    }

    @Override
    protected SubtractionBinaryExpressionCalculator createCalculatorUnderTest() {
        return new SubtractionBinaryExpressionCalculator();
    }
}
