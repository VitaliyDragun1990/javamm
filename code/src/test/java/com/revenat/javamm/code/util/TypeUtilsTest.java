/*
 *
 *  Copyright (c) 2019. http://devonline.academy
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.revenat.javamm.code.util;

import com.revenat.juinit.addons.ReplaceCamelCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("type utils")
class TypeUtilsTest {

    @Test
    @Order(1)
    void shouldReturnTheNameOfTheGivenType() {
        String expectedName = TestClass.class.getSimpleName().toLowerCase();

        assertThat(TypeUtils.getType(TestClass.class), equalTo(expectedName));
    }

    @Test
    @Order(2)
    void shouldReturnTheNameOfTheGivenObjectType() {
        String expectedName = TestClass.class.getSimpleName().toLowerCase();

        assertThat(TypeUtils.getType(new TestClass()), equalTo(expectedName));
    }

    @Test
    @Order(3)
    void shouldReturnStringNullIfSpecifiedTypeIsNull() {
        String expectedName = "null";

        assertThat(TypeUtils.getType(null), equalTo(expectedName));
        assertThat(TypeUtils.getType((TestClass) null), equalTo(expectedName));
    }

    @Test
    @Order(4)
    void shouldReturnTrueIfProvidedValuesAllHaveSpecifiedType() {
        assertTrue(TypeUtils.confirmType(TestClass.class, new TestClass(), new TestClass()));
    }

    @Test
    @Order(5)
    void shouldReturnFalseIfAnyProvidedValueDoesNotHaveSpecifiedType() {
        assertFalse(TypeUtils.confirmType(TestClass.class, new TestClass(), new Object()));
    }

    private static class TestClass {
    }
}