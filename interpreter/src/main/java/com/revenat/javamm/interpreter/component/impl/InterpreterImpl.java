
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

import com.revenat.javamm.code.fragment.ByteCode;
import com.revenat.javamm.interpreter.Interpreter;
import com.revenat.javamm.interpreter.component.FunctionInvoker;
import com.revenat.javamm.interpreter.component.FunctionInvokerBuilder;
import com.revenat.javamm.interpreter.component.RuntimeBuilder;
import com.revenat.javamm.interpreter.model.CurrentRuntime;
import com.revenat.javamm.interpreter.model.CurrentRuntimeProvider;

import static java.util.Objects.requireNonNull;

/**
 * Simple interpreter
 *
 * @author Vitaliy Dragun
 */
public class InterpreterImpl implements Interpreter {

    private final FunctionInvokerBuilder functionInvokerBuilder;

    private final RuntimeBuilder runtimeBuilder;

    public InterpreterImpl(final FunctionInvokerBuilder blockOperationInterpreter,
                           final RuntimeBuilder runtimeBuilder) {
        this.functionInvokerBuilder = requireNonNull(blockOperationInterpreter);
        this.runtimeBuilder = requireNonNull(runtimeBuilder);
    }

    @Override
    public void interpret(final ByteCode byteCode) {
        final FunctionInvoker functionInvoker = functionInvokerBuilder.build(byteCode);
        setCurrentRuntime(runtimeBuilder.buildCurrentRuntime(functionInvoker));
        try {
            functionInvoker.invokeMain();
        } finally {
            releaseCurrentRuntime();
        }
    }

    private void setCurrentRuntime(final CurrentRuntime currentRuntime) {
        CurrentRuntimeProvider.setCurrentRuntime(currentRuntime);
    }

    private void releaseCurrentRuntime() {
        CurrentRuntimeProvider.releaseCurrentRuntime();
    }
}
