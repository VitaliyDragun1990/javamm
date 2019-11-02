
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

package com.revenat.javamm.compiler.integration.operation;

import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.List.of;

import org.junit.jupiter.params.provider.Arguments;

/**
 * @author Vitaliy Dragun
 *
 */
public class ForOperationReader_Expected_Fail_IntegrationTest
        extends AbstractOperationReaderInvalidValuesIntegrationTest {

    @Override
    protected Stream<Arguments> invalidSourceLineProvider() {
        return Stream.of(
              blockValidation(),
              expressionValidation(),
              forValidation(),
              semicolonValidation()
        ).flatMap(Function.identity());
    }

    private Stream<Arguments> blockValidation() {
        return named(Stream.of(
                arguments(
                        of(
                         "for"
                        ), "Syntax error in 'module1' [Line: 2]: '{' expected at the end of the line"),
                arguments(
                        of(
                                "for ( var i = 0; i < 10 ; i++ )"
                         ), "Syntax error in 'module1' [Line: 2]: '{' expected at the end of the line"),
                arguments(
                        of(
                                "for ( var i = 0; i < 10 ; i++ ) {"
                         ), "Syntax error in 'module1': '}' expected to close block statement at the end of file"),
                arguments(
                        of(
                                "for ( var i = 0; i < 10 ; i++ ) {",

                                "} var a = 5"
                         ), "Syntax error in 'module1' [Line: 3]: '}' expected only")
        ), "block");
    }

    private Stream<Arguments> expressionValidation() {
        return named(Stream.of(
                arguments(
                        of(
                                "for ( ) {"
                         ), "Syntax error in 'module1' [Line: 2]: An expression is expected between '(' and ')'")
                ), "expression");
    }

    private Stream<Arguments> forValidation() {
        return named(Stream.of(
                arguments(
                        of(
                                "for {"
                        ), "Syntax error in 'module1' [Line: 2]: '(' expected after 'for'"),
                arguments(
                        of(
                                "for var i = 0 ; i < 10 ; i ++ {"
                        ), "Syntax error in 'module1' [Line: 2]: '(' expected after 'for'"),
                arguments(
                        of(
                                "for var i = 0 ; i < 10 ; i ++ ) {"
                        ), "Syntax error in 'module1' [Line: 2]: '(' expected after 'for'"),
                arguments(
                        of(
                                "for ( var i = 0 ; i < 10 ; i ++ {"
                        ), "Syntax error in 'module1' [Line: 2]: ')' expected before '{'"),
                arguments(
                        of(
                                "for ) var i = 0 ; i < 10 ; i ++ ( {"
                        ), "Syntax error in 'module1' [Line: 2]: '(' expected after 'for'"),
                arguments(
                        of(
                                "for a ( var i = 0 ; i < 10 ; i ++ ) {"
                         ), "Syntax error in 'module1' [Line: 2]: '(' expected after 'for'"),
                arguments(
                        of(
                                "for this true ( var i = 0 ; i < 10 ; i ++ ) {"
                        ), "Syntax error in 'module1' [Line: 2]: '(' expected after 'for'"),
                arguments(
                        of(
                                "for ( var i = 0 ; i < 10 ; i ++ ) then {"
                        ), "Syntax error in 'module1' [Line: 2]: ')' expected before '{'"),
                arguments(
                        of(
                                "for ( var i = 0 ; i < 10 ; i ++ ) then should be {"
                                ), "Syntax error in 'module1' [Line: 2]: ')' expected before '{'")
                ), "for");
    }

    private Stream<Arguments> semicolonValidation() {
        return named(Stream.of(
                arguments(
                        of(
                                "for ( var i = 0 , i < 10 , i ++ ) {"
                        ), "Syntax error in 'module1' [Line: 2]: Missing ';'"),
                arguments(
                        of(
                                "for ( var i = 0 , i < 10 ; i ++ ) {"
                        ), "Syntax error in 'module1' [Line: 2]: Missing ';'"),
                arguments(
                        of(
                                "for ( var i = 0 ; i < 10 , i ++ ) {"
                        ), "Syntax error in 'module1' [Line: 2]: Missing ';'"),
                arguments(
                        of(
                                "for ( var i = 0 ; i < 10 ; i ++ ; ) {"
                        ), "Syntax error in 'module1' [Line: 2]: Redundant ';'")
                ), "semicolon");
    }
}
