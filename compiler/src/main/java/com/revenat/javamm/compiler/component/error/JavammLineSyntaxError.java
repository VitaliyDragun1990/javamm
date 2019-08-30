
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

package com.revenat.javamm.compiler.component.error;

import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.compiler.JavammSyntaxError;

import static java.util.Objects.requireNonNull;

/**
 * Signals that particular source line contains syntax error
 *
 * @author Vitaliy Dragun
 *
 */
public final class JavammLineSyntaxError extends JavammSyntaxError {
    private static final long serialVersionUID = -529150170431648757L;

    public JavammLineSyntaxError(final String message, final SourceLine sourceLine) {
        super(String.format("Syntax error in '%s' [Line: %s]: %s",
                sourceLine.getModuleName(), sourceLine.getLineNumber(), requireNonNull(message)));
    }
}
