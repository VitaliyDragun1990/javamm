
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

import static java.util.Objects.requireNonNull;

/**
 * Responsible for storing {@linkplain CurrentRuntime current runtime} in order
 * to obtain access to it from every point in the application where it may be
 * needed.
 *
 * @author Vitaliy Dragun
 *
 */
public final class CurrentRuntimeProvider {
    private static final ThreadLocal<CurrentRuntime> CURRENT_RUNTIME_THREAD_LOCAL = new ThreadLocal<>();

    private CurrentRuntimeProvider() {
    }

    /**
     * Returns {@linkplain CurrentRuntime current runtime} instance managed by this
     * provider
     *
     * @throws NullPointerException if current runtime has not been set yet.
     */
    public static CurrentRuntime getCurrentRuntime() {
        return requireNonNull(CURRENT_RUNTIME_THREAD_LOCAL.get(), "CurrentRuntime is not set");
    }

    /**
     * Makes provided {@linkplain CurrentRuntime currentRuntime} instance managed by
     * this provided. Can be obtained by by calling
     * {@link CurrentRuntimeProvider#getCurrentRuntime()} method.
     *
     * @throws NullPointerException if specified {@code currentRuntime} is
     *                              {@code null}
     */
    public static void setCurrentRuntime(final CurrentRuntime currentRuntime) {
        CURRENT_RUNTIME_THREAD_LOCAL.set(requireNonNull(currentRuntime, "CurrentRuntime can not be null"));
    }

    /**
     * Release {@linkplain CurrentRuntime current runtime} instance managed by this
     * provider if any
     */
    public static void releaseCurrentRuntime() {
        CURRENT_RUNTIME_THREAD_LOCAL.remove();
    }
}
