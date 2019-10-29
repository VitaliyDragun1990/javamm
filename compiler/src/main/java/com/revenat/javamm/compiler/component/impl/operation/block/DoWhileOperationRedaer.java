
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
import com.revenat.javamm.code.fragment.operation.DoWhileOperation;
import com.revenat.javamm.compiler.component.ExpressionResolver;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;
import com.revenat.javamm.compiler.component.error.JavammStructSyntaxError;

import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

import static com.revenat.javamm.code.syntax.Keywords.DO;
import static com.revenat.javamm.code.syntax.Keywords.WHILE;
import static com.revenat.javamm.compiler.component.impl.util.SyntaxParseUtils.getTokensBetweenBrackets;
import static com.revenat.javamm.compiler.component.impl.util.SyntaxValidationUtils.validateOpeningParenthesisAfterTokenInPosition;
import static com.revenat.javamm.compiler.component.impl.util.SyntaxValidationUtils.validateThatLineEndsWithOpeningCurlyBrace;

import static java.util.Objects.requireNonNull;

/**
 * @author Vitaliy Dragun
 *
 */
public class DoWhileOperationRedaer extends AbstractBlockOperationReader<DoWhileOperation> {

    private static final String OPENING_PARENTHESIS = "(";

    private static final String CLOSING_PARENTHESIS = ")";

    private static final String OPENING_CURLY_BRACE = "{";

    private final ExpressionResolver expressionResolver;

    public DoWhileOperationRedaer(final ExpressionResolver expressionResolver) {
        this.expressionResolver = requireNonNull(expressionResolver);
    }

    @Override
    protected void validate(final SourceLine sourceLine) {
        validateDoClause(sourceLine);
    }

    @Override
    protected Optional<String> getOperationDefiningKeyword() {
        return Optional.of(DO);
    }

    @Override
    protected DoWhileOperation get(final SourceLine sourceLine, final ListIterator<SourceLine> codeIterator) {
        final Block body = getBody(sourceLine, codeIterator);
        final SourceLine whileSourceLine = getWhileSourceLine(codeIterator, sourceLine.getModuleName());
        final Expression condition = getCondition(whileSourceLine);

        return new DoWhileOperation(whileSourceLine, condition, body);
    }

    private Block getBody(final SourceLine sourceLine, final ListIterator<SourceLine> codeIterator) {
        return getBlockOperationReader().readWithExpectedClosingCurlyBrace(sourceLine, codeIterator);
    }

    private SourceLine getWhileSourceLine(final ListIterator<SourceLine> codeIterator, final String moduleName) {
        if (codeIterator.hasNext()) {
            return getWhileSourceLine(codeIterator);
        } else {
            throw new JavammStructSyntaxError("'while' expected at the end of file", moduleName);
        }
    }

    private SourceLine getWhileSourceLine(final ListIterator<SourceLine> codeIterator) {
        return validateWhileClause(codeIterator.next());
    }

    private Expression getCondition(final SourceLine sourceLine) {
        final List<String> tokens =
                getTokensBetweenBrackets(OPENING_PARENTHESIS, CLOSING_PARENTHESIS, sourceLine, false);
        return expressionResolver.resolve(tokens, sourceLine);
    }

    private SourceLine validateWhileClause(final SourceLine sourceLine) {
        validateThatLineStartsWithWhileToken(sourceLine);
        validateThatOpeningParenthesisIsRightAfterWhile(sourceLine);
        validateThatLineEndsWithClosingParenthesis(sourceLine);
        return sourceLine;
    }

    private void validateThatLineEndsWithClosingParenthesis(final SourceLine sourceLine) {
        if (!CLOSING_PARENTHESIS.equals(sourceLine.getLast())) {
            throw new JavammLineSyntaxError(sourceLine, "'%s' expected at the end of the line", CLOSING_PARENTHESIS);
        }
    }

    private void validateThatOpeningParenthesisIsRightAfterWhile(final SourceLine sourceLine) {
        validateOpeningParenthesisAfterTokenInPosition(sourceLine, WHILE, 0);
    }

    private void validateThatLineStartsWithWhileToken(final SourceLine sourceLine) {
        if (!WHILE.equals(sourceLine.getFirst())) {
            throw new JavammLineSyntaxError(sourceLine, "'%s' expected", WHILE);
        }
    }

    private void validateDoClause(final SourceLine sourceLine) {
        validateThatLineEndsWithOpeningCurlyBrace(sourceLine);
        validateThatOpeningCurlyBraceIsRightAfterDo(sourceLine);
    }

    private void validateThatOpeningCurlyBraceIsRightAfterDo(final SourceLine sourceLine) {
        if (sourceLine.getTokenCount() > 2) {
            throw new JavammLineSyntaxError(sourceLine, "'%s' expected after '%s'", OPENING_CURLY_BRACE, DO);
        }
    }
}
