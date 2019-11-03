
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

package com.revenat.javamm.vm.integration.operation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.revenat.javamm.interpreter.error.JavammRuntimeError;
import com.revenat.javamm.vm.integration.AbstractIntegrationTest;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

import static com.revenat.javamm.vm.helper.CustomAsserts.assertErrorMessageContains;

import static java.util.List.of;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import org.junit.jupiter.api.AfterAll;
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
import org.junit.jupiter.params.provider.ValueSource;

import com.revenat.juinit.addons.ReplaceCamelCase;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("a Javamm virtual machine interpreter")
public class DoWhileOperationInterpreterIntegrationTest extends AbstractIntegrationTest {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();

    private static final long INFINITE_LOOP_TIMEOUT_IN_MILLISECONDS = 500;

    @AfterAll
    static void cleanup() {
        EXECUTOR_SERVICE.shutdownNow();
    }

    @ParameterizedTest
    @ArgumentsSource(DoWhileOperationProvider.class)
    @Order(1)
    void shouldInterpretDoWhileOperation(final List<String> lines, final List<Object> expectedOutput) {
        assertDoesNotThrow(() -> runBlock(lines));

        assertExpectedOutput(expectedOutput);
    }

    @Test
    @Order(2)
    void shouldFailIfDoWhileConditionExpressionEvaluatesToNonBooleanResult() {
        final List<String> whileWithInvalidCondition = of(
                "do {",

                "}",
                "while ( 8 + 10 )"
        );

        final JavammRuntimeError e = assertThrows(JavammRuntimeError.class, () -> runBlock(whileWithInvalidCondition));

        assertErrorMessageContains(e,
                "Runtime error in 'test' [Line: 4]: Condition expression should be boolean. Current type is integer");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "true",
            "1 > 0",
            "1 typeof integer"
    })
    @Order(3)
    void shouldSupportAnInterruptionOfInfiniteLoop(final String condition) throws InterruptedException, ExecutionException {
        final List<String> lines = of(
                "do {",
                "}",
                "while ( " + condition + " )"
        );

        final Future<?> future = EXECUTOR_SERVICE.submit(() -> runBlock(lines));
        try {
            future.get(INFINITE_LOOP_TIMEOUT_IN_MILLISECONDS, MILLISECONDS);
            fail("Expected infinite loop");
        } catch (final TimeoutException e) {
            // do nothing. TimeoutException is expected.
            assertTrue(future.cancel(true));
        }
    }

    static class DoWhileOperationProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(final ExtensionContext context) throws Exception {
            return Stream.of(
                    arguments(of(
                            "var i = 0",
                            "do {",
                            "   println (i)",
                            "}",
                            "while ( i++ < 5 )",
                            "println ('after do while')"
                    ), of(0, 1, 2, 3, 4, 5, "after do while")),
                    arguments(of(
                            "var i = 0",
                            "do {",
                            "   println (i++)",
                            "}",
                            "while ( i < 5 )",
                            "println ('after do while')"
                   ), of(0, 1, 2, 3, 4, "after do while"))
            );
        }
    }
}
