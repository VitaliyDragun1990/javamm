
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
import com.revenat.javamm.code.fragment.expression.CaseExpression;
import com.revenat.javamm.code.fragment.operation.Block;
import com.revenat.javamm.code.fragment.operation.CaseOperation;
import com.revenat.javamm.compiler.component.impl.operation.CaseExpressionResolver;
import com.revenat.javamm.compiler.component.impl.operation.block.AbstractBlockOperationReader;

import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

import static com.revenat.javamm.code.syntax.Keywords.CASE;
import static com.revenat.javamm.compiler.component.impl.util.SyntaxValidationUtils.validateThatLineEndsWithOpeningCurlyBrace;
import static com.revenat.javamm.compiler.component.impl.util.SyntaxValidationUtils.validateTokenRightBeforeOpeningCurlyBrace;

/**
 * @author Vitaliy Dragun
 *
 */
public class CaseOperationReader extends AbstractBlockOperationReader<CaseOperation> {

    private static final String COLON = ":";

    private final CaseExpressionResolver labelExpressionresolver;

    public CaseOperationReader(final CaseExpressionResolver labelExpressionresolver) {
        this.labelExpressionresolver = labelExpressionresolver;
    }

    @Override
    protected Optional<String> getOperationDefiningKeyword() {
        return Optional.of(CASE);
    }

    @Override
    protected void validate(final SourceLine sourceLine) {
        validateThatLineEndsWithOpeningCurlyBrace(sourceLine);
        validateSemicolonRightBeforeOpeningCurlyBrace(sourceLine);
    }

    @Override
    protected CaseOperation get(final SourceLine sourceLine, final ListIterator<SourceLine> sourceCode) {
        final CaseExpression label = getLabel(sourceLine);
        final Block body = getBody(sourceLine, sourceCode);
        return new CaseOperation(sourceLine, label, body);
    }

    private Block getBody(final SourceLine sourceLine, final ListIterator<SourceLine> sourceCode) {
        return getBlockOperationReader().readWithExpectedClosingCurlyBrace(sourceLine, sourceCode);
    }

    private CaseExpression getLabel(final SourceLine sourceLine) {
        final List<String> expressionTokens = sourceLine.subList(1, sourceLine.indexOf(COLON));
        return labelExpressionresolver.resolve(expressionTokens, sourceLine);
    }

    private void validateSemicolonRightBeforeOpeningCurlyBrace(final SourceLine sourceLine) {
        validateTokenRightBeforeOpeningCurlyBrace(COLON, sourceLine);
    }
}
