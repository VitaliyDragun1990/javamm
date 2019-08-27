
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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.revenat.javamm.code.fragment.SourceCode;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.compiler.ReplaceCamelCase;
import com.revenat.javamm.compiler.component.SourceLineReader;
import com.revenat.javamm.compiler.component.TokenParser;
import com.revenat.javamm.compiler.model.TokenParserResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("Source line reader")
public class SourceLineReaderImplTest {
    private static final String MODULE_NAME = "test";

//    @Mock
//    private TokenParser parser;
    private TokenParserStub parser;

    private SourceLineReader sourceLineReader;


    @BeforeEach
    void createSourceLineReader() {
        parser = new TokenParserStub();
        sourceLineReader = new SourceLineReaderImpl(parser);
    }

//    private List<SourceLine> sourceLines(final SourceLine... sourceLines) {
//        return List.copyOf(Arrays.asList(sourceLines));
//    }

    private static SourceCode sourceCodeWith(final String... lines) {
        return SourceCodeStub.withLines(lines);
    }

    @Test
    @Order(1)
    void shouldReturnNotNullResult() {
        SourceCode sourceCode = sourceCodeWith();
        assertThat(sourceLineReader.read(sourceCode), is(notNullValue()));
    }

    @Test
    @Order(2)
    void shouldReturnUnmodifiableList() {
        assertUnmodifiableList(sourceLineReader.read(sourceCodeWith()));
    }

    @Test
    @Order(3)
    void shouldReturnCorrectNumberOfSourceLines() {
        String[] lines = {
                "",
                "// comment",
                "var a"};
        parser.defineAnswer("var a", new TokenParserResult(List.of("var", "a"), false));

        List<SourceLine> result = sourceLineReader.read(sourceCodeWith(lines));

        assertThat(result, hasSize(1));
    }

    @Test
    @Order(4)
    void shouldReturnCorrectSourceLine() {
        String[] lines = {
                "// comment",
                "var a"};
        parser.defineAnswer("var a", new TokenParserResult(List.of("var", "a"), false));

        List<SourceLine> result = sourceLineReader.read(sourceCodeWith(lines));

        assertThat(result.get(0), equalTo(new SourceLine(MODULE_NAME, 2, List.of("var", "a"))));
    }

    @Test
    void shouldSupportMultilineComments() {
      String[] lines = {
              "/* */",
              "var a /* comment */ = 5",
              "a = 4 /* multiline",
              " comment ",
              " example */ println (a)"
      };
      parser.defineAnswer("var a /* comment */ = 5", new TokenParserResult(List.of("var", "a", "=", "5"), false));
      parser.defineAnswer("a = 4 /* multiline", new TokenParserResult(List.of("a", "=", "4"), true));
      parser.defineAnswer(" comment ", new TokenParserResult(List.of(), true));
      parser.defineAnswer(" example /*", new TokenParserResult(List.of("println","(", "a",")"), false));

      sourceLineReader.read(sourceCodeWith(lines));

      assertThat(parser.numberWithCommentStartedCalls(), equalTo(2));
    }

    private void assertUnmodifiableList(final List<SourceLine> list) {
        assertAll("unmodifiable list",
                () -> assertThat(list.getClass(), equalTo(Collections.unmodifiableList(List.of())))/*,
                () -> assertThrows(UnsupportedOperationException.class, () -> list.add(null)),
                () -> assertThrows(UnsupportedOperationException.class, () -> list.add(EMPTY_SOURCE_LINE)),
                () -> assertThrows(UnsupportedOperationException.class, () -> list.set(0, EMPTY_SOURCE_LINE)),
                () -> assertThrows(UnsupportedOperationException.class, () -> list.add(0, EMPTY_SOURCE_LINE)),
                () -> assertThrows(UnsupportedOperationException.class, () -> list.addAll(List.of(EMPTY_SOURCE_LINE))),

                () -> assertThrows(UnsupportedOperationException.class, () -> list.removeAll(List.of())),
                () -> assertThrows(UnsupportedOperationException.class, () -> list.retainAll(List.of())),
                () -> assertThrows(UnsupportedOperationException.class, () -> list.remove(0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> list.remove(EMPTY_SOURCE_LINE)),

                () -> assertThrows(UnsupportedOperationException.class, () -> list.iterator().remove()),
                () -> assertThrows(UnsupportedOperationException.class, () -> list.listIterator().remove()),
                () -> assertThrows(UnsupportedOperationException.class, () -> list.listIterator().add(EMPTY_SOURCE_LINE)),
                () -> assertThrows(UnsupportedOperationException.class, () -> list.listIterator().set(EMPTY_SOURCE_LINE)),

                () -> assertThrows(UnsupportedOperationException.class, () -> list.removeIf(sourceLine -> false)),
                () -> assertThrows(UnsupportedOperationException.class, () -> list.replaceAll(UnaryOperator.identity())),
                () -> assertThrows(UnsupportedOperationException.class, () -> list.sort((s1, s2) -> 0)),
                () -> assertThrows(UnsupportedOperationException.class, list::clear)*/
        );

    }

    static class SourceCodeStub implements SourceCode {
        private List<String> lines = new ArrayList<>();

        private SourceCodeStub(final List<String> lines) {
            this.lines = List.copyOf(lines);
        }

        public static SourceCode withLines(final String... lines) {
            return new SourceCodeStub(Arrays.asList(lines));
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

    static class TokenParserStub implements TokenParser {
        private Map<String, TokenParserResult> parseLineMap = new HashMap<>();
        private int numberWithCommentStartedCalls = 0;

        public void defineAnswer(final String line, final TokenParserResult answer) {
            parseLineMap.put(line, answer);
        }

        public int numberWithCommentStartedCalls() {
            return numberWithCommentStartedCalls;
        }

        @Override
        public TokenParserResult parseLine(final String sourceCodeLine) {
            return parseLineMap.getOrDefault(sourceCodeLine, new TokenParserResult(false));
        }

        @Override
        public TokenParserResult parseLineWithStartedMultilineComment(final String sourceCodeLine) {
            numberWithCommentStartedCalls++;
            return parseLine(sourceCodeLine);
        }

    }

}
