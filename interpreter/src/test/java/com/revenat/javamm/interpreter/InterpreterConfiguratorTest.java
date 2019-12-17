
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

package com.revenat.javamm.interpreter;

import com.revenat.juinit.addons.ReplaceCamelCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("a interpreter configurator")
class InterpreterConfiguratorTest {

    private final InterpreterConfigurator interpreterConfigurator = new InterpreterConfigurator();

    @Test
    @Order(1)
    void shouldProvideWithInterpreter() {
        final Interpreter interpreter = interpreterConfigurator.getInterpreter();

        assertNotNull(interpreter);
    }

    @Test
    @Order(2)
    void shouldEachTimeReturnTheSameInterpreter() {
        final Interpreter interpreterA = interpreterConfigurator.getInterpreter();
        final Interpreter interpreterB = interpreterConfigurator.getInterpreter();

        assertThat(interpreterA, sameInstance(interpreterB));
    }
}
