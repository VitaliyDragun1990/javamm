
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

package com.revenat.javamm.code.fragment;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.DisplayNameGeneration;

import com.revenat.juinit.addons.ReplaceCamelCase;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("source line")
class SourceLineTest {
    private static final String MODULE_NAME = "test";
    private static final int LINE_NUMBER = 1;
    private static final List<String> TOKENS = List.of("var", "a", "=", "10");

    private SourceLine sourceLine;


    @BeforeEach
    void setUp() {
        sourceLine = new SourceLine(MODULE_NAME, LINE_NUMBER, TOKENS);
    }

    @Test
    @Order(1)
    void shouldContainModuleName() {
        assertThat(sourceLine.getModuleName(), equalTo(MODULE_NAME));
    }

    @Test
    @Order(2)
    void shouldContainLineNumber() {
        assertThat(sourceLine.getLineNumber(), equalTo(LINE_NUMBER));
    }

    @Test
    @Order(3)
    void shouldContainTokens() {
        assertThat(sourceLine.getTokens(), equalTo(TOKENS));
    }

    @Test
    @Order(4)
    void shouldAllowToGetFirstToken() {
        assertThat(sourceLine.getFirst(), equalTo("var"));
    }

    @Test
    @Order(5)
    void shouldAllowToGetLastToken() {
        assertThat(sourceLine.getLast(), equalTo("10"));
    }

    @Test
    @Order(6)
    void shouldAllowToGetTokenCount() {
        assertThat(sourceLine.getTokenCount(), equalTo(4));
    }

    @Test
    @Order(7)
    void shouldAllowToGetTokensByIndex() {
        assertThat(sourceLine.getToken(1), equalTo("a"));
    }

    @Test
    @Order(8)
    void shouldAllowToGetTokenIndex() {
        assertThat(sourceLine.indexOf("="), equalTo(2));
        assertThat(sourceLine.indexOf("println"), equalTo(-1));
    }

    @Test
    @Order(9)
    void shouldAllowToCheckIfGivenTokenPresent() {
        assertThat(sourceLine.contains("var"), is(true));
        assertThat(sourceLine.contains("if"), is(false));
    }

    @Test
    @Order(10)
    void shouldAllowToGetCommandContainingAllTokens() {
        assertThat(sourceLine.getCommand(), equalTo("var a = 10"));
    }

    @Test
    @Order(11)
    void shouldConsideredEqualToAnotherSourceLineWithSameModuleNameAndLineNumber() {
        final SourceLine another = new SourceLine(MODULE_NAME, 1, List.of());

        assertThat(sourceLine, equalTo(another));
        assertThat(sourceLine.hashCode(), equalTo(another.hashCode()));
    }

    @Test
    @Order(11)
    void shouldConsideredNotEqualToSourceLineWithSameModuleNameAndLineNumberButAnotherClass() {
        final SourceLine another = new TestSourceLine(MODULE_NAME, 1, List.of());

        assertThat(sourceLine, not(equalTo(another)));
    }


    @Test
    @Order(11)
    void shouldConsideredNotEqualToSourceLineWithSameModuleNameAndButDifferentLineNumber() {
        final SourceLine another = new SourceLine(MODULE_NAME, 2, List.of());

        assertThat(sourceLine, not(equalTo(another)));
        assertThat(sourceLine.hashCode(), not(equalTo(another.hashCode())));
    }

    @Test
    @Order(11)
    void shouldConsideredNotEqualToSourceLineDifferentSameModuleNameAndButSameLineNumber() {
        final SourceLine another = new SourceLine("some different value", 1, List.of());

        assertThat(sourceLine, not(equalTo(another)));
        assertThat(sourceLine.hashCode(), not(equalTo(another.hashCode())));
    }


    @Test
    @Order(11)
    void shouldConsideredNotEqualToNull() {
        assertThat(sourceLine, not(equalTo(null)));
    }

    @Test
    @Order(12)
    void shouldCompareFirstByModuleNameInLexicographicalOrder() {
        final SourceLine another = new SourceLine("another test", 1, List.of());

        assertGreaterThan(sourceLine, another);
    }

    @Test
    @Order(13)
    void shouldCompareByLineNumberIfModuleNameTheSame() {
        final SourceLine another = new SourceLine(MODULE_NAME, 10, List.of());

        assertLessThan(sourceLine, another);
    }


    @Test
    @Order(14)
    void shouldAllowToGetAdequateStringRepresentation() {
        assertThat(sourceLine.toString(), equalTo("[test:1] -> var a = 10"));
    }

    void assertGreaterThan(final SourceLine first, final SourceLine second) {
        assertThat(first.compareTo(second), greaterThan(0));
    }

    void assertLessThan(final SourceLine first, final SourceLine second) {
        assertThat(first.compareTo(second), lessThan(0));
    }

    private static class TestSourceLine extends SourceLine {

        TestSourceLine(final String moduleName, final int lineNumber, final List<String> tokens) {
            super(moduleName, lineNumber, tokens);
        }
    }
}
