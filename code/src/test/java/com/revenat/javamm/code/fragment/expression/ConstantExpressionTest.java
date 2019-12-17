
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

package com.revenat.javamm.code.fragment.expression;

import com.revenat.javamm.code.component.ExpressionContext;
import com.revenat.javamm.code.test.doubles.ExpressionContextDummy;
import com.revenat.juinit.addons.ReplaceCamelCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("a constant expression")
class ConstantExpressionTest {
    private static final ExpressionContext DUMMY_CONTEXT = new ExpressionContextDummy();

    private static Stream<Arguments> supportedLiterals() {
        return Stream.of(
            Arguments.arguments("test"),
            Arguments.arguments(1),
            Arguments.arguments(10, 5),
            Arguments.arguments(true),
            Arguments.arguments(false)
        );
    }

    private static Stream<Arguments> unsupportedLiterals() {
        return Stream.of(
            Arguments.arguments(null, "null value is not allowed"),
            Arguments.arguments(10.5F, "Unsupported value type"),
            Arguments.arguments(100L, "Unsupported value type")
        );
    }

    @ParameterizedTest
    @Order(1)
    @MethodSource("supportedLiterals")
    void canBeCreatedSupportedLiteral(final Object literal) {
        assertNotNull(ConstantExpression.valueOf(literal));
    }

    @ParameterizedTest
    @Order(2)
    @MethodSource("unsupportedLiterals")
    void canNotBeCreatedForUnsupportedLiteral(final Object literal, final String errorMsg) {
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> ConstantExpression.valueOf(literal));
        assertThat(exception.getMessage(), containsString(errorMsg));
    }

    @ParameterizedTest
    @Order(3)
    @MethodSource("supportedLiterals")
    void shouldReturnValueFromWhichItHasBeenCreated(final Object literal) {
        final ConstantExpression expression = ConstantExpression.valueOf(literal);

        assertValue(expression, literal);
    }

    @Test
    @Order(4)
    void shouldBeEqualToAnotherConstantExpressionWithEqualValue() {
        assertEquals(ConstantExpression.valueOf(true), ConstantExpression.valueOf(true));
        assertEquals(ConstantExpression.valueOf(10), ConstantExpression.valueOf(10));
        assertEquals(ConstantExpression.valueOf(10.5), ConstantExpression.valueOf(10.5));
        assertEquals(ConstantExpression.valueOf("test"), ConstantExpression.valueOf("test"));
    }

    @Test
    @Order(5)
    void shouldNotBeEqualToAnotherConstantExpressionWithDifferentValue() {
        assertNotEquals(ConstantExpression.valueOf(true), ConstantExpression.valueOf(false));
        assertNotEquals(ConstantExpression.valueOf(10), ConstantExpression.valueOf(10.0));
        assertNotEquals(ConstantExpression.valueOf(true), ConstantExpression.valueOf(0));
        assertNotEquals(ConstantExpression.valueOf(false), ConstantExpression.valueOf(-1));
        assertNotEquals(ConstantExpression.valueOf(10.5), ConstantExpression.valueOf("10.5"));
        assertNotEquals(ConstantExpression.valueOf("true"), ConstantExpression.valueOf(true));
        assertNotEquals(ConstantExpression.valueOf("true"), null);
        assertNotEquals(ConstantExpression.valueOf("true"), new Object());
    }

    private void assertValue(final ConstantExpression constantExpression, final Object value) {
        assertThat(constantExpression.getValue(DUMMY_CONTEXT), equalTo(value));
    }

}
