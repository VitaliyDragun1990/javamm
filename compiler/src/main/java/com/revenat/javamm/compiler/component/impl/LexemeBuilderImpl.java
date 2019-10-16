
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
import com.revenat.javamm.code.fragment.Parenthesis;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.operator.BinaryOperator;
import com.revenat.javamm.code.fragment.operator.TernaryConditionalOperator;
import com.revenat.javamm.code.fragment.operator.UnaryOperator;
import com.revenat.javamm.compiler.component.LexemeAmbiguityResolver;
import com.revenat.javamm.compiler.component.LexemeBuilder;
import com.revenat.javamm.compiler.component.SingleTokenExpressionBuilder;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

/**
 * @author Vitaliy Dragun
 *
 */
public class LexemeBuilderImpl implements LexemeBuilder {

    private final SingleTokenExpressionBuilder singleTokenExpressionBuilder;

    private final LexemeAmbiguityResolver lexemeAmbiguityResolver;

    public LexemeBuilderImpl(final SingleTokenExpressionBuilder singleTokenExpressionBuilder,
                             final LexemeAmbiguityResolver lexemeAmbiguityResolver) {
        this.singleTokenExpressionBuilder = singleTokenExpressionBuilder;
        this.lexemeAmbiguityResolver = lexemeAmbiguityResolver;
    }

    @Override
    public List<Lexeme> build(final List<String> tokens, final SourceLine sourceLine) {
        final List<Lexeme> lexemes = new ArrayList<>();

        for (final ListIterator<String> source = tokens.listIterator(); source.hasNext();) {
            buildLexeme(source.next(), lexemes, source, sourceLine);
        }

        return List.copyOf(lexemes);
    }

    private void buildLexeme(final String currentToken,
                             final List<Lexeme> lexemes,
                             final ListIterator<String> tokens,
                             final SourceLine sourceLine) {
        if (isOperator(currentToken)) {
            buildOperatorLexeme(currentToken, lexemes, tokens, sourceLine);

        } else if (isParenthesis(currentToken)) {
            lexemes.add(resolveAmbiguity(parenthesisFrom(currentToken), lexemes));

        } else if (isSingleTokenExpression(currentToken)) {
            lexemes.add(resolveAmbiguity(buildExpressionFrom(currentToken, sourceLine), lexemes));

        } else {
            throw syntaxError(currentToken, sourceLine);
        }
    }

    private void buildOperatorLexeme(final String currentToken,
                                     final List<Lexeme> lexemes,
                                     final ListIterator<String> tokens,
                                     final SourceLine sourceLine) {
        if (isBinaryOperator(currentToken)) {
            lexemes.add(resolveAmbiguity(binaryOpertorFrom(currentToken), lexemes));
        } else if (isUnaryOperator(currentToken)) {
            lexemes.add(resolveAmbiguity(unaryOperatorFrom(currentToken), lexemes));
        } else {
            lexemes.add(TernaryConditionalOperator.OPERATOR);
            new TernaryConditionalOperatorBuilder(tokens, lexemes, sourceLine).build();
        }
    }

    private Expression buildExpressionFrom(final String token, final SourceLine sourceLine) {
        return singleTokenExpressionBuilder.build(List.of(token), sourceLine);
    }

    private UnaryOperator unaryOperatorFrom(final String token) {
        return UnaryOperator.of(token).get();
    }

    private BinaryOperator binaryOpertorFrom(final String token) {
        return BinaryOperator.of(token).get();
    }

    private Parenthesis parenthesisFrom(final String token) {
        return Parenthesis.of(token).get();
    }

    private boolean isSingleTokenExpression(final String token) {
        return singleTokenExpressionBuilder.canBuild(List.of(token));
    }

    private boolean isParenthesis(final String token) {
        return Parenthesis.of(token).isPresent();
    }

    private Lexeme resolveAmbiguity(final Lexeme lexeme, final List<Lexeme> resolved) {
        return lexemeAmbiguityResolver.resolve(lexeme, resolved.size(), resolved);
    }

    private boolean isOperator(final String token) {
        return isBinaryOperator(token) || isUnaryOperator(token) || isTernaryOperator(token);
    }

    private boolean isTernaryOperator(final String token) {
        return TernaryConditionalOperator.isContitional(token);
    }

    private boolean isUnaryOperator(final String token) {
        return UnaryOperator.of(token).isPresent();
    }

    private boolean isBinaryOperator(final String token) {
        return BinaryOperator.of(token).isPresent();
    }

    private JavammLineSyntaxError syntaxError(final String token, final SourceLine sourceLine) {
        return new JavammLineSyntaxError(buildErrorMessage(token), sourceLine);
    }

    private String buildErrorMessage(final String token) {
        return format(
                "Unsupported token: %s (%s)",
                token,
                token.codePoints().mapToObj(v -> "0x" + format("%04x", v)).collect(joining(" ")));
    }

    private final class TernaryConditionalOperatorBuilder {

        private static final String CONDITIONAL_SYMBOL = "?";

        private final ListIterator<String> tokens;

        private final List<Lexeme> resolvedLexemes;

        private final SourceLine sourceLine;

        private boolean isSeparatorFound;

        private TernaryConditionalOperatorBuilder(final ListIterator<String> tokens,
                                                  final List<Lexeme> resolvedLexemes,
                                                  final SourceLine sourceLine) {
            this.tokens = tokens;
            this.resolvedLexemes = resolvedLexemes;
            this.sourceLine = sourceLine;
            isSeparatorFound = false;
        }

        public void build() {
            while (tokens.hasNext() && !isSeparatorFound) {
                final String token = tokens.next();

                if (isTernarySeparator(token)) {
                    resolvedLexemes.add(TernaryConditionalOperator.SEPARATOR);
                    isSeparatorFound = true;
                } else {
                    buildLexeme(token, resolvedLexemes, tokens, sourceLine);
                }
            }

            if (!isSeparatorFound) {
                throw syntaxError(CONDITIONAL_SYMBOL, sourceLine);
            }
        }

        private boolean isTernarySeparator(final String token) {
            return TernaryConditionalOperator.isSeparator(token);
        }
    }
}
