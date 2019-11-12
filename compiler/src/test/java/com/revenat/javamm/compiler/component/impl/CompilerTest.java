
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
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.quality.Strictness.LENIENT;

import com.revenat.javamm.code.fragment.ByteCode;
import com.revenat.javamm.code.fragment.FunctionName;
import com.revenat.javamm.code.fragment.Operation;
import com.revenat.javamm.code.fragment.SourceCode;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.function.DeveloperFunction;
import com.revenat.javamm.code.fragment.operation.Block;
import com.revenat.javamm.compiler.Compiler;
import com.revenat.javamm.compiler.component.FunctionDefinitionsReader;
import com.revenat.javamm.compiler.component.FunctionNameBuilder;
import com.revenat.javamm.compiler.component.SourceLineReader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import com.revenat.juinit.addons.ReplaceCamelCase;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("compiler")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = LENIENT)
class CompilerTest {

    private static final SourceLine ANY_SOURCE_LINE = new SourceLine("test", 1, List.of());

    private static final List<Object> NO_PARAMETERS = List.of();

    private static final String MAIN_FUNCTION_NAME = "main";

    @Mock
    private FunctionNameBuilder functionNameBuilder;

    @Mock
    private SourceLineReader sourceLineReader;

    @Mock
    private FunctionDefinitionsReader functionDefinitionsReader;

    private Compiler compiler;

    @BeforeEach
    void setUp() {
        compiler = new CompilerImpl(sourceLineReader, functionNameBuilder, functionDefinitionsReader);
        when(functionNameBuilder.build(any(), any(), any())).thenReturn(mock(FunctionName.class));
    }

    @Test
    @Order(1)
    void shouldDefineMainFunctionName(@Mock final FunctionName mainFunctionName, @Mock final SourceCode sourceCode) {
        when(functionNameBuilder.build(eq(MAIN_FUNCTION_NAME), eq(NO_PARAMETERS), any())).thenReturn(mainFunctionName);

        final ByteCode byteCode = compiler.compile(sourceCode);

        assertThat(byteCode.getMainFunctionName(), equalTo(mainFunctionName));
    }

    @Test
    @Order(2)
    void shouldReadFunctionDefinitions(@Mock final SourceCode sourceCode,
                                       @Mock final SourceLine sourceLineA,
                                       @Mock final SourceLine sourceLineB) {
        final List<SourceLine> sourceLines = List.of(sourceLineA, sourceLineB);
        final List<DeveloperFunction> expectedFunctions = createDummyFunctions(2);
        when(sourceLineReader.read(sourceCode)).thenReturn(sourceLines);
        when(functionDefinitionsReader.read(sourceLines)).thenReturn(expectedFunctions);

        final ByteCode byteCode = compiler.compile(sourceCode);

        final Collection<DeveloperFunction> actualFunctions = byteCode.getAllFunctions();

        assertEqualContent(actualFunctions, expectedFunctions);
    }

    private void assertEqualContent(final Collection<DeveloperFunction> actualFunctions,
                                    final List<DeveloperFunction> expectedFunctions) {
        assertThat(actualFunctions, hasSize(expectedFunctions.size()));
        assertThat(actualFunctions, hasItems(expectedFunctions.toArray(new DeveloperFunction[0])));
    }

    private List<DeveloperFunction> createDummyFunctions(final int quantity) {
        final List<DeveloperFunction> functions = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            final FunctionName functionName = mock(FunctionName.class);
            when(functionName.getName()).thenReturn("function" + i);
            final Block functionBody = new Block(mock(Operation.class), ANY_SOURCE_LINE);
            final DeveloperFunction dummy = new DeveloperFunction.Builder()
                    .setBody(functionBody)
                    .setName(functionName)
                    .setDeclarationSourceLine(ANY_SOURCE_LINE)
                    .build();
            functions.add(dummy);
        }
        return functions;
    }
}
