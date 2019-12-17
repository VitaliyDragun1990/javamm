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

package com.revenat.javamm.code.fragment.function;

import com.revenat.javamm.code.fragment.FunctionName;
import com.revenat.juinit.addons.ReplaceCamelCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("an abstract function")
@ExtendWith(MockitoExtension.class)
class AbstractFunctionTest {

    @Test
    @Order(1)
    void canNotBeCreatedWithoutFunctionName() {
        assertThrows(NullPointerException.class, () -> new TestAbstractFunction(null));
    }

    @Test
    @Order(2)
    void shouldProvideFunctionName(@Mock FunctionName functionName) {
        final TestAbstractFunction abstractFunction = new TestAbstractFunction(functionName);

        assertThat(abstractFunction.getName(), equalTo(functionName));
    }

    @Test
    @Order(3)
    void shouldReturnStringRepresentationOfItsName(@Mock FunctionName functionName) {
        when(functionName.toString()).thenReturn("some name");

        final TestAbstractFunction abstractFunction = new TestAbstractFunction(functionName);

        assertThat(abstractFunction.toString(), equalTo("some name"));
    }

    private static class TestAbstractFunction extends AbstractFunction {

        protected TestAbstractFunction(final FunctionName name) {
            super(name);
        }
    }
}