
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

import com.revenat.javamm.code.fragment.operator.BinaryOperator;
import com.revenat.javamm.interpreter.component.BinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.predicate.IsGreaterThanOrEqualsBinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.error.JavammLineRuntimeError;
import com.revenat.javamm.interpreter.test.helper.CustomAsserts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.revenat.javamm.code.fragment.expression.TypeExpression.DOUBLE;
import static com.revenat.javamm.code.fragment.expression.TypeExpression.INTEGER;
import static com.revenat.javamm.code.util.TypeUtils.getType;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName(">= binary expression calculator")
class IsGreaterThanOrEqualsBinaryExpressionCalculatorTest extends AbstractBinaryExpressionCalculatorTest {

    static Stream<Arguments> unsupportedPairsProvider() {
        return Stream.of(
            Arguments.arguments(10, true),
            Arguments.arguments(10, false),
            Arguments.arguments(0, false),
            Arguments.arguments(true, false),
            Arguments.arguments(true, null),
            Arguments.arguments("10", 10),
            Arguments.arguments("10", true),
            Arguments.arguments(INTEGER, DOUBLE),
            Arguments.arguments(INTEGER, 10)
        );
    }

    @Test
    @Order(1)
    void shouldSupportGreaterOrEqualsBinaryOperator() {
        assertCalculatorSupportsOperator(calculator, BinaryOperator.PREDICATE_GREATER_THAN_OR_EQUALS);
    }

    @Test
    @Order(2)
    void shouldCalculateGreaterOrEqualsOperationForNumbers() {
        assertThat(calculate(2, 1), is(true));
        assertThat(calculate(5.5, 3), is(true));
        assertThat(calculate(10, 9.99), is(true));
        assertThat(calculate(5, 5.0), is(true));
        assertThat(calculate(10.00001, 10.00000001), is(true));
        assertThat(calculate(0, 0.001), is(false));
        assertThat(calculate(100, 100.00000001), is(false));
    }

    @ParameterizedTest
    @MethodSource("unsupportedPairsProvider")
    @Order(3)
    void shouldFailToCalculateGreaterOrEqualsOperationForTypesOtherThanNumbers(final Object value1, final Object value2) {
        final JavammLineRuntimeError e = assertThrows(JavammLineRuntimeError.class, () -> calculate(value1, value2));

        CustomAsserts.assertErrorMessageContains(e, "Operator '>=' is not supported for types: %s and %s",
            getType(value1), getType(value2));
    }

    @Override
    protected BinaryExpressionCalculator createCalculatorUnderTest() {
        return new IsGreaterThanOrEqualsBinaryExpressionCalculator();
    }
}
