
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

import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;
import com.revenat.juinit.addons.ReplaceCamelCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Iterator;
import java.util.List;

import static com.revenat.javamm.compiler.component.impl.util.SyntaxParseUtils.getTokensUntilClosingBracketIsMet;
import static com.revenat.javamm.compiler.test.helper.CustomAsserts.assertErrorMessageContains;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("a syntax parse utils getTokensBetweenBrackets method")
class SyntaxParseUtils_getTokensUntilSpecifiedClosingBracketIsMet_Test {

    private static final String ANY_MODULE_NAME = "module1";

    private static final int ANY_LINE_NUMBER = 5;

    private static final boolean ALLOW_EMPTY_RESULT = true;

    private static final boolean PROHIBIT_EMPTY_RESULT = false;

    @ParameterizedTest
    @CsvSource(delimiter = ';', value = {
        // VALID EXPRESSIONS
        // ---------------- Operations and functions ------------------
        "(; );      if ( a < 4 ) {;                                         2;     a < 4",
        "(; );      if ( ( a < 4 ) ) {;                                     2;     ( a < 4 )",
        "(; );      if ( ( a < 4 ) * 5 ( b - 6 ) / ( - c ) ) {;             2;     ( a < 4 ) * 5 ( b - 6 ) / ( - c )",
        "(; );      function ( ) {;                                         2;                                      ",
        "(; );      function ( ( 2 + 2 ) ) {;                               2;      ( 2 + 2 )",
        "(; );      function ( sum ( max ( arr [ 3 ] , 2 ) , 5 ) , 4 ) {;   4;      max ( arr [ 3 ] , 2 ) , 5",
        // ------------------ Array declaration with values ---------------
        "{; };      var a = { };                                            4;  ",
        "{; };      var a = { 5 , 6 , 7 };                                  4;     5 , 6 , 7",
        "{; };      var a = { { 5 } , sum ( 6 , 7 ) };                      4;     { 5 } , sum ( 6 , 7 )",
        "{; };      var a = { { 6 , { 7 } } , 3 , { { { } } } };            5;     6 , { 7 }",
        // ------------------- Empty array declaration -------------------------
        "[; ];      var a = array [ 4 + a ];                                5;      4 + a",
        "[; ];      var a = array [ b [ 4 + a [ ar [ 0 ] ] ] ];             5;      b [ 4 + a [ ar [ 0 ] ] ]",
        "[; ];      var a = array [ 4 * sum ( 3 , a [ 5 ] ) ];              5;      4 * sum ( 3 , a [ 5 ] )",
        // INVALID EXPRESSIONS
        "(; );      if ( a < 4 ) ) {;                                       2;      a < 4",
        "(; );      if ( a + 4 ) * 5 < ( b - 6 ) / ( - c ) ) {;             2;      a + 4",
        "{; };      var a = { 5 } , { 6, 7 } };                             4;      5",
        "{; };      var a = { 6, 7 } } , 3 , } } } };                       4;      6, 7",
        "[; ];      var a = array b 4 + a [ ar [ 0 ] ] ] ];                 9;      ar [ 0 ]",
        "[; ];      var a = array 4 * sum [ ( 3 , a [ 5 ] ) ] ];            8;  ( 3 , a [ 5 ] )"
    })
    @Order(1)
    void shouldReturnTokensBetweenBracketsIgnoringValidationAndAllowingEmptyResults(final String openingBracket,
                                                                                    final String closingBracket,
                                                                                    final String source,
                                                                                    final int iteratorPosition,
                                                                                    final String expected) {
        final SourceLine sourceLine = new SourceLine(ANY_MODULE_NAME, ANY_LINE_NUMBER, toList(source));
        final Iterator<String> expressionTokens = getIteratorInPosition(source, iteratorPosition);

        final List<String> actualTokens = getTokensUntilClosingBracketIsMet(openingBracket,
            closingBracket,
            expressionTokens,
            sourceLine,
            ALLOW_EMPTY_RESULT);

        assertThat(actualTokens, equalTo(toList(expected)));
    }

    @ParameterizedTest
    @CsvSource(delimiter = ';', value = {
        "(; );      if ( a < 4 {;               2",
        "(; );      function1 ( {;              2",
        "{; };      var a = {;                  4",
        "{; };      var a = { 5, 6, 7;          4",
        "[; ];      var a = array [;            5",
        "[; ];      var a = array [ 4 + a;      5",
    })
    @Order(3)
    void shouldFailIfClosingBracketIsMissing(final String openingBracket,
                                             final String closingBracket,
                                             final String source,
                                             final int iteratorPosition) {
        final SourceLine sourceLine = new SourceLine(ANY_MODULE_NAME, ANY_LINE_NUMBER, toList(source));
        final Iterator<String> expressionTokens = getIteratorInPosition(source, iteratorPosition);

        final JavammLineSyntaxError e = assertThrows(JavammLineSyntaxError.class,
            () -> getTokensUntilClosingBracketIsMet(openingBracket,
                closingBracket,
                expressionTokens,
                sourceLine,
                ALLOW_EMPTY_RESULT));

        assertErrorMessageContains(e, "Syntax error in '%s' [Line: %s]: Missing '%s'", ANY_MODULE_NAME, ANY_LINE_NUMBER, closingBracket);
    }

    @ParameterizedTest
    @CsvSource(delimiter = ';', value = {
        "(; );      if ( );             2",
        "(; );      for ( );            2",
        "[; ];      var a = array [ ];  5",
        "[; ];      a [ ] = 5;          2"
    })
    @Order(4)
    void shouldFailIfExpressionBetweenBracketsIsEmptyAndEmptyResultIsProhibited(final String openingBracket,
                                                                                final String closingBracket,
                                                                                final String source,
                                                                                final int iteratorPosition) {
        final SourceLine sourceLine = new SourceLine(ANY_MODULE_NAME, ANY_LINE_NUMBER, toList(source));
        final Iterator<String> expressionTokens = getIteratorInPosition(source, iteratorPosition);

        final JavammLineSyntaxError e = assertThrows(JavammLineSyntaxError.class,
            () -> getTokensUntilClosingBracketIsMet(openingBracket,
                closingBracket,
                expressionTokens,
                sourceLine,
                PROHIBIT_EMPTY_RESULT));

        assertErrorMessageContains(e, "Syntax error in '%s' [Line: %s]: An expression is expected between '%s' and '%s'",
            ANY_MODULE_NAME, ANY_LINE_NUMBER, openingBracket, closingBracket);
    }

    private Iterator<String> getIteratorInPosition(final String tokenString, final int iteratorPosition) {
        final List<String> tokens = toList(tokenString);
        final Iterator<String> iterator = tokens.iterator();
        for (int i = 0; i < iteratorPosition; i++) {
            iterator.next();
        }
        return iterator;
    }

    private List<String> toList(final String expression) {
        return expression == null ? List.of() : List.of(expression.split(" "));
    }
}
