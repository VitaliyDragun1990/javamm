
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

package com.revenat.javamm.interpreter.component.impl.operation.simple;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import com.revenat.javamm.code.component.Console;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.operation.PrintlnOperation;
import com.revenat.javamm.interpreter.test.doubles.ExpressionContextDummy;
import com.revenat.javamm.interpreter.test.doubles.ExpressionStub;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.revenat.juinit.addons.ReplaceCamelCase;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("a 'println' operation interpreter")
class PrintlnOperationInterpreterTest {
    private static final PrintStream ORIGINAL_OUTPUT = System.out;

    private final ByteArrayOutputStream memoryOutput = new ByteArrayOutputStream();

    private PrintlnOperationInterpreter interpreter;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(memoryOutput));
        interpreter = new PrintlnOperationInterpreter(new ExpressionContextDummy(), Console.DEFAULT);
    }

    @AfterAll
    static void setOriginalOutput() {
        System.setOut(ORIGINAL_OUTPUT);
    }

    @Test
    @Order(1)
    void designatesItCanInterpretPrintlnOperation() {
        assertThat(interpreter.getOperationClass(), equalTo(PrintlnOperation.class));
    }

    @Test
    void shouldInterpretPrintlnOperation() {
        final PrintlnOperation operation = printlnOperation("Hello");

        interpreter.interpret(operation);

        assertOutput("Hello");
    }

    private void assertOutput(final String expectedOutput) {
        assertThat(outputFrom(memoryOutput), equalTo(println(expectedOutput)));
    }

    private String outputFrom(final ByteArrayOutputStream output) {
        return new String(output.toByteArray(), StandardCharsets.UTF_8);
    }

    private String println(final String text) {
        return text + System.lineSeparator();
    }

    private PrintlnOperation printlnOperation(final String text) {
        return new PrintlnOperation(
                new SourceLine("test", 1, List.of("println", "(", text, ")")),
                new ExpressionStub(text)
        );
    }
}
