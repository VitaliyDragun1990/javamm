
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
import com.revenat.javamm.interpreter.component.impl.calculator.logical.bianry.LogicalAndBinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.error.JavammLineRuntimeError;
import com.revenat.javamm.interpreter.test.doubles.ExpressionSpy;

import java.util.stream.Stream;

import static com.revenat.javamm.code.util.TypeUtils.getType;
import static com.revenat.javamm.interpreter.test.helper.CustomAsserts.assertErrorMessageContains;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("logical and (&&) binary expression calculator")
public class LogicalAndBinaryExpressionCalculatorTest extends AbstractBinaryExpressionmCalculatorTest {

    @Test
    @Order(1)
    void shouldSupportLogicalAndOperator() {
        assertCalculatorSupportsOperator(calculator, BinaryOperator.LOGICAL_AND);
    }

    @Test
    @Order(2)
    void shouldReturnTrueOnlyIfBothOperandsAreTrue() {
        final Object result = calculate(true, true);

        assertThat(result, is(true));
    }

    @Test
    @Order(3)
    void shouldReturnFalseIfEitherOfTwoOperandsIsFalse() {
        final Object result1 = calculate(true, false);
        final Object result2 = calculate(false, true);

        assertThat(result1, is(false));
        assertThat(result2, is(false));
    }

    @Test
    @Order(4)
    void shouldNotEvaluateSecondOperandIfFirstIsFalse() {
        final ExpressionSpy operandSpy = new ExpressionSpy(true);

        calculate(expressionWithValue(false), operandSpy);

        assertThat(operandSpy.numberOfValueEvaluation(), is(0));
    }

    @ParameterizedTest
    @MethodSource("unsupportedOperandProvider")
    @Order(5)
    void shouldFailToEvaluateIfFirstOperandNotABoolean(final Object operand1) {
        final Object operand2 = true;

        final JavammLineRuntimeError e = assertThrows(JavammLineRuntimeError.class, () -> calculate(operand1, true));

        assertErrorMessageContains(e, "Operator '&&' is not supported for types: %s and %s",
                getType(operand1), getType(operand2));
    }

    @ParameterizedTest
    @MethodSource("unsupportedOperandProvider")
    @Order(6)
    void shouldFailToEvaluateIfFirstOperandIsTrueAndSecondIsNotABoolean(final Object operand2) {
        final Object operand1 = true;

        final JavammLineRuntimeError e = assertThrows(JavammLineRuntimeError.class, () -> calculate(operand1, operand2));

        assertErrorMessageContains(e, "Operator '&&' is not supported for types: %s and %s",
                getType(operand1), getType(operand2));
    }

    static Stream<Arguments> unsupportedOperandProvider() {
        return Stream.of(
                Arguments.arguments("Hello"),
                Arguments.arguments((Object)null),
                Arguments.arguments(125),
                Arguments.arguments(150.25)
                );
    }

    @Override
    protected BinaryExpressionCalculator createCalculatorUnderTest() {
        return new LogicalAndBinaryExpressionCalculator();
    }
}
