
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

package com.revenat.javamm.compiler.component.impl.operation.simple;

import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.operation.PrintlnOperation;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;
import com.revenat.javamm.compiler.component.impl.operation.AbstractOperationReader;

import java.util.ListIterator;
import java.util.Optional;

/**
 * Responsible for reading {@code println} operation
 *
 * @author Vitaliy Dragun
 *
 */
public class PrintlnOperationReader extends AbstractOperationReader<PrintlnOperation> {

    @Override
    protected PrintlnOperation get(final SourceLine startingLine, final ListIterator<SourceLine> compiledCodeIterator) {
        final String text = startingLine.getToken(2);
        return new PrintlnOperation(startingLine, text);
    }

    @Override
    protected void validate(final SourceLine sourceLine) {
        if (!"(".equals(sourceLine.getToken(1))) {
            throw new JavammLineSyntaxError("Expected ( after 'println'", sourceLine);
        }
        if (!")".equals(sourceLine.getLast())) {
            throw new JavammLineSyntaxError("Expected ) at the end of the line", sourceLine);
        }
    }

    @Override
    protected Optional<String> getOperationDefiningKeyword() {
        return Optional.of("println");
    }
}