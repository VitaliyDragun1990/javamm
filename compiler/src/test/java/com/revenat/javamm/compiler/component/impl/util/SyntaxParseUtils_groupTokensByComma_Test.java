
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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.jupiter.api.Assertions.*;

import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.revenat.javamm.compiler.component.impl.util.SyntaxParseUtils.groupTokensByComma;
import static com.revenat.javamm.compiler.test.helper.CustomAsserts.assertErrorMessageContains;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.revenat.juinit.addons.ReplaceCamelCase;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("syntax parse utils group tokens by comma method")
public class SyntaxParseUtils_groupTokensByComma_Test {

    private static final SourceLine SOURCE_LINE = new SourceLine("module1", 5, List.of());

    @Test
    @Order(1)
    void shouldProduceEmptyListIfTokenListIsEmpty() {
        final List<List<String>> result = groupTokensByComma(List.of(), SOURCE_LINE);

        assertThat(result, is(empty()));
    }

    @Test
    @Order(2)
    void shouldProduceUnmodifiableListOfUnmodifiableLists() {
        final List<List<String>> result = groupTokensByComma(List.of("a", ",", "b"), SOURCE_LINE);

        assertUnmodifiable(result);
    }

    @ParameterizedTest
    @CsvSource(delimiter = ';', value = {
            "a;                                                                 a",
            "a , b;                                                             a|b",
            "a , ( b;                                                           a|( b",
            "a - 2 , b + 1;                                                     a - 2|b + 1",
            "a - 2 , b + 1 , 4 , a + ( 4 * b );                                 a - 2|b + 1|4|a + ( 4 * b )",
            "a [ i - 8 ] , { 4 } , a + ( 4 * b );                               a [ i - 8 ]|{ 4 }|a + ( 4 * b )",
            "sum ( 4 , a ) , { 1 , 2 } , a [ sum ( 3 , 5 ) ] );                 sum ( 4 , a )|{ 1 , 2 }|a [ sum ( 3 , 5 ) ] )",
            "sum ( sum ( 4 , a ) , a ) , { 1 , 2 } , a [ sum ( 3 , 5 ) ] );     sum ( sum ( 4 , a ) , a )|{ 1 , 2 }|a [ sum ( 3 , 5 ) ] )",
            "sum ( sum ( 4 , sum ( 4 , a ) ) , a ) , { 1 , 2 };                 sum ( sum ( 4 , sum ( 4 , a ) ) , a )|{ 1 , 2 }",
            // Missing or redundant comma error is not expected here
            "a , sum ( 4 , a ) , fun ( 3 , fun ( 3 , , 5 ) );                   a|sum ( 4 , a )|fun ( 3 , fun ( 3 , , 5 ) )",
            "sum ( 4 , , a ) , { 1 , , 2 } , a [ sum ( 3 , , 5 ) ] );           sum ( 4 , , a )|{ 1 , , 2 }|a [ sum ( 3 , , 5 ) ] )",
    })
    @Order(3)
    void shouldGroupProvidedTokensByComma(final String sourceExpression, final String expectedGroups) {
        final List<String> tokens = splitBy(sourceExpression, " ");
        final List<List<String>> expectedResult = splitBy(expectedGroups, "\\|").stream()
                .map(group -> splitBy(group, " "))
                .collect(Collectors.toUnmodifiableList());

        final List<List<String>> result = groupTokensByComma(tokens, SOURCE_LINE);

        assertThat(result, equalTo(expectedResult));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "a ,",
            ", a",
            "a , , b",
            "a , b ,",
            "sum ( 4 , a ) , { 1 , 2 } , , a [ sum ( 3 , 5 ) ] )",
    })
    @Order(4)
    void shouldFailIfMissingValueOrRedundantCommaIsPresent(final String sourceExpression) {
        final List<String> tokens = splitBy(sourceExpression, " ");

        final JavammLineSyntaxError e = assertThrows(JavammLineSyntaxError.class, () -> groupTokensByComma(tokens, SOURCE_LINE));

        assertErrorMessageContains(e, "Syntax error in 'module1' [Line: 5]: Missing value or redundant ','");
    }

    private List<String> splitBy(final String sourceExpression, final String delimiter) {
        return List.of(sourceExpression.split(delimiter));
    }

    private void assertUnmodifiable(final List<List<String>> list) {
        final Class<?> expectedClass = Collections.unmodifiableList(List.of()).getClass();

        assertThat(list.getClass(), is(expectedClass));
        for (final List<String> inner : list) {
            assertThat(inner.getClass(), is(expectedClass));
        }
    }
}
