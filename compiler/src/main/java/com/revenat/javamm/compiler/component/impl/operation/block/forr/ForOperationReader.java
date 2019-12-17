
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

package com.revenat.javamm.compiler.component.impl.operation.block.forr;

import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.operation.Block;
import com.revenat.javamm.code.fragment.operation.ForOperation;
import com.revenat.javamm.compiler.component.impl.operation.ForOperationHeader;
import com.revenat.javamm.compiler.component.impl.operation.ForOperationHeaderResolver;
import com.revenat.javamm.compiler.component.impl.operation.block.AbstractBlockOperationReader;

import java.util.ListIterator;

import static com.revenat.javamm.code.syntax.Keywords.FOR;
import static com.revenat.javamm.compiler.component.impl.util.SyntaxValidationUtils.validateClosingParenthesisBeforeOpeningCurlyBrace;
import static com.revenat.javamm.compiler.component.impl.util.SyntaxValidationUtils.validateOpeningParenthesisAfterTokenInPosition;
import static com.revenat.javamm.compiler.component.impl.util.SyntaxValidationUtils.validateThatLineEndsWithOpeningCurlyBrace;
import static java.util.Objects.requireNonNull;

/**
 * @author Vitaliy Dragun
 */
public class ForOperationReader extends AbstractBlockOperationReader<ForOperation> {

    private final ForOperationHeaderResolver headerResolver;

    public ForOperationReader(final ForOperationHeaderResolver headerResolver) {
        this.headerResolver = requireNonNull(headerResolver);
    }

    @Override
    public boolean canRead(final SourceLine sourceLine) {
        return FOR.equals(sourceLine.getFirst());
    }

    @Override
    protected void validate(final SourceLine sourceLine) {
        validateThatLineEndsWithOpeningCurlyBrace(sourceLine);
        validateThatOpeningParenthesisRightAfterForToken(sourceLine);
        validateClosingParenthesisBeforeOpeningCurlyBrace(sourceLine);
    }

    @Override
    protected ForOperation get(final SourceLine sourceLine, final ListIterator<SourceLine> sourceCode) {
        final ForOperationHeader header = headerResolver.resolve(sourceLine);
        final Block body = getBody(sourceLine, sourceCode);

        return header.mergeWith(body, sourceLine);
    }

    private Block getBody(final SourceLine sourceLine, final ListIterator<SourceLine> sourceCode) {
        return getBlockOperationReader().read(sourceLine, sourceCode);
    }

    private void validateThatOpeningParenthesisRightAfterForToken(final SourceLine sourceLine) {
        validateOpeningParenthesisAfterTokenInPosition(sourceLine, FOR, 0);
    }
}
