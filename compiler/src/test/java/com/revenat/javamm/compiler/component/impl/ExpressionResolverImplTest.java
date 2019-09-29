
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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.revenat.javamm.code.fragment.Expression;
import com.revenat.javamm.code.fragment.Lexeme;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.expression.ComplexExpression;
import com.revenat.javamm.compiler.component.ExpressionResolver;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;
import com.revenat.javamm.compiler.test.doubles.ComplexExpressionBuilderStub;
import com.revenat.javamm.compiler.test.doubles.ExpressionBuilderStub;
import com.revenat.javamm.compiler.test.doubles.ExpressionDummy;
import com.revenat.javamm.compiler.test.doubles.LexemeBuilderStub;
import com.revenat.javamm.compiler.test.helper.CustomAsserts;

import java.util.List;
import java.util.Set;

import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ARITHMETIC_ADDITION;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.revenat.juinit.addons.ReplaceCamelCase;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("a expression resolver")
class ExpressionResolverImplTest {
    private static final Expression EXPRESSION = new ExpressionDummy();
    private static final SourceLine SOURCE_LINE = SourceLine.EMPTY_SOURCE_LINE;

    private ExpressionBuilderStub expressionBuilder;
    private ComplexExpressionBuilderStub complexExpressionBuilder;
    private LexemeBuilderStub lexemeBuilder;

    private ExpressionResolver expressionResolver;

    @BeforeEach
    void setUp() {
        expressionBuilder = new ExpressionBuilderStub();
        complexExpressionBuilder = new ComplexExpressionBuilderStub();
        lexemeBuilder = new LexemeBuilderStub();

        expressionResolver = new ExpressionResolverImpl(Set.of(expressionBuilder), complexExpressionBuilder, lexemeBuilder);
    }

    @Test
    @Order(1)
    void canNotBeBuildWithoutDependencies() {
        assertThrows(NullPointerException.class, () -> new ExpressionResolverImpl(null, complexExpressionBuilder, lexemeBuilder));
        assertThrows(NullPointerException.class, () -> new ExpressionResolverImpl(Set.of(expressionBuilder), null, lexemeBuilder));
        assertThrows(NullPointerException.class, () -> new ExpressionResolverImpl(Set.of(expressionBuilder), complexExpressionBuilder, null));
    }

    @Test
    @Order(2)
    void shouldResolveExpressionFromSingleToken() {
        final List<String> tokens = List.of("hello");
        expressionBuilder.setCanBuild(true);
        expressionBuilder.setExpressionToBuild(tokens, EXPRESSION);

        final Expression resolvedExpression = expressionResolver.resolve(tokens, SOURCE_LINE);

        assertThat(resolvedExpression, is(EXPRESSION));
    }

    @Test
    @Order(3)
    void shouldResolveComplexExpressionFromSeveralTokens() {
        final List<String> tokens = List.of("a", "+", "10");
        final List<Lexeme> lexemes = List.of(EXPRESSION, ARITHMETIC_ADDITION, EXPRESSION);
        final ComplexExpression complexExpression = complexExpression(lexemes);
        expressionBuilder.setCanBuild(false);
        lexemeBuilder.setLexemesToBuild(tokens, lexemes);
        complexExpressionBuilder.setExpressionToBuild(lexemes, complexExpression);

        final Expression resolvedExpression = expressionResolver.resolve(tokens, SOURCE_LINE);

        assertThat(resolvedExpression, is(complexExpression));
    }

    @Test
    @Order(4)
    void shouldFailIfProvidedTokenDoesNotRepresentExpression() {
        final List<String> singleTokenList = List.of("+");
        final List<Lexeme> lexemes = List.of(ARITHMETIC_ADDITION);
        expressionBuilder.setCanBuild(false);
        lexemeBuilder.setLexemesToBuild(singleTokenList, lexemes);

        final JavammLineSyntaxError e = assertThrows(JavammLineSyntaxError.class, () -> expressionResolver.resolve(singleTokenList, SOURCE_LINE));

        CustomAsserts.assertErrorMessageContains(e, "Unsupported expression: %s",lexemes.get(0));
    }

    private ComplexExpression complexExpression(final List<Lexeme> lexemes) {
        return new ComplexExpression(lexemes);
    }
}
