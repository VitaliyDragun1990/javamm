
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

import com.revenat.javamm.code.component.ExpressionContext;
import com.revenat.javamm.code.fragment.Expression;
import com.revenat.javamm.code.fragment.Variable;
import com.revenat.javamm.code.fragment.function.DeveloperFunction;
import com.revenat.javamm.interpreter.component.BlockOperationInterpreter;
import com.revenat.javamm.interpreter.component.DeveloperFunctionInvoker;
import com.revenat.javamm.interpreter.component.LocalContextBuilder;
import com.revenat.javamm.interpreter.component.impl.error.JavammLineRuntimeError;
import com.revenat.javamm.interpreter.component.impl.operation.exception.InterruptOperationException;
import com.revenat.javamm.interpreter.component.impl.operation.exception.ReturnOperationException;
import com.revenat.javamm.interpreter.model.CurrentRuntime;
import com.revenat.javamm.interpreter.model.CurrentRuntimeProvider;
import com.revenat.javamm.interpreter.model.LocalContext;

import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * @author Vitaliy Dragun
 */
public class DeveloperFunctionInvokerImpl implements DeveloperFunctionInvoker {

    private final LocalContextBuilder localContextBuilder;

    private final BlockOperationInterpreter blockOperationInterpreter;

    private final ExpressionContext expressionContext;

    public DeveloperFunctionInvokerImpl(final LocalContextBuilder localContextBuilder,
                                        final BlockOperationInterpreter blockOperationInterpreter,
                                        final ExpressionContext expressionContext) {
        this.localContextBuilder = requireNonNull(localContextBuilder);
        this.blockOperationInterpreter = requireNonNull(blockOperationInterpreter);
        this.expressionContext = requireNonNull(expressionContext);
    }

    @Override
    public Object invokeMain(final DeveloperFunction mainFunction) {
        final CurrentRuntime currentRuntime = getCurrentRuntime();
        final LocalContext localContext = localContextBuilder.buildLocalContext();

        try {
            currentRuntime.setCurrentLocalContext(localContext);
            currentRuntime.enterToFunction(mainFunction);
            return interpretFunctionBody(mainFunction);
        } finally {
            currentRuntime.exitFromFunction();
        }
    }

    @Override
    public Object invoke(final DeveloperFunction function, final List<Expression> arguments) {
        final CurrentRuntime currentRuntime = getCurrentRuntime();
        final LocalContext currentLocalContext = currentRuntime.getCurrentLocalContext();

        final LocalContext separateLocalContext = localContextBuilder.buildLocalContext();
        setFunctionParametersIntoLocalContext(function.getParameters(), arguments, separateLocalContext);
        try {
            currentRuntime.setCurrentLocalContext(separateLocalContext);
            currentRuntime.enterToFunction(function);
            return interpretFunctionBody(function);
        } finally {
            currentRuntime.setCurrentLocalContext(currentLocalContext);
            currentRuntime.exitFromFunction();
        }
    }

    private void setFunctionParametersIntoLocalContext(final List<Variable> parameters,
                                                       final List<Expression> arguments,
                                                       final LocalContext functionLocalContext) {
        for (int i = 0; i < parameters.size(); i++) {
            final Variable parameter = parameters.get(i);
            final Object argumentValue = arguments.get(i).getValue(expressionContext);
            functionLocalContext.setVariableValue(parameter, argumentValue);
        }
    }

    private Object interpretFunctionBody(final DeveloperFunction function) {
        try {
            blockOperationInterpreter.interpret(function.getBody());
            return com.revenat.javamm.code.fragment.Void.INSTANCE;
        } catch (final ReturnOperationException e) {
            return e.getReturnValue();
        } catch (final InterruptOperationException e) {
            throw new JavammLineRuntimeError("Operation '%s' is not expected here", e.getOperation());
        }
    }

    private CurrentRuntime getCurrentRuntime() {
        return CurrentRuntimeProvider.getCurrentRuntime();
    }
}
