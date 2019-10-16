
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

import com.revenat.javamm.code.fragment.Expression;
import com.revenat.javamm.code.fragment.Lexeme;
import com.revenat.javamm.code.fragment.Operator;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.expression.ComplexExpression;
import com.revenat.javamm.code.fragment.expression.TernaryConditionalExpression;
import com.revenat.javamm.code.fragment.operator.TernaryConditionalOperator;
import com.revenat.javamm.code.util.LexemeUtils;
import com.revenat.javamm.compiler.component.ComplexExpressionBuilder;
import com.revenat.javamm.compiler.component.ComplexLexemeValidator;
import com.revenat.javamm.compiler.component.ExpressionBuilder;
import com.revenat.javamm.compiler.component.ExpressionResolver;
import com.revenat.javamm.compiler.component.LexemeBuilder;
import com.revenat.javamm.compiler.component.OperatorPrecedenceResolver;
import com.revenat.javamm.compiler.component.UnaryAssignmentExpressionResolver;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.revenat.javamm.code.fragment.operator.TernaryConditionalOperator.OPERATOR;
import static com.revenat.javamm.code.util.LexemeUtils.isClosingParenthesis;
import static com.revenat.javamm.code.util.LexemeUtils.isOpeningParenthesis;
import static com.revenat.javamm.code.util.LexemeUtils.isTernaryOperator;
import static com.revenat.javamm.code.util.TypeUtils.confirmType;

import static java.util.Objects.requireNonNull;

/**
 * @author Vitaliy Dragun
 *
 */
public class ExpressionResolverImpl implements ExpressionResolver {

    private final Collection<ExpressionBuilder> expressionBuilders;

    private final ComplexExpressionBuilder complexExpressionBuilder;

    private final LexemeBuilder lexemeBuilder;

    private final ComplexLexemeValidator lexemeValidator;

    private final UnaryAssignmentExpressionResolver unaryAssignmentExpressionResolver;

    private final OperatorPrecedenceResolver operatorPrecedenceResolver;

    public ExpressionResolverImpl(final Set<ExpressionBuilder> expressionBuilders,
                                  final ComplexExpressionBuilder complexExpressionBuilder,
                                  final LexemeBuilder lexemeBuilder,
                                  final ComplexLexemeValidator lexemeValidator,
                                  final UnaryAssignmentExpressionResolver unaryAssignmentExpressionResolver) {
        this.expressionBuilders = List.copyOf(expressionBuilders);
        this.complexExpressionBuilder = requireNonNull(complexExpressionBuilder);
        this.lexemeBuilder = requireNonNull(lexemeBuilder);
        this.lexemeValidator = requireNonNull(lexemeValidator);
        this.unaryAssignmentExpressionResolver = requireNonNull(unaryAssignmentExpressionResolver);
        this.operatorPrecedenceResolver = new OperatorPrecedenceResolverImpl();
    }

    @Override
    public Expression resolve(final List<String> expressionTokens, final SourceLine sourceLine) {
        final Optional<ExpressionBuilder> builder = getAppropriateExpressionBuilder(expressionTokens);

        if (builder.isPresent()) {
            return resolveSimpleExpression(expressionTokens, sourceLine, builder.get());
        } else {
            return resolveComplexExpression(expressionTokens, sourceLine);
        }
    }

    private Expression resolveSimpleExpression(final List<String> expressionTokens, final SourceLine sourceLine,
            final ExpressionBuilder builder) {
        return builder.build(expressionTokens, sourceLine);
    }

    /**
     * var a = 1 + 3 * 5
     * var a = ( 1 + 3 ) * a
     *
     * var a = sum ( 1, 2 + b ) + 4                                              ----------> var a = x + 4
     * var a = array [ 23 + g - h ] - a                                          ----------> var a = x - 4
     * var a = sum ( array[ 23 + g ] , array [ 23 ] ) - 4 * sum ( 1 , 2 + b )   ----------> var a = x - 4 * y
     * var a = sum ( 1 , 2 + b ) + 4 * ( array [ 23 + g - h ] - 6 )              ----------> var a = x + 4 * ( y - z )
     */
    private Expression resolveComplexExpression(final List<String> expressionTokens, final SourceLine sourceLine) {
        final List<Lexeme> lexemes = lexemeBuilder.build(expressionTokens, sourceLine);

        if (containsSingleLexeme(lexemes)) {
            return resolveFromLexeme(lexemes.get(0), sourceLine);
        } else {
            return resolveFromLexemes(lexemes, sourceLine);
        }
    }

    @Override
    public Expression resolveFromLexemes(final List<Lexeme> lexemes, final SourceLine sourceLine) {
        List<Lexeme> processedLexemes = new TernaryConditionalExpressionResolver(lexemes, sourceLine).resolve();
        processedLexemes = unaryAssignmentExpressionResolver.resolve(processedLexemes, sourceLine);

        if (containsSingleLexeme(processedLexemes)) {
            return resolveFromLexeme(processedLexemes.get(0), sourceLine);
        } else {
            lexemeValidator.validate(processedLexemes, sourceLine);
            return buildComplexExpression(processedLexemes, sourceLine);
        }
    }

    private Expression resolveFromLexeme(final Lexeme lexeme, final SourceLine sourceLine) {
        if (confirmType(Expression.class, lexeme)) {
            return (Expression) lexeme;
        } else {
            throw syntaxError(lexeme, sourceLine);
        }
    }

    private boolean containsSingleLexeme(final List<Lexeme> lexemes) {
        return lexemes.size() == 1;
    }

    private ComplexExpression buildComplexExpression(final List<Lexeme> lexemes, final SourceLine sourceLine) {
        return complexExpressionBuilder.build(lexemes, sourceLine);
    }

    private Optional<ExpressionBuilder> getAppropriateExpressionBuilder(final List<String> expressionTokens) {
        return expressionBuilders.stream()
                .filter(b -> b.canBuild(expressionTokens))
                .findFirst();
    }

    private JavammLineSyntaxError syntaxError(final Lexeme lexeme, final SourceLine sourceLine) {
        return new JavammLineSyntaxError("Unsupported expression: " + lexeme, sourceLine);
    }

    private final class TernaryConditionalExpressionResolver {

        private final List<Lexeme> head = new ArrayList<>();

        private final List<Lexeme> tail = new ArrayList<>();

        private final List<Lexeme> target;

        private final SourceLine sourceLine;

        private TernaryConditionalExpressionResolver(final List<Lexeme> lexemes, final SourceLine sourceLine) {
            this.target = lexemes;
            this.sourceLine = sourceLine;
        }

        public List<Lexeme> resolve() {
            if (containsTernaryConditionalOperator(target)) {
                return resolve(target.listIterator());
            }
            return target;
        }

        private List<Lexeme> resolve(final ListIterator<Lexeme> lexemes) {
            final List<Lexeme> predicateClause = buildPredicateClause(lexemes);

            final List<Lexeme> trueClause = buildTrueClause(lexemes);

            final List<Lexeme> falseClause = buildFalseClause(lexemes);

            final TernaryConditionalExpression ternary = buildTernaryExpression(predicateClause,
                                                                                trueClause,
                                                                                falseClause);
            final List<Lexeme> assembledLexemes = assembleAllTogether(ternary);

            return resolveNextTernaryIfAny(assembledLexemes);
        }

        private List<Lexeme> buildPredicateClause(final ListIterator<Lexeme> lexemes) {
            final List<Lexeme> predicateClause = buildPredicateClauseWithLexemesUpToTernaryOperator(lexemes);

            moveRedunduntLexemesToHead(predicateClause);
            return requireNotEmptyClause(predicateClause, "predicate clause");
        }

        private List<Lexeme> buildPredicateClauseWithLexemesUpToTernaryOperator(final ListIterator<Lexeme> lexemes) {
            final List<Lexeme> predicateClause = new ArrayList<>();
            while (lexemes.hasNext()) {
                final Lexeme current = lexemes.next();
                if (isTernaryOperator(current)) {
                    break;
                }
                predicateClause.add(current);
            }
            return predicateClause;
        }

        private void moveRedunduntLexemesToHead(final List<Lexeme> predicateClause) {
            int danglingParenthesis = 0;

            for (int i = predicateClause.size() - 1; i >= 0; i--) {
                final Lexeme current = predicateClause.get(i);

                if (isClosingParenthesis(current)) {
                    danglingParenthesis--;
                } else if (isOpeningParenthesis(current)) {
                    danglingParenthesis++;
                }

                if (danglingParenthesis > 0 || (isOperatorWithLowerPrecedence(current) && (danglingParenthesis == 0))) {
                    populateHeadFrom(predicateClause, i);
                    return;
                }
            }
        }

        private void populateHeadFrom(final List<Lexeme> predicateClause, final int upperLimit) {
            for (int j = 0; j <= upperLimit; j++) {
                head.add(predicateClause.remove(0));
            }
        }

        private List<Lexeme> buildTrueClause(final ListIterator<Lexeme> lexemes) {
            final List<Lexeme> trueClause = buildTrueClauseWithLexemesUpToColonTernarySeparator(lexemes);
            return requireNotEmptyClause(trueClause, "true clause");
        }

        private List<Lexeme> buildTrueClauseWithLexemesUpToColonTernarySeparator(final ListIterator<Lexeme> lexemes) {
            final List<Lexeme> trueClause = new ArrayList<>();
            int danglingParenthesis = 0;

            while (lexemes.hasNext()) {
                final Lexeme current = lexemes.next();

                if (isOpeningParenthesis(current)) {
                    danglingParenthesis++;
                } else if (isClosingParenthesis(current)) {
                    danglingParenthesis--;
                } else if (isSeparator(current) && (danglingParenthesis == 0)) {
                    break;
                }

                trueClause.add(current);
            }
            return trueClause;
        }

        private List<Lexeme> buildFalseClause(final ListIterator<Lexeme> lexemes) {
            final List<Lexeme> falseClause = buildFalseClauseWithLexemesAfterColonTernarySeparator(lexemes);
            moveLeftLexemesToTail(lexemes);
            return requireNotEmptyClause(falseClause, "false clause");
        }

        private void moveLeftLexemesToTail(final ListIterator<Lexeme> lexemes) {
            while (lexemes.hasNext()) {
                tail.add(lexemes.next());
            }
        }

        private List<Lexeme> buildFalseClauseWithLexemesAfterColonTernarySeparator(final ListIterator<Lexeme> lexemes) {
            final List<Lexeme> falseClause = new ArrayList<>();
            int danglingParenthesis = 0;

            while (lexemes.hasNext()) {
                final Lexeme current = lexemes.next();

                if (isClosingParenthesis(current)) {
                    danglingParenthesis++;
                } else if (isOpeningParenthesis(current)) {
                    danglingParenthesis--;
                }
                if (danglingParenthesis > 0) {
                    lexemes.previous();
                    break;
                }
                falseClause.add(current);
            }
            return falseClause;
        }

        private TernaryConditionalExpression buildTernaryExpression(final List<Lexeme> predicateClause,
                                                                    final List<Lexeme> trueClause,
                                                                    final List<Lexeme> falseClause) {
            return new TernaryConditionalExpression(resolveFromLexemes(predicateClause, sourceLine),
                                                    resolveFromLexemes(trueClause, sourceLine),
                                                    resolveFromLexemes(falseClause, sourceLine));
        }

        private List<Lexeme> assembleAllTogether(final TernaryConditionalExpression ternary) {
            return Stream.of(
                    head.stream(),
                    Stream.of(ternary),
                    tail.stream()
                    ).flatMap(i -> i)
                    .collect(Collectors.toList());
        }

        private List<Lexeme> resolveNextTernaryIfAny(final List<Lexeme> assembledLexemes) {
            if (containsTernaryConditionalOperator(assembledLexemes)) {
                return new TernaryConditionalExpressionResolver(assembledLexemes, sourceLine).resolve();
            }

            return assembledLexemes;
        }

        private boolean isOperatorWithLowerPrecedence(final Lexeme lexeme) {
            return LexemeUtils.isOperator(lexeme) &&
                    operatorPrecedenceResolver.hasLowerPrecedence((Operator) lexeme, OPERATOR);
        }

        private List<Lexeme> requireNotEmptyClause(final List<Lexeme> l, final String clause) {
            if (l.isEmpty()) {
                throw new JavammLineSyntaxError(sourceLine, "Ternary operator '?:' should have %s expression",
                        clause);
            }
            return l;
        }

        private boolean isSeparator(final Lexeme lexeme) {
            return TernaryConditionalOperator.SEPARATOR == lexeme;
        }

        private boolean containsTernaryConditionalOperator(final List<Lexeme> list) {
            return list.stream()
                    .anyMatch(LexemeUtils::isTernaryOperator);
        }
    }
}
