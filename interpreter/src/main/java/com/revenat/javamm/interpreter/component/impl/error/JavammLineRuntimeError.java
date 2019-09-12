
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

package com.revenat.javamm.interpreter.component.impl.error;

import com.revenat.javamm.interpreter.error.JavammRuntimeError;

import java.util.Objects;

import static com.revenat.javamm.interpreter.model.CurrentRuntimeProvider.getCurrentRuntime;

import static java.lang.String.format;


/**
 * Represents interpreter runtime error
 *
 * @author Vitaliy Dragun
 *
 */
public class JavammLineRuntimeError extends JavammRuntimeError {
    private static final long serialVersionUID = -2098638364439526984L;

    public JavammLineRuntimeError(final String message) {
        super(buildErrorMessage(Objects.requireNonNull(message)));
    }

    public JavammLineRuntimeError(final String template, final Object...args) {
        this(format(template, args));
    }

    private static String buildErrorMessage(final String message) {
        return format("Runtime error in '%s' [Line: %s]: %s",
                getCurrentRuntime().getCurrentModuleName(),
                getCurrentRuntime().getCurrentSourceLine().getLineNumber(),
                message);
    }
}
