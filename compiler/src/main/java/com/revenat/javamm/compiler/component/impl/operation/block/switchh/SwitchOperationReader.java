
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

import com.revenat.javamm.code.fragment.Expression;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.operation.SwitchBodyEntry;
import com.revenat.javamm.code.fragment.operation.SwitchOperation;
import com.revenat.javamm.compiler.component.ExpressionResolver;
import com.revenat.javamm.compiler.component.impl.operation.SwitchBodyReader;
import com.revenat.javamm.compiler.component.impl.operation.block.AbstractBlockOperationReader;

import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

import static com.revenat.javamm.code.syntax.Keywords.SWITCH;
import static com.revenat.javamm.compiler.component.impl.util.SyntaxParseUtils.getTokensBetweenBrackets;
import static com.revenat.javamm.compiler.component.impl.util.SyntaxValidationUtils.validateClosingParenthesisBeforeOpeningCurlyBrace;
import static com.revenat.javamm.compiler.component.impl.util.SyntaxValidationUtils.validateOpeningParenthesisAfterTokenInPosition;
import static com.revenat.javamm.compiler.component.impl.util.SyntaxValidationUtils.validateThatLineEndsWithOpeningCurlyBrace;

/**
 * @author Vitaliy Dragun
 *
 */
public class SwitchOperationReader extends AbstractBlockOperationReader<SwitchOperation> {

    private static final String CLOSING_PARENTHESIS = ")";

    private static final String OPENING_PARENTHESIS = "(";

    private final SwitchBodyReader bodyReader;

    private final ExpressionResolver expressionResolver;


    public SwitchOperationReader(final SwitchBodyReader childOperationReader,
                                 final ExpressionResolver expressionResolver) {
        this.bodyReader = childOperationReader;
        this.expressionResolver = expressionResolver;
    }

    @Override
    protected Optional<String> getOperationDefiningKeyword() {
        return Optional.of(SWITCH);
    }

    @Override
    protected void validate(final SourceLine sourceLine) {
        validateThatLineEndsWithOpeningCurlyBrace(sourceLine);
        validateThatOpeningParenthesisRightAfterSwitchToken(sourceLine);
        validateClosingParenthesisBeforeOpeningCurlyBrace(sourceLine);
    }

    private void validateThatOpeningParenthesisRightAfterSwitchToken(final SourceLine sourceLine) {
        validateOpeningParenthesisAfterTokenInPosition(sourceLine, SWITCH, 0);
    }

    @Override
    protected SwitchOperation get(final SourceLine sourceLine, final ListIterator<SourceLine> sourceCode) {
        final Expression condition = getCondition(sourceLine);
        final List<SwitchBodyEntry> entries =
                bodyReader.read(sourceLine.getModuleName(), sourceCode, getBlockOperationReader());
        return new SwitchOperation(sourceLine, condition, entries);
    }

    private Expression getCondition(final SourceLine sourceLine) {
        final List<String> expressionTokens =
                getTokensBetweenBrackets(OPENING_PARENTHESIS, CLOSING_PARENTHESIS, sourceLine, false);
        return expressionResolver.resolve(expressionTokens, sourceLine);
    }
}
