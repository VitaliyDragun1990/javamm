
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

package com.revenat.javamm.compiler.component.impl.operation.block;

import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.operation.Block;
import java.util.ListIterator;
import java.util.Optional;

import static com.revenat.javamm.compiler.component.impl.util.SyntaxValidationUtils.validateThatLineContainsOpeningCurlyBraceOnly;

/**
 * @author Vitaliy Dragun
 *
 */
public class SimpleBlockOperationReader extends AbstractBlockOperationReader<Block> {

    private static final String OPENING_CURLY_BRACE = "{";

    @Override
    protected void validate(final SourceLine sourceLine) {
        validateThatLineContainsOpeningCurlyBraceOnly(sourceLine);
    }

    @Override
    public boolean canRead(final SourceLine sourceLine) {
        return OPENING_CURLY_BRACE.equals(sourceLine.getFirst());
    }

    @Override
    protected Block get(final SourceLine sourceLine, final ListIterator<SourceLine> codeIterator) {
        return readBlockBody(sourceLine, codeIterator);
    }

    private Block readBlockBody(final SourceLine sourceLine, final ListIterator<SourceLine> codeIterator) {
        return getBlockOperationReader().read(sourceLine, codeIterator);
    }
}
