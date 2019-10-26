
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
import com.revenat.javamm.compiler.component.BlockOperationReaderAware;
import com.revenat.javamm.compiler.component.OperationReader;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;
import com.revenat.javamm.compiler.component.error.JavammStructSyntaxError;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import static com.revenat.javamm.code.util.TypeUtils.confirmType;
import static com.revenat.javamm.compiler.component.impl.util.SyntaxParseUtils.isClosingBlockOperation;
import static com.revenat.javamm.compiler.component.impl.util.SyntaxValidationUtils.validateThatLineContainsOnlyClosingCurlyBrace;

/**
 * @author Vitaliy Dragun
 *
 */
public class BlockOperationReaderImpl implements BlockOperationReader {
    private final Collection<OperationReader> operationReaders;

    public BlockOperationReaderImpl(final Collection<OperationReader> operationReaders) {
        this.operationReaders = initializeOperationReaders(operationReaders);
    }

    private List<OperationReader> initializeOperationReaders(final Collection<OperationReader> readers) {
        final List<OperationReader> result = List.copyOf(readers);
        result.forEach(this::setOperationReaderIfRequired);
        return result;
    }

    private void setOperationReaderIfRequired(final OperationReader operationReader) {
        if (confirmType(BlockOperationReaderAware.class, operationReader)) {
            ((BlockOperationReaderAware) operationReader).setBlockOperationReader(this);
        }
    }

    @Override
    public Block read(final SourceLine startingLine, final ListIterator<SourceLine> compiledCodeIterator) {
        return readBlock(startingLine, compiledCodeIterator, false);
    }

    @Override
    public Block readWithExpectedClosingCurlyBrace(final SourceLine blockStartingLine,
                                                   final ListIterator<SourceLine> compiledCodeIterator) {
        return readBlock(blockStartingLine, compiledCodeIterator, true);
    }


    private Block readBlock(final SourceLine startingLine,
                            final ListIterator<SourceLine> compiledCodeIterator,
                            final boolean closingBraceExpected) {
        final String moduleName = startingLine.getModuleName();
        return new Block(readOperations(compiledCodeIterator, moduleName, closingBraceExpected), startingLine);
    }

    private List<Operation> readOperations(final ListIterator<SourceLine> iterator,
                                           final String moduleName,
                                           final boolean closingBraceExpected) {
        final List<Operation> operations = new ArrayList<>();
        populateWithOperations(operations, iterator, moduleName, closingBraceExpected);
        return operations;
    }

    private void populateWithOperations(final List<Operation> operations,
                                        final ListIterator<SourceLine> sourceLines,
                                        final String moduleName,
                                        final boolean closingBraceExpected) {
        while (sourceLines.hasNext()) {
            final SourceLine sourceLine = sourceLines.next();

            if (isClosingBlockOperation(sourceLine)) {
                validateThatLineContainsOnlyClosingCurlyBrace(sourceLine);
                return;
            } else {
                operations.add(readOperation(sourceLines, sourceLine));
            }
        }

        checkWhetherBlockShouldEndWithClosingBrace(moduleName, closingBraceExpected);
    }

    private void checkWhetherBlockShouldEndWithClosingBrace(final String moduleName,
            final boolean closingBraceExpected) {
        if (closingBraceExpected) {
            throw new JavammStructSyntaxError("'}' expected to close block statement at the end of file",
                    moduleName);
        }
    }

    private Operation readOperation(final ListIterator<SourceLine> iterator, final SourceLine sourceLine) {
        final OperationReader operationReader = findOperationReaderFor(sourceLine);
        return operationReader.read(sourceLine, iterator);
    }

    private OperationReader findOperationReaderFor(final SourceLine sourceLine) {
        return operationReaders.stream()
                .filter(reader -> reader.canRead(sourceLine))
                .findFirst()
                .orElseThrow(
                    () -> new JavammLineSyntaxError(sourceLine, "Unsupported operation: %s", sourceLine.getCommand()));
    }
}
