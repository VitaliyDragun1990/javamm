
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

import com.revenat.javamm.code.fragment.operator.UnaryOperator;
import com.revenat.javamm.interpreter.component.UnaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.bitwise.unary.BitwiseInverseUnaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.error.JavammLineRuntimeError;

import java.util.stream.Stream;

import static com.revenat.javamm.code.fragment.expression.TypeExpression.DOUBLE;
import static com.revenat.javamm.code.fragment.expression.TypeExpression.INTEGER;
import static com.revenat.javamm.code.util.TypeUtils.getType;
import static com.revenat.javamm.interpreter.test.helper.CustomAsserts.assertErrorMessageContains;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("a bitwise inverse '~' unary expression calculator")
class BitwiseInverseUnaryExpressionCalculatorTest extends AbstractUnaryExpressionCalculatorTest {

    @Test
    @Order(1)
    void shouldSupportBitwiseInverseOperator() {
        assertSupportOperator(UnaryOperator.BITWISE_INVERSE);
    }

    @Test
    @Order(2)
    void shouldCalculateByteInversionForIntegers() {
        assertThat(calculate(2), is(-3));
        assertThat(calculate(-3), is(2));
        assertThat(calculate(0), is(-1));
    }

    @ParameterizedTest
    @MethodSource("unsupportedTypesProvider")
    @Order(3)
    void shouldFailToInverseBytesForTypesOtherThanInteger(final Object valueOfUnsupportedType) {
        final JavammLineRuntimeError e = assertThrows(JavammLineRuntimeError.class, () -> calculate(valueOfUnsupportedType));

        assertErrorMessageContains(e, "Operator '~' is not supported for type: %s", getType(valueOfUnsupportedType));
    }

    static Stream<Object> unsupportedTypesProvider() {
        return Stream.of(null, true, false, 1.05, "10", INTEGER, DOUBLE);
    }

    @Override
    protected UnaryExpressionCalculator createCalculatorUnderTest() {
        return new BitwiseInverseUnaryExpressionCalculator();
    }
}
