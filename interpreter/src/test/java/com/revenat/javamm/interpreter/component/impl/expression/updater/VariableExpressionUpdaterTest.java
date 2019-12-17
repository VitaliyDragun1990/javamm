
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

package com.revenat.javamm.interpreter.component.impl.expression.updater;

import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.expression.VariableExpression;
import com.revenat.javamm.interpreter.component.impl.error.JavammLineRuntimeError;
import com.revenat.javamm.interpreter.test.doubles.VariableStub;
import com.revenat.javamm.interpreter.test.helper.TestCurrentRuntimeManager;
import com.revenat.juinit.addons.ReplaceCamelCase;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static com.revenat.javamm.interpreter.test.helper.CustomAsserts.assertErrorMessageContains;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("a variable expression updater")
class VariableExpressionUpdaterTest {
    private static final String UPDATED_VARIABLE_VALUE = "updated value";

    private static final VariableStub VARIABLE = new VariableStub("a");

    private static final VariableExpression VARIABLE_EXPRESSION = new VariableExpression(VARIABLE);

    private VariableExpressionUpdater variableExpressionUpdater;

    @BeforeAll
    static void setupFakeRuntime() {
        TestCurrentRuntimeManager.setFakeCurrentRuntime(SourceLine.EMPTY_SOURCE_LINE);
    }

    @AfterAll
    static void releaseFakeRuntime() {
        TestCurrentRuntimeManager.releaseFakeCurrentRuntime();
    }


    @BeforeEach
    void setUp() {
        TestCurrentRuntimeManager.refreshLocalContext();
        variableExpressionUpdater = new VariableExpressionUpdater();
    }

    @Test
    @Order(1)
    void shouldConfirmItCanUpdateVariableExpression() {
        assertThat(variableExpressionUpdater.getExpressionClass(), is(VariableExpression.class));
    }

    @Test
    @Order(2)
    void shouldFailIfVariableToUpdateHasNotBeenDefinedYet() {
        TestCurrentRuntimeManager.getLocalContextSpy().setVariableDefined(false);

        final JavammLineRuntimeError e = assertThrows(JavammLineRuntimeError.class,
            () -> variableExpressionUpdater.update(VARIABLE_EXPRESSION, UPDATED_VARIABLE_VALUE));

        assertErrorMessageContains(e, "Variable '%s' is not defined", VARIABLE);
    }

    @Test
    @Order(3)
    void shouldUpdateVariableExpression() {
        TestCurrentRuntimeManager.getLocalContextSpy().setVariableDefined(true);

        variableExpressionUpdater.update(VARIABLE_EXPRESSION, UPDATED_VARIABLE_VALUE);

        assertThat(TestCurrentRuntimeManager.getLocalContextSpy().getLastVarValue(), is(UPDATED_VARIABLE_VALUE));
    }
}
