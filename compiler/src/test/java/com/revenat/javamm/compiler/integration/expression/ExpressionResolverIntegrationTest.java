
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

package com.revenat.javamm.compiler.integration.expression;

import com.revenat.javamm.code.fragment.Expression;
import com.revenat.javamm.code.fragment.Lexeme;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.expression.ComplexExpression;
import com.revenat.javamm.code.fragment.expression.PostfixNotationComplexExpression;
import com.revenat.javamm.code.fragment.expression.TernaryConditionalExpression;
import com.revenat.javamm.compiler.component.ExpressionResolver;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;
import com.revenat.javamm.compiler.test.builder.ComponentBuilder;
import com.revenat.juinit.addons.ReplaceCamelCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static com.revenat.javamm.compiler.test.helper.CustomAsserts.assertErrorMessageContains;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("an expression resolver")
public class ExpressionResolverIntegrationTest {

    private static final SourceLine SOURCE_LINE = new SourceLine("module1", 5, List.of());

    private ExpressionResolver expressionResolver;

    @BeforeEach
    void setUp() {
        expressionResolver = new ComponentBuilder().buildExpressionResolver();
    }

    private Expression resolve(final String expression) {
        return expressionResolver.resolve(List.of(expression.split(" ")), SOURCE_LINE);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        // Unary ~
        "5",
        "~ 5",
        "~ 5 + 5",
        "~ 5 + ~ 5",
        "~ 5 * ~ ~ ~ ~ 5",
        "~ 5 * ~ ( ~ ( ~ 5 ) )",

        // Unary -
        "- 5",
        "- 5 + 5",
        "- 5 + - 5",
        "- 5 * - + - + 5",
        "~ 5 * - ( + ( - 5 ) )",

        // Increment
        "a ++",
        "++ a",
        "a ++ + 5",
        "5 + a ++",
        "5 + a ++ + 5",
        "++ a + 5",
        "5 + ++ a",
        "5 + ++ a + 5",
        "+ 5 + ++ a + + + + + + 5",
        "+ 5 + + a ++ + + + + + 5",
        "+ 5 + ++ a + ++ a + ++ a + 5",
        "+ 5 + a ++ + a ++ + a ++ + 5",

        // Binary assignment
        "a += 5",
        "a += 4 * 5",
        "3 * ( a += 4 + 5 )"
    })
    @Order(1)
    void shouldResolveCorrectExpression(final String expression) {
        assertDoesNotThrow(() -> resolve(expression));
    }

    @ParameterizedTest
    @CsvSource( {
        "true ? 1 : 2,                 true, 1, 2",
        "false ? 1 + 2 : 2 - 1,        false, 1 + 2, 2 - 1",
        "a ? 1 : 2,                    a, 1, 2",
    })
    @Order(2)
    void shouldResolveSimpleTernaryConditionalExpressions(final String expression,
                                                          final String expectedPredicateOperand,
                                                          final String expectedTrueCaseOperand,
                                                          final String expectedFalseCaseOperand) {
        final Expression result = resolve(expression);

        assertTernaryConditionalExpression(result, expectedPredicateOperand, expectedTrueCaseOperand, expectedFalseCaseOperand);
    }

    @ParameterizedTest
    @CsvSource( {
        "? 1 : 2,               Ternary operator '?:' should have predicate clause expression",
        "+= ? 1 : 2,            Ternary operator '?:' should have predicate clause expression",  // '+=' operator has lower precedence than '?:'
        "true ? : 2,            Ternary operator '?:' should have true clause expression",
        "true ? 1 :,            Ternary operator '?:' should have false clause expression",
    })
    @Order(3)
    void shouldFailIfAnyClauseOfTernaryConditionalOperatorIsAbsent(final String expression, final String expectedMessage) {
        final JavammLineSyntaxError e = assertThrows(JavammLineSyntaxError.class, () -> resolve(expression));

        assertErrorMessageContains(e, expectedMessage);
    }

    @ParameterizedTest
    @CsvSource( {
        "true ? 1 : +,              Unsupported expression: +",
        "false ? - : 2,             Unsupported expression: -",
        "+ ? true : false,          Unsupported expression: +",
    })
    @Order(4)
    void shouldFailIfAnyClauseOfTernaryConditionalOperatorIsNotExpression(final String expression, final String expectedMsg) {
        final JavammLineSyntaxError e = assertThrows(JavammLineSyntaxError.class, () -> resolve(expression));

        assertErrorMessageContains(e, expectedMsg);
    }

    @ParameterizedTest
    @CsvSource( {
        "10 > a ? 1 : 2,                                            10 > a, 1, 2",
        "a / 2 + b ? 1 : 2,                                         a / 2 + b, 1, 2",
        "a + ( 2 > b ) ? 1 : 2,                                     a + ( 2 > b ), 1, 2",
        "a + b / c - ( 2 * ( b + 2 ) ) ? 1 : 2,                     a + b / c - ( 2 * ( b + 2 ) ), 1, 2",
        "a + b / c - ( 2 * ( b + 2 ) / ( 1 + 6 ) ) ? 1 : 2,         a + b / c - ( 2 * ( b + 2 ) / ( 1 + 6 ) ), 1, 2",
    })
    @Order(5)
    void shouldResolveTernaryConditionalExpressionWithComplexPredicateClause(final String expression,
                                                                             final String expectedPredicate,
                                                                             final String expectedTrueClause,
                                                                             final String expectedFalseClause) {

        final Expression result = resolve(expression);

        assertTernaryConditionalExpression(result, expectedPredicate, expectedTrueClause, expectedFalseClause);

    }

    @ParameterizedTest
    @CsvSource( {
        "a += 4 > b ? 10 : 20,                      4 > b",
        "a + ( ( b > 4 ) ? 10 : 20 ),               ( b > 4 )",
    })
    @Order(6)
    void shouldCorrectlySeparateTernaryConditionalPredicateClauseFromPreviousLexemes(final String expression,
                                                                                     final String expectedPredicate) {
        final Expression result = resolve(expression);

        final List<Lexeme> resolved = assertPostfixComplexExpression(result).getLexemes();
        assertThat(resolved, hasSize(3));
        assertTernaryConditionalExpressionWithPredicate(resolved.get(1), expectedPredicate);
    }

    @ParameterizedTest
    @CsvSource( {
        "a > 9 ? ( 1 + 2 - a ) : 2,                                     a > 9, ( 1 + 2 - a ) , 2",
        "a > 9 ? ( ( 1 + 2 - ( a * 2 ) ) ) : 2,                         a > 9, ( ( 1 + 2 - ( a * 2 ) ) ) , 2",
        "a > 9 ? ( true ? 10 : 20 ) : 2,                                a > 9, ( true ? 10 : 20 ) , 2",
        "a > 9 ? ( ( ( true ? 10 : 20 ) ) ) : 2,                        a > 9, ( ( ( true ? 10 : 20 ) ) ) , 2",
        "b > a ? 10 > 20 ? 10 : 20 : c,                                 b > a, 10 > 20 ? 10 : 20, c"
    })
    @Order(7)
    void shouldResolveTernaryConditionalExpressionWithComplexTrueClause(final String expression,
                                                                        final String expectedPredicate,
                                                                        final String expectedTrueClause,
                                                                        final String expectedFalseClause) {

        final Expression result = resolve(expression);

        assertTernaryConditionalExpression(result, expectedPredicate, expectedTrueClause, expectedFalseClause);

    }

    @ParameterizedTest
    @CsvSource( {
        "a > 9 ? 1 : ( ( 2 + b ) / c ),                               a > 9, 1, ( ( 2 + b ) / c )",
        "a > 9 ? 1 : ( ( ( 2 + b ) / ( c - 2 ) ) ),                   a > 9, 1, ( ( ( 2 + b ) / ( c - 2 ) ) )",
        "a > 9 ? 1 : ( true ? ( true ? 10 : 20 ) : ( 1 + 2 - a ) ),   a > 9, 1, ( true ? ( true ? 10 : 20 ) : ( 1 + 2 - a ) )",
    })
    @Order(8)
    void shouldResolveTernaryConditionalExpressionWithComplexFalseClause(final String expression,
                                                                         final String expectedPredicate,
                                                                         final String expectedTrueClause,
                                                                         final String expectedFalseClause) {

        final Expression result = resolve(expression);

        assertTernaryConditionalExpression(result, expectedPredicate, expectedTrueClause, expectedFalseClause);

    }

    @ParameterizedTest
    @CsvSource( {
        "( true ? 10 : 25 + 10 ) + a,                   3, 0, true, 10, 25 + 10",
        "( ( ( true ? 10 : 25 + 10 ) ) ) + a / 2,       5, 0, true, 10, 25 + 10",
        "( a + ( true ? 10 : 25 + 10 ) ) * a / 2,       7, 1, true, 10, 25 + 10",
        "( a + ( true ? 10 : 25 + 10 ) * b ) * a / 2,   9, 1, true, 10, 25 + 10",
    })
    @Order(9)
    void shouldResolveTernaryConditionalExpressionFollowedBySomeDifferentExpression(final String expression,
                                                                                    final int expectedLexemeCount,
                                                                                    final int expectedPosition,
                                                                                    final String expectedPredicate,
                                                                                    final String expectedTrueClause,
                                                                                    final String expectedFalseClause) {
        final Expression result = resolve(expression);

        final List<Lexeme> resolved = assertPostfixComplexExpression(result).getLexemes();
        assertThat(resolved, hasSize(expectedLexemeCount));
        assertTernaryConditionalExpression(resolved.get(expectedPosition), expectedPredicate, expectedTrueClause, expectedFalseClause);
    }

    @ParameterizedTest
    @CsvSource( {
        "( true ? 10 : 20 ) + ( false ? 20 : 10 ),                      3, 1, false, 20, 10",
        "( ( ( true ? 10 : 20 ) + ( false ? 20 : 10 ) ) ),              3, 1, false, 20, 10",
    })
    @Order(10)
    void shouldResolveTernaryConditionalExpressionFollowedByAnotherTernaryExpression(final String expression,
                                                                                     final int expectedLexemeCount,
                                                                                     final int expectedSecondTernaryPosition,
                                                                                     final String secondTernaryExpectedPredicate,
                                                                                     final String secondTernaryExpectedTrueClause,
                                                                                     final String secondTernaryExpectedFalseClause) {
        final Expression result = resolve(expression);

        final List<Lexeme> resolved = assertPostfixComplexExpression(result).getLexemes();
        assertThat(resolved, hasSize(expectedLexemeCount));
        assertTernaryConditionalExpression(resolved.get(expectedSecondTernaryPosition),
            secondTernaryExpectedPredicate,
            secondTernaryExpectedTrueClause,
            secondTernaryExpectedFalseClause);
    }

    @ParameterizedTest
    @CsvSource( {
        // Constants only
        "* 5 + 5,                   Expression can not start with binary operator: '*'",
        "5 + 5 *,                   Expression can not end with binary operator: '*'",
        "5 + 5 ~,                   Expression can not end with unary operator: '~'",
        "5 * / 5,                   An expression is expected between binary operators: '*' and '/'",
        "4 5 + 6,                   A binary operator is expected between expressions: '4' and '5'",
        "3 + 4 ( 5 - 6 ),           A binary operator is expected between expressions: '4' and '5'",
        "( 3 + 4 ) 5 - 6,           A binary operator is expected between expressions: '4' and '5'",
        "( 5 + 5 - ~ ) - 5,         An expression is expected for unary operator: '~'",
        "( * 5 + 5 ) - 5,           An expression is expected for binary operator: '*'",
        "( 5 + 5 * ) - 5,           An expression is expected for binary operator: '*'",
        "~ * 5,                     An expression is expected for binary operator: '*'",
        "4 + 5 ( ) 5 - 6,           Parentheses are incorrectly placed",
        "4 + 5 ) ( 5 - 6,           Parentheses are incorrectly placed",
        "4 + 5 ( ( ) ) 5 - 6,       Parentheses are incorrectly placed",
        "4 + 5 ) ) ( ( 5 - 6,       Parentheses are incorrectly placed",
        "5 += 5,                    A variable expression is expected for binary operator: '+='",
        "5 + 5 += 5,                A variable expression is expected for binary operator: '+='",
        "5 + 5 += 5 * 5,            A variable expression is expected for binary operator: '+='",
        "5 + ( 5 += 5 ) * 5,        A variable expression is expected for binary operator: '+='",
        "( 5 + 5 ) += 5,            A variable expression is expected for binary operator: '+='",

        // Increment / Decrement
        "5 ++,                      A variable expression is expected for unary operator: '++'",
        "++ 5,                      A variable expression is expected for unary operator: '++'",
        "++ 5 + 5 + + + + + 5,      A variable expression is expected for unary operator: '++'",
        "5 ++ + 5 + + + + + 5,      A variable expression is expected for unary operator: '++'",
        "+ 5 + ++ 5 + + + + 5,      A variable expression is expected for unary operator: '++'",
        "+ 5 + 5 ++ + + + + 5,      A variable expression is expected for unary operator: '++'",
        "+ 5 + + + + 5 + ++ 5,      A variable expression is expected for unary operator: '++'",
        "+ 5 + + + + 5 + 5 ++,      A variable expression is expected for unary operator: '++'",

        // Binary assignment
        "3 * a += 4 + 5,            A variable expression is expected for binary operator: '+='",
        "- a += 4 + 5,              A variable expression is expected for binary operator: '+='",

        // Increment without variable
        "++ +,                      A variable expression is expected for unary operator: '++'",
        "++ (,                      A variable expression is expected for unary operator: '++'",
        "+ ++,                      A variable expression is expected for unary operator: '++'",
        ") ++,                      A variable expression is expected for unary operator: '++'",

        // Increment and something is missing
        "++ a ++,                   A variable expression is expected for unary operator: '++'",
        "++ a ++ a ++,              A variable expression is expected for unary operator: '++'",
        "a ++ ++ a,                 A binary operator is expected between expressions: 'a++' and '++a'",
        "a ++ a ++,                 A binary operator is expected between expressions: 'a++' and 'a++'",
        "++ a ++ a,                 A binary operator is expected between expressions: '++a' and '++a'",
        "++ ++ a,                   A variable expression is expected for unary operator: '++'",
        "a ++ ++,                   A variable expression is expected for unary operator: '++'",
        "5 ++ a,                    A binary operator is expected between expressions: '5' and '++a'",
        "a ++ 5,                    A binary operator is expected between expressions: 'a++' and '5'",
        "5 ++ 5,                    A variable expression is expected for unary operator: '++'",
    })
    @Order(11)
    void shouldFailIfExpressionIsSyntacticallyIncorrect(final String expression, final String expectedMessage) {
        final JavammLineSyntaxError e = assertThrows(JavammLineSyntaxError.class, () -> resolve(expression));

        assertErrorMessageContains(e, "Syntax error in 'module1' [Line: 5]: %s", expectedMessage);
    }

    private void assertTernaryConditionalExpressionWithPredicate(final Lexeme expression, final String expectedPredicate) {
        final TernaryConditionalExpression ternary = assertTernaryConditionalExpression(expression);

        assertThat(ternary.getPredicateOperand(), instanceOf(ComplexExpression.class));
        assertThat(ternary.getPredicateOperand().toString(), equalTo(expectedPredicate));
    }

    private void assertTernaryConditionalExpression(final Lexeme expectedExpression,
                                                    final String expectedPredicateOperand,
                                                    final String expectedTrueCaseOperand,
                                                    final String expectedFalseCaseOperand) {
        final TernaryConditionalExpression expression = assertTernaryConditionalExpression(expectedExpression);

        assertThat(expression.getPredicateOperand().toString(), equalTo(expectedPredicateOperand));
        assertThat(expression.getTrueClauseOperand().toString(), equalTo(expectedTrueCaseOperand));
        assertThat(expression.getFalseClauseOperand().toString(), equalTo(expectedFalseCaseOperand));

    }

    private TernaryConditionalExpression assertTernaryConditionalExpression(final Lexeme expression) {
        assertThat(expression, instanceOf(TernaryConditionalExpression.class));
        return (TernaryConditionalExpression) expression;
    }

    private ComplexExpression assertPostfixComplexExpression(final Expression expression) {
        assertThat(expression, instanceOf(PostfixNotationComplexExpression.class));
        return (ComplexExpression) expression;
    }
}
