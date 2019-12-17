
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("a null value expression")
class NullValueExpressionTest {
    private static final ExpressionContext EXPRESSION_CONTEXT_DUMMY = new ExpressionContextDummy();

    private NullValueExpression nullValueExpression;


    @BeforeEach
    void createNullValueExpression() {
        nullValueExpression = NullValueExpression.getInstance();
    }

    @Test
    @Order(1)
    void shouldHaveValueOfNull() {
        assertNull(nullValueExpression.getValue(EXPRESSION_CONTEXT_DUMMY));
    }

    @Test
    @Order(2)
    void shouldHaveStringRepresentationAsTextNull() {
        assertThat(nullValueExpression.toString(), is("null"));
    }

    @Test
    @Order(3)
    void shouldHaveOnlyOneInstanceInTheSystem() {
        final NullValueExpression anotherExpression = NullValueExpression.getInstance();

        assertSame(nullValueExpression, anotherExpression);
    }
}
