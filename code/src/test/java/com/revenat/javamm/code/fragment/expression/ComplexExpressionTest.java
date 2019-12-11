/*
 *
 *  Copyright (c) 2019. http://devonline.academy
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.revenat.javamm.code.fragment.expression;

import com.revenat.javamm.code.fragment.Lexeme;
import com.revenat.juinit.addons.ReplaceCamelCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("a complex expression")
class ComplexExpressionTest {

    @Test
    @Order(1)
    void canNotBeCreatedWithoutLexemes() {
        final IllegalArgumentException e
            = assertThrows(IllegalArgumentException.class, () -> new ComplexExpression(List.of()));

        assertThat(e.getMessage(), containsString("Empty lexemes not allowed"));
    }

    @Test
    @Order(2)
    void shouldProvideLexemesItWasCreatedWith() {
        List<Lexeme> lexemes = List.of(new LexemeStub("a"), new LexemeStub("b"));

        final ComplexExpression complexExpression = new ComplexExpression(lexemes);

        assertThat(complexExpression.getLexemes(), equalTo(lexemes));
    }

    @Test
    @Order(3)
    void shouldProvideStringRepresentationOfTheLexemesItContains() {
        List<Lexeme> lexemes = List.of(new LexemeStub("a"), new LexemeStub("b"));
        String expectedString = "a b";

        final ComplexExpression complexExpression = new ComplexExpression(lexemes);

        assertThat(complexExpression.toString(), equalTo(expectedString));
    }

    private static class LexemeStub implements Lexeme {
        private final String name;

        LexemeStub(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}