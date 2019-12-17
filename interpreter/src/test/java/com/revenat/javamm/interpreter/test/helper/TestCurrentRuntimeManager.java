
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

package com.revenat.javamm.interpreter.test.helper;

import com.revenat.javamm.code.fragment.Operation;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.function.DeveloperFunction;
import com.revenat.javamm.interpreter.component.FunctionInvoker;
import com.revenat.javamm.interpreter.model.CurrentRuntime;
import com.revenat.javamm.interpreter.model.CurrentRuntimeProvider;
import com.revenat.javamm.interpreter.model.LocalContext;
import com.revenat.javamm.interpreter.model.StackTraceItem;
import com.revenat.javamm.interpreter.test.doubles.LocalContextSpy;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vitaliy Dragun
 */
public final class TestCurrentRuntimeManager {
    private static final LocalContextSpy localContext = new LocalContextSpy();

    private static FakeCurrentRuntime currentRuntime;

    public static void releaseFakeCurrentRuntime() {
        CurrentRuntimeProvider.releaseCurrentRuntime();
        currentRuntime = null;
    }

    public static void refreshLocalContext() {
        localContext.refresh();
    }

    public static void refreshFakeCurrentRuntime() {
        currentRuntime.refresh();
    }

    public static LocalContextSpy getLocalContextSpy() {
        return localContext;
    }

    public static FakeCurrentRuntime getFakeCurrentRuntime() {
        return currentRuntime;
    }

    public static void setFakeCurrentRuntime(final SourceLine sourceLine) {
        currentRuntime = new FakeCurrentRuntime(localContext, sourceLine);
        CurrentRuntimeProvider.setCurrentRuntime(currentRuntime);
    }

    public static class FakeCurrentRuntime implements CurrentRuntime {
        private final List<Operation> processedOperations = new ArrayList<>();

        private LocalContext localContext;

        private SourceLine sourceLine;

        private FakeCurrentRuntime(final LocalContext localContext, final SourceLine sourceLine) {
            this.localContext = localContext;
            this.sourceLine = sourceLine;
        }

        public List<Operation> getProcessedOperations() {
            return List.copyOf(processedOperations);
        }

        public void refresh() {
            processedOperations.clear();
        }

        @Override
        public SourceLine getCurrentSourceLine() {
            return sourceLine;
        }

        @Override
        public void setCurrentSourceLine(final SourceLine currentSourceLine) {
            sourceLine = currentSourceLine;
        }

        @Override
        public LocalContext getCurrentLocalContext() {
            return localContext;
        }

        @Override
        public void setCurrentLocalContext(final LocalContext localContext) {
            this.localContext = localContext;
        }

        @Override
        public void setCurrentOperation(final Operation operation) {
            processedOperations.add(operation);
            CurrentRuntime.super.setCurrentOperation(operation);
        }

        @Override
        public FunctionInvoker getCurrentFunctionInvoker() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void enterToFunction(final DeveloperFunction developerFunction) {
            // TODO Auto-generated method stub

        }

        @Override
        public void exitFromFunction() {
            // TODO Auto-generated method stub

        }

        @Override
        public List<StackTraceItem> getCurrentStackTrace() {
            return List.of();
        }
    }
}
