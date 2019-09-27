
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

import com.revenat.javamm.code.fragment.expression.TypeExpression;
import com.revenat.javamm.code.fragment.operator.BinaryOperator;
import com.revenat.javamm.interpreter.component.BinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.predicate.TypeOfBinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.error.JavammLineRuntimeError;
import com.revenat.javamm.interpreter.test.helper.CustomAsserts;

import java.util.stream.Stream;

import static com.revenat.javamm.code.fragment.expression.TypeExpression.BOOLEAN;
import static com.revenat.javamm.code.fragment.expression.TypeExpression.DOUBLE;
import static com.revenat.javamm.code.fragment.expression.TypeExpression.INTEGER;
import static com.revenat.javamm.code.fragment.expression.TypeExpression.STRING;
import static com.revenat.javamm.code.util.TypeUtils.getType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("typeof binary expression calculator")
class TypeOfBinaryExpressionCalculatorTest extends AbstractBinaryExpressionmCalculatorTest {

    @Test
    @Order(1)
    void shouldSupportTypeOfPredicateOperator() {
        assertCalculatorSupportsOperator(calculator, BinaryOperator.PREDICATE_TYPEOF);
    }

    @Test
    @Order(2)
    void shouldSupportTypeofOperationForIntegers() {
        assertThat(calculate(10, INTEGER), is(true));
        assertThat(calculate(10.5, INTEGER), is(false));
    }

    @Test
    @Order(3)
    void shouldSupportTypeofOperationForDoubles() {
        assertThat(calculate(5.2, DOUBLE), is(true));
        assertThat(calculate(5, DOUBLE), is(false));
    }

    @Test
    @Order(4)
    void shouldSupportTypeofOperationForStrings() {
        assertThat(calculate("hello", STRING), is(true));
        assertThat(calculate(10.5, STRING), is(false));
    }

    @Test
    @Order(5)
    void shouldSupportTypeofOperationForBooleans() {
        assertThat(calculate(true, BOOLEAN), is(true));
        assertThat(calculate(false, BOOLEAN), is(true));
        assertThat(calculate("true", BOOLEAN), is(false));
    }

    @Test
    @Order(6)
    void shouldNotAssignNullToAnyType() {
        assertThat(calculate((Object) null, STRING), is(false));
        assertThat(calculate((Object) null, INTEGER), is(false));
        assertThat(calculate((Object) null, DOUBLE), is(false));
        assertThat(calculate((Object) null, BOOLEAN), is(false));
    }

    @ParameterizedTest
    @MethodSource("incorrectOrderProvider")
    @Order(7)
    void shouldFailIfIncorrectOrderOfOperands(final TypeExpression typeExpression, final Object value) {
        final JavammLineRuntimeError e = assertThrows(JavammLineRuntimeError.class, () -> calculate(typeExpression, value));

        CustomAsserts.assertErrorMessageContains(e, "Operator 'typeof' is not supported for types: %s and %s",
                getType(typeExpression), getType(value));
    }

    @ParameterizedTest
    @MethodSource("wrongOperandsProvider")
    @Order(8)
    void shouldFailIfSecondOperandNotTypeExpression(final Object value, final Object notTypeExpression) {
        final JavammLineRuntimeError e = assertThrows(JavammLineRuntimeError.class, () -> calculate(value, notTypeExpression));

        CustomAsserts.assertErrorMessageContains(e, "Operator 'typeof' is not supported for types: %s and %s",
                getType(value), getType(notTypeExpression));
    }

    static Stream<Arguments> incorrectOrderProvider() {
        return Stream.of(
                Arguments.arguments(STRING, "test"),
                Arguments.arguments(INTEGER, 10),
                Arguments.arguments(BOOLEAN, false),
                Arguments.arguments(DOUBLE, 10.5)
                );
    }

    static Stream<Arguments> wrongOperandsProvider() {
        return Stream.of(
                Arguments.arguments(false, true),
                Arguments.arguments(3, true),
                Arguments.arguments("test", "string"),
                Arguments.arguments("10", "integer")
                );
    }

    @Override
    protected BinaryExpressionCalculator createCalculatorUnderTest() {
        return new TypeOfBinaryExpressionCalculator();
    }
}
