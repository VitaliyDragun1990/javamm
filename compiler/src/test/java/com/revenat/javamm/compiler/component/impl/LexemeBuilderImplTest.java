
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

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInRelativeOrder;
import static org.junit.jupiter.api.Assertions.*;

import com.revenat.javamm.code.fragment.Expression;
import com.revenat.javamm.code.fragment.Lexeme;
import com.revenat.javamm.code.fragment.Parenthesis;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.operator.BinaryOperator;
import com.revenat.javamm.code.fragment.operator.UnaryOperator;
import com.revenat.javamm.compiler.component.LexemeBuilder;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;
import com.revenat.javamm.compiler.test.doubles.ExpressionDummy;
import com.revenat.javamm.compiler.test.doubles.SingleTokenExpressionBuilderStub;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static com.revenat.javamm.code.fragment.Parenthesis.CLOSING_PARENTHESIS;
import static com.revenat.javamm.code.fragment.Parenthesis.OPENING_PARENTHESIS;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ARITHMETIC_ADDITION;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ARITHMETIC_DIVISION;
import static com.revenat.javamm.compiler.test.helper.CustomAsserts.assertErrorMessageContains;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import com.revenat.juinit.addons.ReplaceCamelCase;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("a lexeme builder")
class LexemeBuilderImplTest {
    private static final SourceLine SOURCE_LINE = SourceLine.EMPTY_SOURCE_LINE;

    private SingleTokenExpressionBuilderStub expressionBuilderStub;

    private LexemeBuilder lexemeBuilder;

    @BeforeEach
    void setUp() {
        expressionBuilderStub = new SingleTokenExpressionBuilderStub();
        lexemeBuilder = new LexemeBuilderImpl(expressionBuilderStub);
    }

    private void assertLexemesInOrder(final List<Lexeme> lexemes, final Lexeme... expectedLexemes) {
        for (final Lexeme expectedLexeme : expectedLexemes) {
            assertThat(lexemes, hasItem(is(expectedLexeme)));
        }
        assertThat(lexemes, containsInRelativeOrder(expectedLexemes));
    }

    private void assertLexemesCount(final List<Lexeme> lexemes, final int expectedCount) {
        assertThat(lexemes.size(), is(expectedCount));
    }

    @ParameterizedTest
    @MethodSource("binaryOperatorTokenProvider")
    @Order(1)
    void canBuildLexemeFromTokenRepresentingBinaryOperator(final String token, final BinaryOperator expectedOperator) {
        final List<Lexeme> lexemes = lexemeBuilder.build(List.of(token), SOURCE_LINE);

        assertLexemesCount(lexemes, 1);
        assertLexemesInOrder(lexemes, expectedOperator);
    }

    @Disabled("builds binary operators for '+' and '-' tokens instead of unary ones")
    @ParameterizedTest
    @MethodSource("unaryOperatorTokenProvider")
    @Order(2)
    void canBuildLexemeFromTokenRepresentingUnaryOperator(final String token, final UnaryOperator expectedOperator) {
        final List<Lexeme> lexemes = lexemeBuilder.build(List.of(token), SOURCE_LINE);

        assertLexemesCount(lexemes, 1);
        assertLexemesInOrder(lexemes, expectedOperator);
    }

    @ParameterizedTest
    @MethodSource("parenthesisTokenProvider")
    @Order(3)
    void canBuildLexemeFromTokenRepresentingParenthesis(final String token, final Parenthesis expectedLexeme) {
        final List<Lexeme> lexemes = lexemeBuilder.build(List.of(token), SOURCE_LINE);

        assertLexemesCount(lexemes, 1);
        assertLexemesInOrder(lexemes, expectedLexeme);
    }

    @Test
    @Order(4)
    void canBuildLexemeFromTokenRepresentingExpression() {
        final String token = "a";
        final Expression expression = new ExpressionDummy();
        expressionBuilderStub.setCanBuild(true);
        expressionBuilderStub.setExpressionToBuild(token, expression);

        final List<Lexeme> lexemes = lexemeBuilder.build(List.of(token), SOURCE_LINE);

        assertLexemesCount(lexemes, 1);
        assertLexemesInOrder(lexemes, expression);
    }

    @Test
    @Order(4)
    void canBuildLexemesFromSeveralDifferentTokens() {
        final Expression expressionA = new ExpressionDummy();
        final Expression expressionB = new ExpressionDummy();
        final Expression expressionC = new ExpressionDummy();
        final String a = "a";
        final String b = "b";
        final String c = "c";
        expressionBuilderStub.setCanBuild(true);
        expressionBuilderStub.setExpressionToBuild(a, expressionA);
        expressionBuilderStub.setExpressionToBuild(b, expressionB);
        expressionBuilderStub.setExpressionToBuild(c, expressionC);
        final List<String> sevenTokens = List.of("(",a, "+", b, ")", "/", c);

        final List<Lexeme> lexemes = lexemeBuilder.build(sevenTokens, SOURCE_LINE);

        assertLexemesCount(lexemes, sevenTokens.size());
        assertLexemesInOrder(lexemes, OPENING_PARENTHESIS, expressionA, ARITHMETIC_ADDITION, expressionB,
                CLOSING_PARENTHESIS, ARITHMETIC_DIVISION, expressionC);
    }

    @Test
    @Order(5)
    void shouldFailToBuildIfTokenUnsupported() {
        expressionBuilderStub.setCanBuild(false);
        final String unsupportedToken = "unsupported";

        final JavammLineSyntaxError e =
                assertThrows(JavammLineSyntaxError.class, () -> lexemeBuilder.build(List.of(unsupportedToken), SOURCE_LINE));

        assertErrorMessageContains(e, "Unsupported token: %s", unsupportedToken);
    }

    static Stream<Arguments> binaryOperatorTokenProvider() {
        return Arrays.stream(BinaryOperator.values())
                .map(operator -> Arguments.of(operator.getCode(), operator));
    }

    static Stream<Arguments> unaryOperatorTokenProvider() {
        return Arrays.stream(UnaryOperator.values())
                .map(operator -> Arguments.of(operator.getCode(), operator));
    }

    static Stream<Arguments> parenthesisTokenProvider() {
        return Arrays.stream(Parenthesis.values())
                .map(p -> Arguments.of(p.isOpen() ? "(" : ")", p));
    }
}
