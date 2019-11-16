
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

import static org.junit.jupiter.api.Assertions.*;
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
public class ForOperationInterpreterIntegrationTest extends AbstractIntegrationTest {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();

    private static final long INFINITE_LOOP_TIMEOUT_IN_MILLISECONDS = 500;

    @AfterAll
    static void cleanup() {
        EXECUTOR_SERVICE.shutdownNow();
    }

    @ParameterizedTest
    @ArgumentsSource(ForOperationProvider.class)
    @Order(1)
    void shouldInterpretForOperation(final List<String> lines,
                                     final List<Object> expectedOutput) {
        assertDoesNotThrow(() -> {
            runBlock(lines);
            assertExpectedOutput(expectedOutput);
        });
    }

    @Test
    @Order(2)
    void shouldFailIfForOperationConditionEvaluatesToNonBooleanResult() {
        final List<String> forWithInvalidCondition = of(
                "for ( var i = 0 ; 8 + 10 ; i ++ ) {",

                "}"
        );

        final JavammRuntimeError e = assertThrows(JavammRuntimeError.class, () -> runBlock(forWithInvalidCondition));

        assertErrorMessageContains(e,buildErrorMsg("Condition expression should be boolean. Current type is integer", 2));
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
                "for ( ; " + condition + " ; ) {",
                "}"
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

    @Test
    @Order(4)
    void shouldCreateNewScopeForLocalVariable() {
        final List<String> lines = of(
                "for ( var i = 0 ; i < 5 ; i++) {",
                "   println (i)",
                "}",
                "println (i)"
        );

        final JavammRuntimeError e = assertThrows(JavammRuntimeError.class, () -> runBlock(lines));

        assertErrorMessageContains(e, buildErrorMsg("Variable 'i' is not defined", 5));
        assertExpectedOutput(of(0, 1, 2, 3, 4));
    }

    static class ForOperationProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(final ExtensionContext context) throws Exception {
            return Stream.of(
                    arguments(of(
                            "for ( var i = 0 ; i < 5 ; i ++ ) {",
                            "   println (i)",
                            "}",
                            "println ('after for')"
                    ), of(0, 1, 2, 3, 4, "after for")),
                    arguments(of(
                            "for ( var i = 0 ; i < 5 ; ) {",
                            "   println (i++)",
                            "}",
                            "println ('after for')"
                   ), of(0, 1, 2, 3, 4, "after for")),
                    arguments(of(
                            "var i = 0",
                            "for ( ; i < 5 ; i = i + 1 ) {",
                            "   println (i)",
                            "}",
                            "println ('after for')"
                   ), of(0, 1, 2, 3, 4, "after for")),
                    arguments(of(
                            "var i",
                            "for ( i = (5 - 3) * (2 - 2); i < 5 ; i = i + (5 - 4) * (3 - 2)) {",
                            "   println (i)",
                            "}",
                            "println ('after for')"
                   ), of(0, 1, 2, 3, 4, "after for")),
                    arguments(of(
                            "var condition = true",
                            "for ( var i = 0 ; condition ; ++ i) {",
                            "   println (i)",
                            "   if (i == 4) {",
                            "       condition = false",
                            "   }",
                            "}",
                            "println ('after for')"
                   ), of(0, 1, 2, 3, 4, "after for")),
                    arguments(of(
                            "var i = 0",
                            "for ( final j = 5 ; i < 5 ; ++ i) {",
                            "   println (j)",
                            "}",
                            "println ('after for')"
                   ), of(5, 5, 5, 5, 5, "after for")),
                    arguments(of(
                            "var i = 0",
                            "for ( println (i) ; i < 5 ; println (i ++) ) {",
                            "}",
                            "println ('after for')"
                   ), of(0, 0, 1, 2, 3, 4, "after for"))
            );
        }
    }
}
