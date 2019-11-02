
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
import com.revenat.javamm.code.fragment.expression.ConstantExpression;
import com.revenat.javamm.code.fragment.operation.Block;
import com.revenat.javamm.code.fragment.operation.ForOperation;
import com.revenat.javamm.compiler.component.ExpressionResolver;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

import static com.revenat.javamm.code.syntax.Keywords.FOR;
import static com.revenat.javamm.compiler.component.impl.util.SyntaxParseUtils.getTokensBetweenBrackets;
import static com.revenat.javamm.compiler.component.impl.util.SyntaxValidationUtils.validateClosingParenthesisBeforeOpeningCurlyBrace;
import static com.revenat.javamm.compiler.component.impl.util.SyntaxValidationUtils.validateOpeningParenthesisAfterTokenInPosition;
import static com.revenat.javamm.compiler.component.impl.util.SyntaxValidationUtils.validateThatLineEndsWithOpeningCurlyBrace;

/**
 * @author Vitaliy Dragun
 *
 */
public class ForOperationReader extends AbstractBlockOperationReader<ForOperation> {

    private static final int EXPECTED_NUMBER_OF_SEMICOLONS = 2;

    private static final String SEMICOLON = ";";

    private static final String OPENING_PARENTHESIS = "(";

    private static final String CLOSING_PARENTHESIS = ")";

    private final ExpressionResolver expressionResolver;

    public ForOperationReader(final ExpressionResolver expressionResolver) {
        this.expressionResolver = expressionResolver;
    }

    @Override
    protected Optional<String> getOperationDefiningKeyword() {
        return Optional.of(FOR);
    }

    @Override
    protected void validate(final SourceLine sourceLine) {
        validateThatLineEndsWithOpeningCurlyBrace(sourceLine);
        validateThatOpeningParenthesisRightAfterForToken(sourceLine);
        validateClosingParenthesisBeforeOpeningCurlyBrace(sourceLine);
    }

    @Override
    protected ForOperation get(final SourceLine sourceLine, final ListIterator<SourceLine> sourceCode) {
        final List<String> expressionTokens = extractExpressionTokensFrom(sourceLine);

        final Block initializationClause = getInitializationClause(expressionTokens, sourceLine);
        final Expression conditionClause = getConditionClause(expressionTokens, sourceLine);
        final Block updateClause = getUpdateClause(expressionTokens, sourceLine);
        final Block body = getBody(sourceLine, sourceCode);

        return new ForOperation(sourceLine, conditionClause, initializationClause, updateClause, body);
    }

    private List<String> extractExpressionTokensFrom(final SourceLine sourceLine) {
        final List<String> expressionTokens =
                getTokensBetweenBrackets(OPENING_PARENTHESIS, CLOSING_PARENTHESIS, sourceLine, false);
        validateRightNumberOfSemicolonDelimitersWithinExpression(expressionTokens, sourceLine);

        return expressionTokens;
    }

    private void validateRightNumberOfSemicolonDelimitersWithinExpression(final List<String> expressionTokens,
                                                                          final SourceLine sourceLine) {
        final int numberOfDelimiters = calculateNumberOfSemicolonsAmong(expressionTokens);
        requireValidNumberOfDelimiters(sourceLine, numberOfDelimiters);
    }

    private int calculateNumberOfSemicolonsAmong(final List<String> expressionTokens) {
        int numberOfDelimiters = 0;
        for (final String token : expressionTokens) {
            if (SEMICOLON.equals(token)) {
                numberOfDelimiters++;
            }
        }
        return numberOfDelimiters;
    }

    private void requireValidNumberOfDelimiters(final SourceLine sourceLine, final int numberOfDelimiters) {
        if (numberOfDelimiters > EXPECTED_NUMBER_OF_SEMICOLONS) {
            throw new JavammLineSyntaxError(sourceLine, "Redundant '%s'", SEMICOLON);
        } else if (numberOfDelimiters < EXPECTED_NUMBER_OF_SEMICOLONS) {
            throw new JavammLineSyntaxError(sourceLine, "Missing '%s'", SEMICOLON);
        }
    }

    private Block getInitializationClause(final List<String> expressionTokens, final SourceLine sourceLine) {
        final int firstSemicolonIndex = expressionTokens.indexOf(SEMICOLON);
        final List<String> clauseTokens = expressionTokens.subList(0, firstSemicolonIndex);
        return readOperation(sourceLine, clauseTokens);
    }

    private Expression getConditionClause(final List<String> expressionTokens, final SourceLine sourceLine) {
        final int firstSemicolonIndex = expressionTokens.indexOf(SEMICOLON);
        final int lastSemicolonIndex = expressionTokens.lastIndexOf(SEMICOLON);
        final List<String> clauseTokens = expressionTokens.subList(firstSemicolonIndex + 1, lastSemicolonIndex);

        if (clauseTokens.isEmpty()) {
            return ConstantExpression.valueOf(true);
        } else {
            return expressionResolver.resolve(clauseTokens, sourceLine);
        }
    }

    private Block getUpdateClause(final List<String> expressionTokens, final SourceLine sourceLine) {
        final int lastSemicolonIndex = expressionTokens.lastIndexOf(SEMICOLON);
        final List<String> clauseTokens = expressionTokens.subList(lastSemicolonIndex + 1, expressionTokens.size());
        return readOperation(sourceLine, clauseTokens);
    }


    private Block readOperation(final SourceLine sourceLine, final List<String> clauseTokens) {
        final List<SourceLine> clauseLines = new ArrayList<>();
        if (!clauseTokens.isEmpty()) {
            clauseLines.add(new SourceLine(sourceLine.getModuleName(), sourceLine.getLineNumber(), clauseTokens));
        }
        return getBlockOperationReader().read(sourceLine, clauseLines.listIterator());
    }

    private Block getBody(final SourceLine sourceLine, final ListIterator<SourceLine> sourceCode) {
        return getBlockOperationReader().readWithExpectedClosingCurlyBrace(sourceLine, sourceCode);
    }

    private void validateThatOpeningParenthesisRightAfterForToken(final SourceLine sourceLine) {
        validateOpeningParenthesisAfterTokenInPosition(sourceLine, FOR, 0);
    }
}
