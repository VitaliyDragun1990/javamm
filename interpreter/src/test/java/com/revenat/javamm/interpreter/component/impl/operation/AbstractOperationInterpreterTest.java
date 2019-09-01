
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

package com.revenat.javamm.interpreter.component.impl.operation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.revenat.javamm.interpreter.component.impl.OperationDummyA;
import com.revenat.javamm.interpreter.error.TerminateInterpreterException;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.revenat.juinit.addons.ReplaceCamelCase;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("a abstract operation interpreter")
class AbstractOperationInterpreterTest {

    private final TestAbstractOperationInterpreter abstractInterpreter = new TestAbstractOperationInterpreter();

    @Test
    @Order(1)
    void shouldInterpretOperation() {
        final OperationDummyA operationToInterpret = new OperationDummyA();

        abstractInterpreter.interpret(operationToInterpret);

        assertThat(abstractInterpreter.getInterpretedOperation(), Matchers.sameInstance(operationToInterpret));
    }

    @Test
    @Order(2)
    void shouldFailToInterpretIfTerminated() {
        abstractInterpreter.setTerimated(true);

        assertThrows(TerminateInterpreterException.class, () -> abstractInterpreter.interpret(new OperationDummyA()));
    }

    private class TestAbstractOperationInterpreter extends AbstractOperationInterpreter<OperationDummyA> {
        private boolean isTerimated = false;
        private  OperationDummyA interpretedOperation = null;

        OperationDummyA getInterpretedOperation() {
            return interpretedOperation;
        }

         void setTerimated(final boolean isTerimated) {
            this.isTerimated = isTerimated;
        }

        @Override
        public Class<OperationDummyA> getOperationClass() {
            return OperationDummyA.class;
        }

        @Override
        protected void interpretOperation(final OperationDummyA operation) {
            this.interpretedOperation = operation;
        }

        @Override
        protected boolean isTerminated() {
            return isTerimated;
        }
    }
}
