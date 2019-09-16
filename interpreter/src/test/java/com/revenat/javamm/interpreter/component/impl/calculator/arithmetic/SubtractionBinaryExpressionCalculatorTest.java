
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

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.revenat.javamm.code.component.ExpressionContext;
import com.revenat.javamm.code.fragment.Expression;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.operator.BinaryOperator;
import com.revenat.javamm.code.util.TypeUtils;
import com.revenat.javamm.interpreter.component.impl.error.JavammLineRuntimeError;
import com.revenat.javamm.interpreter.test.doubles.ExpressionContextDummy;
import com.revenat.javamm.interpreter.test.doubles.ExpressionStub;
import com.revenat.javamm.interpreter.test.helper.TestCurrentRuntimeManager;

import java.util.stream.Stream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
@DisplayName("a subtraction binary expression calculator")
class SubtractionBinaryExpressionCalculatorTest {
    private static final ExpressionContext EXPRESSION_CONTEXT_DUMMY = new ExpressionContextDummy();

    private SubtractionBinaryExpressionCalculator calculator;

    @BeforeAll
    static void setupCurrentRuntimeStub() {
        TestCurrentRuntimeManager.setFakeCurrentRuntime(SourceLine.EMPTY_SOURCE_LINE);
    }

    @AfterAll
    static void releaseCurrentRuntime() {
        TestCurrentRuntimeManager.releaseFakeCurrentRuntime();
    }

    @BeforeEach
    void setUp() {
        calculator = new SubtractionBinaryExpressionCalculator();
    }

    private Expression expressionWithValue(final Object value) {
        return new ExpressionStub(value);
    }

    @Test
    @Order(1)
    void shouldSupportArithmeticalSubtractionOperator() {
        assertThat(calculator.getOperator(), is(BinaryOperator.ARITHMETIC_SUBTRACTION));
    }

    @Test
    @Order(2)
    void shouldCalculateIntegerSubtraction() {
        final Object result = calculator.calculate(EXPRESSION_CONTEXT_DUMMY, expressionWithValue(10), expressionWithValue(5));

        assertThat(result, equalTo(5));
    }

    @Test
    @Order(3)
    void shouldCalculateDoubleSubtraction() {
        final Object result = calculator.calculate(EXPRESSION_CONTEXT_DUMMY, expressionWithValue(10.0), expressionWithValue(2.0));

        assertThat(result, equalTo(8.0));
    }

    @Test
    @Order(4)
    void shouldPresentresultAsDoubleIfCalculateIntegerAndDouble() {
        final Object result = calculator.calculate(EXPRESSION_CONTEXT_DUMMY, expressionWithValue(5), expressionWithValue(7.0));

        assertThat(result, equalTo(-2.0));
    }

    @ParameterizedTest
    @MethodSource("unsubtractedPairsProvider")
    @Order(5)
    void shouldFailToCalculateNotNumberSubtraction(final Object value1, final Object value2) {
        final JavammLineRuntimeError e = assertThrows(
                JavammLineRuntimeError.class,
                () -> calculator.calculate(EXPRESSION_CONTEXT_DUMMY, expressionWithValue(value1), expressionWithValue(value2)));
        assertErrorMessageContains(e, "Operator '-' is not supported for types: %s and %s", value1, value2);
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

    private void assertErrorMessageContains(final JavammLineRuntimeError e, final String msgTemplate,
            final Object value1, final Object value2) {
        final String msg = String.format(msgTemplate, TypeUtils.getType(value1), TypeUtils.getType(value2));
        assertThat(e.getMessage(), containsString(msg));
    }
}
