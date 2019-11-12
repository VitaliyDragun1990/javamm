
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
import com.revenat.javamm.compiler.component.FunctionDefinitionsReader;
import com.revenat.javamm.compiler.component.FunctionNameBuilder;
import com.revenat.javamm.compiler.component.SourceLineReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.revenat.javamm.code.fragment.SourceLine.EMPTY_SOURCE_LINE;

import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;

/**
 * Default implementation that supports only single source code module
 *
 * @author Vitaliy Dragun
 *
 */
public class CompilerImpl implements Compiler {

    private final FunctionNameBuilder functionNameBuilder;

    private final SourceLineReader sourceLineReader;

    private final FunctionDefinitionsReader functionDefinitionsReader;

    public CompilerImpl(final SourceLineReader sourceLineReader,
                        final FunctionNameBuilder functionNameBuilder,
                        final FunctionDefinitionsReader functionDefinitionsReader) {
        this.sourceLineReader = requireNonNull(sourceLineReader);
        this.functionNameBuilder = requireNonNull(functionNameBuilder);
        this.functionDefinitionsReader = requireNonNull(functionDefinitionsReader);
    }

    @Override
    public ByteCode compile(final SourceCode... sourceCodes) {
        final FunctionName mainFunctionName = functionNameBuilder.build("main", List.of(), EMPTY_SOURCE_LINE);
        final List<SourceLine> aggregateSourceLines = getAggregateSourceLines(sourceCodes);
        final List<DeveloperFunction> definedFunctions = functionDefinitionsReader.read(aggregateSourceLines);

        return new ByteCodeimpl(asMap(definedFunctions), mainFunctionName);
    }

    private List<SourceLine> getAggregateSourceLines(final SourceCode... sourceCodes) {
        return Arrays.stream(sourceCodes)
                .map(sourceLineReader::read)
                .flatMap(List<SourceLine>::stream)
                .collect(toList());
    }

    private Map<FunctionName, DeveloperFunction> asMap(final List<DeveloperFunction> definedFunctions) {
        return definedFunctions.stream()
                .collect(Collectors.toMap(DeveloperFunction::getName, identity()));
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
