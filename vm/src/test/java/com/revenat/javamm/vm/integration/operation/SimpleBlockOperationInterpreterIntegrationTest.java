
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
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.revenat.javamm.vm.integration.AbstractIntegrationTest;

import java.util.List;
import java.util.stream.Stream;

import static java.util.List.of;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
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
public class SimpleBlockOperationInterpreterIntegrationTest extends AbstractIntegrationTest {

    @ParameterizedTest
    @ArgumentsSource(BlockScopeProvider.class)
    @Order(1)
    void shouldInterpretSimpleBlockOperation(final List<String> lines, final List<Object> expectedOutput) {
        assertDoesNotThrow(() -> runBlock(lines));

        assertExpectedOutput(expectedOutput);
    }

    static class BlockScopeProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(final ExtensionContext context) throws Exception {
            return Stream.of(
                    arguments(of(
                            "{",

                            "}"
                    ), of()),
                    arguments(of(
                            "{",

                            "}",
                            "println ('after block')"
                            ), of("after block")),
                    arguments(of(
                            "var i = 10",
                            "{",
                            "   var b = i",
                            "}",
                            "println ('i = ' + i)"
                   ), of("i = 10"))
            );
        }
    }
}
