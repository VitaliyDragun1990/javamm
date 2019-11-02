
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
import com.revenat.javamm.code.fragment.operation.ForOperation;
import com.revenat.javamm.interpreter.component.CalculatorFacade;

/**
 * @author Vitaliy Dragun
 *
 */
public class ForOperationInterpreter extends AbstractLoopBlockOperationInterpreter<ForOperation> {

    public ForOperationInterpreter(final ExpressionContext expressionContext,
                                      final CalculatorFacade calculatorFacade) {
        super(expressionContext, calculatorFacade);
    }

    @Override
    public Class<ForOperation> getOperationClass() {
        return ForOperation.class;
    }

    @Override
    protected void interpretOperation(final ForOperation operation) {
        final BlockScopeLocalContextController contextController = new BlockScopeLocalContextController();
        try {
            contextController.setChildLocalContextForNestedBlock();
            interpretForOperation(operation);
        } finally {
            contextController.disposeChildLocalContext();
        }
    }

    private void interpretForOperation(final ForOperation operation) {
        interpretInitializationClause(operation);
        while (isConditionTrue(operation)) {
            interpretLoopBody(operation);
            interpretUpdateClause(operation);
        }
    }

    private void interpretInitializationClause(final ForOperation operation) {
        getBlockOperationInterpreter().interpret(operation.getInitialization());
    }

    private void interpretUpdateClause(final ForOperation operation) {
        getBlockOperationInterpreter().interpret(operation.getUpdate());
    }
}
