
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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
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
class IfElseOperationInterpreterIntegrationTest extends AbstractIntegrationTest {

    @ParameterizedTest
    @ArgumentsSource(IfElseOperationProvider.class)
    @Order(1)
    void shouldInterpretIfElseOperationCorrectly(final List<String> lines, final List<Object> expectedOutput) {
        assertInterpretCorrectly(lines, expectedOutput);
    }

    @Test
    @Order(2)
    void shouldFailWhenIfElseOperationConditionEvaluatesToNonBooleanResult() {
        final List<String> ifElseWithInvalidCondition = of(
                "if ( 8 + 10 ) {",

                "}"
        );

        final JavammRuntimeError e = assertThrows(JavammRuntimeError.class, () -> runBlock(ifElseWithInvalidCondition));

        assertErrorMessageContains(e, buildErrorMsg("Condition expression should be boolean. Current type is integer", 2));
    }

    private void assertInterpretCorrectly(final List<String> lines, final List<Object> expectedOutput) {
        assertDoesNotThrow(() -> {
            runBlock(lines);
            assertThat(getOutput(), equalTo(expectedOutput));
        });
    }

    static class IfElseOperationProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(final ExtensionContext context) throws Exception {
            return Stream.of(
                    // 1 condition is true -> evaluates true block
                    arguments(of(
                            "if ( 1 < 100 ) {",
                            "   println ('case 1: Inside if')",
                            "}"
                            ), of("case 1: Inside if")),
                    // 2 condition is false -> no false block -> empty output
                    arguments(of(
                            "if ( 2 < -100 ) {",
                            "   println ('cate 2: Inside if')",
                            "}"
                            ), of()),
                    // 3 condition is false -> false block present -> evaluates false block
                    arguments(of(
                            "if ( 2 < -100 ) {",
                            "   println ('cate 3: Inside if')",
                            "}",
                            "else {",
                            "   println ('case 3: Inside else')",
                            "}"
                            ), of("case 3: Inside else"))
                  );
        }
    }
}
