
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

import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.interpreter.component.FunctionInvoker;
import com.revenat.javamm.interpreter.model.CurrentRuntime;
import com.revenat.javamm.interpreter.model.LocalContext;

public class CurrentRuntimeDummy implements CurrentRuntime {

    @Override
    public String getCurrentModuleName() {
        return null;
    }

    @Override
    public SourceLine getCurrentSourceLine() {
        return null;
    }

    @Override
    public void setCurrentSourceLine(final SourceLine currentSourceLine) {
    }

    @Override
    public LocalContext getCurrentLocalContext() {
        return null;
    }

    @Override
    public void setCurrentLocalContext(final LocalContext localContext) {
    }

    @Override
    public FunctionInvoker getCurrentFunctionInvoker() {
        // TODO Auto-generated method stub
        return null;
    }
}