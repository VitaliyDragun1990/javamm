
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
import com.revenat.javamm.code.fragment.Expression;
import com.revenat.javamm.code.fragment.FunctionName;
import com.revenat.javamm.code.fragment.function.DeveloperFunction;
import com.revenat.javamm.interpreter.component.DeveloperFunctionInvoker;
import com.revenat.javamm.interpreter.component.FunctionInvoker;
import com.revenat.javamm.interpreter.component.FunctionInvokerBuilder;
import com.revenat.javamm.interpreter.component.impl.error.JavammLineRuntimeError;
import com.revenat.javamm.interpreter.component.impl.error.JavammStructRuntimeError;

import java.util.List;
import java.util.Optional;

import static com.revenat.javamm.code.syntax.Keywords.FUNCTION;

import static java.util.Objects.requireNonNull;

/**
 * @author Vitaliy Dragun
 *
 */
public class FunctionInvokerBuilderImpl implements FunctionInvokerBuilder {

    private final DeveloperFunctionInvoker developerFunctionInvoker;

    public FunctionInvokerBuilderImpl(final DeveloperFunctionInvoker developerFunctionInvoker) {
        this.developerFunctionInvoker = requireNonNull(developerFunctionInvoker);
    }

    @Override
    public FunctionInvoker build(final ByteCode byteCode) {
        return new FunctionInvokerImpl(developerFunctionInvoker, byteCode);
    }

    private static final class FunctionInvokerImpl implements FunctionInvoker {

        private final DeveloperFunctionInvoker developerFunctionInvoker;

        private final ByteCode byteCode;

        private FunctionInvokerImpl(final DeveloperFunctionInvoker developerFunctionInvoker, final ByteCode byteCode) {
            this.developerFunctionInvoker = requireNonNull(developerFunctionInvoker);
            this.byteCode = requireNonNull(byteCode);
        }

        @Override
        public Object invokeMain() {
            final Optional<DeveloperFunction> functionOptional = byteCode.getMainFunction();
            if (functionOptional.isPresent()) {
                return developerFunctionInvoker.invokeMain(functionOptional.get());
            } else {
                throw new JavammStructRuntimeError(
                        "Main function not found, please define the main functions as: '%s %s'",
                        FUNCTION, byteCode.getMainFunctionName());
            }
        }

        @Override
        public Object invoke(final FunctionName functionName, final List<Expression> arguments) {
            final Optional<DeveloperFunction> functionOptional = byteCode.getFunction(functionName);
            if (functionOptional.isPresent()) {
                return developerFunctionInvoker.invoke(functionOptional.get(), arguments);
            } else {
             // TODO: Add support for standard functions -> try to find and call library function
                throw new JavammLineRuntimeError("Function '%s' is not defined", functionName);
            }
        }
    }
}
