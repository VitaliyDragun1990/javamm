
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

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.revenat.javamm.code.fragment.Variable;
import com.revenat.javamm.interpreter.component.impl.error.JavammLineRuntimeError;
import com.revenat.javamm.interpreter.model.CurrentRuntimeProvider;
import com.revenat.javamm.interpreter.model.LocalContext;
import com.revenat.javamm.interpreter.test.doubles.CurrentRuntimeStub;
import com.revenat.javamm.interpreter.test.doubles.VariableStub;

import static com.revenat.javamm.interpreter.test.helper.CustomAsserts.assertErrorMessageContains;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.revenat.juinit.addons.ReplaceCamelCase;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("a local context")
class LocalContextTest {
    private static final Variable VARIABLE = new VariableStub("var");
    private static final Variable FINAL = new VariableStub("final");
    private static final Object FINAL_VALUE = "final value";
    private static final Object VAR_VALUE = "variable value";

    protected LocalContext localContext;

    @BeforeAll
    static void setTestCurrentRuntime() {
        CurrentRuntimeProvider.setCurrentRuntime(CurrentRuntimeStub.simple());
    }

    void assertNotDefined(final Variable variable) {
        assertFalse(localContext.isVariableDefined(variable));
    }

    void assertDefined(final Variable variable) {
        assertTrue(localContext.isVariableDefined(variable));
    }

    void assertValue(final Variable variable, final Object expectedValue) {
        final Object actualValue = localContext.getVariableValue(variable);
        assertThat(actualValue, sameInstance(expectedValue));
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("when empty")
    class WhenEmptyTest {

        @BeforeEach
        void setUp() {
            localContext = new LocalContextImpl();
        }

        @Test
        @Order(1)
        void shouldConfirmVariableNotDefined() {
            assertNotDefined(VARIABLE);
        }

        @Test
        @Order(2)
        void shouldConfirmFinalNotDefined() {
            assertNotDefined(FINAL);
        }

        @Test
        @Order(3)
        void shouldFailIfGetVariableValue() {
            final JavammLineRuntimeError e = assertThrows(JavammLineRuntimeError.class,
                    () -> localContext.getVariableValue(VARIABLE));

            assertErrorMessageContains(e, "variable is not defined");
        }

        @Test
        @Order(4)
        void shouldFailIfGetFinalValue() {
            final JavammLineRuntimeError e = assertThrows(JavammLineRuntimeError.class,
                    () -> localContext.getVariableValue(FINAL));

            assertErrorMessageContains(e, "variable is not defined");
        }

        @Test
        @Order(5)
        void shouldDefineNewFinal() {
            localContext.setFinalValue(FINAL, FINAL_VALUE);

            assertDefined(FINAL);
        }

        @Test
        @Order(6)
        void shouldDefineNewVariable() {
            localContext.setFinalValue(VARIABLE, VAR_VALUE);

            assertDefined(VARIABLE);
        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("when contains final")
    class WhenContainsFinalTest {

        @BeforeEach
        void setUp() {
            localContext = new LocalContextImpl();
            localContext.setFinalValue(FINAL, FINAL_VALUE);
        }

        @Test
        @Order(1)
        void shouldGetValueForThatFinal() {
            assertValue(FINAL, FINAL_VALUE);
        }

        @Test
        @Order(2)
        void shouldFailToSetValueFoThatFinal() {
            final JavammLineRuntimeError e = assertThrows(
                    JavammLineRuntimeError.class,
                    () -> localContext.setFinalValue(FINAL, "new final value"));

            assertErrorMessageContains(e, "can not be changed");
        }

        @Test
        @Order(3)
        void shouldFailToDefineVariableWithSameName() {
            final Variable nameLikeFinal = new VariableStub("final");

            final JavammLineRuntimeError e = assertThrows(
                    JavammLineRuntimeError.class,
                    () -> localContext.setFinalValue(nameLikeFinal, VAR_VALUE));

            assertErrorMessageContains(e, "can not be changed");
        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("when contains variable")
    class WhenContainsVariableTest {

        @BeforeEach
        void setUp() {
            localContext = new LocalContextImpl();
            localContext.setVariableValue(VARIABLE, VAR_VALUE);
        }

        @Test
        @Order(1)
        void shouldGetValueForThatVariable() {
            assertValue(VARIABLE, VAR_VALUE);
        }

        @Test
        @Order(2)
        void shouldUpdateValueForThatVariableWhenSetAgain() {
            final String newVariableValue = "new variable value";

            localContext.setVariableValue(VARIABLE, newVariableValue);

            assertValue(VARIABLE, newVariableValue);
        }

        @Test
        @Order(3)
        void shouldFailToDefineFinalWithSameName() {
            final Variable nameLikeVar = new VariableStub("var");

            final JavammLineRuntimeError e = assertThrows(
                    JavammLineRuntimeError.class,
                    () -> localContext.setFinalValue(nameLikeVar, FINAL_VALUE));

            assertErrorMessageContains(e, "variable with same name is already defined");
        }
    }
}
