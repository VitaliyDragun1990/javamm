
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

package com.revenat.javamm.code.fragment.operation;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;

import com.revenat.javamm.code.fragment.Operation;
import com.revenat.javamm.code.fragment.ReplaceCamelCase;
import com.revenat.javamm.code.fragment.SourceLine;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("a block")
class BlockTest {
    private static final SourceLine BLOCK_STARTING_LINE = new SourceLine("test", 1, List.of());
    private static final SourceLine OPERATION_A_LINE = new SourceLine("test", 2, List.of("var", "a", "=", "10"));
    private static final SourceLine OPERATION_B_LINE = new SourceLine("test", 3, List.of("var", "b", "=", "0"));

    private Block block;

    private Operation blockOperationA;
    private Operation blockOperationB;

    @BeforeEach
    void setUp() {
        blockOperationA = new OperationStub(OPERATION_A_LINE);
        blockOperationB = new OperationStub(OPERATION_B_LINE);
        block = new Block(List.of(blockOperationA, blockOperationB), BLOCK_STARTING_LINE);
    }

    @Test
    @Order(1)
    void canNotBeCreatedWithoutSourceLine() {
        assertThrows(NullPointerException.class, () -> new Block(blockOperationA, null));
    }

    @Test
    @Order(2)
    void canNotBeCreatedWithoutOperation() {
        assertThrows(NullPointerException.class, () -> new Block(((Operation) null), BLOCK_STARTING_LINE));
    }

    @Test
    @Order(3)
    void canNotBeCreatedWithNullOperation() {
        assertThrows(NullPointerException.class, () -> new Block(List.of(null, blockOperationA), BLOCK_STARTING_LINE));
    }

    @Test
    @Order(4)
    void shouldProvideContainingOperations() {
        final List<Operation> blockOperations = block.getOperations();

        assertThat(blockOperations, hasSize(2));
        assertThat(blockOperations, hasItems(blockOperationA, blockOperationB));
    }

    @Test
    @Order(5)
    void shouldReturnStringRepresentationOfContainingOperations() {
        assertThat(block.toString(), containsString(blockOperationA.toString()));
        assertThat(block.toString(), containsString(blockOperationB.toString()));
    }

    private class OperationStub implements Operation {
        private final SourceLine sourceLine;

        public OperationStub(final SourceLine sourceLine) {
            this.sourceLine = sourceLine;
        }

        @Override
        public SourceLine getSourceLine() {
            return sourceLine;
        }

        @Override
        public String toString() {
            return sourceLine.toString();
        }

    }
}
