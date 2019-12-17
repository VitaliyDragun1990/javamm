
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

package com.revenat.javamm.compiler.component.impl;

import com.revenat.javamm.code.fragment.Lexeme;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.util.LexemeUtils;
import com.revenat.javamm.compiler.component.UnaryAssignmentExpressionResolver;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import static com.revenat.javamm.code.util.LexemeUtils.isClosingParenthesis;
import static com.revenat.javamm.code.util.LexemeUtils.isExpression;
import static com.revenat.javamm.code.util.LexemeUtils.isOpeningParenthesis;
import static com.revenat.javamm.code.util.LexemeUtils.isUnaryAssignmentOperator;
import static com.revenat.javamm.code.util.LexemeUtils.isVariableExpression;
import static com.revenat.javamm.compiler.component.impl.expression.processor.UnaryAssignmentOperatorProcessor.processExpressionBeforeOperator;
import static com.revenat.javamm.compiler.component.impl.expression.processor.UnaryAssignmentOperatorProcessor.processOperatorBeforeExpression;
import static com.revenat.javamm.compiler.component.impl.expression.processor.UnaryAssignmentOperatorProcessor.processOperatorBeforeOpeningParenthesis;
import static com.revenat.javamm.compiler.component.impl.expression.processor.UnaryAssignmentOperatorProcessor.processPossibleOperatorAfterClosingParenthesis;

/**
 * @author Vitaliy Dragun
 */
public class UnaryAssignmentExpressionResolverImpl implements UnaryAssignmentExpressionResolver {

    @Override
    public List<Lexeme> resolve(final List<Lexeme> lexemes, final SourceLine sourceLine) {
        if (containsUnaryAssignmentOperator(lexemes)) {
            return resolveFrom(lexemes, sourceLine);
        } else {
            return lexemes;
        }
    }

    private boolean containsUnaryAssignmentOperator(final List<Lexeme> lexemes) {
        return lexemes.stream()
            .anyMatch(LexemeUtils::isUnaryAssignmentOperator);
    }

    private List<Lexeme> resolveFrom(final List<Lexeme> lexemes, final SourceLine sourceLine) {
        final List<Lexeme> result = new ArrayList<>();

        for (final ListIterator<Lexeme> original = lexemes.listIterator(); original.hasNext();) {
            processNextLexemeFrom(original, sourceLine, result);
        }

        return result;
    }

    private void processNextLexemeFrom(final ListIterator<Lexeme> lexemes,
                                       final SourceLine sourceLine,
                                       final List<Lexeme> result) {
        final Lexeme current = lexemes.next();

        if (lexemes.hasNext()) {
            processLexemePair(current, lexemes, result, sourceLine);
        } else if (isUnaryAssignmentOperator(current)) {
            throw syntaxError(sourceLine, "A variable expression is expected for unary operator: '%s'", current);

        } else {
            result.add(current);
        }
    }

    private void processLexemePair(final Lexeme current,
                                   final ListIterator<Lexeme> lexemes,
                                   final List<Lexeme> result,
                                   final SourceLine sourceLine) {
        final Lexeme next = lexemes.next();

        if (isUnaryAssignmentOperatorBeforeExpression(current, next)) {
            processOperatorBeforeExpression(current, next, result, sourceLine);

        } else if (isUnaryAssignmentOperatorBeforeOpeningParenthesis(current, next)) {
            processOperatorBeforeOpeningParenthesis(current,
                next,
                lexemes,
                result,
                sourceLine);

        } else if (isVariableExpressionBeforeUnaryAssignmentOperator(current, next)) {
            processExpressionBeforeOperator(current, next, result, sourceLine);

        } else if (isExpressionBeforeClosingParenthesis(current, next)) {
            processPossibleOperatorAfterClosingParenthesis(current,
                next,
                lexemes,
                result,
                sourceLine);

        } else if (isUnaryAssignmentOperator(current)) {
            throw syntaxError(sourceLine, "A variable expression is expected for unary operator: '%s'", current);
        } else {
            lexemes.previous();
            result.add(current);
        }
    }

    private boolean isExpressionBeforeClosingParenthesis(final Lexeme current, final Lexeme next) {
        return isExpression(current) && isClosingParenthesis(next);
    }

    private boolean isUnaryAssignmentOperatorBeforeOpeningParenthesis(final Lexeme current, final Lexeme next) {
        return isUnaryAssignmentOperator(current) && isOpeningParenthesis(next);
    }

    private boolean isVariableExpressionBeforeUnaryAssignmentOperator(final Lexeme current, final Lexeme next) {
        return isVariableExpression(current) && isUnaryAssignmentOperator(next);
    }

    private boolean isUnaryAssignmentOperatorBeforeExpression(final Lexeme current, final Lexeme next) {
        return isUnaryAssignmentOperator(current) && isExpression(next);
    }

    private JavammLineSyntaxError syntaxError(final SourceLine sourceLine, final String message, final Object... args) {
        return new JavammLineSyntaxError(sourceLine, message, args);
    }
}
