
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

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * @author Vitaliy Dragun
 *
 */
public class JavammStructRuntimeError extends JavammRuntimeError {

    private static final long serialVersionUID = 1L;

    public JavammStructRuntimeError(final String message, final Object...args) {
        super(buildErrorMessage(format(requireNonNull(message), args)));
    }

    private static String buildErrorMessage(final String message) {
        return format("Runtime error: %s", message);
    }
}
