
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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.revenat.javamm.compiler.test.doubles.VariableDummy;
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

    @Test
    @Order(5)
    void shouldBuildEqualVariablesForEqualNames() {
        String name = "test";

        final Variable varA = variableBuilder.build(name, DUMMY_SOURCE_LINE);
        final Variable varB = variableBuilder.build(name, DUMMY_SOURCE_LINE);

        assertEquals(varA, varB);
        assertEquals(varA, varA);
        assertEquals(varB, varB);
        assertEquals(varA.hashCode(), varB.hashCode());
    }

    @Test
    @Order(6)
    void shouldBuildDifferentVariablesForDifferentNames() {
        final Variable varA = variableBuilder.build("testA", DUMMY_SOURCE_LINE);
        final Variable varB = variableBuilder.build("testB", DUMMY_SOURCE_LINE);

        assertNotEquals(varA, varB);
        assertNotEquals(varA.hashCode(), varB.hashCode());
    }

    @Test
    @Order(7)
    void builtVariableNotEqualToVariableNotBuiltByAnotherMeans() {
        Variable varNull = null;
        Variable varBuiltByOtherMeans = new VariableDummy();
        Variable variable = variableBuilder.build("test", DUMMY_SOURCE_LINE);

        assertNotEquals(variable, varNull);
        assertNotEquals(variable, varBuiltByOtherMeans);
    }

    @Test
    @Order((8))
    void shouldBuildVariableWhichCanBeComparedByTheirNames() {
        String nameA = "nameA";
        String nameB = "nameB";

        Variable varA = variableBuilder.build(nameA, DUMMY_SOURCE_LINE);
        Variable varB = variableBuilder.build(nameB, DUMMY_SOURCE_LINE);
        Variable varC = variableBuilder.build(nameA, DUMMY_SOURCE_LINE);

        assertThat(varA.compareTo(varC), is(equalTo(0)));
        assertThat(varA.compareTo(varB), is(lessThan(0)));
        assertThat(varB.compareTo(varC), is(greaterThan(0)));
    }

}
