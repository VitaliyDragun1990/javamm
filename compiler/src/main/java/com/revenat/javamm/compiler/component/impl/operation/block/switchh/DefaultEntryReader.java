
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
import com.revenat.javamm.code.fragment.operation.SwitchDefaultEntry;
import com.revenat.javamm.compiler.component.BlockOperationReader;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;
import com.revenat.javamm.compiler.component.impl.operation.SwitchBodyEntryReader;

import java.util.ListIterator;
import static com.revenat.javamm.code.syntax.Keywords.DEFAULT;
import static com.revenat.javamm.compiler.component.impl.util.SyntaxValidationUtils.validateOneTokenAfterAnotherOne;
import static com.revenat.javamm.compiler.component.impl.util.SyntaxValidationUtils.validateThatLineEndsWithOpeningCurlyBrace;
import static com.revenat.javamm.compiler.component.impl.util.SyntaxValidationUtils.validateTokenRightBeforeOpeningCurlyBrace;

/**
 * @author Vitaliy Dragun
 *
 */
public class DefaultEntryReader implements SwitchBodyEntryReader<SwitchDefaultEntry> {

    private static final String COLON = ":";

    @Override
    public boolean canRead(final SourceLine sourceLine) {
        return DEFAULT.equals(sourceLine.getFirst());
    }

    @Override
    public DefaultEntry read(final SourceLine sourceLine,
                             final ListIterator<SourceLine> sourceCode,
                             final BlockOperationReader blockOperationReader) {
        validate(sourceLine);
        return get(sourceLine, sourceCode, blockOperationReader);
    }

    private void validate(final SourceLine sourceLine) {
        validateThatLineEndsWithOpeningCurlyBrace(sourceLine);
        validateSemicolonAfterDefaultToken(sourceLine);
        validateColonBetweenDefaultTokenAndOpeningCurlyBrace(sourceLine);
    }

    private DefaultEntry get(final SourceLine sourceLine,
                             final ListIterator<SourceLine> sourceCode,
                             final BlockOperationReader blockOperationReader) {
        final Block body = getBody(sourceLine, sourceCode, blockOperationReader);
        return new DefaultEntry(body);
    }

    private Block getBody(final SourceLine sourceLine,
                          final ListIterator<SourceLine> sourceCode,
                          final BlockOperationReader blockOperationReader) {
        return blockOperationReader.read(sourceLine, sourceCode);
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

    private static final class DefaultEntry implements SwitchDefaultEntry {

        private final Block body;

        private DefaultEntry(final Block body) {
            this.body = body;
        }

        @Override
        public Block getBody() {
            return body;
        }

        @Override
        public int hashCode() {
            return getClass().hashCode();
        }

        @Override
        public boolean equals(final Object other) {
            return other != null && getClass().equals(other.getClass());
        }
    }
}
