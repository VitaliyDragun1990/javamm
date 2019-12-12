
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

package com.revenat.javamm.compiler.component.impl.operation;

import com.revenat.javamm.code.fragment.Operation;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.compiler.component.OperationReader;

import java.util.ListIterator;

/**
 * Contains common behavior for all operation readers
 *
 * @author Vitaliy Dragun
 *
 */
public abstract class AbstractOperationReader<T extends Operation> implements OperationReader {

    @Override
    public T read(final SourceLine sourceLine, final ListIterator<SourceLine> sourceCode) {
        validate(sourceLine);
        return get(sourceLine, sourceCode);
    }

    protected void validate(final SourceLine sourceLine) {
        // do nothing
    }

    protected abstract T get(SourceLine startingLine, ListIterator<SourceLine> compiledCodeIterator);
}
