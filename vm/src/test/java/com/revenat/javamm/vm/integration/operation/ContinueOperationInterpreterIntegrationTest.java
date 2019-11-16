
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
import java.util.stream.Stream;

import static com.revenat.javamm.vm.helper.CustomAsserts.assertErrorMessageContains;

import static java.util.List.of;

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

import com.revenat.juinit.addons.ReplaceCamelCase;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("a Javamm virtual machine interpreter")
public class ContinueOperationInterpreterIntegrationTest extends AbstractIntegrationTest {

    @ParameterizedTest
    @ArgumentsSource(ContinueOperationProider.class)
    @Order(1)
    void shouldInterpretContinueOperation(final List<String> lines,
                                          final List<Object> expectedOutput) {
        assertDoesNotThrow(() -> {
            runBlock(lines);
            assertExpectedOutput(expectedOutput);
        });
    }

    @Test
    @Order(2)
    void shouldFailIfContinueOperationOutsideLoopBody() {
        final List<String> continueOutsideLoopBody = of(
                "continue"
        );

        final JavammRuntimeError e = assertThrows(JavammRuntimeError.class, () -> runBlock(continueOutsideLoopBody));

        assertErrorMessageContains(e, buildErrorMsg("Operation 'continue' is not expected here", 2));
    }

    static final class ContinueOperationProider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(final ExtensionContext context) throws Exception {
            return Stream.of(
                    arguments(of(
                            "var i = 0",
                            "while ( i ++ < 5 ) {",
                            "   println (i)",
                            "   if ( i > 2 ) {",
                            "       continue",
                            "   }",
                            "   println (i)",
                            "}",
                            "println ('after while')"
                    ), of(1, 1, 2, 2, 3, 4, 5, "after while")),
                    arguments(of(
                            "var i = 0",
                            "do {",
                            "   println (i)",
                            "   if ( i > 2 ) {",
                            "       continue",
                            "   }",
                            "   println (i)",
                            "}",
                            "while ( i ++ < 5 )",
                            "println ('after do while')"
                  ), of(0, 0, 1, 1, 2, 2, 3, 4, 5, "after do while")),
                    arguments(of(
                            "var i = 0",
                            "for ( ; i < 5 ; i ++ ) {",
                            "   println (i)",
                            "   if ( i > 2 ) {",
                            "       continue",
                            "   }",
                            "   println (i)",
                            "}",
                            "println ('after for')"
                  ), of(0, 0, 1, 1, 2, 2, 3, 4, "after for"))
            );
        }
    }
}
