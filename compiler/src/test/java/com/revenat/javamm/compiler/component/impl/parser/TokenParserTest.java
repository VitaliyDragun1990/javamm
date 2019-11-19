
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

package com.revenat.javamm.compiler.component.impl.parser;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.revenat.javamm.code.fragment.operator.BinaryOperator;
import com.revenat.javamm.code.fragment.operator.UnaryOperator;
import com.revenat.javamm.compiler.component.TokenParser;
import com.revenat.javamm.compiler.component.impl.parser.custom.TokenParserImpl;
import com.revenat.javamm.compiler.model.TokenParserResult;

import java.util.List;
import java.util.stream.Stream;

import static java.util.function.Function.identity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;
import org.junit.jupiter.params.provider.ValueSource;

import com.revenat.juinit.addons.ReplaceCamelCase;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("a token parser")
class TokenParserTest {

    private final TokenParser tokenParser = new TokenParserImpl();

    @ParameterizedTest(name = "[{index}] -> [{0}]")
    @ValueSource(strings = {
            "",
            "\u00A0\u00A0",
            " ",
            "     ",
            "\r\r\r",
            "\t",
            "\t\t\t",

    })
    @Order(1)
    void shouldReturnEmptyResultForIgnoredDelimiters(final String sourceLine) {
        final TokenParserResult result = tokenParser.parseLine(sourceLine, false);

        assertEmpty(result);
        assertMultiLineCommentNotStarted(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "//",
            "//some text",
            "//1 + 1 /* 5 - 7 */",
            "//1 + 1 */",
            "//1 + 1 */ /* ",
            "//1 + 1 // 5 - 7",

    })
    @Order(2)
    void shouldReturnEmptyResultForSingleLineComments(final String sourceLine) {
        final TokenParserResult result = tokenParser.parseLine(sourceLine, false);

        assertEmpty(result);
        assertMultiLineCommentNotStarted(result);
    }

    @ParameterizedTest
    @CsvSource({
        "/*,        false",
        "1 + 1,     true",
        "/* 1 + 1,  true",
        "/* 1 + 1,  false"
    })
    @Order(3)
    void shouldCorrectlyHandleStartedMultiLineComment(final String sourceLine, final boolean isMultiLneCommendStartedBefore) {
        final TokenParserResult result = tokenParser.parseLine(sourceLine, isMultiLneCommendStartedBefore);

        assertEmpty(result);
        assertMultiLineCommentStarted(result);
    }

    @ParameterizedTest
    @CsvSource({
        "*/,            true",
        "1 + 1 */,      true",
        "/*1 + 1 */,    true",
        "/*1 + 1 */,    false"
    })
    @Order(4)
    void shouldCorrectlyDetermineWhenMultiLineCommentCompletes(final String sourceLine, final boolean isMultiLineCommentStartedBefore) {
        final TokenParserResult result = tokenParser.parseLine(sourceLine, isMultiLineCommentStartedBefore);

        assertEmpty(result);
        assertMultiLineCommentNotStarted(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "\"\"" ,
            "''",
            "\"test\"",
            "'test'",
            "'should treat as single token'",
            "'should \"treat\" as single token'",
            "\"should treat as single token\"",
            "\"should 'treat' as single token\"",
    })
    @Order(5)
    void shouldTreatStringsAsSingleToken(final String sourceLine) {
        final TokenParserResult result = tokenParser.parseLine(sourceLine, false);

        assertTokens(result, sourceLine);
    }

    @ParameterizedTest
    @CsvSource({
            "\"\" + \"\",                  \"\", +, \"\"",
            "'' + '',                       '''''', +, ''''''",
            "\"test\" + 'test',             \"test\", +, '''test'''",
            "'test one' + 'test two',       '''test one''', +, '''test two'''",
            "\"test one\" + 'test two',     \"test one\", +, '''test two'''",
    })
    @Order(6)
    void shouldProcessSeveralStringsInOneLine(final String sourceLine,
                                              final String token1,
                                              final String token2,
                                              final String token3) {
        final TokenParserResult result = tokenParser.parseLine(sourceLine, false);

        assertTokens(result, token1, token2, token3);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "'",
            "'without closing quote",
            "\"",
            "\"without closing quote"
    })
    @Order(7)
    void shouldNotFailIfStringConstantDoesNotEndWithClosingQuote(final String sourceLine) {
        final TokenParserResult result = assertDoesNotThrow(() -> tokenParser.parseLine(sourceLine, false));

        assertTokens(result, sourceLine);
        assertMultiLineCommentNotStarted(result);
    }

    @ParameterizedTest
    @CsvSource({
        "b>>>=2,                            b >>>= 2",
        "b>>> =2,                           b >>> = 2",
        "b>> >=2,                           b >> >= 2",
        "b> >>=2,                           b > >>= 2",
        "b>> >=2,                           b >> >= 2",

        "b>>>>3,                            b >>> > 3",
        "a+++3,                             a ++ + 3",
        "a+++++3,                           a ++ ++ + 3",
        "a>>>>>3,                           a >>> >> 3",
        "a<<<<<3,                           a << << < 3",
        "a<<<<<=3,                          a << << <= 3",
        "a<<<<<<=3,                         a << << <<= 3",
        "a<<<<<==3,                         a << << <= = 3",
        "a<<<<<<==3,                        a << << <<= = 3",
        "a<<<<<===3,                        a << << <= == 3",

        "var a=b>>>>>2>> >4>>>5<<<6<<<=8,   var a = b >>> >> 2 >> > 4 >>> 5 << < 6 << <= 8"
    })
    @Order(8)
    void shouldExtractOperatorsWithLongestCodesFirst(final String sourceLine, final String expectedTokens) {
        final TokenParserResult result = tokenParser.parseLine(sourceLine, false);

        assertTokens(result, tokensFrom(expectedTokens, " "));
    }

    @ParameterizedTest
    @CsvSource({
        "a + \"test\\here\",             a:+:\"test\\here\",    false",
        "a + \" /* test\",               a:+:\" /* test\",      false",
        "a + \" */ \"test\"/*\"test\",   \"test\",              true",
        "a + /*test*/\"/*test\",         a:+:\"/*test\",        false",
        "a + /*test*/\"/*test\",         \"/*test\",            true",

        "a + 'test\\here',               a:+:'test\\here',      false",
        "a + ' /* test',                 a:+:' /* test',        false",
        "a + ' */ 'test'/*'test',          '''test''',               true",
        "a + /*test*/'/*test\',          a:+:'/*test',          false",
        "a + /*test*/'/*test',           '''/*test''',              true",
    })
    @Order(9)
    void shouldNotEvaluateCommentsIfTheyInsideStringConstants(final String sourceLine,
                                                              final String expectedTokens,
                                                              final boolean multiLineCommentStartedBefore) {
        final TokenParserResult result = tokenParser.parseLine(sourceLine, multiLineCommentStartedBefore);

        assertTokens(result, tokensFrom(expectedTokens, ":"));

    }

    @ParameterizedTest(name = "[{index}] -> [{0}] (multiLineCommentStartedBefore= {1})")
    @ArgumentsSource(DifferentSourcesButSameResultProvider.class)
    @Order(10)
    void shouldIgnoreAllCommentedOutStuff(final String sourceLine,
                                          final boolean multiLineCommentStartedBefore,
                                          final TokenParserResult expectedResult) {
        final TokenParserResult result = tokenParser.parseLine(sourceLine, multiLineCommentStartedBefore);

        assertExpectedResult(result, expectedResult);
    }

    @ParameterizedTest
    @EnumSource(value = BinaryOperator.class, names = "PREDICATE_TYPEOF", mode = Mode.EXCLUDE)
    @Order(11)
    void shouldSupportAllBinaryOperatorTokens(final BinaryOperator operator) {
        final String sourceLine = sourceLine("var a = b[operatorToken]c", operator.getCode());

        final TokenParserResult result = tokenParser.parseLine(sourceLine, false);

        assertTokens(result, "var", "a", "=", "b", operator.getCode(), "c");
    }

    @ParameterizedTest
    @EnumSource(UnaryOperator.class)
    @Order(12)
    void shouldSupportAllUnaryOperatorTokens(final UnaryOperator operator) {
        final String sourceLine = sourceLine("var a = [operatorToken]c", operator.getCode());

        final TokenParserResult result = tokenParser.parseLine(sourceLine, false);

        assertTokens(result, "var", "a", "=", operator.getCode(), "c");
    }

    @Test
    @Order(13)
    void shouldSupportTernaryOperatorTokens() {
        final String sourceLine = "var a = b ? c : d";

        final TokenParserResult result = tokenParser.parseLine(sourceLine, false);

        assertTokens(result, "var", "a", "=", "b", "?", "c", ":", "d");
    }

    @ParameterizedTest(name = "[{index}] -> {0}")
    @ArgumentsSource(ComplexExpressionProvider.class)
    @Order(14)
    void shouldBeAbleToSplitLineUsingAllSupportedDelimiters(final String sourceLine,
                                                            final List<String> expectedTokens) {
        final TokenParserResult result = tokenParser.parseLine(sourceLine, false);

        assertTokens(result, expectedTokens);
    }

    private String sourceLine(final String template, final String operatorToken) {
        return template.replace("[operatorToken]", operatorToken);
    }

    private void assertExpectedResult(final TokenParserResult actualResult, final TokenParserResult expectedResult) {
        assertThat(actualResult.getTokens(), equalTo(expectedResult.getTokens()));
        assertThat(actualResult.isMultiLineCommentStarted(), equalTo(expectedResult.isMultiLineCommentStarted()));
    }

    private String[] tokensFrom(final String expectedTokens, final String separator) {
        return expectedTokens.split(separator);
    }

    private void assertTokens(final TokenParserResult result, final List<String> expectedTokens) {
        final List<String> actualTokens = result.getTokens();
        assertThat(actualTokens, equalTo(expectedTokens));
    }

    private void assertTokens(final TokenParserResult result, final String... expectedTokens) {
        assertTokens(result, List.of(expectedTokens));
    }

    private void assertMultiLineCommentStarted(final TokenParserResult result) {
        assertTrue(result.isMultiLineCommentStarted(), "Should detect that multiLine comment started");
    }

    private void assertMultiLineCommentNotStarted(final TokenParserResult result) {
        assertFalse(result.isMultiLineCommentStarted(), "Should not detect that multiLine comment started");
    }

    private void assertEmpty(final TokenParserResult result) {
        assertThat(result.getTokens(), hasSize(0));
    }

    static class DifferentSourcesButSameResultProvider implements ArgumentsProvider {

        private final List<String> expectedTokens = List.of("var", "a", "=", "3");

        @Override
        public Stream<? extends Arguments> provideArguments(final ExtensionContext context) throws Exception {
            return Stream.of(
                    multiLineCommentNotStartedBeforeAndNotStartedAfter(),
                    multiLineCommentNotStartedBeforeButStartedAfter(),
                    multiLineCommentStartedBeforeButNotStartedAfter(),
                    multiLineCommentStartedBeforeAndStartedAfter()
                    ).flatMap(identity());
        }

        private Stream<Arguments> multiLineCommentNotStartedBeforeAndNotStartedAfter() {
            return Stream.of(
                    arguments("var a = 3", false, parserResult(expectedTokens, false)),
                    arguments("var a = 3 // 4 - 5", false, parserResult(expectedTokens, false)),
                    arguments("var a = 3 /* 2 + 2 */", false, parserResult(expectedTokens, false)),
                    arguments("var a = 3 /* 2 + 2 */ // 4 - 5", false, parserResult(expectedTokens, false)),
                    arguments("var a = /* 2 + 2 */ 3", false, parserResult(expectedTokens, false)),
                    arguments("var a = /* 2 + 2 */ 3 // 4 - 5", false, parserResult(expectedTokens, false)),
                    arguments("var /* 1 + 1 */ a /* 2 + 2 */ = /* 3 + 2 */ 3 /* 4 + 4 */", false, parserResult(expectedTokens, false)),
                    arguments("var /* 1 + 1 */ a /* 2 + 2 */ = /* 3 + 2 */ 3 // 4 - 5", false, parserResult(expectedTokens, false))
                    );
        }

        private Stream<Arguments> multiLineCommentNotStartedBeforeButStartedAfter() {
            return Stream.of(
                    arguments("var a = 3 /* 2 + 2", false, parserResult(expectedTokens, true)),
                    arguments("var a = 3 /* 2 + 2 */ /* 3 + 3", false, parserResult(expectedTokens, true)),
                    arguments("var a = 3 /* // 2 + 2 */ /* //3 + 3", false, parserResult(expectedTokens, true))
                    );
        }

        private Stream<Arguments> multiLineCommentStartedBeforeButNotStartedAfter() {
            return Stream.of(
                    arguments("2 + 2 */ var a = 3", true, parserResult(expectedTokens, false)),
                    arguments("2 + 2 */ var a = 3 // 4 - 5", true, parserResult(expectedTokens, false)),
                    arguments("2 + 2 */ var a = 3 /* 4 - 5 */", true, parserResult(expectedTokens, false)),
                    arguments("// 2 + 2 */ var a = 3 /* 4 - 5 */", true, parserResult(expectedTokens, false))
                    );
        }

        private Stream<Arguments> multiLineCommentStartedBeforeAndStartedAfter() {
            return Stream.of(
                    arguments("2 + 2 */ var a = 3 /* 3 + 3", true, parserResult(expectedTokens, true)),
                    arguments("2 + 2 */ /* 3 + 3 */ var a = 3 /* 4 + 4 */ /* 5 + 5", true, parserResult(expectedTokens, true)),
                    arguments("2 + 2 */ /* // 3 + 3 */ var a = 3 /* // 4 + 4 */ /* // 5 + 5", true, parserResult(expectedTokens, true))
                    );
        }

        private TokenParserResult parserResult(final List<String> tokens, final boolean isMultiLineStartedBefore) {
            return new TokenParserResult(tokens, isMultiLineStartedBefore);
        }
    }

    static class ComplexExpressionProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(final ExtensionContext context) throws Exception {
            return Stream.of(
                    // var e = c + 2 * d - 5 / a[0]
                    arguments("\tvar e = c+2*d-5 / a[0]\t ",
                        List.of("var", "e", "=", "c", "+", "2", "*", "d", "-", "5", "/", "a", "[", "0", "]")),

                    // println ("pr = " + (c + sum (4, d - 2) * 3 - 12 ) % a[3])
                    arguments("  println (\"pr = \" + (c + sum (4, d-2) * 3-12) % a[3])",
                        List.of("println", "(", "\"pr = \"", "+", "(", "c", "+", "sum", "(", "4", ",", "d", "-",
                            "2", ")", "*", "3", "-", "12", ")", "%", "a", "[", "3", "]", ")")),

                    // println ('unary = ' + (-c) + ',' + (-3) + ',' + (+c) + ',' + (++c) + ',' + (c++) + '.')
                    arguments("  println ('unary = ' + (-c) + ',' + (-3) + ',' + (+c) + ',' + (++c) + ',' + (c++) + '.')",
                        List.of("println", "(", "'unary = '", "+", "(", "-", "c", ")", "+", "','", "+", "(", "-", "3", ")",
                            "+", "','", "+", "(", "+", "c", ")", "+", "','", "+", "(", "++", "c", ")", "+", "','",
                            "+", "(", "c", "++", ")", "+", "'.'", ")")),

                    // var a = 'Hello ' + 2 + '5 + 678 - 6'
                    arguments(" var a = 'Hello ' + 2 + '5 + 678 - 6'  \t \t",
                        List.of("var", "a", "=", "'Hello '", "+", "2", "+", "'5 + 678 - 6'")),

                    // var b = a > 100 ? 'true' : 'false'
                    arguments(" var b = a > 100    ?'true':'false'  \t \t",
                            List.of("var", "b", "=", "a", ">", "100", "?", "'true'", ":", "'false'")),

                    // println (a[0] > ar[a[4 - a[3]] * sum (a[1], 0 - a[1])] ? a typeof array && ar typeof array : sum (parseInt (\"12\"), parseDouble (\"12.1\")))
                    arguments("\tprintln (a[0] > ar[a[4 - a[3]] * sum (a[1], 0 - a[1])] ? " +
                            "a typeof array && ar typeof array : sum (parseInt (\"12\"), parseDouble (\"12.1\")))",
                        List.of("println", "(", "a", "[", "0", "]", ">", "ar", "[", "a", "[", "4", "-", "a", "[", "3", "]",
                            "]", "*", "sum", "(", "a", "[", "1", "]", ",", "0", "-", "a", "[", "1", "]", ")", "]",
                            "?", "a", "typeof", "array", "&&", "ar", "typeof", "array", ":", "sum", "(",
                            "parseInt", "(", "\"12\"", ")", ",", "parseDouble", "(", "\"12.1\"", ")", ")", ")"))
                );
        }
    }
}
