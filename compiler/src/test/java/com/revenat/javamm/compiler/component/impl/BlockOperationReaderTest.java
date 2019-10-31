
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

package com.revenat.javamm.compiler.component.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.revenat.javamm.code.fragment.Expression;
import com.revenat.javamm.code.fragment.Operation;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.operation.Block;
import com.revenat.javamm.compiler.component.BlockOperationReader;
import com.revenat.javamm.compiler.component.ExpressionOperationBuilder;
import com.revenat.javamm.compiler.component.ExpressionResolver;
import com.revenat.javamm.compiler.component.OperationReader;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;
import com.revenat.javamm.compiler.test.doubles.OperationDummy;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.revenat.juinit.addons.ReplaceCamelCase;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("Block operation reader")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BlockOperationReaderTest {
    private final static SourceLine STARTING_LINE = new SourceLine("Test", 1, Arrays.asList("start"));
    private final static SourceLine TEST_LINE = new SourceLine("Test", 2, Arrays.asList("end"));
    private final ListIterator<SourceLine> COMPILED_CODE = List.of(TEST_LINE).listIterator();

    private OperationReaderStub operationReaderStub;

    @Mock
    private ExpressionResolver expressionResolver;

    @Mock
    private ExpressionOperationBuilder expressionOperationBuilder;

    private BlockOperationReader blockOperationReader;

    @BeforeEach
    void setUp() {
        operationReaderStub = new OperationReaderStub();
        blockOperationReader = new BlockOperationReaderImpl(List.of(operationReaderStub), expressionOperationBuilder, expressionResolver);
    }

    @Test
    @Order(1)
    void shouldReadBlockStartingAtSpecifiedSourceLine() {
        final Block block = blockOperationReader.read(STARTING_LINE, COMPILED_CODE);

        assertThat(block.getSourceLine(), equalTo(STARTING_LINE));
    }

    @Test
    @Order(2)
    void shouldThrowExceptionIfSourceLineCannotBeRead(@Mock final Expression expression) {
        operationReaderStub.setCanRead(false);
        when(expressionResolver.resolve(Mockito.any(), Mockito.any())).thenReturn(expression);
        when(expressionOperationBuilder.build(expression, TEST_LINE)).thenThrow(new JavammLineSyntaxError("", TEST_LINE));


        assertThrows(JavammLineSyntaxError.class, () -> blockOperationReader.read(STARTING_LINE, COMPILED_CODE));
    }

    private static class OperationReaderStub implements OperationReader {
        private boolean canRead = true;

        public void setCanRead(final boolean canRead) {
            this.canRead = canRead;
        }

        @Override
        public boolean canRead(final SourceLine sourceLine) {
            return canRead;
        }

        @Override
        public Operation read(final SourceLine sourceLine, final ListIterator<SourceLine> compiledCodeIterator) {
            return new OperationDummy();
        }
    }
}
