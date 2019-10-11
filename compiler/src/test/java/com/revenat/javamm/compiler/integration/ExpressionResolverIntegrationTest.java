
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

import static org.junit.jupiter.api.Assertions.*;

import com.revenat.javamm.code.fragment.Expression;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.compiler.component.ExpressionResolver;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;
import com.revenat.javamm.compiler.test.builder.ComponentBuilder;

import java.util.List;

import static com.revenat.javamm.compiler.test.helper.CustomAsserts.assertErrorMessageContains;

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
    @CsvSource({
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
    @Order(2)
    void shouldFailIfExpressionIsSyntacticallyIncorrect(final String expression, final String expectedMessage) {
        final JavammLineSyntaxError e = assertThrows(JavammLineSyntaxError.class, () -> resolve(expression));

        assertErrorMessageContains(e, "Syntax error in 'module1' [Line: 5]: %s", expectedMessage);
    }
}
