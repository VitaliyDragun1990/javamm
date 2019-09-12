
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

package com.revenat.javamm.interpreter.model;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.revenat.javamm.interpreter.test.doubles.CurrentRuntimeDummy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.revenat.juinit.addons.ReplaceCamelCase;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("a current runtime provider")
class CurrentRuntimeProviderTest {
    private static final CurrentRuntime RUNTIME_DUMMY = new CurrentRuntimeDummy();

    @AfterEach
    void releaseCurrentRuntime() {
        CurrentRuntimeProvider.releaseCurrentRuntime();
    }

    @Test
    @Order(1)
    void shouldFailToProvideCurrentRuntimeIfNotSetBefore() {
        assertThrows(NullPointerException.class, () -> CurrentRuntimeProvider.getCurrentRuntime());
    }

    @Test
    @Order(2)
    void shouldProvideCurrentRuntimeIfSetBefore() {
        CurrentRuntimeProvider.setCurrentRuntime(RUNTIME_DUMMY);

        assertThat(CurrentRuntimeProvider.getCurrentRuntime(), sameInstance(RUNTIME_DUMMY));
    }

    @Test
    @Order(3)
    void shouldFailIfTryToSetNullInsteadOfCurrentRuntimeProviderInstance() {
        assertThrows(NullPointerException.class, () -> CurrentRuntimeProvider.setCurrentRuntime(null));
    }

    @Test
    @Order(4)
    void shouldReleasePreviouslySetCurrentRuntime() {
        CurrentRuntimeProvider.setCurrentRuntime(RUNTIME_DUMMY);

        CurrentRuntimeProvider.releaseCurrentRuntime();

        assertThrows(NullPointerException.class, () -> CurrentRuntimeProvider.getCurrentRuntime());
    }
}
