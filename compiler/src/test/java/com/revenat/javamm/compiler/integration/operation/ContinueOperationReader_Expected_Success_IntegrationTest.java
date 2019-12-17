
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

import com.revenat.javamm.code.fragment.Operation;
import com.revenat.javamm.code.fragment.operation.ContinueOperation;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static java.util.List.of;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * @author Vitaliy Dragun
 */
public class ContinueOperationReader_Expected_Success_IntegrationTest
    extends AbstractOperationReaderHappyPathIntegrationTest {

    @Override
    protected Class<? extends Operation> getExpectedOperationClass() {
        return ContinueOperation.class;
    }

    @Override
    protected Stream<Arguments> validSourceLineProvider() {
        return Stream.of(
            arguments(of(
                "continue"
            ))
        );
    }
}
