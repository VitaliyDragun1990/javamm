
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

import com.revenat.javamm.code.fragment.Expression;
import com.revenat.javamm.code.fragment.Operation;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.operation.Block;
import com.revenat.javamm.compiler.component.BlockOperationReader;
import com.revenat.javamm.compiler.component.BlockOperationReaderAware;
import com.revenat.javamm.compiler.component.ExpressionOperationBuilder;
import com.revenat.javamm.compiler.component.ExpressionResolver;
import com.revenat.javamm.compiler.component.OperationReader;
import com.revenat.javamm.compiler.component.error.JavammStructSyntaxError;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

import static com.revenat.javamm.code.util.TypeUtils.confirmType;
import static com.revenat.javamm.compiler.component.impl.util.SyntaxParseUtils.isClosingBlockOperation;
import static com.revenat.javamm.compiler.component.impl.util.SyntaxValidationUtils.validateThatLineContainsClosingCurlyBraceOnly;
import static java.util.Objects.requireNonNull;

/**
 * @author Vitaliy Dragun
 */
public class BlockOperationReaderImpl implements BlockOperationReader {
    private final Collection<OperationReader> operationReaders;

    private final ExpressionOperationBuilder expressionOperationBuilder;

    private final ExpressionResolver expressionResolver;

    public BlockOperationReaderImpl(final Collection<OperationReader> operationReaders,
                                    final ExpressionOperationBuilder expressionOperationBuilder,
                                    final ExpressionResolver expressionResolver) {
        this.operationReaders = initializeOperationReaders(operationReaders);
        this.expressionOperationBuilder = requireNonNull(expressionOperationBuilder);
        this.expressionResolver = requireNonNull(expressionResolver);
    }

    @Override
    public Block read(final SourceLine blockStartingLine,
                      final ListIterator<SourceLine> compiledCodeIterator) {
        return readBlock(blockStartingLine, compiledCodeIterator);
    }

    private Block readBlock(final SourceLine startingLine,
                            final ListIterator<SourceLine> compiledCodeIterator) {
        final String moduleName = startingLine.getModuleName();
        return new Block(readOperations(compiledCodeIterator, moduleName), startingLine);
    }

    private List<Operation> readOperations(final ListIterator<SourceLine> iterator,
                                           final String moduleName) {
        final List<Operation> operations = new ArrayList<>();
        populateWithOperations(operations, iterator, moduleName);
        return operations;
    }

    private void populateWithOperations(final List<Operation> operations,
                                        final ListIterator<SourceLine> sourceCode,
                                        final String moduleName) {
        while (sourceCode.hasNext()) {
            final SourceLine sourceLine = sourceCode.next();

            if (!isBlockFinished(sourceLine)) {
                operations.add(readOperation(sourceCode, sourceLine));
            } else {
                return;
            }
        }

        throw new JavammStructSyntaxError("'}' expected to close block statement at the end of file", moduleName);
    }

    private boolean isBlockFinished(final SourceLine sourceLine) {
        if (isClosingBlockOperation(sourceLine)) {
            validateThatLineContainsClosingCurlyBraceOnly(sourceLine);
            return true;
        } else {
            return false;
        }
    }

    private Operation readOperation(final ListIterator<SourceLine> sourceCode, final SourceLine sourceLine) {
        final Optional<OperationReader> operationReader = findOperationReaderFor(sourceLine);
        if (operationReader.isPresent()) {
            return operationReader.get().read(sourceLine, sourceCode);
        } else {
            return readAsExpressionOperation(sourceLine);
        }
    }

    private Optional<OperationReader> findOperationReaderFor(final SourceLine sourceLine) {
        return operationReaders.stream()
            .filter(reader -> reader.canRead(sourceLine))
            .findFirst();
    }

    private Operation readAsExpressionOperation(final SourceLine sourceLine) {
        final Expression expression = expressionResolver.resolve(sourceLine.getTokens(), sourceLine);
        return expressionOperationBuilder.build(expression, sourceLine);
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
}
