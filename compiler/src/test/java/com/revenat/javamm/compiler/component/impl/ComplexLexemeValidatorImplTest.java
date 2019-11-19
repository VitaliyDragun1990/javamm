
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

import static org.junit.jupiter.api.Assertions.*;

import com.revenat.javamm.code.fragment.Expression;
import com.revenat.javamm.code.fragment.Lexeme;
import com.revenat.javamm.code.fragment.Parenthesis;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.expression.VariableExpression;
import com.revenat.javamm.code.fragment.operator.BinaryOperator;
import com.revenat.javamm.code.fragment.operator.UnaryOperator;
import com.revenat.javamm.compiler.component.ComplexLexemeValidator;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;
import com.revenat.javamm.compiler.test.doubles.ExpressionDummy;
import com.revenat.javamm.compiler.test.doubles.ExpressionStub;
import com.revenat.javamm.compiler.test.doubles.VariableDummy;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.revenat.javamm.code.fragment.Parenthesis.CLOSING_PARENTHESIS;
import static com.revenat.javamm.code.fragment.Parenthesis.OPENING_PARENTHESIS;
import static com.revenat.javamm.compiler.test.helper.CustomAsserts.assertErrorMessageContains;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toUnmodifiableList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import com.revenat.juinit.addons.ReplaceCamelCase;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("a complex lexeme validator")
class ComplexLexemeValidatorImplTest {

    private static final BinaryOperator ANY_BINARY_OPERATOR = BinaryOperator.ARITHMETIC_MULTIPLICATION;

    private static final BinaryOperator ANY_BINARY_ASSIGNMENT_OPERATOR = BinaryOperator.ASSIGNMENT_ADDITION;

    private static final UnaryOperator ANY_UNARY_OPERATOR = UnaryOperator.LOGICAL_NOT;

    private static final SourceLine SOURCE_LINE = new SourceLine("test", 5, List.of());

    private static final Expression ANY_EXPRESSION = new ExpressionDummy();

    private static final VariableExpression VARIABLE_EXPRESSION = new VariableExpression(new VariableDummy());

    private ComplexLexemeValidator lexemeValidator;

    @BeforeEach
    void setUp() {
        lexemeValidator = new ComplexLexemeValidatorImpl(new OperatorPrecedenceResolverImpl());
    }

    private void validate(final Lexeme... lexemes) {
        lexemeValidator.validate(List.of(lexemes), SOURCE_LINE);
    }

    @Test
    @Order(1)
    void shouldFailIfNoLexemesToValidate() {
        final IllegalArgumentException e = assertThrows(IllegalArgumentException.class, this::validate);

        assertErrorMessageContains(e, "Found no lexemes to validate");
    }

    @Test
    @Order(2)
    void shouldFailIfFirstLexemeIsBinaryOperator() {
        final JavammLineSyntaxError e = assertThrows(JavammLineSyntaxError.class,
                () -> validate(ANY_BINARY_OPERATOR, ANY_EXPRESSION));

        assertErrorMessageContains(e, "Expression can not start with binary operator: '%s'", ANY_BINARY_OPERATOR);
    }

    @Test
    @Order(3)
    void shouldFailIfLastLexemeIsBinaryOperator() {
        final JavammLineSyntaxError e = assertThrows(JavammLineSyntaxError.class,
                () -> validate(ANY_EXPRESSION, ANY_BINARY_OPERATOR));

        assertErrorMessageContains(e, "Expression can not end with binary operator: '%s'", ANY_BINARY_OPERATOR);
    }

    @Test
    @Order(4)
    void shouldFailIfLastLexemeIsUnaryOperator() {
        final JavammLineSyntaxError e = assertThrows(JavammLineSyntaxError.class,
                () -> validate(ANY_EXPRESSION, ANY_UNARY_OPERATOR));

        assertErrorMessageContains(e, "Expression can not end with unary operator: '%s'", ANY_UNARY_OPERATOR);
    }

    @Test
    @Order(5)
    void shouldFailIfNoExpressionBetweenTwoBinaryOperators() {
        final JavammLineSyntaxError e = assertThrows(JavammLineSyntaxError.class,
                () -> validate(ANY_EXPRESSION, ANY_BINARY_OPERATOR, ANY_BINARY_OPERATOR, ANY_EXPRESSION));

        assertErrorMessageContains(e, "An expression is expected between binary operators: '%s' and '%s'",
                ANY_BINARY_OPERATOR, ANY_BINARY_OPERATOR);
    }

    @Test
    @Order(6)
    void shouldFailIfNoBinaryOperatorBetweenTwoExpressions() {
        final JavammLineSyntaxError e = assertThrows(JavammLineSyntaxError.class,
                () -> validate(ANY_EXPRESSION, ANY_EXPRESSION));

        assertErrorMessageContains(e, "A binary operator is expected between expressions: '%s' and '%s'", ANY_EXPRESSION,
                ANY_EXPRESSION);
    }

    @Test
    @Order(7)
    void shouldFailIfBinaryOperatorRightAfterOpeningParenthesis() {
        final JavammLineSyntaxError e = assertThrows(JavammLineSyntaxError.class,
                () -> validate(OPENING_PARENTHESIS, ANY_BINARY_OPERATOR, ANY_EXPRESSION));

        assertErrorMessageContains(e, "An expression is expected for binary operator: '%s'", ANY_BINARY_OPERATOR);
    }

    @Test
    @Order(8)
    void shouldFailIfBinaryOperatorRightBeforeClosingParenthesis() {
        final JavammLineSyntaxError e = assertThrows(JavammLineSyntaxError.class,
                () -> validate(ANY_EXPRESSION, ANY_BINARY_OPERATOR, CLOSING_PARENTHESIS));

        assertErrorMessageContains(e, "An expression is expected for binary operator: '%s'", ANY_BINARY_OPERATOR);
    }

    @Test
    @Order(9)
    void shouldFailIfUnaryOperatorRightBeforeClosingParenthesis() {
        final JavammLineSyntaxError e = assertThrows(JavammLineSyntaxError.class,
                () -> validate(ANY_EXPRESSION, ANY_UNARY_OPERATOR, CLOSING_PARENTHESIS));

        assertErrorMessageContains(e, "An expression is expected for unary operator: '%s'", ANY_UNARY_OPERATOR);
    }

    @Test
    @Order(10)
    void shouldFailIfBinaryOperatorAfterUnaryOperator() {
        final JavammLineSyntaxError e = assertThrows(JavammLineSyntaxError.class,
                () -> validate(ANY_EXPRESSION, ANY_UNARY_OPERATOR, ANY_BINARY_OPERATOR, ANY_EXPRESSION));

        assertErrorMessageContains(e, "An expression is expected for binary operator: '%s'", ANY_BINARY_OPERATOR);
    }

    @Test
    @Order(11)
    void shouldFailIfNoLexemeBetweenOpeningAndClosingParenthesis() {
        final JavammLineSyntaxError e = assertThrows(JavammLineSyntaxError.class,
                () -> validate(ANY_EXPRESSION, OPENING_PARENTHESIS, CLOSING_PARENTHESIS));

        assertErrorMessageContains(e, "Parentheses are incorrectly placed");
    }

    @Test
    @Order(12)
    void shouldFailIfNoLexemeBetweenClosingAndOpeningParenthesis() {
        final JavammLineSyntaxError e = assertThrows(JavammLineSyntaxError.class,
                () -> validate(ANY_EXPRESSION, CLOSING_PARENTHESIS, OPENING_PARENTHESIS));

        assertErrorMessageContains(e, "Parentheses are incorrectly placed");
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidLexemeCombinationWithParenthesesProvider.class)
    @Order(13)
    void shouldFailIfNoBinaryOperatorBetweenTwoExpressionsIgnoringParentheses(final List<Lexeme> lexemes,
                                                                              final String expectedError) {
        final JavammLineSyntaxError e = assertThrows(JavammLineSyntaxError.class,
                () -> validate(lexemes.toArray(new Lexeme[0])));

        assertErrorMessageContains(e, expectedError);
    }

    @Test
    @Order(14)
    void shouldFailIfFirstOperandOfBinaryAssignmentOperatorIsNotVariableExpression() {
        final JavammLineSyntaxError e = assertThrows(JavammLineSyntaxError.class,
                () -> validate(ANY_EXPRESSION, ANY_BINARY_ASSIGNMENT_OPERATOR, ANY_EXPRESSION));

        assertErrorMessageContains(e, "A variable expression is expected for binary operator: '%s'", ANY_BINARY_ASSIGNMENT_OPERATOR);
    }

    @Test
    @Order(15)
    void shouldPassIfFirstOperandOfBinaryAssignmentOperatorIsVariableExpression() {
        assertDoesNotThrow(() -> validate(VARIABLE_EXPRESSION, ANY_BINARY_ASSIGNMENT_OPERATOR, ANY_EXPRESSION));
    }

    @Test
    @Order(16)
    void shouldFailIfVariableExpressionFollowedByBinaryAssignmentOperatorPrecededByAnyOperatorWithHigherPrecedence() {
        final JavammLineSyntaxError e = assertThrows(JavammLineSyntaxError.class,
                () -> validate(ANY_EXPRESSION, ANY_BINARY_OPERATOR, VARIABLE_EXPRESSION, ANY_BINARY_ASSIGNMENT_OPERATOR, ANY_EXPRESSION));

        assertErrorMessageContains(e, "A variable expression is expected for binary operator: '%s'", ANY_BINARY_ASSIGNMENT_OPERATOR);
    }

//    @Disabled
    @Test
    @Order(17)
    void shouldPassIfVariableExpressionFollowedByBinaryAssignmentOperatorNotPrecededByAnyOperatorWithHigherPrecedence() {
        assertDoesNotThrow(() -> validate(ANY_EXPRESSION, ANY_BINARY_OPERATOR, OPENING_PARENTHESIS,
                        VARIABLE_EXPRESSION, ANY_BINARY_ASSIGNMENT_OPERATOR, ANY_EXPRESSION, CLOSING_PARENTHESIS));
    }

    static final class InvalidLexemeCombinationWithParenthesesProvider implements ArgumentsProvider {

        private static final int MAX_PARENTHESES_REPEAT_COUNT = 5;

        @Override
        public Stream<? extends Arguments> provideArguments(final ExtensionContext context) {
            final Expression expression1 = new ExpressionStub(1);
            final Expression expression2 = new ExpressionStub(2);
            final String errorMessage = "A binary operator is expected between expressions: '1' and '2'";

            return Arrays.stream(Parenthesis.values())
                    .map(parenthesis -> IntStream.range(1, MAX_PARENTHESES_REPEAT_COUNT + 1)
                            .mapToObj(repeatCount -> Stream.generate(() -> parenthesis).limit(repeatCount))
                            .map(parenthesisRepeatStream -> Arguments.arguments(
                                    Stream.of(
                                            Stream.of(expression1),
                                            parenthesisRepeatStream,
                                            Stream.of(expression2)
                                            ).flatMap(identity()).collect(toUnmodifiableList()),
                                    errorMessage
                                    ))).flatMap(identity());
            /*
            return
                1 ( 2
                1 ( ( 2
                1 ( ( ( 2
                1 ( ( ( ( 2
                1 ( ( ( ( ( 2
                1 ) 2
                1 ) ) 2
                1 ) ) ) 2
                1 ) ) ) ) 2
                1 ) ) ) ) ) 2
             */
        }
    }
}
