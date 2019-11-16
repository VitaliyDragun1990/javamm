
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

package com.revenat.javamm.interpreter.component.impl.model;

import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.function.DeveloperFunction;
import com.revenat.javamm.interpreter.model.StackTraceItem;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * @author Vitaliy Dragun
 *
 */
public class StackTraceItemImpl implements StackTraceItem {

    private final DeveloperFunction function;

    private final SourceLine invokeSourceLine;

    public StackTraceItemImpl(final DeveloperFunction function, final SourceLine invokeSourceLine) {
        this.function = requireNonNull(function);
        this.invokeSourceLine = requireNonNull(invokeSourceLine);
    }

    @Override
    public DeveloperFunction getFunction() {
        return function;
    }

    @Override
    public String getModuleName() {
        return invokeSourceLine.getModuleName();
    }

    @Override
    public int getSourceLineNumber() {
        return invokeSourceLine.getLineNumber();
    }

    @Override
    public SourceLine getSourceLine() {
        return invokeSourceLine;
    }

    @Override
    public String toString() {
        return format("at %s [%s:%s]", function, getModuleName(), getSourceLineNumber());
    }
}
