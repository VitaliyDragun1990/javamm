
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

package com.revenat.javamm.interpreter.component;

import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.interpreter.model.CurrentRuntime;
import com.revenat.javamm.interpreter.model.LocalContext;

/**
 * Manages some phases of {@linkplain LocalContext local context} life cycle
 *
 * @author Vitaliy Dragun
 *
 */
public interface LocalContextManager {

    /**
     * Creates brand new {@linkplain LocalContext local context} and sets it into
     * {@linkplain CurrentRuntime current runtime} as currently active. If there was
     * some local context before this method execution, all its content will be
     * discarded.
     *
     * @param sourceLine {@linkplain SourceLine source line} which will be set as
     *                   current for {@linkplain CurrentRuntime current runtime}
     */
    void setNewLocalContext(SourceLine sourceLine);

    /**
     * First, creates brand new {@linkplain LocalContext local context} and sets it
     * into {@linkplain CurrentRuntime current runtime} as currently active. Then
     * executes given {@code action} and sets back previous local context. New
     * separate local context doesn't inherit any content from previous one before
     * being set, neither old local context gets any content from new separate one
     * when such old local context returns back again. Some optional preset on newly
     * created separate local context can be done via given {@code optionalPreset}
     * parameter before such separate local context will be set and given
     * {@code action} will be executed.
     *
     * @param optionalPreset optional preset that can be done on new separate local
     *                       context before it will be set
     * @param action         action that will be executed during new separate local
     *                       context is set as currently active for current runtime
     * @param sourceLine     {@linkplain SourceLine source line} which will be set
     *                       as current for {@linkplain CurrentRuntime current
     *                       runtime} while new separate local context is active.
     */
    Object executeInSeparateLocalContext(LocalContextPreset optionalPreset,
                                         LocalContextAction action,
                                         SourceLine sourceLine);


    @FunctionalInterface
    public interface LocalContextPreset {
        void apply(LocalContext localContext);
    }

    @FunctionalInterface
    public interface LocalContextAction {
        Object execute();
    }
}
