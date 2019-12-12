
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

import com.revenat.javamm.code.fragment.Expression;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.operation.Block;
import com.revenat.javamm.code.fragment.operation.WhileOperation;
import com.revenat.javamm.compiler.component.ExpressionResolver;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

import static com.revenat.javamm.code.syntax.Keywords.WHILE;
import static com.revenat.javamm.compiler.component.impl.util.SyntaxParseUtils.getTokensBetweenBrackets;
import static com.revenat.javamm.compiler.component.impl.util.SyntaxValidationUtils.validateClosingParenthesisBeforeOpeningCurlyBrace;
import static com.revenat.javamm.compiler.component.impl.util.SyntaxValidationUtils.validateOpeningParenthesisAfterTokenInPosition;
import static com.revenat.javamm.compiler.component.impl.util.SyntaxValidationUtils.validateThatLineEndsWithOpeningCurlyBrace;

import static java.util.Objects.requireNonNull;

/**
 * @author Vitaliy Dragun
 *
 */
public class WhileOperationReader extends AbstractBlockOperationReader<WhileOperation> {

    private static final String OPENING_PARENTHESIS = "(";

    private static final String CLOSING_PARENTHESIS = ")";

    private final ExpressionResolver expressionResolver;

    public WhileOperationReader(final ExpressionResolver expressionResolver) {
        this.expressionResolver = requireNonNull(expressionResolver);
    }

    @Override
    public boolean canRead(final SourceLine sourceLine) {
        return WHILE.equals(sourceLine.getFirst());
    }

    @Override
    protected void validate(final SourceLine sourceLine) {
        validateThatLineEndsWithOpeningCurlyBrace(sourceLine);
        validateThatOpeningParenthesisRightAfterWhileToken(sourceLine);
        validateClosingParenthesisBeforeOpeningCurlyBrace(sourceLine);
    }

    @Override
    protected WhileOperation get(final SourceLine sourceLine, final ListIterator<SourceLine> codeIterator) {
        final Expression condition = getCondition(sourceLine);
        final Block body = getBody(sourceLine, codeIterator);
        return new WhileOperation(sourceLine, condition, body);
    }

    private Expression getCondition(final SourceLine sourceLine) {
        final List<String> expressionTokens =
                getTokensBetweenBrackets(OPENING_PARENTHESIS, CLOSING_PARENTHESIS, sourceLine, false);
        return expressionResolver.resolve(expressionTokens, sourceLine);
    }

    private Block getBody(final SourceLine sourceLine, final ListIterator<SourceLine> codeIterator) {
        return getBlockOperationReader().read(sourceLine, codeIterator);
    }

    private void validateThatOpeningParenthesisRightAfterWhileToken(final SourceLine sourceLine) {
        validateOpeningParenthesisAfterTokenInPosition(sourceLine, WHILE, 0);
    }
}
