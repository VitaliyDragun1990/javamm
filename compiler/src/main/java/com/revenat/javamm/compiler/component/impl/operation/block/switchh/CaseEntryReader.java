
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
import com.revenat.javamm.code.fragment.expression.CaseValueExpression;
import com.revenat.javamm.code.fragment.operation.Block;
import com.revenat.javamm.code.fragment.operation.SwitchCaseEntry;
import com.revenat.javamm.compiler.component.BlockOperationReader;
import com.revenat.javamm.compiler.component.impl.operation.CaseValueExpressionResolver;
import com.revenat.javamm.compiler.component.impl.operation.SwitchBodyEntryReader;

import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

import static com.revenat.javamm.code.syntax.Keywords.CASE;
import static com.revenat.javamm.compiler.component.impl.util.SyntaxValidationUtils.validateThatLineEndsWithOpeningCurlyBrace;
import static com.revenat.javamm.compiler.component.impl.util.SyntaxValidationUtils.validateTokenRightBeforeOpeningCurlyBrace;

import static java.util.Objects.requireNonNull;

/**
 * @author Vitaliy Dragun
 *
 */
public class CaseEntryReader implements SwitchBodyEntryReader<SwitchCaseEntry> {

    private static final String COLON = ":";

    private final CaseValueExpressionResolver labelExpressionresolver;

    public CaseEntryReader(final CaseValueExpressionResolver labelExpressionresolver) {
        this.labelExpressionresolver = labelExpressionresolver;
    }

    @Override
    public boolean canRead(final SourceLine sourceLine) {
        return CASE.equals(sourceLine.getFirst());
    }

    @Override
    public CaseEntry read(final SourceLine sourceLine,
                          final ListIterator<SourceLine> sourceCode,
                          final BlockOperationReader blockOperationReader) {
        validate(sourceLine);
        return get(sourceLine, sourceCode, blockOperationReader);
    }

    private void validate(final SourceLine sourceLine) {
        validateThatLineEndsWithOpeningCurlyBrace(sourceLine);
        validateSemicolonRightBeforeOpeningCurlyBrace(sourceLine);
    }

    private CaseEntry get(final SourceLine sourceLine,
                          final ListIterator<SourceLine> sourceCode,
                          final BlockOperationReader blockOperationReader) {
        final CaseValueExpression expression = getExpression(sourceLine);
        final Block body = getBody(sourceLine, sourceCode, blockOperationReader);
        return new CaseEntry(expression, body);
    }

    private Block getBody(final SourceLine sourceLine,
                          final ListIterator<SourceLine> sourceCode,
                          final BlockOperationReader blockOperationReader) {
        return blockOperationReader.readWithExpectedClosingCurlyBrace(sourceLine, sourceCode);
    }

    private CaseValueExpression getExpression(final SourceLine sourceLine) {
        final List<String> expressionTokens = sourceLine.subList(1, sourceLine.indexOf(COLON));
        return labelExpressionresolver.resolve(expressionTokens, sourceLine);
    }

    private void validateSemicolonRightBeforeOpeningCurlyBrace(final SourceLine sourceLine) {
        validateTokenRightBeforeOpeningCurlyBrace(COLON, sourceLine);
    }

    private static final class CaseEntry implements SwitchCaseEntry {

        private final CaseValueExpression expression;

        private final Block body;

        private CaseEntry(final CaseValueExpression expression, final Block body) {
            this.expression = requireNonNull(expression);
            this.body = requireNonNull(body);
        }

        public CaseValueExpression getExpression() {
            return expression;
        }

        @Override
        public Block getBody() {
            return body;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(expression.getValue());
        }

        @Override
        public boolean equals(final Object other) {
            return other != null &&
                    getClass().equals(other.getClass()) &&
                    compareExpressionsWith((CaseEntry) other);
        }

        private boolean compareExpressionsWith(final CaseEntry another) {
            return Objects.equals(expression.getValue(), another.getExpression().getValue());
        }
    }
}
