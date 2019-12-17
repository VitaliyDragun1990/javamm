
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

import com.revenat.javamm.code.fragment.operator.UnaryOperator;
import com.revenat.javamm.interpreter.component.UnaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.arithmetic.unary.IncrementUnaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.error.JavammLineRuntimeError;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.revenat.javamm.code.fragment.expression.TypeExpression.DOUBLE;
import static com.revenat.javamm.code.fragment.expression.TypeExpression.INTEGER;
import static com.revenat.javamm.code.util.TypeUtils.getType;
import static com.revenat.javamm.interpreter.test.helper.CustomAsserts.assertErrorMessageContains;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("increment '++' unary expression calculator")
class IncrementUnaryExpressionCalculatorTest extends AbstractUnaryExpressionCalculatorTest {

    static Stream<Object> unsupportedTypesProvider() {
        return Stream.of(null, true, false, "10", INTEGER, DOUBLE);
    }

    @Test
    @Order(1)
    void shouldSupportUnaryIncrementOperator() {
        assertSupportOperator(UnaryOperator.INCREMENT);
    }

    @Test
    @Order(2)
    void shouldReturnIncrementedValueByOneForNumbers() {
        assertThat(calculate(10), is(11));
        assertThat(calculate(-10), is(-9));
        assertThat(calculate(0), is(1));
        assertThat(calculate(2.5), is(3.5));
    }

    @ParameterizedTest
    @MethodSource("unsupportedTypesProvider")
    @Order(3)
    void shouldFailIfValueNotANumber(final Object value) {
        final JavammLineRuntimeError e = assertThrows(JavammLineRuntimeError.class, () -> calculate(value));

        assertErrorMessageContains(e, "Operator '++' is not supported for type: %s", getType(value));
    }

    @Override
    protected UnaryExpressionCalculator createCalculatorUnderTest() {
        return new IncrementUnaryExpressionCalculator();
    }
}
