
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

import com.revenat.javamm.code.component.ExpressionContext;
import com.revenat.javamm.interpreter.error.TerminateInterpreterException;
import com.revenat.javamm.interpreter.test.doubles.ExpressionContextDummy;
import com.revenat.javamm.interpreter.test.doubles.OperationDummy;
import com.revenat.juinit.addons.ReplaceCamelCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("a abstract operation interpreter")
class AbstractOperationInterpreterTest {

    private final AbstractOperationInterpreterSpy abstractInterpreter = new AbstractOperationInterpreterSpy(new ExpressionContextDummy());

    private void assertOperationInterpreted(final OperationDummy operationToInterpret) {
        assertThat(abstractInterpreter.getInterpretedOperation(), sameInstance(operationToInterpret));
    }

    @Test
    @Order(1)
    void canNotBeCreatedWithoutExpressionContext() {
        assertThrows(NullPointerException.class, () -> new AbstractOperationInterpreterSpy(null));
    }

    @Test
    @Order(2)
    void shouldInterpretOperation() {
        final OperationDummy operationToInterpret = new OperationDummy();

        abstractInterpreter.interpret(operationToInterpret);

        assertOperationInterpreted(operationToInterpret);
    }

    @Test
    @Order(3)
    void shouldFailToInterpretIfTerminated() {
        abstractInterpreter.setTerminated();

        assertThrows(TerminateInterpreterException.class, () -> abstractInterpreter.interpret(new OperationDummy()));
    }

    private static class AbstractOperationInterpreterSpy extends AbstractOperationInterpreter<OperationDummy> {
        private boolean isTerminated = false;

        private OperationDummy interpretedOperation = null;

        public AbstractOperationInterpreterSpy(final ExpressionContext expressionContext) {
            super(expressionContext);
        }

        OperationDummy getInterpretedOperation() {
            return interpretedOperation;
        }

        @Override
        public Class<OperationDummy> getOperationClass() {
            return OperationDummy.class;
        }

        @Override
        protected void interpretOperation(final OperationDummy operation) {
            this.interpretedOperation = operation;
        }

        @Override
        protected boolean isTerminated() {
            return isTerminated;
        }

        void setTerminated() {
            this.isTerminated = true;
        }
    }
}
