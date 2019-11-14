
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

import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.interpreter.component.LocalContextBuilder;
import com.revenat.javamm.interpreter.component.LocalContextManager;
import com.revenat.javamm.interpreter.model.CurrentRuntime;
import com.revenat.javamm.interpreter.model.CurrentRuntimeProvider;
import com.revenat.javamm.interpreter.model.LocalContext;

/**
 *
 * @author Vitaliy Dragun
 *
 */
public class LocalContextManagerImpl implements LocalContextManager {

    private final LocalContextBuilder localContextBuilder;

    public LocalContextManagerImpl(final LocalContextBuilder localContextBuilder) {
        this.localContextBuilder = localContextBuilder;
    }

    @Override
    public void setNewLocalContext(final SourceLine sourceLine) {
        final CurrentRuntime currentRuntime = getCurrentRuntime();
        final LocalContext localContext = localContextBuilder.buildLocalContext();

        currentRuntime.setCurrentSourceLine(sourceLine);
        currentRuntime.setCurrentLocalContext(localContext);
    }

    @Override
    public Object executeInSeparateLocalContext(final LocalContextPreset optionalPreset,
                                                final LocalContextAction action,
                                                final SourceLine sourceLine) {

        final CurrentRuntime currentRuntime = getCurrentRuntime();
        final SourceLine currentLine = currentRuntime.getCurrentSourceLine();
        final LocalContext currentLocalContext = currentRuntime.getCurrentLocalContext();

        final LocalContext separateLocalContext = localContextBuilder.buildLocalContext();
        optionalPreset.apply(separateLocalContext);
        try {
            currentRuntime.setCurrentSourceLine(sourceLine);
            currentRuntime.setCurrentLocalContext(separateLocalContext);
            return action.execute();
        } finally {
            currentRuntime.setCurrentLocalContext(currentLocalContext);
            currentRuntime.setCurrentSourceLine(currentLine);
        }
    }

    private CurrentRuntime getCurrentRuntime() {
        return CurrentRuntimeProvider.getCurrentRuntime();
    }
}
