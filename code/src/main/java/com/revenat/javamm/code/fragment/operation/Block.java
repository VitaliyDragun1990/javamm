
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

package com.revenat.javamm.code.fragment.operation;

import com.revenat.javamm.code.fragment.Operation;
import com.revenat.javamm.code.fragment.SourceLine;

import java.util.List;

import static java.lang.System.lineSeparator;
import static java.util.stream.Collectors.joining;

/**
 * Represents block of compiled code which can compose several operations.
 *
 * @author Vitaliy Dragun
 */
public final class Block extends AbstractOperation implements Operation {
    private final List<Operation> operations;

    /**
     * Creates new byte code block which contains specified operations
     *
     * @param operations operations this block contains
     * @param sourceLine source line where this block starts
     */
    public Block(final List<Operation> operations, final SourceLine sourceLine) {
        super(sourceLine);
        this.operations = List.copyOf(operations);
    }

    /**
     * Creates new byte code block which contains specified operation
     *
     * @param operation  operation this block contains
     * @param sourceLine source line where this block starts
     */
    public Block(final Operation operation, final SourceLine sourceLine) {
        this(List.of(operation), sourceLine);
    }

    /**
     * Returns operations this block contains
     */
    public List<Operation> getOperations() {
        return operations;
    }

    @Override
    public String toString() {
        return operations.stream()
            .map(Object::toString)
            .collect(joining(lineSeparator()));
    }
}
