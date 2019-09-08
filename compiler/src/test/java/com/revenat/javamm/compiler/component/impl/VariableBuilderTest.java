
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

package com.revenat.javamm.compiler.component.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.Variable;
import com.revenat.javamm.compiler.component.VariableBuilder;
import com.revenat.javamm.compiler.error.JavammSyntaxError;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.revenat.juinit.addons.ReplaceCamelCase;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("a variable builder")
class VariableBuilderTest {
    private static final SourceLine DUMMY_SOURCE_LINE = SourceLine.EMPTY_SOURCE_LINE;

    private VariableBuilder variableBuilder;

    @BeforeEach
    void setUp() {
        variableBuilder = new VariableBuilderImpl();
    }

    private void assertValidName(final String name) {
        assertTrue(variableBuilder.isValid(name));
    }

    private void assertInvalidName(final String name) {
        assertFalse(variableBuilder.isValid(name));
    }

    @ParameterizedTest
    @ValueSource(strings = {"test", "a123", "a"})
    @Order(1)
    void shouldDetermineIfVariableNameIsValid(final String name) {
        assertValidName(name);
    }

    @ParameterizedTest
    @ValueSource(strings = {"_test", "1a", "null", "integer"})
    @Order(2)
    void shouldDetermineIfVariableNameIsInvalid(final String name) {
        assertInvalidName(name);
    }

    @ParameterizedTest
    @ValueSource(strings = {"test", "a123", "a"})
    @Order(3)
    void canBuildNewVariableWithValidName(final String name) {
        final Variable variable = variableBuilder.build(name, DUMMY_SOURCE_LINE);
        assertThat(variable.getName(), is(name));
    }

    @ParameterizedTest
    @ValueSource(strings = {"_test", "1a", "null", "integer"})
    @Order(4)
    void shouldFailToBuildVariableWithInvalidName(final String name) {
        assertThrows(JavammSyntaxError.class, () -> variableBuilder.build(name, DUMMY_SOURCE_LINE));
    }
}
