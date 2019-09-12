
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

package com.revenat.javamm.interpreter.component.impl.expression.evaluator;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.expression.VariableExpression;
import com.revenat.javamm.interpreter.component.impl.error.JavammLineRuntimeError;
import com.revenat.javamm.interpreter.component.impl.expression.evaluator.VariableExpressionEvaluator;
import com.revenat.javamm.interpreter.test.doubles.VariableStub;
import com.revenat.javamm.interpreter.test.helper.TestCurrentRuntimeManager;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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
@DisplayName("a variable expression evaluator")
class VariableExpressionEvaluatorTest {
    private static final String VARIABLE_VALUE = "test";
    private static final VariableStub VARIABLE = new VariableStub("name");
    private static final VariableExpression VARIABLE_EXPRESSION = new VariableExpression(VARIABLE);

    private VariableExpressionEvaluator evaluator;

    @BeforeAll
    static void setupCurrentRuntimeStub() {
        TestCurrentRuntimeManager.setFakeCurrentRuntime(SourceLine.EMPTY_SOURCE_LINE);
    }

    @AfterAll
    static void releaseCurrentRuntime() {
        TestCurrentRuntimeManager.releaseFakeCurrentRuntime();
    }

    @BeforeEach
    void setUp() {
        TestCurrentRuntimeManager.refreshLocalContext();
        evaluator = new VariableExpressionEvaluator();
    }

    @Test
    @Order(1)
    void shouldDefineClassForExpressionItCanEvaluate() {
        assertThat(evaluator.getExpressionClass(), equalTo(VariableExpression.class));
    }

    @Test
    @Order(2)
    void shouldFailToEvaluateIfVariableNotDefined() {
        TestCurrentRuntimeManager.getLocalContextSpy().setVariableDefined(false);

        assertThrows(
                JavammLineRuntimeError.class,
                () -> evaluator.evaluate(VARIABLE_EXPRESSION));
    }

    @Test
    @Order(3)
    void shouldEvaluateVariableExpression() {
        TestCurrentRuntimeManager.getLocalContextSpy().setVariableDefined(true);
        TestCurrentRuntimeManager.getLocalContextSpy().setVariableValue(VARIABLE_VALUE);

        final Object value = evaluator.evaluate(VARIABLE_EXPRESSION);

        assertThat(value, equalTo(VARIABLE_VALUE));
    }
}
