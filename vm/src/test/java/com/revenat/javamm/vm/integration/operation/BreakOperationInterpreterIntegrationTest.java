
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

import com.revenat.javamm.interpreter.error.JavammRuntimeError;
import com.revenat.javamm.vm.integration.AbstractIntegrationTest;
import com.revenat.juinit.addons.ReplaceCamelCase;
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

import java.util.List;
import java.util.stream.Stream;

import static com.revenat.javamm.vm.helper.CustomAsserts.assertErrorMessageContains;
import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("a Javamm virtual machine interpreter")
public class BreakOperationInterpreterIntegrationTest extends AbstractIntegrationTest {

    @ParameterizedTest
    @ArgumentsSource(BreakOperationProvider.class)
    @Order(1)
    void shouldInterpretBreakOperation(final List<String> lines,
                                       final List<Object> expectedOutput) {
        assertDoesNotThrow(() -> {
            runBlock(lines);
            assertExpectedOutput(expectedOutput);
        });
    }

    @Test
    @Order(2)
    void shouldFailIfBreakOperationOutsideLoopBody() {
        final List<String> breakOutsideLoopBody = of(
            "break"
        );

        final JavammRuntimeError e = assertThrows(JavammRuntimeError.class, () -> runBlock(breakOutsideLoopBody));

        assertErrorMessageContains(e, buildErrorMsg("Operation 'break' is not expected here", 2));
    }

    static final class BreakOperationProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(final ExtensionContext context) {
            return Stream.of(
                arguments(of(
                    "var i = 0",
                    "while ( i ++ < 5 ) {",
                    "   if ( i > 2 ) {",
                    "       break",
                    "   }",
                    "   println (i)",
                    "}",
                    "println ('after while')"
                ), of(1, 2, "after while")),
                arguments(of(
                    "var i = 0",
                    "do {",
                    "   if ( i > 2 ) {",
                    "       break",
                    "   }",
                    "   println (i)",
                    "}",
                    "while ( i ++ < 5 )",
                    "println ('after do while')"
                ), of(0, 1, 2, "after do while")),
                arguments(of(
                    "var i = 0",
                    "for ( ; i < 5 ; i ++ ) {",
                    "   if ( i > 2 ) {",
                    "       break",
                    "   }",
                    "   println (i)",
                    "}",
                    "println ('after for')"
                ), of(0, 1, 2, "after for")),
                // ------------ Interrupts infinite loop
                arguments(of(
                    "var i = 0",
                    "while ( true ) {",
                    "   if ( i > 2 ) {",
                    "       break",
                    "   }",
                    "   println (i ++)",
                    "}",
                    "println ('after while')"
                ), of(0, 1, 2, "after while")),
                arguments(of(
                    "var i = 0",
                    "do {",
                    "   if ( i > 2 ) {",
                    "       break",
                    "   }",
                    "   println (i ++)",
                    "}",
                    "while ( true )",
                    "println ('after do while')"
                ), of(0, 1, 2, "after do while")),
                arguments(of(
                    "var i = 0",
                    "for ( ; ; i++ ) {",
                    "   if ( i > 2 ) {",
                    "       break",
                    "   }",
                    "   println (i)",
                    "}",
                    "println ('after for')"
                ), of(0, 1, 2, "after for"))
            );
        }
    }
}
