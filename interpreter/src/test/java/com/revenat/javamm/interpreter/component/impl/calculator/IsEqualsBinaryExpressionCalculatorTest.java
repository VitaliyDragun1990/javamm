
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
import com.revenat.javamm.interpreter.component.impl.calculator.predicate.IsEqualsBinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.error.JavammLineRuntimeError;

import java.util.stream.Stream;

import static com.revenat.javamm.code.fragment.expression.TypeExpression.BOOLEAN;
import static com.revenat.javamm.code.fragment.expression.TypeExpression.DOUBLE;
import static com.revenat.javamm.code.fragment.expression.TypeExpression.INTEGER;
import static com.revenat.javamm.code.fragment.expression.TypeExpression.STRING;
import static com.revenat.javamm.code.util.TypeUtils.getType;
import static com.revenat.javamm.interpreter.test.helper.CustomAsserts.assertErrorMessageContains;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("predicate equals binary expression calculator")
class IsEqualsBinaryExpressionCalculatorTest extends AbstractBinaryExpressionmCalculatorTest {

    @Test
    @Order(1)
    void shouldSupportPredicateEqualsOperator() {
        assertThat(calculator.getOperator(), is(BinaryOperator.PREDICATE_EQUALS));
    }

    @Test
    @Order(2)
    void shouldCalculateEqualsForNulls() {
        assertThat(calculate((Object)null, (Object)null), is(true));
    }

    @Test
    @Order(3)
    void shouldCalculateEqualsForNumbers() {
        assertThat(calculate(0, 0), is(true));
        assertThat(calculate(0, 0.0), is(true));
        assertThat(calculate(10.0, 10), is(true));
        assertThat(calculate(5, 5), is(true));
        assertThat(calculate(-5, 5), is(false));
        assertThat(calculate(10, 100), is(false));
    }

    @Test
    @Order(4)
    void shouldCalculateEqualsForStrings() {
        assertThat(calculate("", ""), is(true));
        assertThat(calculate("hello", "hello"), is(true));
        assertThat(calculate("HELLO", "hello"), is(false));
        assertThat(calculate(" hello ", "hello"), is(false));
        assertThat(calculate("     ", ""), is(false));
    }

    @Test
    @Order(5)
    void shouldCalculateEqualsForBooleans() {
        assertThat(calculate(false, false), is(true));
        assertThat(calculate(true, true), is(true));
        assertThat(calculate(true, false), is(false));
        assertThat(calculate(false, true), is(false));
    }

    @Test
    @Order(6)
    void shouldCalculateEqualsForTypes() {
        assertThat(calculate(BOOLEAN, BOOLEAN), is(true));
        assertThat(calculate(STRING, STRING), is(true));
        assertThat(calculate(DOUBLE, DOUBLE), is(true));
        assertThat(calculate(INTEGER, INTEGER), is(true));

        assertThat(calculate(INTEGER, DOUBLE), is(false));
        assertThat(calculate(DOUBLE, STRING), is(false));
        assertThat(calculate(STRING, BOOLEAN), is(false));
        assertThat(calculate(BOOLEAN, INTEGER), is(false));
    }

    @Test
    @Order(7)
    void shouldCalculateEqualsAsFalseForDifferentTypes() {
        assertThat(calculate(null, "hello"), is(false));
        assertThat(calculate("", null), is(false));
        assertThat(calculate(null, 10), is(false));
        assertThat(calculate(null, 0.0), is(false));
        assertThat(calculate((Object)null, STRING), is(false));
        assertThat(calculate(1, BOOLEAN), is(false));
        assertThat(calculate(1, "1"), is(false));
        assertThat(calculate(1.5, ""), is(false));
        assertThat(calculate(1.5, DOUBLE), is(false));
        assertThat(calculate("hello", STRING), is(false));
    }

    @ParameterizedTest
    @MethodSource("uncompatiblePairsProvider")
    @Order(8)
    void shouldFailToCalculateEqualsBetweenBooleanAndNotBooleanType(final Object value1, final Object value2) {
        final JavammLineRuntimeError e = assertThrows(JavammLineRuntimeError.class, () -> {
            calculate(value1, value2);
        });

        assertErrorMessageContains(e, "Operator '==' is not supported for types: %s and %s",
                getType(value1), getType(value2));
    }

    static Stream<Arguments> uncompatiblePairsProvider() {
        return Stream.of(
                Arguments.arguments(0, false),
                Arguments.arguments(false, 0),
                Arguments.arguments(10.5, true),
                Arguments.arguments(null, false),
                Arguments.arguments("hello", false),
                Arguments.arguments(BOOLEAN, false)
                );
    }

    @Override
    protected BinaryExpressionCalculator createCalculatorUnderTest() {
        return new IsEqualsBinaryExpressionCalculator();
    }
}
