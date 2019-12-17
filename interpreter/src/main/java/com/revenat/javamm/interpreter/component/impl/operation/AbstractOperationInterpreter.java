
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

package com.revenat.javamm.interpreter.component.impl.operation;

import com.revenat.javamm.code.component.ExpressionContext;
import com.revenat.javamm.code.fragment.Operation;
import com.revenat.javamm.interpreter.component.OperationInterpreter;
import com.revenat.javamm.interpreter.error.TerminateInterpreterException;

import static java.util.Objects.requireNonNull;

/**
 * @author Vitaliy Dragun
 */
public abstract class AbstractOperationInterpreter<T extends Operation> implements OperationInterpreter<T> {
    protected final ExpressionContext expressionContext;

    public AbstractOperationInterpreter(final ExpressionContext expressionContext) {
        this.expressionContext = requireNonNull(expressionContext);
    }

    @Override
    public final void interpret(final T operation) {
        checkForTermination();
        interpretOperation(operation);
    }

    protected final void checkForTermination() {
        if (isTerminated()) {
            throw new TerminateInterpreterException();
        }
    }

    protected boolean isTerminated() {
        return Thread.interrupted();
    }

    protected abstract void interpretOperation(T operation);
}
