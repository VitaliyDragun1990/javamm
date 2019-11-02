
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

import com.revenat.javamm.code.fragment.Operation;
import com.revenat.javamm.code.fragment.operation.ForOperation;

import java.util.stream.Stream;

import static java.util.List.of;

import org.junit.jupiter.params.provider.Arguments;

/**
 * @author Vitaliy Dragun
 *
 */
public class ForOperationReader_Expected_Success_IntegrationTest
        extends AbstractOperationReaderHappyPathIntegrationTest {

    @Override
    protected Class<? extends Operation> getExpectedOperationClass() {
        return ForOperation.class;
    }

    @Override
    protected Stream<Arguments> validSourceLineProvider() {
        return Stream.of(
                //  ALL PARTS PRESENT
                arguments(of(
                        "for ( var i = 0 ; i < 10 ; i ++ ) {",

                        "}"
                )),
                arguments(of(
                        "for ( i = 0 ; i < 10 ; i ++ ) {",

                        "}"
                 )),
                arguments(of(
                        "for ( i ++ ; i < 10 ; i ++ ) {",

                        "}"
                )),
                arguments(of(
                        "for ( i = i + 1 ; i < 10 ; i = i + 1 ) {",

                        "}"
                )),
                arguments(of(
                        "for ( i = 0 ; i < 10 ; i = i + 1 ) {",

                        "}"
                )),
                arguments(of(
                        "for ( i = 0 ; ( a + 4 ) * ( b - 8 ) + 4 / ( ( 4 - c ) * 3 - d ) > 0 ; ++ i ) {",

                        "}"
                )),
                arguments(of(
                        "for ( i = 2 * ( b - 4 ) / 5 ; i < 10 ; i = 2 * ( b - 4 ) / 5 ) {",

                        "}"
                )),
                arguments(of(
                        "for (println (i) ; i < 10 ; println (i) ) {",

                        "}"
                 )),
                // SOME (ALL) PARTS ARE MISSING
                arguments(of(
                        "for ( var i = 0 ; ; ) {",

                        "}"
                )),
                arguments(of(
                        "for ( ; i < 10 ; ) {",

                        "}"
                )),
                arguments(of(
                        "for ( ; ; i ++ ) {",

                        "}"
                 )),
                arguments(of(
                        "for ( ; i < 10 ; i ++ ) {",

                        "}"
                )),
                arguments(of(
                        "for ( final i = 0 ; i < 10 ; ) {",

                        "}"
                 )),
                arguments(of(
                        "for ( ; ; ) {",

                        "}"
                 )),
                arguments(of(
                        "for (println (i) ; ; println (i)) {",

                        "}"
                 )),
                // NESTED FOR
                arguments(of(
                        "for ( var i = 0 ; i < 10 ; i ++ ) {",
                        "   for ( var j = 0 ; j < 10 ; j ++ ) {",
                        "       for ( var k = 0 ; k < 10 ; k ++ ) {",
                        "       }",
                        "   }",
                        "}"
                        ))
        );
    }

}
