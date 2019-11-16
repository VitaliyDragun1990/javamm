
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
import com.revenat.javamm.code.fragment.function.DeveloperFunction;
import com.revenat.javamm.interpreter.component.FunctionInvoker;
import com.revenat.javamm.interpreter.component.impl.model.StackTraceItemImpl;
import com.revenat.javamm.interpreter.model.CurrentRuntime;
import com.revenat.javamm.interpreter.model.LocalContext;
import com.revenat.javamm.interpreter.model.StackTraceItem;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;

/**
 * @author Vitaliy Dragun
 *
 */
public class CurrentRuntimeImpl implements CurrentRuntime {

    private SourceLine currentSourceLine;

    private LocalContext currentLocalContext;

    private final FunctionInvoker functionInvoker;

    private final Deque<StackTraceItem> currentStackTrace = new ArrayDeque<>();

    private DeveloperFunction currentFunction;

    public CurrentRuntimeImpl(final FunctionInvoker functionInvoker) {
        this.functionInvoker = requireNonNull(functionInvoker);
    }

    @Override
    public String getCurrentModuleName() {
        return currentSourceLine.getModuleName();
    }

    @Override
    public SourceLine getCurrentSourceLine() {
        return requireNonNull(currentSourceLine, "currentSourceLine is not set");
    }

    @Override
    public void setCurrentSourceLine(final SourceLine currentSourceLine) {
        this.currentSourceLine = requireNonNull(currentSourceLine, "currentSourceLine can not be null");
    }

    @Override
    public LocalContext getCurrentLocalContext() {
        return requireNonNull(currentLocalContext, "currentLocalContext is not set");
    }

    @Override
    public void setCurrentLocalContext(final LocalContext currentLocalContext) {
        this.currentLocalContext = requireNonNull(currentLocalContext, "currentLocalContext can not be null");
    }

    @Override
    public FunctionInvoker getCurrentFunctionInvoker() {
        return functionInvoker;
    }

    @Override
    public void enterToFunction(final DeveloperFunction function) {
        if (currentFunction != null) {
            currentStackTrace.push(new StackTraceItemImpl(currentFunction, currentSourceLine));
        }
        currentFunction = function;
        setCurrentSourceLine(function.getDeclarationSourceLine());
    }

    @Override
    public void exitFromFunction() {
        final StackTraceItem item = currentStackTrace.poll();
        if (item != null) {
            currentFunction = item.getFunction();
            setCurrentSourceLine(item.getSourceLine());
        } else {
            currentFunction = null;
        }
    }

    @Override
    public List<StackTraceItem> getCurrentStackTrace() {
        final List<StackTraceItem> stackTrace = new ArrayList<>();
        if (currentFunction != null) {
            stackTrace.add(new StackTraceItemImpl(currentFunction, currentSourceLine));
        }
        stackTrace.addAll(currentStackTrace);
        return unmodifiableList(stackTrace);
    }
}
