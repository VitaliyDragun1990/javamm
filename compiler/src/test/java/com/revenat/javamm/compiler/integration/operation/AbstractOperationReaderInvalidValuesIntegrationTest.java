
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.revenat.javamm.compiler.error.JavammSyntaxError;
import com.revenat.javamm.compiler.integration.AbstractIntegrationTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static com.revenat.javamm.compiler.test.helper.CustomAsserts.assertErrorMessageContains;

import static java.lang.String.format;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.provider.Arguments;

/**
 * @author Vitaliy Dragun
 *
 */
public abstract class AbstractOperationReaderInvalidValuesIntegrationTest extends AbstractIntegrationTest {
    private final int[] nameIndex = new int[1];
    private final Set<String> uniqueNames = new HashSet<>();

    @TestFactory
    @Order(1)
    @SuppressWarnings("unchecked")
    protected Stream<DynamicTest> shouldThrowSyntaxError() {

        return invalidSourceLineProvider().map(args -> {
            final List<String> lines = (List<String>) args.get()[0];
            final String expectedMessage = (String) args.get()[1];
            final String subName = getSubName(uniqueNames, args);
            final List<String> invalidLines = getInvalidLines(lines);

            return dynamicTest(
                    buildTestName(subName, lines, expectedMessage),
                    buildTestBody(invalidLines, expectedMessage));
        });

    }

    private String buildTestName(final String subName, final List<String> lines, final String expectedMessage) {
        return format("%s :: %s -> '%s'", subName, String.join(" ", lines), expectedMessage);
    }

    private Executable buildTestBody(final List<String> invalidLines, final String expectedMessage) {
        return () -> {
            final JavammSyntaxError e = assertThrows(JavammSyntaxError.class, () ->  compile(invalidLines));
            assertErrorMessageContains(e, expectedMessage);
        };
    }

    private List<String> getInvalidLines(final List<String> lines) {
        final List<String> invalidLines = new ArrayList<>();
        invalidLines.add(""); // TODO add function declaration
        invalidLines.addAll(lines);
        return invalidLines;
    }

    private String getSubName(final Set<String> names, final Arguments args) {
        final Optional<String> name = findName(args);
        dropIndexIfNewName(name);
        return name.isPresent() ? format(" [%s-%s]", name.get(), getNameIndex()) : "";
    }

    private Optional<String> findName(final Arguments args) {
        return Optional.ofNullable(args.get().length == 3 ? String.valueOf(args.get()[2]) : null);
    }

    private int getNameIndex() {
        return ++nameIndex[0];
    }

    private void dropIndexIfNewName(final Optional<String> name) {
        if (name.isPresent()) {
            if (uniqueNames.add(name.get())) {
                nameIndex[0] = 0;
            }
        }
    }

    protected abstract Stream<Arguments> invalidSourceLineProvider();

    protected final Stream<Arguments> named(final Stream<Arguments> stream, final String name) {
        return stream.map(arguments -> addNameToExistingArguments(name, arguments));
    }

    private Arguments addNameToExistingArguments(final String name, final Arguments arguments) {
        final List<Object> args = new ArrayList<>(Arrays.asList(arguments.get()));
           args.add(name.toUpperCase());
           return arguments(args.toArray());
    }
}
