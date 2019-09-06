
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
import com.revenat.javamm.test.doubles.AbstractOperationInterpreterSpy;
import com.revenat.javamm.test.doubles.OperationDummy;
import com.revenat.javamm.test.doubles.OperationInterpreterSpy;

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
class BlockOperationInterpreterTest {
    private static final OperationInterpreterSpy OPERATION_A_INTERPRETER = new OperationInterpreterSpy();
    private static final OperationInterpreterSpy ANOTHER_OPERATION_A_INTERPRETER = new OperationInterpreterSpy();

    private BlockOperationInterpreter blockInterpreter;

    private Block blockOf(final Operation... operations) {
        return new Block(List.of(operations), SourceLine.EMPTY_SOURCE_LINE);
    }

    @Test
    @Order(1)
    void canBeCreatedWithDistinctOperationInterpreters() {
        blockInterpreter = new BlockOperationInterpreterImpl(Set.of(OPERATION_A_INTERPRETER));
    }

    @Test
    @Order(2)
    void canNotBeCreaterWithSeveralInterpretersForSameOperation() {
        assertThrows(ConfigException.class,
                () -> new BlockOperationInterpreterImpl(Set.of(OPERATION_A_INTERPRETER, ANOTHER_OPERATION_A_INTERPRETER)));
    }

    @Test
    @Order(3)
    void shouldFailIfTryToHandleUnsupportedOperation() {
        blockInterpreter = new BlockOperationInterpreterImpl(Set.of());

        assertThrows(ConfigException.class, () -> blockInterpreter.interpret(blockOf(new OperationDummy())));
    }

    @Test
    @Order(4)
    void shouldInterpretBlockOfSupportedOperations() {
        blockInterpreter = new BlockOperationInterpreterImpl(Set.of(OPERATION_A_INTERPRETER));

        blockInterpreter.interpret(blockOf(new OperationDummy(), new OperationDummy()));

       assertInterpreterWorked(OPERATION_A_INTERPRETER, 2);
    }

    private void assertInterpreterWorked(final AbstractOperationInterpreterSpy<?> interpreter, final int numberOfInterpretedOperations) {
        assertThat(interpreter.getNumberOfInterpretedOperations(), is(numberOfInterpretedOperations));
    }
}
