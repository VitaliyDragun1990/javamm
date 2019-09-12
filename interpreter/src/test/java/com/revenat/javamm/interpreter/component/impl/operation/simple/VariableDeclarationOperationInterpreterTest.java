
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

package com.revenat.javamm.interpreter.component.impl.operation.simple;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.revenat.javamm.code.component.ExpressionContext;
import com.revenat.javamm.code.fragment.Expression;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.Variable;
import com.revenat.javamm.code.fragment.operation.VariableDeclarationOperation;
import com.revenat.javamm.interpreter.component.impl.error.JavammLineRuntimeError;
import com.revenat.javamm.interpreter.test.doubles.ExpressionContextDummy;
import com.revenat.javamm.interpreter.test.doubles.ExpressionStub;
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
@DisplayName("a variable declaration operation interpreter")
class VariableDeclarationOperationInterpreterTest {
    private static final String VARIABLE_VALUE = "test";
    private static final SourceLine SOURCE_LINE_DUMMY = SourceLine.EMPTY_SOURCE_LINE;
    private static final ExpressionContext EXPRESSION_CONTEXT_DUMMY = new ExpressionContextDummy();
    private static final Expression EXPRESSION_STUB = new ExpressionStub(VARIABLE_VALUE);

    private Variable variableStub;

    private VariableDeclarationOperationInterpreter interpreter;

    @BeforeAll
    static void setupFakeCurrentRuntime() {
        TestCurrentRuntimeManager.setFakeCurrentRuntime(SOURCE_LINE_DUMMY);
    }

    @AfterAll
    static void releaseFakeCurrentRuntime() {
        TestCurrentRuntimeManager.releaseFakeCurrentRuntime();
    }

    @BeforeEach
    void setUp() {
        TestCurrentRuntimeManager.refreshLocalContext();
        variableStub = new VariableStub("name");

        interpreter = new VariableDeclarationOperationInterpreter(EXPRESSION_CONTEXT_DUMMY);
    }

    private VariableDeclarationOperation constantDeclaration() {
        return new VariableDeclarationOperation(SOURCE_LINE_DUMMY, true, variableStub, EXPRESSION_STUB);
    }

    private VariableDeclarationOperation notConstantDeclaration() {
        return new VariableDeclarationOperation(SOURCE_LINE_DUMMY, false, variableStub, EXPRESSION_STUB);
    }

    private void assertVariableDeclared() {
        assertThat(TestCurrentRuntimeManager.getLocalContextSpy().getLastVar(), sameInstance(variableStub));
        assertThat(TestCurrentRuntimeManager.getLocalContextSpy().getLastVarValue(), sameInstance(VARIABLE_VALUE));
    }

    private void assertFinalDeclared() {
        assertThat(TestCurrentRuntimeManager.getLocalContextSpy().getLastFinal(), sameInstance(variableStub));
        assertThat(TestCurrentRuntimeManager.getLocalContextSpy().getLastFinalValue(), sameInstance(VARIABLE_VALUE));
    }

    @Test
    @Order(1)
    void shouldDefineClassForOperationItCanInterpret() {
        assertThat(interpreter.getOperationClass(), equalTo(VariableDeclarationOperation.class));
    }

    @Test
    @Order(2)
    void shouldFailToDeclareVariableIfThereIsOneWithSameName() {
        TestCurrentRuntimeManager.getLocalContextSpy().setVariableDefined(true);

        assertThrows(JavammLineRuntimeError.class, () -> interpreter.interpret(constantDeclaration()));
    }

    @Test
    @Order(3)
    void shouldDeclareVariable() {
        interpreter.interpret(notConstantDeclaration());

        assertVariableDeclared();
    }

    @Test
    @Order(4)
    void shouldDeclareFinal() {
        interpreter.interpret(constantDeclaration());

        assertFinalDeclared();
    }
}
