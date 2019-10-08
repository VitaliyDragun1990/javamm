
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

package com.revenat.javamm.compiler.integration;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import com.revenat.javamm.code.fragment.Lexeme;
import com.revenat.javamm.code.fragment.Operator;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.compiler.component.LexemeBuilder;
import com.revenat.javamm.compiler.component.impl.LexemeAmbiguityResolverImpl;
import com.revenat.javamm.compiler.component.impl.LexemeBuilderImpl;
import com.revenat.javamm.compiler.component.impl.VariableBuilderImpl;
import com.revenat.javamm.compiler.component.impl.expression.builder.SingleTokenExpressionBuilderImpl;

import java.util.List;

import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ARITHMETIC_ADDITION;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ARITHMETIC_SUBTRACTION;
import static com.revenat.javamm.code.fragment.operator.UnaryOperator.ARITHMETICAL_UNARY_MINUS;
import static com.revenat.javamm.code.fragment.operator.UnaryOperator.ARITHMETICAL_UNARY_PLUS;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.revenat.juinit.addons.ReplaceCamelCase;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("a lexeme builder")
class LexemeBuilderIntegrationTest {
    private static final SourceLine SOURCE_LINE = SourceLine.EMPTY_SOURCE_LINE;

    private LexemeBuilder lexemeBuilder;

    @BeforeEach
    void setUp() {
        lexemeBuilder = new LexemeBuilderImpl(new SingleTokenExpressionBuilderImpl(new VariableBuilderImpl()),
                                              new LexemeAmbiguityResolverImpl());
    }

    @ParameterizedTest
    @CsvSource({
        "1 + 2, 1",
        "a + b, 1",
        "( 1 * 2 ) + 3, 5",
        "( 1 * 2 ) + ( 3 / a ), 5",
        "( 1 * 2 ) + - a, 5",
    })
    @Order(1)
    void shouldDistinguishBinaryArithmeticAdditionOperator(final String expression, final String operatorPosition) {
        final List<Lexeme> lexemes = lexemeBuilder.build(parseTokens(expression), SOURCE_LINE);

        assertOperatorAtPosition(lexemes, ARITHMETIC_ADDITION, parsePosition(operatorPosition));
    }

    @ParameterizedTest
    @CsvSource({
        "1 - 2, 1",
        "a - b, 1",
        "( 1 * 2 ) - 3, 5",
        "( 1 * 2 ) - ( 3 / a ), 5",
        "( 1 * 2 ) - - a, 5",
    })
    @Order(2)
    void shouldDistinguishBinaryArithmeticSubtractionOperator(final String expression, final String operatorPosition) {
        final List<Lexeme> lexemes = lexemeBuilder.build(parseTokens(expression), SOURCE_LINE);

        assertOperatorAtPosition(lexemes, ARITHMETIC_SUBTRACTION, parsePosition(operatorPosition));
    }

    @ParameterizedTest
    @CsvSource({
        "+ 5 - 2, 0",
        "+ 5, 0",
        "( 1 * 2 ) - + 3, 6",
        "( 1 * 2 ) - ( + a ), 7",
        "( 1 * 2 ) - + ( a ), 6",
    })
    @Order(3)
    void shouldDistinguishUnaryArithmeticalPlusOperator(final String expression, final String operatorPosition) {
        final List<Lexeme> lexemes = lexemeBuilder.build(parseTokens(expression), SOURCE_LINE);

        assertOperatorAtPosition(lexemes, ARITHMETICAL_UNARY_PLUS, parsePosition(operatorPosition));
    }

    @ParameterizedTest
    @CsvSource({
        "- 5 - 2, 0",
        "- 5, 0",
        "( 1 * 2 ) - - 3, 6",
        "( 1 * 2 ) - ( - a ), 7",
        "( 1 * 2 ) - - ( a ), 6",
    })
    @Order(4)
    void shouldDistinguishUnaryArithmeticalMinusOperator(final String expression, final String operatorPosition) {
        final List<Lexeme> lexemes = lexemeBuilder.build(parseTokens(expression), SOURCE_LINE);

        assertOperatorAtPosition(lexemes, ARITHMETICAL_UNARY_MINUS, parsePosition(operatorPosition));
    }

    private void assertOperatorAtPosition(final List<Lexeme> lexemes, final Operator operator, final Integer position) {
        assertThat(lexemes.get(position), is(operator));

    }

    private Integer parsePosition(final String operatorPosition) {
        return Integer.parseInt(operatorPosition);
    }

    private List<String> parseTokens(final String tokens) {
        return List.of(tokens.split(" "));
    }
}
