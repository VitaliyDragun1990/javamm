
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

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.revenat.javamm.code.fragment.Operation;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.interpreter.component.FunctionInvoker;
import com.revenat.javamm.interpreter.model.CurrentRuntime;
import com.revenat.javamm.interpreter.model.LocalContext;
import com.revenat.javamm.interpreter.test.doubles.LocalContextDummy;

import org.junit.jupiter.api.BeforeEach;
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
@DisplayName("a current runtime")
@ExtendWith(MockitoExtension.class)
class CurrentRuntimeTest {

    private static final int ANY_MAX_STACK_SIZE = 10;
    private static final SourceLine CURRENT_SOURCE_LINE = SourceLine.EMPTY_SOURCE_LINE;
    private static final LocalContext DUMMY_LOCAL_CONTEXT = new LocalContextDummy();
    private static final Operation OPERATION_STUB = () -> CURRENT_SOURCE_LINE;

    @Mock
    private FunctionInvoker functionInvoker;

    private CurrentRuntime runtime;

    @BeforeEach
    void setUp() {
        runtime = new CurrentRuntimeImpl(functionInvoker, ANY_MAX_STACK_SIZE);
    }

    @Test
    @Order(2)
    void shouldFailToReturnCurrentSourceLineIfNotDefined() {
        assertThrows(NullPointerException.class, () -> runtime.getCurrentSourceLine());
    }

    @Test
    @Order(3)
    void shouldFailToReturnCurrentLocalContextIfNotDefined() {
        assertThrows(NullPointerException.class, () -> runtime.getCurrentLocalContext());
    }

    @Test
    @Order(4)
    void shouldReturnCurrentSourceLineIfDefined() {
        runtime.setCurrentSourceLine(CURRENT_SOURCE_LINE);

        assertThat(runtime.getCurrentSourceLine(), sameInstance(CURRENT_SOURCE_LINE));
    }

    @Test
    @Order(6)
    void shouldReturnCurrentLocalContextIfDefined() {
        runtime.setCurrentLocalContext(DUMMY_LOCAL_CONTEXT);

        assertThat(runtime.getCurrentLocalContext(), sameInstance(DUMMY_LOCAL_CONTEXT));
    }

    @Test
    @Order(7)
    void shouldSetCurrentOperation() {
        runtime.setCurrentOperation(OPERATION_STUB);

        assertThat(runtime.getCurrentSourceLine(), sameInstance(OPERATION_STUB.getSourceLine()));
    }
}
