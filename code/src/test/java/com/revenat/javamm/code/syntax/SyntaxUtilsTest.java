
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

package com.revenat.javamm.code.syntax;

import com.revenat.juinit.addons.ReplaceCamelCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.revenat.javamm.code.syntax.Delimiters.SIGNIFICANT_TOKEN_DELIMITERS;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("syntax utils")
class SyntaxUtilsTest {

    static Stream<String> significantTokenProvider() {
        return SIGNIFICANT_TOKEN_DELIMITERS.stream();
    }

    private void assertValid(final String token) {
        assertTrue(SyntaxUtils.isValidSyntaxToken(token));
    }

    private void assertInvalid(final String token) {
        assertFalse(SyntaxUtils.isValidSyntaxToken(token));
    }

    @Test
    @Order(1)
    void shouldDefineValidIfTokenIsAssumedStringLiteral() {
        final String literalA = "\"test\"";
        final String literalB = "'test'";

        assertValid(literalA);
        assertValid(literalB);
    }

    @ParameterizedTest
    @MethodSource("significantTokenProvider")
    @Order(2)
    void shouldDefineValidIfTokenIsSignificantTokenDelimiter(final String significantToken) {
        assertValid(significantToken);
    }

    @Test
    @Order(3)
    void shouldDefineValidIfTokenContainsDigits() {
        assertValid("123587114");
    }

    @Test
    @Order(4)
    void shouldDefineValidIfTokenContainsLatinSymbols() {
        assertValid("aBsHiOy");
    }

    @Test
    @Order(5)
    void shouldDefineValidIfTokenContainsDots() {
        assertValid("...");
    }

    @Test
    @Order(6)
    void shouldDefineValidIfTokenContainsDigitsAndLatinSymbolsAndDots() {
        //noinspection SpellCheckingInspection
        assertValid("125Hfyri..");
    }

    @Test
    @Order(7)
    void shouldDefineInvalidIfTokenContainsNotLatinSymbol() {
        assertInvalid("пР587");
    }

}
