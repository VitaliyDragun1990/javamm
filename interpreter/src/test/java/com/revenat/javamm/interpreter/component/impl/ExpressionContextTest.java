
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

package com.revenat.javamm.interpreter.component.impl;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.revenat.javamm.code.component.ExpressionContext;
import com.revenat.javamm.code.exception.ConfigException;
import com.revenat.javamm.interpreter.component.ExpressionEvaluator;
import com.revenat.javamm.interpreter.component.ExpressionUpdater;
import com.revenat.javamm.test.builder.ExpressionContextBuilder;
import com.revenat.javamm.test.doubles.ExpressionDummy;
import com.revenat.javamm.test.doubles.ExpressionEvaluatorStub;
import com.revenat.javamm.test.doubles.ExpressionUpdaterSpy;
import com.revenat.javamm.test.doubles.UpdatableExpressionDummy;

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
@DisplayName("an expression context")
class ExpressionContextTest {
    private static final ExpressionDummy DUMMY_EXPRESSION = new ExpressionDummy();
    private static final UpdatableExpressionDummy DUMMY_UPDATABLE_EXPRESSION = new UpdatableExpressionDummy();
    private static final Object TEST_VALUE = new Object();

    private ExpressionEvaluatorStub expressionEvaluatorStub;
    private ExpressionUpdaterSpy expressionUpdaterSpy;

    private ExpressionContext expressionContext;


    @BeforeEach
    void createTestDoubles() {
        expressionEvaluatorStub = new ExpressionEvaluatorStub();
        expressionUpdaterSpy = new ExpressionUpdaterSpy();
    }

    private void assertExceptionMessage(final ConfigException e, final String msg) {
        assertThat(e.getMessage(), containsString(msg));
    }

    @Test
    @Order(1)
    void canNotBeCreatedWithSeveralEvaluatorsForSingleExpression() {
        final ExpressionEvaluator<ExpressionDummy> evaluatorA = new ExpressionEvaluatorStub();
        final ExpressionEvaluator<ExpressionDummy> evaluatorB = new ExpressionEvaluatorStub();

        final ConfigException e =
                assertThrows(
                        ConfigException.class,
                        () -> new ExpressionContextBuilder().withEvaluators(evaluatorA, evaluatorB).build()
                );
        assertExceptionMessage(e, "Duplicate of ExpressionEvaluator found");
    }

    @Test
    @Order(2)
    void canNotBeCreatedWithSeveralUpdatersForSingleExpression() {
        final ExpressionUpdater<UpdatableExpressionDummy> updaterA = new ExpressionUpdaterSpy();
        final ExpressionUpdater<UpdatableExpressionDummy> updaterB = new ExpressionUpdaterSpy();

        final ConfigException e =
                assertThrows(
                        ConfigException.class,
                        () -> new ExpressionContextBuilder().withUpdaters(updaterA, updaterB).build()
                );
        assertExceptionMessage(e, "Duplicate of ExpressionUpdater found");
    }

    @Test
    @Order(3)
    void shouldFailIfCanNotGetAValueForExpression() {
        expressionContext = new ExpressionContextBuilder().build();

        final ConfigException e = assertThrows(ConfigException.class, () -> expressionContext.getValue(DUMMY_EXPRESSION));
        assertExceptionMessage(e, "ExpressionEvaluator not defined for " + ExpressionDummy.class);
    }

    @Test
    @Order(4)
    void shouldFailIfCanNotSetAValueForExpression() {
        expressionContext = new ExpressionContextBuilder().build();

        final ConfigException e =
                assertThrows(ConfigException.class, () -> expressionContext.setValue(DUMMY_UPDATABLE_EXPRESSION, TEST_VALUE));
        assertExceptionMessage(e, "ExpressionUpdater not defined for " + UpdatableExpressionDummy.class);
    }

    @Test
    @Order(5)
    void shouldBeAbleToGetAValueForExpression() {
        expressionEvaluatorStub.setEvaluatedValue(TEST_VALUE);
        expressionContext = new ExpressionContextBuilder().withEvaluators(expressionEvaluatorStub).build();

        final Object value = expressionContext.getValue(DUMMY_EXPRESSION);

        assertThat(value, sameInstance(TEST_VALUE));
    }

    @Test
    @Order(6)
    void shouldBeAbleToSetAValueForExpression() {
        expressionContext = new ExpressionContextBuilder().withUpdaters(expressionUpdaterSpy).build();

        expressionContext.setValue(DUMMY_UPDATABLE_EXPRESSION, TEST_VALUE);

        assertThat(expressionUpdaterSpy.getLastUpdatedExpression(), sameInstance(DUMMY_UPDATABLE_EXPRESSION));
        assertThat(expressionUpdaterSpy.getLastUpdatedValue(), sameInstance(TEST_VALUE));
    }
}
