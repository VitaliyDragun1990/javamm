
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

package com.revenat.javamm.compiler.integration.function;

import com.revenat.javamm.code.fragment.ByteCode;
import com.revenat.javamm.compiler.integration.AbstractIntegrationTest;
import com.revenat.juinit.addons.ReplaceCamelCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static java.util.List.of;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("a function reader")
public class FunctionReader_Expected_Success_IntegrationTest extends AbstractIntegrationTest {

    static Stream<Arguments> validSourceLineProvider() {
        return Stream.of(
            arguments(of(
                "function main() {",

                "}"
            )),
            arguments(of(
                "function factorial(a) {",

                "}"
            )),
            arguments(of(
                "function sum(a, b) {",

                "}"
            )),
            arguments(of(
                "function sum(a, b, c, d, e) {",

                "}"
            ))
        );
    }

    @ParameterizedTest
    @MethodSource("validSourceLineProvider")
    void shouldReadCorrectFunctionDefinitions(final List<String> lines) {
        final ByteCode byteCode = assertDoesNotThrow(() -> compile(lines));

        assertContainSingleFunctionDefinition(byteCode);
    }

    private void assertContainSingleFunctionDefinition(final ByteCode byteCode) {
        assertThat(byteCode.getAllFunctions().size(), is(1));
    }
}
