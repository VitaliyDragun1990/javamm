
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

import com.revenat.javamm.code.fragment.Variable;
import com.revenat.javamm.interpreter.error.JavammRuntimeError;

/**
 * Represents container for local variable, which is used to set value for
 * final/variable, get value for final/variable and check whether specified
 * variable/final is already defined in this local context.
 *
 * @author Vitaliy Dragun
 *
 */
public interface LocalContext {

    /**
     * Sets value for final. Effectively define final in this local context.
     *
     * @throws JavammRuntimeError if another final or variable with same name has
     *                            already been defined in this local context
     */
    void setFinalValue(Variable variable, Object value);

    /**
     * Define new local variable or sets new value for already defined local
     * variable.
     *
     * @throws JavammRuntimeError if there has already been defined final with same
     *                            name in this local context
     */
    void setVariableValue(Variable variable, Object value);

    /**
     * Checks whether variable/final with such name has already been defined in this local
     * context.
     */
    boolean isVariableDefined(Variable variable);

    /**
     * Returns value for variable with such name from this local context
     *
     * @throws JavammRuntimeError if there is no variable with such name in this
     *                            local context.
     */
    Object getVariableValue(Variable variable);
}
