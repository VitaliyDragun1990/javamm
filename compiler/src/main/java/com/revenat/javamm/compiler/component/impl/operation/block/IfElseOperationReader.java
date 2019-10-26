
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
import com.revenat.javamm.code.fragment.operation.IfElseOperation;
import com.revenat.javamm.compiler.component.ExpressionResolver;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;

import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

import static com.revenat.javamm.code.syntax.Keywords.ELSE;
import static com.revenat.javamm.code.syntax.Keywords.IF;
import static com.revenat.javamm.compiler.component.impl.util.SyntaxParseUtils.getTokensBetweenBrackets;
import static com.revenat.javamm.compiler.component.impl.util.SyntaxValidationUtils.validateThatLineEndsWithOpeningCurlyBrace;

import static java.util.Objects.requireNonNull;

/**
 * @author Vitaliy Dragun
 *
 */
public class IfElseOperationReader extends AbstractBlockOperationReader<IfElseOperation> {

    private static final String OPENING_PARENTHESIS = "(";

    private static final String CLOSING_PARENTHESIS = ")";

    private static final String OPENING_CURLY_BRACE = "{";

    private final ExpressionResolver expressionResolver;

    public IfElseOperationReader(final ExpressionResolver expressionResolver) {
        this.expressionResolver = requireNonNull(expressionResolver);
    }

    @Override
    protected Optional<String> getOperationDefiningKeyword() {
        return Optional.of(IF);
    }

    @Override
    protected void validate(final SourceLine sourceLine) {
        final int ifTokenPosition = 0;
        validateIfClause(sourceLine, ifTokenPosition);
    }

    private void validateIfClause(final SourceLine sourceLine, final int ifTokenPosition) {
        validateThatLineEndsWithOpeningCurlyBrace(sourceLine);
        validateThatOpeningParenthesisRightAfterIfToken(sourceLine, ifTokenPosition);
        validateThatClosingParenthesisRightBeforeOpeningCurlyBrace(sourceLine);
    }

    private void validateThatOpeningParenthesisRightAfterIfToken(final SourceLine sourceLine,
                                                                 final int ifTokenPosition) {
        if (!OPENING_PARENTHESIS.equals(sourceLine.getToken(ifTokenPosition + 1))) {
            throw new JavammLineSyntaxError(sourceLine, "'%s' expected after '%s'", OPENING_PARENTHESIS, IF);
        }
    }

    private void validateThatClosingParenthesisRightBeforeOpeningCurlyBrace(final SourceLine sourceLine) {
        final String lastButOneToken = sourceLine.getToken(sourceLine.getTokenCount() - 2);
        if (!CLOSING_PARENTHESIS.equals(lastButOneToken)) {
            throw new JavammLineSyntaxError(sourceLine, "'%s' expected before '%s'",
                    CLOSING_PARENTHESIS, OPENING_CURLY_BRACE);
        }
    }

    @Override
    protected IfElseOperation get(final SourceLine sourceLine, final ListIterator<SourceLine> codeIterator) {
        final Expression condition = getCondition(sourceLine);
        final Block trueBlock = getNextBlock(sourceLine, codeIterator);
        final Optional<Block> falseBlock = findElseOptionalBlock(codeIterator);

        return buildIfElseOperation(sourceLine, condition, trueBlock, falseBlock);
    }

    private Expression getCondition(final SourceLine sourceLine) {
        final List<String> expressionTokens =
                getTokensBetweenBrackets(OPENING_PARENTHESIS, CLOSING_PARENTHESIS, sourceLine, false);
        return expressionResolver.resolve(expressionTokens, sourceLine);
    }

    private Block getNextBlock(final SourceLine sourceLine, final ListIterator<SourceLine> codeIterator) {
        return getBlockOperationReader().readWithExpectedClosingCurlyBrace(sourceLine, codeIterator);
    }

    private Optional<Block> findElseOptionalBlock(final ListIterator<SourceLine> codeIterator) {
        return tryToFindElseBlockOnNextLine(codeIterator);
    }

    private IfElseOperation buildIfElseOperation(final SourceLine sourceLine,
                                                 final Expression condition,
                                                 final Block trueBlock,
                                                 final Optional<Block> falseBlock) {
        return falseBlock
                .map(block -> new IfElseOperation(sourceLine, condition, trueBlock, block))
                .orElseGet(() -> new IfElseOperation(sourceLine, condition, trueBlock));
    }

    private Optional<Block> tryToFindElseBlockOnNextLine(final ListIterator<SourceLine> codeIterator) {
        if (codeIterator.hasNext()) {
            final SourceLine sourceLine = codeIterator.next();
            if (isElseBlockPresent(sourceLine)) {
                return processElseBlock(codeIterator, sourceLine);
            } else {
                codeIterator.previous();
            }
        }
        return Optional.empty();
    }

    private boolean isElseBlockPresent(final SourceLine sourceLine) {
        return sourceLine != null && isElseTheFirstTokenInLine(sourceLine);
    }

    private Optional<Block> processElseBlock(final ListIterator<SourceLine> codeIterator, final SourceLine sourceLine) {
        if (isIfPresentAfterElse(sourceLine)) {
            return readElseIfBlock(sourceLine, codeIterator);
        } else {
            return readElseBlock(codeIterator, sourceLine);
        }
    }

    private Optional<Block> readElseIfBlock(final SourceLine sourceLine, final ListIterator<SourceLine> codeIterator) {
        final int ifTokenPosition = 1;
        validateIfClause(sourceLine, ifTokenPosition);
        final IfElseOperation elseIfOpertion = get(sourceLine, codeIterator);
        return Optional.of(new Block(elseIfOpertion, sourceLine));
    }

    private Optional<Block> readElseBlock(final ListIterator<SourceLine> codeIterator,
                                                final SourceLine sourceLine) {
        validateElseClause(sourceLine);
        return Optional.of(getNextBlock(sourceLine, codeIterator));
    }

    private void validateElseClause(final SourceLine sourceLine) {
        validateThatLineEndsWithOpeningCurlyBrace(sourceLine);
        validateThatLineContainsOnlyTwoTokens(sourceLine);
    }

    private void validateThatLineContainsOnlyTwoTokens(final SourceLine sourceLine) {
        if (sourceLine.getTokenCount() > 2) {
            throw new JavammLineSyntaxError(sourceLine, "'%s' expected after '%s'", OPENING_CURLY_BRACE, ELSE);
        }
    }

    private boolean isElseTheFirstTokenInLine(final SourceLine sourceLine) {
        return ELSE.equals(sourceLine.getFirst());
    }

    private boolean isIfPresentAfterElse(final SourceLine sourceLine) {
        return isIfSecondTokenInLine(sourceLine);
    }

    private boolean isIfSecondTokenInLine(final SourceLine sourceLine) {
        return sourceLine.getTokenCount() > 1 && IF.equals(sourceLine.getToken(1));
    }
}
