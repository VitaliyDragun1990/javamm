
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
import static org.junit.jupiter.api.Assertions.*;

import com.revenat.javamm.code.fragment.ByteCode;
import com.revenat.javamm.code.fragment.Operation;
import com.revenat.javamm.code.fragment.SourceCode;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.operation.Block;
import com.revenat.javamm.compiler.Compiler;
import com.revenat.javamm.compiler.component.BlockOperationReader;
import com.revenat.javamm.compiler.component.SourceLineReader;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.revenat.juinit.addons.ReplaceCamelCase;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("compiler")
class CompilerImplTest {
    private final static List<String> SOURCE_CODE_LINES = List.of("println(test");
    private final static SourceLine OPERATION_SOURCE_LINE = new SourceLine("", 1, List.of("println", "(", "test", ")"));
    private final static List<SourceLine> COMPILED_SOURCE_LINES = List.of(OPERATION_SOURCE_LINE);

    private Compiler compiler;

    private final SourceCodeStub sourceCodeStub = new SourceCodeStub();
    private final SourceLineReaderSpy sourceLineReaderSpy = new SourceLineReaderSpy();
    private final BlockOperationReaderSpy blockOperationReaderSpy = new BlockOperationReaderSpy();

    @BeforeEach
    void setUp() {
        compiler = new CompilerImpl(sourceLineReaderSpy, blockOperationReaderSpy);
    }

    @Test
    void shouldCompileSourceCodeIntoByteCode() {
        final ByteCode byteCode = compiler.compile(sourceCodeStub);

        assertNotNull(byteCode, "compiled byte code can not be null");
        assertCallToSourceLineReader();
        assertCallToBlockOperationReader();
    }

    private void assertCallToBlockOperationReader() {
        assertThat("block operation reader should have been called once",
                blockOperationReaderSpy.getCallTimesFor(COMPILED_SOURCE_LINES.listIterator()), equalTo(1));
    }

    private void assertCallToSourceLineReader() {
        assertThat("source line reader should have been called once",
                sourceLineReaderSpy.getCallTimesFor(sourceCodeStub), equalTo(1));
    }

    private class SourceCodeStub implements SourceCode {

        @Override
        public String getModuleName() {
            return "";
        }

        @Override
        public List<String> getLines() {
            return SOURCE_CODE_LINES;
        }

    }

    private class SourceLineReaderSpy implements SourceLineReader {
        private final Map<SourceCode, Integer> callMap = new HashMap<>();

        @Override
        public List<SourceLine> read(final SourceCode sourceCode) {
            callMap.merge(sourceCode, 1, (a, b) -> a + b);
            return COMPILED_SOURCE_LINES;
        }

        int getCallTimesFor(final SourceCode arg) {
            return callMap.getOrDefault(arg, 0);
        }
    }

    private class BlockOperationReaderSpy implements BlockOperationReader {
        final Operation DUMMY_OPERATION = () -> OPERATION_SOURCE_LINE;
        final Map<SourceLine, Integer> callMap = new HashMap<>();

        @Override
        public Block read(final SourceLine startingLine, final ListIterator<SourceLine> compiledCodeIterator) {
            callMap.merge(compiledCodeIterator.next(), 1, (a, b) -> a + b);
            compiledCodeIterator.previous();
            return new Block(DUMMY_OPERATION, SourceLine.EMPTY_SOURCE_LINE);
        }

        @Override
        public Block readWithExpectedClosingCurlyBrace(final SourceLine blockStartingLine,
                final ListIterator<SourceLine> compiledCodeIterator) {
            // TODO Auto-generated method stub
            return null;
        }

        int getCallTimesFor(final ListIterator<SourceLine> compiledCodeIterator) {
            return callMap.getOrDefault(compiledCodeIterator.next(), 0);
        }

    }

}
