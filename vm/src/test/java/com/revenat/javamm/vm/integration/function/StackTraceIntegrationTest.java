
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

package com.revenat.javamm.vm.integration.function;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.junit.jupiter.api.Assertions.*;

import com.revenat.javamm.interpreter.error.JavammRuntimeError;
import com.revenat.javamm.interpreter.model.StackTraceItem;
import com.revenat.javamm.vm.integration.AbstractIntegrationTest;

import java.util.List;
import java.util.stream.Stream;

import static com.revenat.javamm.interpreter.InterpreterConfigurator.MAX_STACK_SIZE;
import static com.revenat.javamm.vm.helper.CustomAsserts.assertErrorMessageContains;

import static java.lang.String.format;
import static java.lang.System.lineSeparator;
import static java.util.stream.Collectors.joining;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.revenat.juinit.addons.ReplaceCamelCase;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("an interpreter")
public class StackTraceIntegrationTest extends AbstractIntegrationTest {

    @Test
    @Order(1)
    void shouldThorwRuntimeErrorWithStackTrace() {
        final List<String> lines = List.of(
                "function main() {",
                "   first()",
                "}",

                "function first() {",
                "   second()",
                "}",

                "function second() {",
                "   var a = 1 / 0",
                "}"
        );

        final String expectedStackTrace = Stream.of(
                format("    at second() [%s:8]", MODULE_NAME),
                format("    at first() [%s:5]", MODULE_NAME),
                format("    at main() [%s:2]", MODULE_NAME)
        ).collect(joining(lineSeparator()));

        final JavammRuntimeError e = assertThrows(JavammRuntimeError.class, () -> runCode(lines));

        assertErrorMessageContains(e, buildErrorMsg("/ by zero", expectedStackTrace));

        final List<StackTraceItem> actualStackTrace = e.getCurrentStackTrace();
        assertStackTraceItem(actualStackTrace.get(0), MODULE_NAME, "second()", 8);
        assertStackTraceItem(actualStackTrace.get(1), MODULE_NAME, "first()", 5);
        assertStackTraceItem(actualStackTrace.get(2), MODULE_NAME, "main()", 2);
    }

    @Test
    @Order(2)
    void shouldThrowStackOverflowErrorIfStackSizeExceedsMaxLimit() {
        final List<String> lines = List.of(
                "function main() {",
                "   main()",
                "}"
        );

        final String expectedStackTrace =
                Stream.generate(() -> format("    at main() [%s:2]", MODULE_NAME))
                .limit(MAX_STACK_SIZE)
                .collect(joining(lineSeparator()));
        final String expectedErrorMessage =
                buildErrorMsg(format("Stack overflow error. Max stack size is %d", MAX_STACK_SIZE), expectedStackTrace);

        final JavammRuntimeError e = assertThrows(JavammRuntimeError.class, () -> runCode(lines));

        assertErrorMessageContains(e, expectedErrorMessage);
    }

    @Test
    @Order(3)
    void shouldThrowRuntimeErrorWithoutStackTraceIfNoFunctionWasInvoken() {
        final List<String> lines = List.of(
                "function test() {",
                "}"
        );

        final JavammRuntimeError e = assertThrows(JavammRuntimeError.class, () -> runCode(lines));

        final String expectedErrorMessage = "Runtime error: Main function not found, please define the main function as: 'function main()'";
        assertErrorMessageContains(e, expectedErrorMessage);
        assertThat(e.getCurrentStackTrace(), is(empty()));
    }

    private void assertStackTraceItem(final StackTraceItem stackTraceItem,
                                      final String expectedModuleName,
                                      final String expectedFunctionName,
                                      final int expectedLineNumber) {
        assertThat(stackTraceItem.getModuleName(), equalTo(expectedModuleName));
        assertThat(stackTraceItem.getFunction().toString(), equalTo(expectedFunctionName));
        assertThat(stackTraceItem.getSourceLineNumber(), equalTo(expectedLineNumber));
    }
}
