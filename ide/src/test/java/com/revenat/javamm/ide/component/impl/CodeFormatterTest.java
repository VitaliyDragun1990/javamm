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

package com.revenat.javamm.ide.component.impl;

import com.revenat.javamm.ide.component.CodeFormatter;
import com.revenat.juinit.addons.ReplaceCamelCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.revenat.javamm.ide.util.TabReplaceUtils.replaceTabulations;
import static java.lang.String.join;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("a code formatter")
class CodeFormatterTest {

    private static final String TABULATION = replaceTabulations("\t");

    private final CodeFormatter codeFormatter = new CodeFormatterImpl();

    @Test
    @Order(1)
    void shouldProduceUnmodifiableList() {
        assertThat(codeFormatter.format(List.of()).getClass(), equalTo(Collections.unmodifiableList(List.of()).getClass()));
    }

    @Test
    @Order(2)
    void shouldReturnEmptyListIfProvidedListIsEmpty() {
        assertThat(codeFormatter.format(List.of()), hasSize(0));
    }

    @Test
    @Order(3)
    void shouldReplaceTabulationByWhitespaceTabulation() {
        final List<String> formatted = codeFormatter.format(List.of(
            "function main () {",
            "\tprintln('hello')",
            "}"
        ));

        assertThat(join("\n", formatted), not(containsString("\t")));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "  source line  ",
        "\tsource line\t\t",
        "\nsource line\n",
        "\rsource line\r\r",
        "\u00A0source line\u00A0",
        "  \t\u00A0\r\t  \u00A0source line  \u00A0\t\u00A0\r\n \u00A0 "
    })
    @Order(4)
    void shouldTrimAllIgnoredDelimiters(final String line) {
        final List<String> expectedResult = List.of("source line");

        assertThat(codeFormatter.format(List.of(line)), equalTo(expectedResult));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "line1\n\nline2",
        "line1\n\n\nline2",
        "line1\n\n\n\nline2",

        "line1\n\t\nline2",
        "line1\n\t\n\t\nline2",
        "line1\n\t\n\t\n\t\nline2",

        "line1\n  \nline2",
        "line1\n  \n  \nline2",
        "line1\n  \n  \n  \nline2",
    })
    @Order(5)
    void shouldLeaveNoMoreThanOneEmptyLineBetweenAdjacentLinesWithCode(final String sourceCode) {
        final List<String> expectedResult = List.of(
            "line1",
            "",
            "line2"
        );
        final List<String> unFormattedSourceCode = Arrays.asList(sourceCode.split("\n"));

        assertThat(codeFormatter.format(unFormattedSourceCode), equalTo(expectedResult));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "\nline1\nline2",
        "\n\n\nline1\nline2",

        "\n\t\nline1\nline2",
        "\n\t\n\tline1\nline2",
        "\n\t\n\t\n\t\nline1\nline2",

        "\n  \nline1\nline2",
        "\n  \n  \nline1\nline2",
        "\n  \n  \t  \n \t \nline1\nline2",
    })
    @Order(6)
    void shouldLeaveNoMoreThanOneEmptyLineBeforeFirstLineWithCode(final String sourceCode) {
        final List<String> unFormattedSourceCode = Arrays.asList(sourceCode.split("\n"));
        final List<String> expectedResult = List.of(
            "",
            "line1",
            "line2"
        );

        assertThat(codeFormatter.format(unFormattedSourceCode), equalTo(expectedResult));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "line1\nline2\n",
        "line1\nline2\n\n\n",
        "line1\nline2\n\t\n",

        "line1\nline2\n\n\t\n\n\n\t\n\n\t",
        "line1\nline2\n  \n\n  \t  \n\n   \n\n\n",
    })
    @Order(7)
    void shouldLeaveNoEmptyLinesAfterLastLineWithCode(final String sourceCode) {
        final List<String> unFormattedSourceCode = Arrays.asList(sourceCode.split("\n"));
        final List<String> expectedResult = List.of(
            "line1",
            "line2"
        );

        assertThat(codeFormatter.format(unFormattedSourceCode), equalTo(expectedResult));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        " var a = 1 + 2 * 5 / 8 ",
        "\tvar\ta\t=\t1\t+\t2\t*\t5\t/\t8 ",
        "var a=1+2*5/8",
        "    var a=1   +     2     *  5/ 8            ",
        "\u00A0\u00A0var\u00A0a=1\u00A0\u00A0\u00A0+\u00A02\u00A0*\u00A0\u00A05/\u00A08\u00A0",
        "\t\tvar a = 1+2\t* 5   /\u00A08    "
    })
    @Order(8)
    void shouldProvideOnlyOneSpaceBetweenTokens(final String line) {
        final List<String> unFormattedCode = List.of(line);
        final List<String> expectedResult = List.of("var a = 1 + 2 * 5 / 8");

        assertThat(codeFormatter.format(unFormattedCode), equalTo(expectedResult));
    }

    @ParameterizedTest
    @CsvSource(delimiter = '.', value = {
        "(.      a (.        a(",
        ").      a ).        a)",
        "[.      a [.        a[",
        "].      a ].        a]",
        "}.      a }.        a}",
        ",.      a ,.        a,",
        ";.      a ;.        a;",
        ":.      a :.        a:"
    })
    @Order(9)
    void shouldNotLeftSpaceBeforeTheFollowingTokens(final String token,
                                                    final String unFormattedLine,
                                                    final String expectedFormattedResult) {
        final List<String> unFormattedCode = List.of(unFormattedLine);
        final List<String> expectedResult = List.of(expectedFormattedResult);

        assertThat("Expected no empty space before token: " + token,
            codeFormatter.format(unFormattedCode), equalTo(expectedResult));
    }

    @ParameterizedTest
    @CsvSource(delimiter = '.', value = {
        "(.      ( a.        (a",
        "[.      [ a.        [a",
        "{.      { a.        {a"
    })
    @Order(10)
    void shouldNotLeftSpaceAfterTheFollowingTokens(final String token,
                                                   final String unFormattedLine,
                                                   final String expectedFormattedResult) {
        final List<String> unFormattedCode = List.of(unFormattedLine);
        final List<String> expectedResult = List.of(expectedFormattedResult);

        assertThat("Expected no empty space after token: " + token,
            codeFormatter.format(unFormattedCode), equalTo(expectedResult));
    }

    @Test
    @Order(11)
    void shouldAddAppropriateTabulationToCodeInsideAnyBlock() {
        List<String> unFormattedCode = List.of(
            "{",
            "{",
            "{",
            "println",
            "}",
            "}",
            "}"
        );
        List<String> expectedResult = List.of(
            "{",
            TABULATION + "{",
            TABULATION + TABULATION + "{",
            TABULATION + TABULATION + TABULATION + "println",
            TABULATION + TABULATION + "}",
            TABULATION + "}",
            "}"
        );

        assertThat(codeFormatter.format(unFormattedCode), equalTo(expectedResult));
    }

    @Test
    @Order(12)
    void shouldNormalizeTabulationForCodeInsideAnyBlock() {
        List<String> unFormattedCode = List.of(
            "  {",
            "\t\t\t{",
            "\u00A0\u00A0\u00A0\u00A0{\u00A0\u00A0\u00A0\u00A0",
            "println" + TABULATION + TABULATION + TABULATION,
            "}" + TABULATION + TABULATION + TABULATION,
            "}",
            TABULATION + TABULATION + TABULATION + "}"
        );
        List<String> expectedResult = List.of(
            "{",
            TABULATION + "{",
            TABULATION + TABULATION + "{",
            TABULATION + TABULATION + TABULATION + "println",
            TABULATION + TABULATION + "}",
            TABULATION + "}",
            "}"
        );

        assertThat(codeFormatter.format(unFormattedCode), equalTo(expectedResult));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "   // comment   |// comment",
        "   // var \u00A0 a = 3 + 4     |// var \u00A0 a = 3 + 4",
        "\u00A0\u00A0\u00A0\u00A0// var a=3+4\u00A0\u00A0\u00A0\u00A0|// var a=3+4"
    })
    @Order(13)
    void shouldNotFormatLineCommentsExceptTrim(String line) {
        final String[] args = line.split("\\|");
        List<String> unFormattedCode = List.of(args[0]);
        List<String> expectedResult = List.of(args[1]);

        assertThat(codeFormatter.format(unFormattedCode), equalTo(expectedResult));
    }

    @Test
    @Order(14)
    void shouldAddAppropriateTabulationToLineCommentsInsideAnyBlock() {
        List<String> unFormattedCode = List.of(
            "  {",
            "   // comment    ",
            "  var a=6 // var \u00A0 a = 3 + 4     ",
            "\u00A0\u00A0\u00A0\u00A0// var \u00A0 a = 3 + 4 ",
            "a = 5\t\t// var a = ( 3 + 4 ) + array [ a [ 4 + f ] ]\r\r",
            "}"
        );
        List<String> expectedResult = List.of(
            "{",
            TABULATION + "// comment",
            TABULATION + "var a = 6 // var \u00A0 a = 3 + 4",
            TABULATION + "// var \u00A0 a = 3 + 4",
            TABULATION + "a = 5" + TABULATION + TABULATION + "// var a = ( 3 + 4 ) + array [ a [ 4 + f ] ]",
            "}"
        );

        assertThat(codeFormatter.format(unFormattedCode), equalTo(expectedResult));
    }

    @Test
    @Order(15)
    void shouldAddAppropriateTabulationAndTrimMultilineComments() {
        List<String> unFormattedCode = List.of(
            "  {",
            "/* multi                    line",
            "  ** comment               ",
            "\t\t\t** started\u00A0\u00A0\u00A0\u00A0!  ",
            "\u00A0\u00A0*/",
            "}"
        );
        List<String> expectedResult = List.of(
            "{",
            TABULATION + "/* multi                    line",
            TABULATION + "** comment",
            TABULATION + "** started\u00A0\u00A0\u00A0\u00A0!",
            TABULATION + "*/",
            "}"
        );

        assertThat(codeFormatter.format(unFormattedCode), equalTo(expectedResult));
    }

    @Test
    @Order(16)
    void shouldPreserveInitialFormattingOfMultilineCommentsInsideCode() {
        List<String> unFormattedCode = List.of(
            "  {",
            "for ( var i=0;/* // test   comment */ i<9; /*\u00A0test\u00A0comment\u00A0*/ i++ ) /*test comment*/ {",
            "}",
            "}"
        );
        List<String> expectedResult = List.of(
            "{",
            TABULATION + "for (var i = 0;/* // test   comment */ i < 9; /*\u00A0test\u00A0comment\u00A0*/ i ++) /*test comment*/ {",
            TABULATION + "}",
            "}"
        );

        assertThat(codeFormatter.format(unFormattedCode), equalTo(expectedResult));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "'  var a=1 +   2 * 8 / 8  '",
        "\"  var a = 1 + 2*5   / 8  \"",
        "'  var a=1+2*5/8'",
        "\"  var a=1+2*5/8\""
    })
    @Order(17)
    void shouldPreserveInitialFormattingForStringLiterals(String line) {
        List<String> initialCode = List.of(line);

        assertThat(codeFormatter.format(initialCode), equalTo(initialCode));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "'  var a=1 +   2 * 8 / 8",
        "\"  var a = 1 + 2*5   / 8",
        "'  var a=1+2*5/8",
        "\"  var a=1+2*5/8"
    })
    @Order(18)
    void shouldPreserveInitialFormattingForStringLiteralsWithoutClosingQuotationMark(String line) {
        List<String> initialCode = List.of(line);

        assertThat(codeFormatter.format(initialCode), equalTo(initialCode));
    }

    @Test
    @Order(19)
    void shouldCorrectlyDistinguishArrayWithValuesDeclarationFromBlockOfCode() {
        List<String> unFormattedCode = List.of(
            "{",
            "var a={2,c,5}",
            "if(c > 2){",
            "var b={2,a,5}",
            "}",
            "}"
        );
        List<String> expectedResult = List.of(
            "{",
            TABULATION + "var a = {2, c, 5}",
            TABULATION + "if (c > 2) {",
            TABULATION + TABULATION + "var b = {2, a, 5}",
            TABULATION + "}",
            "}"
        );

        assertThat(codeFormatter.format(unFormattedCode), equalTo(expectedResult));
    }

    @Test
    @Order(20)
    void shouldFormatCodeEvenIfOpeningCurlyBraceIsMissing() {
        List<String> unFormattedCode = List.of(
            "{",
            "var a={2,c,5}",
            "\t\t}",
            "    }",
            "\u00A0\u00A0}"
        );
        List<String> expectedResult = List.of(
            "{",
            TABULATION + "var a = {2, c, 5}",
            "}",
            "}",
            "}"
        );

        final List<String> formattedCode = assertDoesNotThrow(() -> codeFormatter.format(unFormattedCode));
        assertThat(formattedCode, equalTo(expectedResult));
    }

    @Test
    @Order(21)
    void shouldFormatCodeEvenIfClosingCurlyBraceIsMissing() {
        List<String> unFormattedCode = List.of(
            "{",
            "{",
            "{",
            "println a +",
            "}"
        );
        List<String> expectedResult = List.of(
            "{",
            TABULATION + "{",
            TABULATION + TABULATION + "{",
            TABULATION + TABULATION + TABULATION + "println a +",
            TABULATION + TABULATION + "}"
        );

        final List<String> formattedCode = assertDoesNotThrow(() -> codeFormatter.format(unFormattedCode));
        assertThat(formattedCode, equalTo(expectedResult));
    }
}