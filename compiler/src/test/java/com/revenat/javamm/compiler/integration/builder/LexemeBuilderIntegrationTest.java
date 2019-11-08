
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

package com.revenat.javamm.compiler.integration.builder;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;

import com.revenat.javamm.code.fragment.Lexeme;
import com.revenat.javamm.code.fragment.Parenthesis;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.expression.ConstantExpression;
import com.revenat.javamm.code.fragment.expression.VariableExpression;
import com.revenat.javamm.code.fragment.operator.BinaryOperator;
import com.revenat.javamm.code.fragment.operator.TernaryConditionalOperator;
import com.revenat.javamm.code.fragment.operator.UnaryOperator;
import com.revenat.javamm.compiler.component.LexemeBuilder;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;
import com.revenat.javamm.compiler.test.builder.ComponentBuilder;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.revenat.javamm.compiler.test.helper.CustomAsserts.assertErrorMessageContains;

import static java.util.Map.entry;
import static java.util.stream.Collectors.toList;

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

    private static final String CE = "CE"; // constant expression
    private static final String VE = "VE"; // variable expression
    private static final String OP = "OP"; // opening parenthesis
    private static final String CP = "CP"; // closing parenthesis
    private static final String BA = "BA"; // binary addition
    private static final String BS = "BS"; // binary subtraction
    private static final String BD = "BD"; // binary division
    private static final String BM = "BM"; // binary multiplication
    private static final String BG = "BG"; // binary greater than
    private static final String UP = "UP"; // unary plus
    private static final String UM = "UM"; // unary minus
    private static final String UI = "UI"; // unary increment
    private static final String UD = "UD"; // unary decrement
    private static final String TC = "TC"; // ternary conditional
    private static final String TS = "TS"; // ternary separator

    private static final Map<String, Class<? extends Lexeme>> LEXEMES = Map.ofEntries(
            entry(CE, ConstantExpression.class),
            entry(VE, VariableExpression.class),
            entry(OP, Parenthesis.OPENING_PARENTHESIS.getClass()),
            entry(CP, Parenthesis.CLOSING_PARENTHESIS.getClass()),
            entry(BA, BinaryOperator.ARITHMETIC_ADDITION.getClass()),
            entry(BS, BinaryOperator.ARITHMETIC_SUBTRACTION.getClass()),
            entry(BD, BinaryOperator.ARITHMETIC_DIVISION.getClass()),
            entry(BM, BinaryOperator.ARITHMETIC_MULTIPLICATION.getClass()),
            entry(BG, BinaryOperator.PREDICATE_GREATER_THAN.getClass()),
            entry(UP, UnaryOperator.ARITHMETICAL_UNARY_PLUS.getClass()),
            entry(UM, UnaryOperator.ARITHMETICAL_UNARY_MINUS.getClass()),
            entry(UI, UnaryOperator.INCREMENT.getClass()),
            entry(UD, UnaryOperator.DECREMENT.getClass()),
            entry(TC, TernaryConditionalOperator.OPERATOR.getClass()),
            entry(TS, TernaryConditionalOperator.SEPARATOR.getClass())
            );

    private LexemeBuilder lexemeBuilder;

    @BeforeEach
    void setUp() {
        lexemeBuilder = new ComponentBuilder().buildLexemeBuilder();
    }

    @ParameterizedTest
    @CsvSource({
        "a,                                   VE",
        "2,                                   CE",
        "1 + 2,                               CE:BA:CE",
        "a * b,                               VE:BM:VE",
        "( 1 * b ) / 3,                       OP:CE:BM:VE:CP:BD:CE",
        "( 1 * b ) + - + 3,                   OP:CE:BM:VE:CP:BA:UM:UP:CE",
        "( 1 * -- b ) + - + a ++,             OP:CE:BM:UI:VE:CP:BA:UM:UP:VE:UI",
        "( ( 1 * + -- b ) ) + - + + + ++ a,   OP:OP:CE:BM:UP:UD:VE:CP:CP:BA:UM:UP:UP:UP:UI:VE",
    })
    @Order(1)
    void shouldBuildCorrectLexemesFromExpressionTokens(final String expression, final String expectedLexemes) {
        assertLexemesFromExpression(expression, expectedLexemes);
    }

    @ParameterizedTest
    @CsvSource({
        "[ a,                [",
        "10 + @,             @",
        "10 + :,             :",
        "a > 2 ? b,          ?",
        "a > 2 ? b : c :,    :",
        "a > 2 ? b : c ?,    ?",
    })
    @Order(2)
    void shouldFailToBuildIfUnsupportedTokenPresent(final String expression, final String unsupportedToken) {
        final JavammLineSyntaxError e = assertThrows(JavammLineSyntaxError.class, () -> build(expression));

        assertErrorMessageContains(e, "Unsupported token: %s", unsupportedToken);
    }

    @ParameterizedTest
    @CsvSource({
        "true ? 10 : 20,                   CE:TC:CE:TS:CE",
        "true ? false ? a : b : 20,        CE:TC:CE:TC:VE:TS:VE:TS:CE",
    })
    @Order(3)
    void shouldSupportTernaryOperator(final String expression, final String expectedLexemes) {
        assertLexemesFromExpression(expression, expectedLexemes);
    }

    private void assertLexemesFromExpression(final String expression, final String expectedLexemes) {
        final List<String> tokens = parseTokens(expression);
        final List<Class<? extends Lexeme>> expectedLexemeTypes = parseLexemes(expectedLexemes);

        final List<Lexeme> actualLexemes = lexemeBuilder.build(tokens, SOURCE_LINE);

        assertThat(actualLexemes, hasSize(expectedLexemeTypes.size()));

        for (int i = 0; i < actualLexemes.size(); i++) {
            final Lexeme actual =  actualLexemes.get(i);
            final Class<? extends Lexeme> expectedType = expectedLexemeTypes.get(i);

            assertThat(actual, instanceOf(expectedType));
        }
    }

    private List<Lexeme> build(final String expression) {
        return lexemeBuilder.build(parseTokens(expression), SOURCE_LINE);
    }

    private List<Class<? extends Lexeme>> parseLexemes(final String expectedLexemes) {
        final String[] tokens = expectedLexemes.split(":");

        return Stream.of(tokens).map(token -> LEXEMES.get(token)).collect(toList());
    }

    private List<String> parseTokens(final String tokens) {
        return List.of(tokens.split(" "));
    }
}
