
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

import com.revenat.javamm.code.fragment.ByteCode;
import com.revenat.javamm.code.fragment.SourceCode;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.operation.Block;
import com.revenat.javamm.compiler.Compiler;
import com.revenat.javamm.compiler.component.BlockOperationReader;
import com.revenat.javamm.compiler.component.SourceLineReader;

import java.util.List;

/**
 * Default implementation that supports only single source code module
 *
 * @author Vitaliy Dragun
 *
 */
public class CompilerImpl implements Compiler {
    private final SourceLineReader sourceLineReader;

    private final BlockOperationReader blockOperationReader;

    public CompilerImpl(final SourceLineReader sourceLineReader, final BlockOperationReader blockOperationReader) {
        this.sourceLineReader = sourceLineReader;
        this.blockOperationReader = blockOperationReader;
    }

    @Override
    public ByteCode compile(final SourceCode... sourceCode) {
        final List<SourceLine> sourceLines = sourceLineReader.read(sourceCode[0]);
        final Block block = blockOperationReader.read(SourceLine.EMPTY_SOURCE_LINE, sourceLines.listIterator());
        return () -> block;
    }

}
