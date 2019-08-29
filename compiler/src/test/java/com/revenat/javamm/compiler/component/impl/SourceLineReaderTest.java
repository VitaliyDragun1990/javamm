
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

package com.revenat.javamm.compiler.component.impl;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import com.revenat.javamm.code.fragment.SourceCode;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.compiler.ReplaceCamelCase;
import com.revenat.javamm.compiler.component.SourceLineReader;
import com.revenat.javamm.compiler.component.TokenParser;
import com.revenat.javamm.compiler.model.TokenParserResult;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("Source line reader")
class SourceLineReaderTest {
    private static final String MODULE_NAME = "test";

    private SourceLineReader sourceLineRedaer;

    private TokenParserStub tokenParser;

    @BeforeEach
    void setUp() {
        tokenParser = new TokenParserStub();
        sourceLineRedaer = new SourceLineReaderImpl(tokenParser);
    }

    private SourceCode sourceCode(final String... lines) {
        return new SourceCodeStub(Arrays.asList(lines));
    }

    private List<String> tokens(final String... tokens) {
        return Arrays.asList(tokens);
    }

    private void assertUnmodifiable(final List<SourceLine> list) {
        assertThat(list.getClass(), equalTo(List.copyOf(List.of()).getClass()));
    }

    @Test
    @Order(1)
    void shouldReturnEmptyListWhenSourceCodeIsEmpty() {
        final List<SourceLine> sourceLines = sourceLineRedaer.read(sourceCode());

        assertThat(sourceLines, is(empty()));
    }

    @Test
    @Order(2)
    void shouldReturnUnmodifiableList() {
        final List<SourceLine> sourceLines = sourceLineRedaer.read(sourceCode());

        assertUnmodifiable(sourceLines);
    }

    @Test
    @Order(3)
    void shouldReturnSingleSourceLineForSourceCodeWithSingleLine() {
        tokenParser.defineAnswer("var a = 10", false, new TokenParserResult(tokens("var", "a", "=", "10"), false));

        final List<SourceLine> sourceLines = sourceLineRedaer.read(sourceCode("var a = 10"));

        assertThat(sourceLines, hasSize(1));
    }

    @Test
    @Order(4)
    void sourceLineShouldContainCorrectModuleName() {
        tokenParser.defineAnswer("var a = 10", false, new TokenParserResult(tokens("var", "a", "=", "10"), false));

        final List<SourceLine> sourceLines = sourceLineRedaer.read(sourceCode("var a = 10"));
        final SourceLine sourceLine = sourceLines.get(0);

        assertThat(sourceLine.getModuleName(), equalTo(MODULE_NAME));
    }

    @Test
    @Order(5)
    void shouldIgnoreEmptyLinesInSourceCode() {
        tokenParser.defineAnswer("var a = 10", false, new TokenParserResult(tokens("var", "a", "=", "10"), false));

        final List<SourceLine> sourceLines = sourceLineRedaer.read(sourceCode("", "var a = 10", ""));

        assertThat(sourceLines, hasSize(1));
    }

    @Test
    @Order(6)
    void eachSourceLineShouldHaveLineNumber() {
        tokenParser.defineAnswer("println(20)", false, new TokenParserResult(tokens("println", "(", "20", ")"), false));
        tokenParser.defineAnswer("var a = 10", false, new TokenParserResult(tokens("var", "a", "=", "10"), false));

        final List<SourceLine> sourceLines = sourceLineRedaer.read(sourceCode("println(20)", "", "var a = 10"));

        assertThat(sourceLines.get(0).getLineNumber(), equalTo(1));
        assertThat(sourceLines.get(1).getLineNumber(), equalTo(3));
    }

    @Test
    @Order(7)
    void shouldHandleMultilineComments() {
        final String[] lines = { "/* var a = 10", "comment goes on", "comment ends */ var b = 10" };
        tokenParser.defineAnswer("/* var a = 10", false, new TokenParserResult(true));
        tokenParser.defineAnswer("comment goes on", true, new TokenParserResult(true));
        tokenParser.defineAnswer("comment ends */ var b = 10", true,
                new TokenParserResult(tokens("var", "b", "=", "10"), false));

        final List<SourceLine> sourceLines = sourceLineRedaer.read(sourceCode(lines));

        assertThat(sourceLines, hasSize(1));
        final SourceLine sourceLine = sourceLines.get(0);
        assertThat(sourceLine.getTokens(), equalTo(tokens("var", "b", "=", "10")));
    }

    private class SourceCodeStub implements SourceCode {
        private final List<String> lines;

        public SourceCodeStub(final List<String> lines) {
            this.lines = List.copyOf(lines);
        }

        @Override
        public String getModuleName() {
            return MODULE_NAME;
        }

        @Override
        public List<String> getLines() {
            return lines;
        }

    }

    private static class TokenParserStub implements TokenParser {
        private static final TokenParserResult LINE_WITHOUT_TOKENS = new TokenParserResult(false);
        private final Map<String, TokenParserResult> answers = new HashMap<>();

        public void defineAnswer(final String arg, final boolean isMultilineStarted, final TokenParserResult answer) {
            answers.put(arg + Boolean.toString(isMultilineStarted), answer);
        }

        @Override
        public TokenParserResult parseLine(final String sourceCodeLine) {
            return answers.getOrDefault(sourceCodeLine + Boolean.toString(false), LINE_WITHOUT_TOKENS);
        }

        @Override
        public TokenParserResult parseLineWithStartedMultilineComment(final String sourceCodeLine) {
            return answers.getOrDefault(sourceCodeLine + Boolean.toString(true), LINE_WITHOUT_TOKENS);
        }

    }

}
