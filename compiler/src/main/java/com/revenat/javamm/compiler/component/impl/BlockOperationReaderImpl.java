
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

import com.revenat.javamm.code.fragment.Operation;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.operation.Block;
import com.revenat.javamm.compiler.component.BlockOperationReader;
import com.revenat.javamm.compiler.component.OperationReader;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

/**
 * @author Vitaliy Dragun
 *
 */
public class BlockOperationReaderImpl implements BlockOperationReader {
    private final Collection<OperationReader> operationReaders;

    public BlockOperationReaderImpl(final Collection<OperationReader> operationReaders) {
        this.operationReaders = operationReaders;
    }

    @Override
    public Block read(final SourceLine startingLine, final ListIterator<SourceLine> compiledCodeIterator) {
        return new Block(readOperations(compiledCodeIterator), startingLine);
    }

    private List<Operation> readOperations(final ListIterator<SourceLine> iterator) {
        final List<Operation> operations = new ArrayList<>();

        while (iterator.hasNext()) {
            final SourceLine sourceLine = iterator.next();

            final OperationReader operationReader = findOperationReaderFor(sourceLine);
            operations.add(operationReader.read(sourceLine, iterator));
        }

        return operations;
    }

    private OperationReader findOperationReaderFor(final SourceLine sourceLine) {
        return operationReaders.stream()
                .filter(reader -> reader.canRead(sourceLine))
                .findFirst()
                .orElseThrow(
                    () -> new JavammLineSyntaxError(sourceLine, "Unsupported operation: %s", sourceLine.getCommand()));
    }
}
