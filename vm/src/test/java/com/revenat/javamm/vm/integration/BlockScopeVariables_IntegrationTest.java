
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

package com.revenat.javamm.vm.integration;

import com.revenat.javamm.interpreter.error.JavammRuntimeError;
import com.revenat.juinit.addons.ReplaceCamelCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;
import java.util.stream.Stream;

import static com.revenat.javamm.vm.helper.CustomAsserts.assertErrorMessageContains;
import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("an interpreter")
public class BlockScopeVariables_IntegrationTest extends AbstractIntegrationTest {

    @ParameterizedTest
    @ArgumentsSource(BlockScopeProvider.class)
    @DisplayName("should throw runtime error if try to access variable defined inside inner scope from outside")
    void shouldThrowRuntimeError(final List<String> lines,
                                 final List<Object> expectedOutput,
                                 final String expectedErrorMessage) {
        final JavammRuntimeError e = assertThrows(JavammRuntimeError.class, () -> runBlock(lines));

        assertErrorMessageContains(e, expectedErrorMessage);
        assertExpectedOutput(expectedOutput);

    }

    static class BlockScopeProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(final ExtensionContext context) {
            return Stream.of(
                // 1 ------------------------------------
                arguments(of(
                    "var a = 3",
                    "{",
                    "   var b = 5",
                    "   println (b)",
                    "}",
                    "println (a)",
                    "println (b)"
                ), of(5, 3), buildErrorMsg("Variable 'b' is not defined", 8)),
                // 2 ------------------------------------
                arguments(of(
                    "var a = 3",
                    "{",
                    "   var b = 5",
                    "   println (a)", // Read 'a' from parent block
                    "   println (b)",
                    "}",
                    "println (a)",
                    "println (b)"
                ), of(3, 5, 3), buildErrorMsg("Variable 'b' is not defined", 9)),
                // 3 ------------------------------------
                arguments(of(
                    "var a = 3",
                    "{",
                    "   var b = 5",
                    "   println (a ++)", // Update 'a' from parent block
                    "   println (b)",
                    "}",
                    "println (a)",
                    "println (b)"
                ), of(3, 5, 4), buildErrorMsg("Variable 'b' is not defined", 9))
            );
        }
    }
}
