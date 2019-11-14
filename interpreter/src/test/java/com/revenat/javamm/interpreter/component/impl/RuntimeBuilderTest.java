
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

package com.revenat.javamm.interpreter.component.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.revenat.javamm.interpreter.component.FunctionInvoker;
import com.revenat.javamm.interpreter.component.RuntimeBuilder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.revenat.juinit.addons.ReplaceCamelCase;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("a runtime builder")
@ExtendWith(MockitoExtension.class)
class RuntimeBuilderTest {
    private RuntimeBuilder runtimeBuilder;

    @BeforeEach
    void setUp() {
        runtimeBuilder = new RuntimeBuilderImpl();
    }

    @Disabled
    @Test
    @Order(1)
    void shouldBuildInstanceOfLocalContext() {
//        assertNotNull(runtimeBuilder.buildLocalContext());
    }

    @Test
    @Order(2)
    void shouldBuildInstanceOfCurrentRuntime(@Mock final FunctionInvoker functionInvoker) {
        assertNotNull(runtimeBuilder.buildCurrentRuntime(functionInvoker));
    }
}
