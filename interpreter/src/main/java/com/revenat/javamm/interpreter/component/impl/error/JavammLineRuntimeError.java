
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
import com.revenat.javamm.interpreter.model.CurrentRuntime;
import com.revenat.javamm.interpreter.model.CurrentRuntimeProvider;
import com.revenat.javamm.interpreter.model.StackTraceItem;

import java.util.List;

import static java.lang.String.format;
import static java.lang.System.lineSeparator;
import static java.util.stream.Collectors.toUnmodifiableList;


/**
 * Represents interpreter runtime error
 *
 * @author Vitaliy Dragun
 */
public class JavammLineRuntimeError extends JavammRuntimeError {
    private static final long serialVersionUID = -2098638364439526984L;

    private final transient List<StackTraceItem> currentStackTrace;

    public JavammLineRuntimeError(final String message) {
        super(message);
        this.currentStackTrace = getCurrentRuntime().getCurrentStackTrace();
    }

    public JavammLineRuntimeError(final String template, final Object... args) {
        this(format(template, args));
    }

    private static CurrentRuntime getCurrentRuntime() {
        return CurrentRuntimeProvider.getCurrentRuntime();
    }

    @Override
    public String getMessage() {
        return format("Runtime error: %s%s%s",
            super.getMessage(),
            System.lineSeparator(),
            buildStackTrace()
        );
    }

    @Override
    public List<StackTraceItem> getCurrentStackTrace() {
        return currentStackTrace;
    }

    private String buildStackTrace() {
        return String.join(lineSeparator(),
            currentStackTrace.stream()
                .map(this::toStackTraceString)
                .collect(toUnmodifiableList())
        );
    }

    private String toStackTraceString(final StackTraceItem item) {
        return format("    at %s [%s:%s]", item.getFunction(), item.getModuleName(), item.getSourceLineNumber());
    }
}
