
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

package com.revenat.javamm.code.test.helper;

import com.revenat.javamm.code.fragment.Variable;
import com.revenat.javamm.code.fragment.expression.VariableExpression;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

/**
 * @author Vitaliy Dragun
 */
public final class MockUtils {

    private MockUtils() {
    }

    public static VariableExpression variable(final String name) {
        final Variable variable = mock(Variable.class);
        lenient().when(variable.toString()).thenReturn(name);
        return new VariableExpression(variable);
    }
}
