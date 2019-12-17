
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

import com.revenat.javamm.code.syntax.Keywords;
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
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("a type expression")
class TypeExpressionTest {
    private static final String UNSUPPORTED = "long";

    private static Stream<Arguments> backendToSupportTypes() {
        return Stream.of(
            arguments(Keywords.BOOLEAN, Boolean.class),
            arguments(Keywords.INTEGER, Integer.class),
            arguments(Keywords.DOUBLE, Double.class),
            arguments(Keywords.STRING, String.class)
        );
    }

    private static Stream<Arguments> typeExpressionToKeyword() {
        return Stream.of(
            arguments(TypeExpression.BOOLEAN, Keywords.BOOLEAN),
            arguments(TypeExpression.STRING, Keywords.STRING),
            arguments(TypeExpression.INTEGER, Keywords.INTEGER),
            arguments(TypeExpression.DOUBLE, Keywords.DOUBLE)
        );
    }

    private void assertType(final TypeExpression expression, final Class<?> expectedType) {
        assertThat(expression.getType(), is(expectedType));
    }

    private void assertDoesNotSupport(final String keyword) {
        assertFalse(TypeExpression.is(keyword));
    }

    private void assertSupports(final String keyword) {
        assertTrue(TypeExpression.is(keyword));
    }

    @ParameterizedTest
    @Order(1)
    @ValueSource(strings = {
        Keywords.STRING,
        Keywords.INTEGER,
        Keywords.DOUBLE,
        Keywords.BOOLEAN,
    })
    void shouldAllowToCheckIfTypeIsSupported(final String typeKeyword) {
        assertSupports(typeKeyword);
    }

    @Test
    @Order(2)
    void shouldAllowToCheckIfTypeIsUnsupported() {
        assertDoesNotSupport(UNSUPPORTED);
    }

    @ParameterizedTest
    @Order(3)
    @MethodSource("backendToSupportTypes")
    void shouldHoldCorrectBackendTypeForEachSupportedType(final String typeKeyword, final Class<?> backendType) {
        assertType(TypeExpression.of(typeKeyword), backendType);
    }

    @Test
    @Order(4)
    void shouldFailForUnknownType() {
        assertThrows(IllegalArgumentException.class, () -> TypeExpression.of(UNSUPPORTED));
    }

    @ParameterizedTest
    @Order(5)
    @EnumSource(TypeExpression.class)
    void shouldReturnItselfAsValue(final TypeExpression expression) {
        assertSame(expression, expression.getValue(new ExpressionContextDummy()));
    }

    @ParameterizedTest
    @Order(6)
    @MethodSource("typeExpressionToKeyword")
    void shouldHoldKeywordWhichItRepresents(final TypeExpression expression, final String keyword) {
        assertThat(expression.getKeyword(), is(keyword));
    }
}
