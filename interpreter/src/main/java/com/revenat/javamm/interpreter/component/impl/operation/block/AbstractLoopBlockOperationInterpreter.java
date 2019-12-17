
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

package com.revenat.javamm.interpreter.component.impl.operation.block;

import com.revenat.javamm.code.component.ExpressionContext;
import com.revenat.javamm.code.fragment.operation.AbstractLoopOperation;
import com.revenat.javamm.interpreter.component.CalculatorFacade;
import com.revenat.javamm.interpreter.component.impl.operation.exception.BreakOperationException;
import com.revenat.javamm.interpreter.component.impl.operation.exception.ContinueOperationException;

import static com.revenat.javamm.interpreter.model.CurrentRuntimeProvider.getCurrentRuntime;
import static java.util.Objects.requireNonNull;

/**
 * @author Vitaliy Dragun
 */
abstract class AbstractLoopBlockOperationInterpreter<T extends AbstractLoopOperation>
    extends AbstractBlockOperationInterpreter<T> {

    protected final CalculatorFacade calculatorFacade;

    protected AbstractLoopBlockOperationInterpreter(final ExpressionContext expressionContext,
                                                    final CalculatorFacade calculatorFacade) {
        super(expressionContext);
        this.calculatorFacade = requireNonNull(calculatorFacade);
    }

    @Override
    protected final void interpretOperation(final T operation) {
        try {
            processLoopOperation(operation);
        } catch (final BreakOperationException e) {
            // do nothing. End loop execution
        }
    }

    protected abstract void processLoopOperation(T operation);

    final void interpretLoopBody(final T operation) {
        checkForTermination();
        try {
            interpretBlock(operation.getBody());
        } catch (final ContinueOperationException e) {
            // do nothing. Current iteration skipped
        }
        getCurrentRuntime().setCurrentOperation(operation);
    }

    final boolean isConditionTrue(final T operation) {
        return calculatorFacade.isTrue(expressionContext, operation.getCondition());
    }
}
