
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

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.revenat.javamm.code.fragment.Variable;
import com.revenat.javamm.code.test.doubles.ExpressionContextSpy;
import com.revenat.javamm.code.test.doubles.VariableDummy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.revenat.juinit.addons.ReplaceCamelCase;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("a variable expression")
class VariableExpressionTest {
    private static final Variable DUMMY_VARIABLE = new VariableDummy();
    private static final Object UPDATED_DATA = new Object();

    private ExpressionContextSpy expressionContextSpy;
    private VariableExpression variableExpression;


    @BeforeEach
    void setUp() {
        expressionContextSpy = new ExpressionContextSpy();
    }

    private VariableExpression build(final Variable variable) {
        return new VariableExpression(variable);
    }

    private void assertGetExpressionValue() {
        assertThat(expressionContextSpy.lastGetValueExpression(), sameInstance(variableExpression));
    }

    private void assertSetExpressionValue(final Object updatedData) {
        assertThat(expressionContextSpy.lastSetValueExpression(), sameInstance(variableExpression));
        assertThat(expressionContextSpy.lastUpdateValue(), sameInstance(updatedData));
    }

    @Test
    @Order(1)
    void canNotBeBuildWithoutVariable() {
        assertThrows(NullPointerException.class, () -> new VariableExpression(null));
    }

    @Test
    @Order(2)
    void shouldContainVariable() {
        variableExpression = build(DUMMY_VARIABLE);

        assertThat(variableExpression.getVariable(), sameInstance(DUMMY_VARIABLE));
    }

    @Test
    @Order(3)
    void shouldGetExpressionValue() {
        variableExpression = build(DUMMY_VARIABLE);

        variableExpression.getValue(expressionContextSpy);

        assertGetExpressionValue();
    }

    @Test
    @Order(4)
    void shouldSetExpressionValue() {
        variableExpression = build(DUMMY_VARIABLE);

        variableExpression.setValue(expressionContextSpy, UPDATED_DATA);

        assertSetExpressionValue(UPDATED_DATA);
    }
}
