
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

package com.revenat.javamm.interpreter.test.doubles;

import com.revenat.javamm.code.fragment.Variable;
import com.revenat.javamm.interpreter.model.LocalContext;

public class LocalContextStub implements LocalContext {
    private Object variableValue = null;
    private boolean isVariableDefined = false;

    public void setVariableDefined(final boolean isVariableDefined) {
        this.isVariableDefined = isVariableDefined;
    }

    public void setVariableValue(final Object value) {
        this.variableValue = value;
    }

    @Override
    public void setFinalValue(final Variable variable, final Object value) {
    }

    @Override
    public void setVariableValue(final Variable variable, final Object value) {
    }

    @Override
    public boolean isVariableDefined(final Variable variable) {
        return isVariableDefined;
    }

    @Override
    public Object getVariableValue(final Variable variable) {
        return variableValue;
    }

    @Override
    public LocalContext createChildLocalContext() {
        // TODO Auto-generated method stub
        return null;
    }
}