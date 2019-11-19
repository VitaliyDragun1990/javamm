
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.quality.Strictness.LENIENT;

import com.revenat.javamm.code.fragment.FunctionName;
import com.revenat.javamm.code.fragment.Operation;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.function.DeveloperFunction;
import com.revenat.javamm.code.fragment.operation.Block;
import com.revenat.javamm.compiler.component.FunctionDefinitionsReader;
import com.revenat.javamm.compiler.component.FunctionReader;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;

import java.util.List;
import java.util.ListIterator;

import static com.revenat.javamm.compiler.test.helper.CustomAsserts.assertErrorMessageContains;

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
import org.mockito.stubbing.Answer;

import com.revenat.juinit.addons.ReplaceCamelCase;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("a function definitions reader")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = LENIENT)
class FunctionDefinitionsReaderTest {

    private static final SourceLine ANY_SOURCE_LINE = new SourceLine("module1", 1, List.of());

    @Mock
    private FunctionReader functionReader;

    private FunctionDefinitionsReader functionDefinitionsReader;

    @BeforeEach
    void setUp() {
        functionDefinitionsReader = new FunctionDefinitionsReaderImpl(functionReader);
    }

    @SuppressWarnings({ "unchecked" })
    @Test
    @Order(1)
    void shouldReadAllDefinedFunctions(@Mock final SourceLine lineA, @Mock final SourceLine lineB) {
        final List<SourceLine> sourceLines = List.of(lineA, lineB);
        final DeveloperFunction functionA = createFunctionWithName("testA");
        final DeveloperFunction functionB = createFunctionWithName("testB");
        when(functionReader.read(any(ListIterator.class)))
            .thenAnswer(withFunction(functionA))
            .thenAnswer(withFunction(functionB));

        final List<DeveloperFunction> actualFunctions = functionDefinitionsReader.read(sourceLines);

        assertThat(actualFunctions, hasSize(2));
        assertThat(actualFunctions, contains(functionA, functionB));

    }

    @SuppressWarnings("unchecked")
    @Test
    @Order(2)
    void shouldFailIfThereAreSeveralFunctionWithSameName(@Mock final SourceLine lineA, @Mock final SourceLine lineB) {
        final List<SourceLine> sourceLines = List.of(lineA, lineB);
        final DeveloperFunction functionA = createFunctionWithName("testA");
        when(functionReader.read(any(ListIterator.class)))
            .thenAnswer(withFunction(functionA))
            .thenAnswer(withFunction(functionA));

        final JavammLineSyntaxError e = assertThrows(JavammLineSyntaxError.class, () -> functionDefinitionsReader.read(sourceLines));

        assertErrorMessageContains(e, "Syntax error in 'module1' [Line: 1]: Function '%s' is already defined", "testA");
    }

    private Answer<?> withFunction(final DeveloperFunction function) {
        return (invocation) -> {
                final ListIterator<?> arg = invocation.getArgument(0);
                arg.next();
                return function;
        };
    }

    private DeveloperFunction createFunctionWithName(final String name) {
        final FunctionName functionName = mock(FunctionName.class, name);
        when(functionName.getName()).thenReturn(name);
        final Block functionBody = new Block(mock(Operation.class), ANY_SOURCE_LINE);
        return new DeveloperFunction.Builder()
                .setBody(functionBody)
                .setName(functionName)
                .setDeclarationSourceLine(ANY_SOURCE_LINE)
                .build();
    }
}
