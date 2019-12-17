
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

import com.revenat.javamm.compiler.error.JavammSyntaxError;
import com.revenat.javamm.compiler.integration.AbstractIntegrationTest;
import com.revenat.juinit.addons.ReplaceCamelCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static com.revenat.javamm.compiler.test.helper.CustomAsserts.assertErrorMessageContains;
import static java.util.List.of;
import static java.util.function.Function.identity;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("a function reader")
public class FunctionReader_Expected_Fail_IntegrationTest extends AbstractIntegrationTest {

    static Stream<Arguments> invalidSourceLineProvider() {
        return Stream.of(
            functionHeader(),
            functionName(),
            functionParameters(),
            functionBody()
        ).flatMap(identity());
    }

    private static Stream<Arguments> functionHeader() {
        return Stream.of(
            arguments(of(
                "fun"
            ), "Syntax error in 'module1' [Line: 1]: 'function' expected at the beginning of the line"),
            arguments(of(
                "function"
            ), "Syntax error in 'module1' [Line: 1]: '{' expected at the end of the line"),
            arguments(of(
                "function {"
            ), "Syntax error in 'module1' [Line: 1]: Missing '('"),
            arguments(of(
                "function ( {"
            ), "Syntax error in 'module1' [Line: 1]: Function name expected between 'function' and '('"),
            arguments(of(
                "function ( ) {"
            ), "Syntax error in 'module1' [Line: 1]: Function name expected between 'function' and '('"),
            arguments(of(
                "function ( test {"
            ), "Syntax error in 'module1' [Line: 1]: Function name expected between 'function' and '('"),
            arguments(of(
                "function test test ( {"
            ), "Syntax error in 'module1' [Line: 1]: '(' expected after function name"),
            arguments(of(
                "function test ( {"
            ), "Syntax error in 'module1' [Line: 1]: ')' expected before '{'")
        );
    }

    private static Stream<Arguments> functionName() {
        return Stream.of(
            arguments(of(
                "function var ( ) {"
            ), "Syntax error in 'module1' [Line: 1]: The keyword 'var' can not be used as a function name"),
            arguments(of(
                "function 1function ( ) {"
            ), "Syntax error in 'module1' [Line: 1]: The function name must start with letter: '1function'"),
            arguments(of(
                "function _function ( ) {"
            ), "Syntax error in 'module1' [Line: 1]: The function name must start with letter: '_function'"),
            arguments(of(
                "function $function ( ) {"
            ), "Syntax error in 'module1' [Line: 1]: The function name must start with letter: '$function'")
        );
    }

    private static Stream<Arguments> functionParameters() {
        return Stream.of(
            arguments(of(
                "function test (a, b, c, d, e, g, h, i, k, l) {"
            ), "Syntax error in 'module1' [Line: 1]: Max allowed function parameter count is 5"),
            arguments(of(
                "function test (a++) {"
            ), "Syntax error in 'module1' [Line: 1]: Expressions not allowed here"),
            arguments(of(
                "function test (a,,b) {"
            ), "Syntax error in 'module1' [Line: 1]: Missing value or redundant ','"),
            arguments(of(
                "function test (var) {"
            ), "Syntax error in 'module1' [Line: 1]: The keyword 'var' can not be used as a variable name"),
            arguments(of(
                "function test (1) {"
            ), "Syntax error in 'module1' [Line: 1]: The variable name must start with letter: '1'"),
            arguments(of(
                "function test ($var) {"
            ), "Syntax error in 'module1' [Line: 1]: The variable name must start with letter: '$var'"),
            arguments(of(
                "function test (_var) {"
            ), "Syntax error in 'module1' [Line: 1]: The variable name must start with letter: '_var'")
        );
    }

    private static Stream<Arguments> functionBody() {
        return Stream.of(
            arguments(of(
                "function test () {"
            ), "Syntax error in 'module1': '}' expected to close block statement at the end of file"));
    }

    @ParameterizedTest
    @MethodSource("invalidSourceLineProvider")
    void shouldFailToReadInvalidFunctionDefinition(final List<String> lines, final String expectedMessage) {
        final JavammSyntaxError e = assertThrows(JavammSyntaxError.class, () -> compile(lines));

        assertErrorMessageContains(e, expectedMessage);
    }
}
