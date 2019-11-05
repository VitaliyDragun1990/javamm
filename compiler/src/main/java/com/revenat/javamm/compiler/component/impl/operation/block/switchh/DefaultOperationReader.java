
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

import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.operation.Block;
import com.revenat.javamm.code.fragment.operation.DefaultOperation;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;
import com.revenat.javamm.compiler.component.impl.operation.block.AbstractBlockOperationReader;

import java.util.ListIterator;
import java.util.Optional;

import static com.revenat.javamm.code.syntax.Keywords.DEFAULT;
import static com.revenat.javamm.compiler.component.impl.util.SyntaxValidationUtils.validateOneTokenAfterAnotherOne;
import static com.revenat.javamm.compiler.component.impl.util.SyntaxValidationUtils.validateThatLineEndsWithOpeningCurlyBrace;
import static com.revenat.javamm.compiler.component.impl.util.SyntaxValidationUtils.validateTokenRightBeforeOpeningCurlyBrace;

/**
 * @author Vitaliy Dragun
 *
 */
public class DefaultOperationReader extends AbstractBlockOperationReader<DefaultOperation> {

    private static final String COLON = ":";

    @Override
    protected Optional<String> getOperationDefiningKeyword() {
        return Optional.of(DEFAULT);
    }

    @Override
    protected void validate(final SourceLine sourceLine) {
        validateThatLineEndsWithOpeningCurlyBrace(sourceLine);
        validateSemicolonAfterDefaultToken(sourceLine);
        validateColonBetweenDefaultTokenAndOpeningCurlyBrace(sourceLine);
    }

    @Override
    protected DefaultOperation get(final SourceLine sourceLine, final ListIterator<SourceLine> sourceCode) {
        final Block body = getBody(sourceLine, sourceCode);
        return new DefaultOperation(sourceLine, body);
    }

    private Block getBody(final SourceLine sourceLine, final ListIterator<SourceLine> sourceCode) {
        return getBlockOperationReader().readWithExpectedClosingCurlyBrace(sourceLine, sourceCode);
    }

    private void validateSemicolonAfterDefaultToken(final SourceLine sourceLine) {
        validateOneTokenAfterAnotherOne(COLON, DEFAULT, 0, sourceLine);
    }

    private void validateColonBetweenDefaultTokenAndOpeningCurlyBrace(final SourceLine sourceLine) {
        validateTokenRightBeforeOpeningCurlyBrace(COLON, sourceLine);
        if (sourceLine.getTokenCount() > 3) {
            throw new JavammLineSyntaxError(sourceLine, "'default : {' expected");
        }
    }
}
