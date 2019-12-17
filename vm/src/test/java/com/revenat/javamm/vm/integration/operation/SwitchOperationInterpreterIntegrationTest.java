
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

import com.revenat.javamm.vm.integration.AbstractIntegrationTest;
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

import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("A javamm virtual machine interpreter")
public class SwitchOperationInterpreterIntegrationTest extends AbstractIntegrationTest {

    @ParameterizedTest
    @ArgumentsSource(SwitchOperationProvider.class)
    void shouldInterpretSwitchOperation(final List<String> lines, final List<Object> expectedOutput) {
        assertDoesNotThrow(() -> {
            runBlock(lines);
            assertExpectedOutput(expectedOutput);
        });
    }

    static class SwitchOperationProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(final ExtensionContext context) {
            return Stream.of(
                // 1 ------------------------------------------
                arguments(of(
                    "// TestCase 1: empty switch block",
                    "var a = 0",
                    "switch ( a ) {",

                    "}",
                    "println ('after switch')"
                ), of("after switch")),
                // 2 ------------------------------------------
                arguments(of(
                    "// TestCase 2: no case match, no default",
                    "var a = 0",
                    "switch ( a ) {",
                    "   case 3 : {",
                    "       println (3)",
                    "       break",
                    "   }",
                    "   case 1 : {",
                    "       println (1)",
                    "       break",
                    "   }",
                    "   case 2 : {",
                    "       println (2)",
                    "       break",
                    "   }",
                    "}",
                    "println ('after switch')"
                ), of("after switch")),
                // 3 ------------------------------------------
                arguments(of(
                    "// TestCase 3: no cases, with default",
                    "var a = 0",
                    "switch ( a ) {",
                    "   default : {",
                    "       println ('default')",
                    "   }",
                    "}",
                    "println ('after switch')"
                ), of("default", "after switch")),
                // 4 ------------------------------------------
                arguments(of(
                    "// TestCase 4: case match with break",
                    "var a = 1",
                    "switch ( a ) {",
                    "   case 3 : {",
                    "       println (3)",
                    "       break",
                    "   }",
                    "   case 1 : {",
                    "       println (1)",
                    "       break",
                    "   }",
                    "   case 2 : {",
                    "       println (2)",
                    "       break",
                    "   }",
                    "   default : {",
                    "       println ('default')",
                    "       break",
                    "   }",
                    "}",
                    "println ('after switch')"
                ), of(1, "after switch")),
                // 5 ------------------------------------------
                arguments(of(
                    "// TestCase 5: no case match with default",
                    "var a = 8",
                    "switch ( a ) {",
                    "   case 3 : {",
                    "       println (3)",
                    "       break",
                    "   }",
                    "   case 1 : {",
                    "       println (1)",
                    "       break",
                    "   }",
                    "   case 2 : {",
                    "       println (2)",
                    "       break",
                    "   }",
                    "   default : {",
                    "       println ('default')",
                    "       break",
                    "   }",
                    "}",
                    "println ('after switch')"
                ), of("default", "after switch")),
                // 6 ------------------------------------------
                arguments(of(
                    "// TestCase 6: case match, without breaks, with default",
                    "var a = 3",
                    "switch ( a ) {",
                    "   case 3 : {",
                    "       println (3)",
                    "   }",
                    "   case 1 : {",
                    "       println (1)",
                    "   }",
                    "   case 2 : {",
                    "       println (2)",
                    "   }",
                    "   default : {",
                    "       println ('default')",
                    "       break",
                    "   }",
                    "}",
                    "println ('after switch')"
                ), of(3, 1, 2, "default", "after switch")),
                // 7 ------------------------------------------
                arguments(of(
                    "// TestCase 7: case match without break, with break in last case, with default",
                    "var a = 3",
                    "switch ( a ) {",
                    "   case 3 : {",
                    "       println (3)",
                    "   }",
                    "   case 1 : {",
                    "       println (1)",
                    "   }",
                    "   case 2 : {",
                    "       println (2)",
                    "       break",
                    "   }",
                    "   default : {",
                    "       println ('default')",
                    "       break",
                    "   }",
                    "}",
                    "println ('after switch')"
                ), of(3, 1, 2, "after switch")),
                // 8 ------------------------------------------
                arguments(of(
                    "// TestCase 8: case match without break, with break in next case, with default",
                    "var a = 3",
                    "switch ( a ) {",
                    "   case 3 : {",
                    "       println (3)",
                    "   }",
                    "   case 1 : {",
                    "       println (1)",
                    "       break",
                    "   }",
                    "   case 2 : {",
                    "       println (2)",
                    "   }",
                    "   default : {",
                    "       println ('default')",
                    "       break",
                    "   }",
                    "}",
                    "println ('after switch')"
                ), of(3, 1, "after switch")),
                // 9 ------------------------------------------
                arguments(of(
                    "// TestCase 9: case match without break, with default",
                    "var a = 3",
                    "switch ( a ) {",
                    "   case 3 : {",
                    "       println (3)",
                    "       while (true) {",
                    "           break",
                    "       }",
                    "   }",
                    "   default : {",
                    "       println ('default')",
                    "   }",
                    "}",
                    "println ('after switch')"
                ), of(3, "default", "after switch")),
                // 10 ------------------------------------------
                arguments(of(
                    "// TestCase 10: no match cases, default first without break",
                    "var a = 8",
                    "switch ( a ) {",
                    "   default : {",
                    "       println ('default')",
                    "   }",
                    "   case 3 : {",
                    "       println (3)",
                    "   }",
                    "   case 1 : {",
                    "       println (1)",
                    "   }",
                    "}",
                    "println ('after switch')"
                ), of("default", 3, 1, "after switch"))
            );
        }
    }
}
