
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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.revenat.javamm.code.fragment.Lexeme;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.expression.ComplexExpression;
import com.revenat.javamm.compiler.component.ComplexExpressionBuilder;
import com.revenat.javamm.compiler.component.LexemeBuilder;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;
import com.revenat.javamm.compiler.component.impl.LexemeAmbiguityResolverImpl;
import com.revenat.javamm.compiler.component.impl.LexemeBuilderImpl;
import com.revenat.javamm.compiler.component.impl.OperatorPrecedenceResolverImpl;
import com.revenat.javamm.compiler.component.impl.VariableBuilderImpl;
import com.revenat.javamm.compiler.component.impl.expression.builder.PostfixNotationComplexExpressionBuilder;
import com.revenat.javamm.compiler.component.impl.expression.builder.SingleTokenExpressionBuilderImpl;
import com.revenat.javamm.compiler.test.helper.CustomAsserts;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.revenat.juinit.addons.ReplaceCamelCase;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("a postfix notation complex expression builder")
class PostfixNotationComplexExpressionBuilderIntegrationTest {
    private static final SourceLine SOURCE_LINE = new SourceLine("test", 5, List.of());

    private LexemeBuilder lexemeBuilder;

    private ComplexExpressionBuilder complexExpressionBuilder;

    @BeforeEach
    void setUp() {
        lexemeBuilder = new LexemeBuilderImpl(new SingleTokenExpressionBuilderImpl(new VariableBuilderImpl()),
                                              new LexemeAmbiguityResolverImpl());
        complexExpressionBuilder = new PostfixNotationComplexExpressionBuilder(new OperatorPrecedenceResolverImpl());
    }

    private ComplexExpression build(final String tokenString) {
        final List<String> expressionTokens = List.of(tokenString.split(" "));
        final List<Lexeme> lexemes = lexemeBuilder.build(expressionTokens, SOURCE_LINE);
        return complexExpressionBuilder.build(lexemes, SOURCE_LINE);
    }

    private String expressionAsString(final ComplexExpression expression) {
        return expression.getLexemes().stream()
                .map(Object::toString)
                .collect(Collectors.joining(" "));
    }

    @ParameterizedTest
    @CsvSource({
        "3 + 4,                 3 4 +",
        "7 - 2 * 3,             7 2 3 * -",
        "5 * 2 + 10,            5 2 * 10 +",
        "( 1 + 2 ) * 4 + 3,     1 2 + 4 * 3 +",
        "5 * ( - 3 + 8 ),       5 3 - 8 + *",
        "5 * ( + - 3 + 8 ),     5 3 - + 8 + *",
        "~ ++ 3,                3 ++ ~",

        "3 + 4 * 2 / ( 1 - 5 ) typeof 2,            3 4 2 * 1 5 - / + 2 typeof",
        "( 4 + 5 ) * 3 - ( 7 / 2 + 15 ),            4 5 + 3 * 7 2 / 15 + -",
        "( 8 + 2 * 5 ) / ( 1 + 3 * 2 - 4 ),         8 2 5 * + 1 3 2 * + 4 - /",
        "( 6 + 10 - 4 ) / ( 1 + 1 * 2 ) + 1,        6 10 + 4 - 1 1 2 * + / 1 +",
        " ( ( ( ( 1 + 2 ) - ( 3 * 4 ) ) / ( 5 % 6 ) ^ 7 ) | 2 ) ^ ( 9 >> 2 | 4 << 2 | ~ 1 ), "
        + "1 2 + 3 4 * - 5 6 % / 7 ^ 2 | 9 2 >> 4 2 << | 1 ~ | ^"
    })
    @Order(1)
    void shouldBuildValidComplexExpressionInPostfixNotation(final String expressionTokens, final String expectedResult) {
        final ComplexExpression result = build(expressionTokens);

        assertThat(expressionAsString(result), equalTo(expectedResult));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            ")",
            "2 )",
            "3 + 5 )",
            "5 + 8 - 6 * 7 )",
            "5 + ( 8 + 4 ) + 6 )",
            "( 5 + 8 ) + 4 + 6 )",
            "( ( 5 + 8 ) * 6 ) + 4 + 6 )",
            "( ( ( 5 + 8 ) * 6 ) - 6 * 7 ) + 4 + 6 )",
            ") + 7",
            "2 ) + 7",
            "3 + 5 ) + 7",
            "5 + 8 - 6 * 7 ) + 7",
            "5 + ( 8 + 4 ) + 6 ) + 7",
            "( 5 + 8 ) + 4 + 6 ) + 7",
            "( ( 5 + 8 ) * 6 ) + 4 + 6 ) + 7",
            "( ( ( 5 + 8 ) * 6 ) - 6 * 7 ) + 4 + 6 ) + 7"
    })
    @Order(2)
    void shouldFailToBuildIfOpenParenthesisIsMissing(final String expressionTokens) {
        final JavammLineSyntaxError e = assertThrows(JavammLineSyntaxError.class, () -> build(expressionTokens));

        CustomAsserts.assertErrorMessageContains(e, "Missing (");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "(",
            "( 2",
            "( 3 + 5",
            "( 5 + 8 - 6 * 7",
            "( 5 + ( 8 + 4 ) + 6",
            "( ( 5 + 8 ) + 4 + 6",
            "( ( ( 5 + 8 ) * 6 ) + 4 + 6",
            "( ( ( ( 5 + 8 ) * 6 ) - 6 * 7 ) + 4 + 6",
            "3 * (",
            "3 * ( 2",
            "3 * ( 3 + 5",
            "3 * ( 5 + 8 - 6 * 7",
            "3 * ( 5 + ( 8 + 4 ) + 6",
            "3 * ( ( 5 + 8 ) + 4 + 6",
            "3 * ( ( ( 5 + 8 ) * 6 ) + 4 + 6",
            "3 * ( ( ( ( 5 + 8 ) * 6 ) - 6 * 7 ) + 4 + 6"
        })
    @Order(3)
    void shouldFailToBuildIfClosingParenthesisIsMissing(final String expressionTokens) {
        final JavammLineSyntaxError e = assertThrows(JavammLineSyntaxError.class, () -> build(expressionTokens));

        CustomAsserts.assertErrorMessageContains(e, "Missing )");
    }
}
