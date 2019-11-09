
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
import com.revenat.javamm.code.fragment.operation.Block;
import com.revenat.javamm.compiler.Compiler;
import com.revenat.javamm.compiler.component.BlockOperationReader;
import com.revenat.javamm.compiler.component.FunctionNameBuilder;
import com.revenat.javamm.compiler.component.SourceLineReader;

import java.util.List;
import java.util.Map;

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

    private final BlockOperationReader blockOperationReader;

    public CompilerImpl(final FunctionNameBuilder functionNameBuilder,
                        final SourceLineReader sourceLineReader,
                        final BlockOperationReader blockOperationReader) {
        this.sourceLineReader = requireNonNull(sourceLineReader);
        this.blockOperationReader = requireNonNull(blockOperationReader);
        this.functionNameBuilder = requireNonNull(functionNameBuilder);
    }

    @Override
    public ByteCode compile(final SourceCode... sourceCodes) {
        final SourceCode sourceCode = sourceCodes[0];
        final List<SourceLine> sourceLines = sourceLineReader.read(sourceCode);
        final SourceLine sourceLine = new SourceLine(sourceCode.getModuleName(), 0, List.of());
        final Block block = blockOperationReader.read(sourceLine, sourceLines.listIterator());

        final FunctionName mainFunctionName = functionNameBuilder.build("main", List.of(), sourceLine);
        final DeveloperFunction mainFunction = new DeveloperFunction.Builder()
            .setName(mainFunctionName)
            .setBody(block)
            .build();
        return new ByteCodeimpl(Map.of(mainFunctionName, mainFunction), mainFunctionName);
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
