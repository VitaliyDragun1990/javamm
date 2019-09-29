
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
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.expression.ComplexExpression;
import com.revenat.javamm.compiler.component.ComplexExpressionBuilder;
import com.revenat.javamm.compiler.component.ExpressionBuilder;
import com.revenat.javamm.compiler.component.ExpressionResolver;
import com.revenat.javamm.compiler.component.LexemeBuilder;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    public ExpressionResolverImpl(final Set<ExpressionBuilder> expressionBuilders,
                                  final ComplexExpressionBuilder complexExpressionBuilder,
                                  final LexemeBuilder lexemeBuilder) {
        this.expressionBuilders = List.copyOf(expressionBuilders);
        this.complexExpressionBuilder = requireNonNull(complexExpressionBuilder);
        this.lexemeBuilder = requireNonNull(lexemeBuilder);
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
     * var a = sum ( array[ (23 + g ] , array [ 23 ] ) - 4 * sum ( 1 , 2 + b )   ----------> var a = x - 4 * y
     * var a = sum ( 1 , 2 + b ) + 4 * ( array [ 23 + g - h ] - 6 )              ----------> var a = x + 4 * ( y - z )
     */
    private Expression resolveComplexExpression(final List<String> expressionTokens, final SourceLine sourceLine) {
        final List<Lexeme> lexemes = lexemeBuilder.build(expressionTokens, sourceLine);

        if (lexemes.size() == 1) { // TODO: How it has been missed by ExpressionBuilder ??????
            return resolveFromSingleLexeme(sourceLine, lexemes.get(0));
        } else {
            return buildComplexExpression(sourceLine, lexemes);
        }
    }

    private Expression resolveFromSingleLexeme(final SourceLine sourceLine, final Lexeme lexeme) {
        if (confirmType(Expression.class, lexeme)) {
            return (Expression) lexeme;
        } else {
            throw syntaxError(lexeme, sourceLine);
        }
    }

    private ComplexExpression buildComplexExpression(final SourceLine sourceLine, final List<Lexeme> lexemes) {
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
}
