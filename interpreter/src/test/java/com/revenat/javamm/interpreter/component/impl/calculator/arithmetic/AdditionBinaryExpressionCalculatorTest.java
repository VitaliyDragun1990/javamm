
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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.revenat.javamm.code.fragment.operator.BinaryOperator;
import com.revenat.javamm.interpreter.component.BinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.error.JavammLineRuntimeError;

import java.util.stream.Stream;

import static com.revenat.javamm.code.util.TypeUtils.getType;
import static com.revenat.javamm.interpreter.test.helper.CustomAsserts.assertErrorMessageContains;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.revenat.juinit.addons.ReplaceCamelCase;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("an addition binary expression calculator")
class AdditionBinaryExpressionCalculatorTest extends AbstractBinaryExpressionmCalculatorTest {

    @Test
    @Order(1)
    void shouldSupportArithmeticAdditionOperator() {
        assertThat(calculator.getOperator(), is(BinaryOperator.ARITHMETIC_ADDITION));
    }

    @Test
    @Order(2)
    void shouldCalculateIntegerAddition() {
        final Object result = calculate(1, 2);

        assertThat(result, is(3));
    }

    @Test
    @Order(3)
    void shouldCalculateDoubleAddition() {
        final Object result = calculate(1.0, 2.5);

        assertThat(result, is(3.5));
    }

    @Test
    @Order(4)
    void shouldCalculateIntegerAndDoubleAdditionAsDouble() {
        final Object result = calculate(2, 5.5);

        assertThat(result, is(7.5));
    }

    @Test
    @Order(5)
    void shouldCalculateStringConcatenation() {
        final Object result = calculate("Hello ", "world");

        assertThat(result, is("Hello world"));
    }

    @Test
    @Order(6)
    void shouldCalculateStringAndNumberConcatenation() {
        assertThat(calculate("Hello ", 10), is("Hello 10"));
        assertThat(calculate("Hello ", 5.5), is("Hello 5.5"));
        assertThat(calculate(5.5, " hello"), is("5.5 hello"));
        assertThat(calculate(10, " hello"), is("10 hello"));
    }

    @Test
    @Order(7)
    void shouldCalculateStringAndBooleanConcatenation() {
        assertThat(calculate("Hello ", true), is("Hello true"));
        assertThat(calculate("Hello ", false), is("Hello false"));
        assertThat(calculate(true, " hello"), is("true hello"));
        assertThat(calculate(false, " hello"), is("false hello"));
    }

    @Test
    @Order(8)
    void shouldCalculateStringAndNullConcatenation() {
        assertThat(calculate("Hello ", null), is("Hello null"));
        assertThat(calculate(null, " hello"), is("null hello"));
    }

    @ParameterizedTest
    @MethodSource("unsupportedPairs")
    @Order(9)
    void shouldFailToCalculateAdditionOfUnsupportedTypes(final Object value1, final Object value2) {
        final JavammLineRuntimeError e = assertThrows(JavammLineRuntimeError.class, () -> calculate(value1, value2));

        assertErrorMessageContains(e, "Operator '+' is not supported for types: %s and %s",
                getType(value1), getType(value2));
    }

    static Stream<Arguments> unsupportedPairs() {
        return Stream.of(
                Arguments.arguments(3, true),
                Arguments.arguments(10.5, false),
                Arguments.arguments(10, null),
                Arguments.arguments(5.5, null),
                Arguments.arguments(null, null),
                Arguments.arguments(true, false),
                Arguments.arguments(true, null)
                );
    }

    @Override
    protected BinaryExpressionCalculator createCalculatorUnderTest() {
        return new AdditionBinaryExpressionCalculator();
    }
}
