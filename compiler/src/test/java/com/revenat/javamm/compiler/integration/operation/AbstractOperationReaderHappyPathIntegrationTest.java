
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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import com.revenat.javamm.code.fragment.ByteCode;
import com.revenat.javamm.code.fragment.Operation;
import com.revenat.javamm.compiler.integration.AbstractIntegrationTest;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.provider.Arguments;

/**
 * @author Vitaliy Dragun
 *
 */
public abstract class AbstractOperationReaderHappyPathIntegrationTest extends AbstractIntegrationTest {

    @TestFactory
    @Order(1)
    @SuppressWarnings("unchecked")
    protected Stream<DynamicTest> shouldCompileTheCodeSuccessfully() {
        return validSourceLineProvider().map(args -> {
           final List<String> lines = (List<String>) args.get()[0];
           return createDynamicTest(lines);
        });
    }

    private DynamicTest createDynamicTest(final List<String> lines) {
        final String testName = String.join(" ", lines);

        return dynamicTest(testName, testBody(lines));
    }

    private Executable testBody(final List<String> lines) {
        return () -> {
            final ByteCode byteCode = assertDoesNotThrow(() -> wrapInsideMainFunctionAndCompile(lines, true));
            final List<Class<? extends Operation>> actualOperations = getCompiledOperations(byteCode);
            assertSingleOperationOfExpectedClass(actualOperations);
        };
    }

    private void assertSingleOperationOfExpectedClass(final List<Class<? extends Operation>> actualOperations) {
        assertThat("Invalid operation model class", actualOperations, equalTo(List.of(getExpectedOperationClass())));
    }

    private List<Class<? extends Operation>> getCompiledOperations(final ByteCode byteCode) {
        return byteCode.getMainFunction().orElseThrow().getBody().getOperations().stream()
                .map(Operation::getClass)
                .collect(toList());
    }

    protected abstract Class<? extends Operation> getExpectedOperationClass();

    protected abstract Stream<Arguments> validSourceLineProvider();
}
