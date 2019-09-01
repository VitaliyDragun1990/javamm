
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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.revenat.javamm.code.exception.ConfigException;
import com.revenat.javamm.code.fragment.Operation;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.operation.Block;
import com.revenat.javamm.interpreter.component.BlockOperationInterpreter;
import com.revenat.javamm.interpreter.component.OperationInterpreter;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import com.revenat.juinit.addons.ReplaceCamelCase;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("a block operation interpreter")
class BlockOperationInterpreterImplTest {
    private static final OperationInterpreterSpyA OPERATION_A_INTERPRETER = new OperationInterpreterSpyA();
    private static final OperationInterpreterStubC ANOTHER_OPERATION_A_INTERPRETER = new OperationInterpreterStubC();
    private static final OperationInterpreterSpyB OPERATION_B_INTERPRETER = new OperationInterpreterSpyB();

    private BlockOperationInterpreter blockInterpreter;

    private Block blockOf(final Operation... operations) {
        return new Block(List.of(operations), SourceLine.EMPTY_SOURCE_LINE);
    }

    @Test
    @Order(1)
    void canBeCreatedWithDistinctOperationInterpreters() {
        blockInterpreter = new BlockOperationInterpreterImpl(Set.of(OPERATION_A_INTERPRETER, OPERATION_B_INTERPRETER));
    }

    @Test
    @Order(2)
    void canNotBeCreaterWithSereverInterpretersForSameOperation() {
        assertThrows(ConfigException.class,
                () -> new BlockOperationInterpreterImpl(Set.of(OPERATION_A_INTERPRETER, ANOTHER_OPERATION_A_INTERPRETER)));
    }

    @Test
    @Order(3)
    void shouldFailIfTryToHandleUnsupportedOperation() {
        blockInterpreter = new BlockOperationInterpreterImpl(Set.of(OPERATION_A_INTERPRETER));

        assertThrows(ConfigException.class, () -> blockInterpreter.interpret(blockOf(new OperationDummyB())));
    }

    @Test
    @Order(4)
    void shouldInterpretBlockOfSupportedOperations() {
        blockInterpreter = new BlockOperationInterpreterImpl(Set.of(OPERATION_A_INTERPRETER, OPERATION_B_INTERPRETER));

        blockInterpreter.interpret(blockOf(new OperationDummyA(), new OperationDummyB()));

       assertInterpreterWorked(OPERATION_A_INTERPRETER);
       assertInterpreterWorked(OPERATION_B_INTERPRETER);
    }

    private void assertInterpreterWorked(final OperationInterpreterSpy<?> interpreter) {
        assertThat(interpreter.isOperationInterpreted(), is(true));
    }

    private abstract static class OperationInterpreterSpy<T extends Operation> implements OperationInterpreter<T> {
        private boolean isInterpreted = false;

        boolean isOperationInterpreted() {
            return isInterpreted;
        }

        @Override
        public abstract Class<T> getOperationClass();

        @Override
        public void interpret(final T operation) {
            isInterpreted = true;
        }
    }

    private static class OperationInterpreterSpyA extends OperationInterpreterSpy<OperationDummyA> {

        @Override
        public Class<OperationDummyA> getOperationClass() {
            return OperationDummyA.class;
        }
    }

    private static class OperationInterpreterSpyB extends OperationInterpreterSpy<OperationDummyB> {

        @Override
        public Class<OperationDummyB> getOperationClass() {
            return OperationDummyB.class;
        }
    }

    private static class OperationInterpreterStubC implements OperationInterpreter<OperationDummyA> {

        @Override
        public Class<OperationDummyA> getOperationClass() {
            return OperationDummyA.class;
        }

        @Override
        public void interpret(final OperationDummyA operation) {
        }
    }
}
