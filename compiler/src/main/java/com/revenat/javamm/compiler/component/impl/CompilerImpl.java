
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

import com.revenat.javamm.code.component.AbstractFunctionStorage;
import com.revenat.javamm.code.fragment.ByteCode;
import com.revenat.javamm.code.fragment.FunctionName;
import com.revenat.javamm.code.fragment.SourceCode;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.function.DeveloperFunction;
import com.revenat.javamm.compiler.Compiler;
import com.revenat.javamm.compiler.component.FunctionNameBuilder;
import com.revenat.javamm.compiler.component.FunctionReader;
import com.revenat.javamm.compiler.component.SourceLineReader;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import static com.revenat.javamm.code.fragment.SourceLine.EMPTY_SOURCE_LINE;

import static java.util.Objects.requireNonNull;

/**
 * Default implementation that supports only single source code module
 *
 * @author Vitaliy Dragun
 *
 */
public class CompilerImpl implements Compiler {

    private final FunctionNameBuilder functionNameBuilder;

    private final SourceLineReader sourceLineReader;

    private final FunctionReader functionReader;

    public CompilerImpl(final SourceLineReader sourceLineReader,
                        final FunctionNameBuilder functionNameBuilder,
                        final FunctionReader functionReader) {
        this.sourceLineReader = requireNonNull(sourceLineReader);
        this.functionNameBuilder = requireNonNull(functionNameBuilder);
        this.functionReader = requireNonNull(functionReader);
    }

    @Override
    public ByteCode compile(final SourceCode... sourceCodes) {
        final FunctionName mainFunctionName = functionNameBuilder.build("main", List.of(), EMPTY_SOURCE_LINE);
        final Map<FunctionName, DeveloperFunction> definedFunctions = new LinkedHashMap<>();

        for (final SourceCode sourceCode : sourceCodes) {
            readFunctionDefinitionsFrom(sourceCode, definedFunctions);
        }

        return new ByteCodeimpl(definedFunctions, mainFunctionName);
    }

    private void readFunctionDefinitionsFrom(final SourceCode sourceCode,
                                             final Map<FunctionName, DeveloperFunction> definedFunctions) {
        final List<SourceLine> sourceLines = sourceLineReader.read(sourceCode);
        final ListIterator<SourceLine> lineIterator = sourceLines.listIterator();

        while (lineIterator.hasNext()) {
            final DeveloperFunction function = readFunctionDefinition(lineIterator, definedFunctions);
            definedFunctions.put(function.getName(), function);
        }
    }

    private DeveloperFunction readFunctionDefinition(final ListIterator<SourceLine> sourceLines,
                                                     final Map<FunctionName, DeveloperFunction> definedFunctions) {
        final DeveloperFunction function = functionReader.read(sourceLines);
        requireUnique(function, definedFunctions);
        return function;
    }

    private void requireUnique(final DeveloperFunction function,
                               final Map<FunctionName, DeveloperFunction> definedFunctions) {
        if (definedFunctions.containsKey(function.getName())) {
            throw duplicateFunctionDefinitionError(function);
        }
    }

    private JavammLineSyntaxError duplicateFunctionDefinitionError(final DeveloperFunction function) {
        return new JavammLineSyntaxError(function.getDeclarationSourceLine(),
                "Function '%s' is already defined", function.getName());
    }

    private static final class ByteCodeimpl extends AbstractFunctionStorage<DeveloperFunction> implements ByteCode {

        private final FunctionName mainFunctionName;

        protected ByteCodeimpl(final Map<FunctionName, DeveloperFunction> functionRegistry,
                               final FunctionName mainFunctionName) {
            super(functionRegistry);
            this.mainFunctionName = requireNonNull(mainFunctionName);
        }

        @Override
        public FunctionName getMainFunctionName() {
            return mainFunctionName;
        }
    }
}
