
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

package com.revenat.javamm.compiler.component.impl.operation.simple;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.operation.PrintlnOperation;
import com.revenat.javamm.compiler.ReplaceCamelCase;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;

import java.util.List;
import java.util.ListIterator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("'println' operation reader")
class PrintlnOperationReaderTest {
    private static final SourceLine CORRECT_PRINTLN_LINE = new SourceLine("test", 1, List.of("println", "(", "test", ")"));
    private static final SourceLine PRINTLN_WITHOUT_OPEN_BRACKET = new SourceLine("test", 1, List.of("println", "test", ")"));
    private static final SourceLine PRINTLN_WITHOUT_CLOSE_BRACKET = new SourceLine("test", 1, List.of("println", "(", "test"));
    private static final ListIterator<SourceLine> DUMMY_ITERATOR = List.<SourceLine>of().listIterator();

    private PrintlnOperationReader operationReader;


    @BeforeEach
    void setUp() {
        operationReader = new PrintlnOperationReader();
    }

    @Test
    @Order(1)
    void canReadPrintlnCommand() {
        assertThat(operationReader.canRead(CORRECT_PRINTLN_LINE), is(true));
    }

    @Test
    @Order(2)
    void shouldFailIfOpenBracketIsAbsent() {
        final JavammLineSyntaxError expected =
                assertThrows(JavammLineSyntaxError.class, () -> operationReader.read(PRINTLN_WITHOUT_OPEN_BRACKET, DUMMY_ITERATOR));

        assertThat(expected.getMessage(), containsString(("Expected ( after 'println'")));
    }

    @Test
    @Order(3)
    void shouldFailIfCloseBracketIsAbsent() {
        final JavammLineSyntaxError expected =
                assertThrows(JavammLineSyntaxError.class, () -> operationReader.read(PRINTLN_WITHOUT_CLOSE_BRACKET, DUMMY_ITERATOR));

        assertThat(expected.getMessage(), containsString(("Expected ) at the end of the line")));
    }

    @Test
    @Order(4)
    void shouldProducePrintlnOperationWithCorrectText() {
        final PrintlnOperation operation = operationReader.read(CORRECT_PRINTLN_LINE, DUMMY_ITERATOR);

        assertThat(operation.getText(), equalTo(CORRECT_PRINTLN_LINE.getToken(2)));
    }

}
