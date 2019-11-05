
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

package com.revenat.javamm.compiler.component.impl.operation.block.switchh;

import com.revenat.javamm.code.fragment.Operation;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.operation.Block;
import com.revenat.javamm.code.fragment.operation.SwitchChildOperation;
import com.revenat.javamm.compiler.component.BlockOperationReader;
import com.revenat.javamm.compiler.component.error.BlockStatementIsNotClosedSyntaxError;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;
import com.revenat.javamm.compiler.component.error.JavammStructSyntaxError;
import com.revenat.javamm.compiler.component.impl.operation.SwitchChildOperationReader;

import java.util.List;
import java.util.ListIterator;

import static com.revenat.javamm.code.util.TypeUtils.confirmType;

import static java.util.stream.Collectors.toList;

/**
 * @author Vitaliy Dragun
 *
 */
public class SwitchChildOperationReaderImpl implements SwitchChildOperationReader {

    @Override
    public List<SwitchChildOperation> read(final SourceLine sourceLine,
                                           final ListIterator<SourceLine> sourceCode,
                                           final BlockOperationReader blockOperationReader) {
        final Block switchBody = readSwitchBodyContent(sourceLine, sourceCode, blockOperationReader);
        return requireSwitchChildOperationsOnly(switchBody);
    }

    private Block readSwitchBodyContent(final SourceLine sourceLine,
                                        final ListIterator<SourceLine> sourceCode,
                                        final BlockOperationReader blockOperationReader) {
        try {
            return blockOperationReader.readWithExpectedClosingCurlyBrace(sourceLine, sourceCode);
        } catch (final BlockStatementIsNotClosedSyntaxError e) {
            throw new JavammStructSyntaxError("'}' expected to close 'switch' block statement at the end of file",
                    sourceLine.getModuleName());
        }
    }

    private List<SwitchChildOperation> requireSwitchChildOperationsOnly(final Block block) {
        return block.getOperations().stream()
                .map(this::requireSwitchChildOperation)
                .collect(toList());
    }

    private SwitchChildOperation requireSwitchChildOperation(final Operation operation) {
        if (confirmType(SwitchChildOperation.class, operation)) {
            return (SwitchChildOperation) operation;
        } else {
            throw new JavammLineSyntaxError(operation.getSourceLine(), "Unsupported 'switch' child statement");
        }
    }
}
