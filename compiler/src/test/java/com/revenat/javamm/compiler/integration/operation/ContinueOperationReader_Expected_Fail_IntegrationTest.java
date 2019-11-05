
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

import java.util.stream.Stream;

import static java.util.List.of;

import org.junit.jupiter.params.provider.Arguments;

/**
 * @author Vitaliy Dragun
 *
 */
public class ContinueOperationReader_Expected_Fail_IntegrationTest
        extends AbstractOperationReaderInvalidValuesIntegrationTest {

    @Override
    protected Stream<Arguments> invalidSourceLineProvider() {
        return Stream.of(
                arguments(
                        of(
                                "continue }"
                        ), "Syntax error in 'module1' [Line: 2]: 'continue' expected only"),
                arguments(
                        of(
                                "continue loop"
                ), "Syntax error in 'module1' [Line: 2]: 'continue' expected only")
        );
    }

}