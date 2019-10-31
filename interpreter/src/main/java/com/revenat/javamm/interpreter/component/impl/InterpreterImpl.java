
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
import com.revenat.javamm.interpreter.component.BlockOperationInterpreter;
import com.revenat.javamm.interpreter.component.LocalContextBuilder;
import com.revenat.javamm.interpreter.component.RuntimeBuilder;
import com.revenat.javamm.interpreter.model.CurrentRuntime;
import com.revenat.javamm.interpreter.model.LocalContext;

import static com.revenat.javamm.interpreter.model.CurrentRuntimeProvider.releaseCurrentRuntime;
import static com.revenat.javamm.interpreter.model.CurrentRuntimeProvider.setCurrentRuntime;

import static java.util.Objects.requireNonNull;

/**
 * Simple interpreter
 *
 * @author Vitaliy Dragun
 *
 */
public class InterpreterImpl implements Interpreter {
    private final BlockOperationInterpreter blockOperationInterpreter;

    private final RuntimeBuilder runtimeBuilder;

    private final LocalContextBuilder localContextBuilder;

    public InterpreterImpl(final BlockOperationInterpreter blockOperationInterpreter,
                           final RuntimeBuilder runtimeBuilder,
                           final LocalContextBuilder localContextBuilder) {
        this.blockOperationInterpreter = requireNonNull(blockOperationInterpreter);
        this.runtimeBuilder = requireNonNull(runtimeBuilder);
        this.localContextBuilder = requireNonNull(localContextBuilder);
    }

    @Override
    public void interpret(final ByteCode byteCode) {
        final CurrentRuntime currentRuntime = runtimeBuilder.buildCurrentRuntime();
        setCurrentRuntime(currentRuntime);

        final LocalContext localContext = localContextBuilder.buildLocalContext();
        currentRuntime.setCurrentLocalContext(localContext);

        try {
            blockOperationInterpreter.interpret(byteCode.getCode());
        } finally {
            releaseCurrentRuntime();
        }
    }
}
