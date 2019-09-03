
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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.revenat.juinit.addons.ReplaceCamelCase;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("a type expression")
class TypeExpressionTest {
    private static final String INTEGER = "integer";
    private static final String DOUBLE = "double";
    private static final String STRING = "string";
    private static final String BOOLEAN = "boolean";

    private static final String UNKNOOWN = "unknoown";

    @Test
    @Order(1)
    void shouldSupportInteger() {
        assertThat(TypeExpression.of(INTEGER), is(notNullValue()));
    }

    @Test
    @Order(2)
    void shouldSupportDouble() {
        assertThat(TypeExpression.of(DOUBLE), is(notNullValue()));
    }

    @Test
    @Order(3)
    void shouldSupportString() {
        assertThat(TypeExpression.of(STRING), is(notNullValue()));
    }

    @Test
    @Order(4)
    void shouldSupportBoolean() {
        assertThat(TypeExpression.of(BOOLEAN), is(notNullValue()));
    }

    @Test
    @Order(5)
    void shouldFailForUnknownType() {
        assertThrows(IllegalArgumentException.class, () -> TypeExpression.of(UNKNOOWN));
    }

    @Test
    @Order(6)
    void shouldAllowToKnowIfTypeSupported() {
        assertTrue(TypeExpression.is(BOOLEAN));
        assertTrue(TypeExpression.is(INTEGER));
        assertTrue(TypeExpression.is(DOUBLE));
        assertTrue(TypeExpression.is(STRING));

        assertFalse(TypeExpression.is(UNKNOOWN));
    }

    @Test
    @Order(7)
    void shouldReturnItselfAsValue() {
        final TypeExpression booleanExpression = TypeExpression.of(BOOLEAN);

        assertSame(booleanExpression, booleanExpression.getValue(new ExpressionContextDummy()));
    }

}
