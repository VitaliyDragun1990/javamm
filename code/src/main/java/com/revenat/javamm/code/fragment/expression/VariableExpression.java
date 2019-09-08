
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

package com.revenat.javamm.code.fragment.expression;

import com.revenat.javamm.code.fragment.UpdatableExpression;
import com.revenat.javamm.code.fragment.Variable;

import java.util.Objects;

/**
 * Represents any variable
 *
 * @author Vitaliy Dragun
 *
 */
public class VariableExpression implements UpdatableExpression {

    private final Variable variable;

    public VariableExpression(final Variable variable) {
        this.variable = Objects.requireNonNull(variable);
    }

    public Variable getVariable() {
        return variable;
    }

    @Override
    public String toString() {
        return variable.toString();
    }
}
