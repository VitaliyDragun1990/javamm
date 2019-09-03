
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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.revenat.javamm.code.component.ExpressionContext;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.revenat.juinit.addons.ReplaceCamelCase;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("a constant expression")
class ConstantExpressionTest {
    private static final ExpressionContext DUMMY_CONTEXT = new ExpressionContextDummy();

    @Test
    void canBeCreatedForStringLiteral() {
        assertNotNull(ConstantExpression.valueOf("test"));
    }

    @Test
    void canBeCreatedForIntegerLiteral() {
        assertNotNull(ConstantExpression.valueOf(1));
    }

    @Test
    void canBeCreatedForDoubleLiteral() {
        assertNotNull(ConstantExpression.valueOf(10.5));
    }

    @Test
    void canBeCreatedForBooleanLiteral() {
        assertNotNull(ConstantExpression.valueOf(true));
        assertNotNull(ConstantExpression.valueOf(false));
    }

    @Test
    void canNotBeCreatedForUnsupportedType() {
        assertThrows(IllegalArgumentException.class, () -> ConstantExpression.valueOf(null));
        assertThrows(IllegalArgumentException.class, () -> ConstantExpression.valueOf(new Object()));
    }

    @Test
    void shouldReturnValueFromWhichItHasBeenCreated() {
        final ConstantExpression boolExpression = ConstantExpression.valueOf(true);
        final ConstantExpression intExpression = ConstantExpression.valueOf(1);
        final ConstantExpression doubleExpression = ConstantExpression.valueOf(10.5);
        final ConstantExpression stringExpression = ConstantExpression.valueOf("test");

        assertValue(boolExpression, true);
        assertValue(intExpression, 1);
        assertValue(doubleExpression, 10.5);
        assertValue(stringExpression, "test");
    }

    private void assertValue(final ConstantExpression constantExpression, final Object value) {
        assertThat(constantExpression.getValue(DUMMY_CONTEXT), equalTo(value));
    }

}
