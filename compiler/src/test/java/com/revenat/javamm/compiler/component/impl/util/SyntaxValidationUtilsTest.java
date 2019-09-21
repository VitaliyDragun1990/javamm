
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

package com.revenat.javamm.compiler.component.impl.util;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.syntax.Keywords;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;
import com.revenat.javamm.compiler.component.impl.util.SyntaxValidationUtils.LanguageFeature;
import com.revenat.javamm.compiler.error.JavammSyntaxError;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.revenat.juinit.addons.ReplaceCamelCase;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("syntax validation utils")
class SyntaxValidationUtilsTest {
    private static final SourceLine DUMMY_SOURCE_LINE = SourceLine.EMPTY_SOURCE_LINE;
    private static final LanguageFeature LANGUAGE_FEATURE = LanguageFeature.VARIABLE;

    private void validateFirstCharacterIsLetter(final String name) {
        SyntaxValidationUtils.validateThatFirstCharacterIsLetter(LANGUAGE_FEATURE, name, DUMMY_SOURCE_LINE);
    }

    private void validateThatNameIsNotKeyword(final String name) {
        SyntaxValidationUtils.validateThatNameIsNotKeyword(LANGUAGE_FEATURE, name, DUMMY_SOURCE_LINE);
    }

    private void assertFirstCaharcterIsNotLetterErrorMessage(final JavammSyntaxError e, final String name) {
        assertThat(e.getMessage(), containsString("The variable name must start with letter: '" + name + "'"));
    }

    private void assertNameIsNotKeywordErrorMessage(final JavammSyntaxError e, final String name) {
        assertThat(e.getMessage(), containsString("The keyword '" + name + "' can not be used as variable name"));
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("validate that first character is letter")
    class ValidateThatFirstCharacterIsLetter {

        @ParameterizedTest
        @ValueSource(strings = {"var", "person", "People", "t123"})
        @Order(1)
        void shouldPassForLetterFirstCharacter(final String name) {
            assertDoesNotThrow(() -> validateFirstCharacterIsLetter(name));
        }

        @ParameterizedTest
        @ValueSource(strings = {"1var", "_name", "$test"})
        @Order(2)
        void shouldFailForNonLetterFirstCharacter(final String name) {
            final JavammLineSyntaxError e = assertThrows(
                    JavammLineSyntaxError.class,
                    () -> validateFirstCharacterIsLetter(name));
            assertFirstCaharcterIsNotLetterErrorMessage(e, name);
        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("validate that name is not keyword")
    class ValidateThatNameIsNotKeyword {

        @ParameterizedTest
        @ValueSource(strings = {"test", "123", "_test", "$var"})
        @Order(1)
        void shouldPassForNonKeyword(final String name) {
            assertDoesNotThrow(() -> validateThatNameIsNotKeyword(name));
        }

        @ParameterizedTest
        @MethodSource("com.revenat.javamm.compiler.component.impl.util.SyntaxValidationUtilsTest#keywordProvider")
        @Order(2)
        void shouldFailForKeyword(final String name) {
            final JavammLineSyntaxError e = assertThrows(
                    JavammLineSyntaxError.class,
                    () -> validateThatNameIsNotKeyword(name));
            assertNameIsNotKeywordErrorMessage(e, name);
        }
    }

    static Stream<String> keywordProvider() {
        return Keywords.ALL_KEYWORDS.stream();
    }
}
