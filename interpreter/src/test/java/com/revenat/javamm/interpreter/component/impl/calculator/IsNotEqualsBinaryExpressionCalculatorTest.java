
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

import com.revenat.javamm.code.fragment.expression.TypeExpression;
import com.revenat.javamm.code.fragment.operator.BinaryOperator;
import com.revenat.javamm.interpreter.component.BinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.predicate.IsNotEqualsBinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.error.JavammLineRuntimeError;

import java.util.stream.Stream;

import static com.revenat.javamm.code.fragment.expression.TypeExpression.BOOLEAN;
import static com.revenat.javamm.code.util.TypeUtils.getType;
import static com.revenat.javamm.interpreter.test.helper.CustomAsserts.assertErrorMessageContains;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("is not equals binary expression calculator")
class IsNotEqualsBinaryExpressionCalculatorTest extends AbstractBinaryExpressionmCalculatorTest {

    @Test
    @Order(1)
    void shouldSupportPredicateNotEqualsOperator() {
        assertThat(calculator.getOperator(), is(BinaryOperator.PREDICATE_NOT_EQUALS));
    }

    @Test
    @Order(2)
    void shouldDenyNotEqualsForSameValuesOfTheSameType() {
        assertThat(calculate(1, 1.0), is(false));
        assertThat(calculate(5.0, 5), is(false));
        assertThat(calculate("test", "test"), is(false));
        assertThat(calculate(TypeExpression.BOOLEAN, TypeExpression.BOOLEAN), is(false));
        assertThat(calculate(false, false), is(false));
        assertThat(calculate(true, true), is(false));
        assertThat(calculate((Object)null, null), is(false));
    }

    @Test
    @Order(3)
    void shouldConfirmNotEqualsForDifferentValuesOfTheSameType() {
        assertThat(calculate("test", " hello"), is(true));
        assertThat(calculate(10, 15), is(true));
        assertThat(calculate(10.5, 15), is(true));
        assertThat(calculate(TypeExpression.BOOLEAN, TypeExpression.STRING), is(true));
        assertThat(calculate(false, true), is(true));
        assertThat(calculate(true, false), is(true));
    }

    @Test
    @Order(4)
    void shouldConfirmNotEqualsForValuesOfDifferentTypes() {
        assertThat(calculate((Object) null, 10), is(true));
        assertThat(calculate("test", null), is(true));
        assertThat(calculate(10.5, "10.5"), is(true));
        assertThat(calculate(TypeExpression.BOOLEAN, "boolean"), is(true));
    }

    @ParameterizedTest
    @MethodSource("uncompatiblePairsProvider")
    @Order(5)
    void shouldFailToCalculateNotEqualsBetweenBooleanAndNotBooleanType(final Object value1, final Object value2) {
        final JavammLineRuntimeError e = assertThrows(JavammLineRuntimeError.class, () -> {
            calculate(value1, value2);
        });

        assertErrorMessageContains(e, "Operator '!=' is not supported for types: %s and %s",
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
        return new IsNotEqualsBinaryExpressionCalculator();
    }
}
