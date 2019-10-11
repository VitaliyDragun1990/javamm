
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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;

import com.revenat.javamm.code.fragment.Expression;
import com.revenat.javamm.code.fragment.Lexeme;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.expression.UnaryAssignmentExpression;
import com.revenat.javamm.code.fragment.expression.UnaryPostfixAssignmentExpression;
import com.revenat.javamm.code.fragment.expression.UnaryPrefixAssignmentExpression;
import com.revenat.javamm.code.fragment.operator.UnaryOperator;
import com.revenat.javamm.compiler.component.LexemeBuilder;
import com.revenat.javamm.compiler.component.UnaryAssignmentExpressionResolver;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;
import com.revenat.javamm.compiler.test.builder.ComponentBuilder;

import java.util.List;
import java.util.stream.Collectors;

import static com.revenat.javamm.compiler.test.helper.CustomAsserts.assertErrorMessageContains;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import com.revenat.juinit.addons.ReplaceCamelCase;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("a unary assignment expression resolver")
class UnaryAssignmentExpressionResolverImplTest {
    private static final SourceLine SOURCE_LINE = new SourceLine("test", 5, List.of());

    private LexemeBuilder lexemeBuilder;

    private UnaryAssignmentExpressionResolver unaryAssignmentExpressionResolver;


    @BeforeEach
    void setUp() {
        lexemeBuilder = new ComponentBuilder().buildLexemeBuilder();
        unaryAssignmentExpressionResolver = new UnaryAssignmentExpressionResolverImpl();
    }

    private List<Lexeme> buildLexemesFrom(final String expression) {
        final List<String> tokens = List.of(expression.split(" "));
        final List<Lexeme> lexemes =  lexemeBuilder.build(tokens, SOURCE_LINE);
        return lexemes;
    }

    private List<Lexeme> resolve(final String expression) {
        final List<Lexeme> lexemes = buildLexemesFrom(expression);
        return unaryAssignmentExpressionResolver.resolve(lexemes, SOURCE_LINE);
    }

    private void assertResolvedLexemeCount(final List<Lexeme> lexemes, final int expectedCount) {
        assertThat(lexemes, hasSize(expectedCount));
    }

    private void assertResolvedExpressionInPositions(final List<Lexeme> lexemes,
                                                     final Class<? extends Expression> expectedType,
                                                     final int... expectedPositions) {
        for (final int expectedPosition : expectedPositions) {
            assertThat(lexemes.get(expectedPosition), instanceOf(expectedType));
        }
    }

    @Test
    @Order(1)
    void shouldCorrectlyResolveUnaryIncrementOperator() {
        final List<Lexeme> resolvedLexemes = resolve("++ a");

        final UnaryPrefixAssignmentExpression resolvedExpression = (UnaryPrefixAssignmentExpression) resolvedLexemes.get(0);

        assertThat(resolvedExpression.getOperator(), is(UnaryOperator.INCREMENT));
    }

    @Test
    @Order(2)
    void shouldCorrectlyResolveUnaryDecrementOperator() {
        final List<Lexeme> resolvedLexemes = resolve("a --");

        final UnaryPostfixAssignmentExpression resolvedExpression = (UnaryPostfixAssignmentExpression) resolvedLexemes.get(0);

        assertThat(resolvedExpression.getOperator(), is(UnaryOperator.DECREMENT));
    }

    @ParameterizedTest
    @CsvSource({
        "++ a + 1,            3, 0",
        "a + ++ a,            3, 2",
        "10 * ( ++ a ),       5, 3",
        "10 * ( ++ a ) / 2,   7, 3",
        "( ++ a ) / 2,        5, 1",
    })
    @Order(3)
    void shouldResolvePrefixUnaryAssignmentExpression(final String expression,
                                                      final int expectedLexemeCount,
                                                      final int expectedResolvedExpressionPosition) {
        final List<Lexeme> resolvedLexemes = resolve(expression);

        assertResolvedLexemeCount(resolvedLexemes, expectedLexemeCount);
        assertResolvedExpressionInPositions(resolvedLexemes, UnaryPrefixAssignmentExpression.class, expectedResolvedExpressionPosition);
    }

    @ParameterizedTest
    @CsvSource({
        "a ++ + 1,            3, 0",
        "a + a ++,            3, 2",
        "10 * ( a ++ ),       5, 3",
        "10 * ( a ++ ) / 2,   7, 3",
        "( a ++ ) / 2,        5, 1"
    })
    @Order(4)
    void shouldResolvePostfixUnaryAssignmentExpression(final String expresison,
                                                       final int expectedLexemeCount,
                                                       final int expectedResolvedExpressionPosition) {
        final List<Lexeme> resolvedLexemes = resolve(expresison);

        assertResolvedLexemeCount(resolvedLexemes, expectedLexemeCount);
        assertResolvedExpressionInPositions(resolvedLexemes, UnaryPostfixAssignmentExpression.class, expectedResolvedExpressionPosition);
    }

    @ParameterizedTest
    @CsvSource({
        "++ a - -- b * ( ++ c ),         7, 0, 2, 5",
        "10 * -- a + ++ b / ( -- c ),    9, 2, 4, 7",
    })
    @Order(5)
    void shouldResolveSeveralPrefixUnaryAssignmentExpressionInOneStatement(final String expression,
                                                                           final int expectedLexemeCount,
                                                                           final int firstResolvedExpressionPosition,
                                                                           final int secondResolvedExpressionPosition,
                                                                           final int thirdResolvedExpressionPosition) {
        final List<Lexeme> resolvedLexemes = resolve(expression);

        assertResolvedLexemeCount(resolvedLexemes, expectedLexemeCount);
        assertResolvedExpressionInPositions(resolvedLexemes,
                                            UnaryPrefixAssignmentExpression.class,
                                            firstResolvedExpressionPosition,
                                            secondResolvedExpressionPosition,
                                            thirdResolvedExpressionPosition);

    }

    @ParameterizedTest
    @CsvSource({
        "a ++ - b -- * ( c ++ ),         7, 0, 2, 5",
        "10 * a -- + b ++ / ( c -- ),    9, 2, 4, 7",
    })
    @Order(6)
    void shouldResolveSeveralPostfixUnaryAssignmentExpressionInOneStatement(final String expression,
                                                                            final int expectedLexemeCount,
                                                                            final int firstResolvedExpressionPosition,
                                                                            final int secondResolvedExpressionPosition,
                                                                            final int thirdResolvedExpressionPosition) {
        final List<Lexeme> resolvedLexemes = resolve(expression);

        assertResolvedLexemeCount(resolvedLexemes, expectedLexemeCount);
        assertResolvedExpressionInPositions(resolvedLexemes,
                                            UnaryPostfixAssignmentExpression.class,
                                            firstResolvedExpressionPosition,
                                            secondResolvedExpressionPosition,
                                            thirdResolvedExpressionPosition);
    }

    @ParameterizedTest
    @CsvSource({
        "++ ( a ),              3, 1",
        "( a ) --,              3, 1",
        "-- ( ( a ) ),          5, 2",
        "( ( a ) ) ++,          5, 2",
        "++ ( ( ( a ) ) ),      7, 3",
        "( ( ( ( a ) ) ) ) --,  9, 4",
    })
    @Order(7)
    void shouldIgnoreParenthesisWhenResolvingUnaryAssignmentExpression(final String expression,
                                                                       final int expectedLexemeCount,
                                                                       final int expectedResolvedExpressionPosition) {
        final List<Lexeme> resolvedLexemes = resolve(expression);

        assertResolvedLexemeCount(resolvedLexemes, expectedLexemeCount);
        assertResolvedExpressionInPositions(resolvedLexemes, UnaryAssignmentExpression.class, expectedResolvedExpressionPosition);
    }

    @ParameterizedTest
    @CsvSource({
            "++ ( a * 2 ),      ++",
            "( ( 2 + a ) ) --,  --"
    })
    @Order(8)
    void shouldFailIfArgumentForUnaryAssignmentOperatorIsNotASignleExpression(final String expression, final String operatorCode) {
        final JavammLineSyntaxError e = assertThrows(JavammLineSyntaxError.class, () -> resolve(expression));

        assertErrorMessageContains(e, "Invalid argument for '%s' operator", operatorCode);
    }

    @ParameterizedTest
    @CsvSource({
        "++ 10,          ++",
        "25 --,          --",
        "( ( 10 ) ) ++,  ++",
        "-- ( ( 10 ) ),  --",
    })
    @Order(9)
    void shouldFailIfArgumentForUnaryAssignmentOperatorIsNotVariableExpression(final String expression, final String operatorCode) {
        final JavammLineSyntaxError e = assertThrows(JavammLineSyntaxError.class, () -> resolve(expression));

        assertErrorMessageContains(e, "A variable expression is expected for unary operator: '%s'", operatorCode);
    }

    @ParameterizedTest
    @CsvSource({
        "++ a --,       --",
        "++ ( a ) ++,   ++",
    })
    @Order(10)
    void shouldFailIfUnaryAssignmentOperatorWithoutCorrespondingArgument(final String expression, final String operatorCode) {
        final JavammLineSyntaxError e = assertThrows(JavammLineSyntaxError.class, () -> resolve(expression));

        assertErrorMessageContains(e, "A variable expression is expected for unary operator: '%s'", operatorCode);
    }

    @ParameterizedTest
    @CsvSource({
        "( a + b )",
        "( ( ( a + b ) ) )",
        "a * ( 1 + b ) / ( 2 + 4 ) % 6",
    })
    void shouldNotChangeAnyLexemeIfNoUnaryAssignmentOperatorFound(final String expression) {
        assertNoLexemeChanged(expression);
    }

    private void assertNoLexemeChanged(final String expression) {
        assertThat(getExpressionFrom(resolve(expression)), is(expression));
    }

    private Object getExpressionFrom(final List<Lexeme> lexemes) {
        return lexemes.stream()
                .map(Object::toString)
                .collect(Collectors.joining(" "));
    }
}
