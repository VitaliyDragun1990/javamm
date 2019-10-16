
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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.revenat.javamm.code.fragment.SourceCode;
import com.revenat.javamm.vm.VirtualMachine;
import com.revenat.javamm.vm.VirtualMachineBuilder;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.function.Function.identity;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
public class ComplexExpressionEvaluationIntegrationTest {

    private final PrintStream originalOutputStream = System.out;

    private final SpyPrintStream testOutputStream = new SpyPrintStream();

    private final VirtualMachine vm = new VirtualMachineBuilder().build();

    @BeforeEach
    public void setupSpyOutput() {
        System.setOut(testOutputStream);
    }

    @AfterEach
    void setUpOriginalOutput() {
        System.setOut(originalOutputStream);
    }

    @ParameterizedTest
    @ArgumentsSource(ValidComplexExpressionProvider.class)
    @Order(1)
    void shouldEvaluateComplexExpressions(final String expression, final Object expectedResult) {
        vm.run(sourceCodeFrom(expression));

        assertEvaluationResult(expectedResult);
    }

    private void assertEvaluationResult(final Object expectedResult) {
        assertThat(testOutputStream.getResult(), equalTo(expectedResult));

    }

    private SourceCode sourceCodeFrom(final String expression) {
        return new TestSourceCode(expression);
    }

    static final class ValidComplexExpressionProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(final ExtensionContext context) {
            return Stream.of(
                    arithmeticExpressionsProvider(),
                    logicalAndPredicateExpressionProvider(),
                    bitwiseExpressionProvider(),
                    ternaryExpressionProvider(),
                    complexExpressionProvider()
                    ).flatMap(identity());
        }

        private Stream<Arguments> arithmeticExpressionsProvider() {
            return Stream.of(
                    arguments(
                            "1 + 2 * ( 3 - 5 )",
                            1 + 2 * (3 - 5)
                            ),
                    arguments(
                            "5 - 2 * 2 - 1",
                            5 - 2 * 2 - 1
                            ),
                    arguments(
                            "1 + 2 - 3 * 4 / 5 % 6",
                            1 + 2 - 3 * 4 / 5 % 6
                            ),
                    arguments(
                            "( ( 1 + 2 ) - ( 3 * 4 ) ) / ( 5 % 6 )",
                            ((1 + 2) - (3 * 4)) / (5 % 6)
                            ),
                    arguments(
                            "+ 4 - - 5 * + 6 / - 7",
                            + 4 - - 5 * + 6 / - 7
                            )
                    );
        }

        @SuppressWarnings("unused")
        private Stream<Arguments> logicalAndPredicateExpressionProvider() {
            return Stream.of(
                    arguments(
                            "true && false || ! true && ! false",
                            true && false || !true && !false
                            ),
                    arguments(
                            "! ( ( true || false ) && ( ! true || ! false ) )",
                            !((true || false) && (!true || !false))
                            ),
                    arguments(
                            "1 > 2 && 1 < 2",
                            1 > 2 && 1 < 2
                            ),
                    arguments(
                            "1 >= 2 && 1 <= 2",
                            1 >= 2 && 1 <= 2
                            ),
                    arguments(
                            "1 != 2 && 1 == 2",
                            1 != 2 && 1 == 2
                        ),
                   arguments(
                            "1 > 2 || 1 < 2",
                            1 > 2 || 1 < 2
                        ),
                   arguments(
                            "1 >= 2 || 1 <= 2",
                            1 >= 2 || 1 <= 2
                        ),
                   arguments(
                            "1 != 2 || 1 == 2",
                            1 != 2 || 1 == 2
                        )
                    );
        }

        private Stream<Arguments> bitwiseExpressionProvider() {
            return Stream.of(
                    arguments(
                        "56 & 45 | 12 | ~ 2",
                        56 & 45 | 12 | ~2
                    ),
                    arguments(
                        "56 & 45 | 12",
                        56 & 45 | 12
                    ),
                    arguments(
                        "56 & 45 | 12 & ~ 1",
                        56 & 45 | 12 & ~1
                    ),
                    arguments(
                        "56 >> 2 ^ 12 << 2 ^ ~ 1",
                        56 >> 2 ^ 12 << 2 ^ ~1
                    ),
                    arguments(
                        "56 >> 2 | 12 << 2 | ~ 1",
                        56 >> 2 | 12 << 2 | ~1
                    )
                );
        }

        @SuppressWarnings("unused")
        private Stream<Arguments> ternaryExpressionProvider() {
            return Stream.of(
                    arguments(
                            "true ? 10 : 20",
                            true ? 10 : 20
                            ),
                    arguments(
                            "false ? 20 : 10",
                            false ? 20 : 10
                            ),
                    arguments(
                            "false ? true ? false : true : false ? false : true",
                            false ? true ? false : true : false ? false : true
                            ),
                    arguments(
                            "( ( ( false ? ( true ? ( false ) : ( true ) ) : ( false ? false : true ) ) ) )",
                            ( ( ( false ? ( true ? ( false ) : ( true ) ) : ( false ? false : true ) ) ) )
                            )
                    );
        }

        @SuppressWarnings("unused")
        private Stream<Arguments> complexExpressionProvider() {
            return Stream.of(
                    arguments(
                        "~ - 1 ^ ~ + - 2 | + - + - 3 & ~ + - + - + - + - 8",
                        ~-1 ^ ~+-2 | +-+-3 & ~+-+-+-+-8
                    ),
                    arguments(
                        "( ( ( ( 1 + 2 ) - ( 3 * 4 ) ) / ( 5 % 6 ) ^ 23 ) | 234567 ) ^ ( 56 >> 2 | 12 << 2 | ~ 1 )",
                        ((((1 + 2) - (3 * 4)) / (5 % 6) ^ 23) | 234567) ^ (56 >> 2 | 12 << 2 | ~1)
                    ),
                    arguments(
                        "( ( 5 & 4 | 8 & ~ + 1 ) >> 4 ) * ( ( ( 1 + 2 ) - ( 3 * 4 ) ) << 3 ) - ( ( ( 5 % 6 ) ^ 1 ) )",
                        ((5 & 4 | 8 & ~+1) >> 4) * (((1 + 2) - (3 * 4)) << 3) - (((5 % 6) ^ 1))
                    ),
                    arguments(
                            "( ( 5 & 4 | 8 & ~ + 1 ) >> 4 ) * ( 10 > 20 ? 10 + ( 255 * 20 ) : ( 100 % 2 >= 0 ? 100 : 255 ) )",
                            ( ( 5 & 4 | 8 & ~ + 1 ) >> 4 ) * ( 10 > 20 ? 10 + ( 255 * 20 ) : ( 100 % 2 >= 0 ? 100 : 255 ) )
                            )
                );
        }
    }

    public static final class TestSourceCode implements SourceCode {
        private final String expression;

        public TestSourceCode(final String expression) {
            this.expression = expression;
        }

        @Override
        public String getModuleName() {
            return "test";
        }

        @Override
        public List<String> getLines() {
            return List.of(format("println ( %s )", expression));
        }
    }

    public static class SpyPrintStream extends PrintStream {
        private Object result;

        public SpyPrintStream() {
            super(new ByteArrayOutputStream());
        }

        public Object getResult() {
            return result;
        }

        @Override
        public void println(final Object x) {
            result = x;
        }
    }
}
