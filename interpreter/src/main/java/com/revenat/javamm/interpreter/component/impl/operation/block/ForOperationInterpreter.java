
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
import com.revenat.javamm.code.fragment.Operation;
import com.revenat.javamm.code.fragment.operation.Block;
import com.revenat.javamm.code.fragment.operation.ForInitOperation;
import com.revenat.javamm.code.fragment.operation.ForOperation;
import com.revenat.javamm.code.fragment.operation.VariableDeclarationOperation;
import com.revenat.javamm.interpreter.component.CalculatorFacade;

import java.util.Optional;

import static com.revenat.javamm.code.util.TypeUtils.confirmType;

/**
 * @author Vitaliy Dragun
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
    protected void processLoopOperation(final ForOperation operation) {
        if (isVariableDeclaredInsideInitializationScope(operation)) {
            NestedScopeLocalContextExecutor.executeInsideNestedScope(() -> interpretForOperation(operation));
        } else {
            interpretForOperation(operation);
        }
    }

    private void interpretForOperation(final ForOperation operation) {
        for (interpretInitializationClause(operation); isConditionTrue(operation); interpretUpdateClause(operation)) {
            interpretLoopBody(operation);
        }
    }

    private void interpretInitializationClause(final ForOperation operation) {
        operation.getInitOperation().ifPresent(this::interpretAsBlock);
    }

    private void interpretUpdateClause(final ForOperation operation) {
        operation.getUpdateOperation().ifPresent(this::interpretAsBlock);
    }

    private void interpretAsBlock(final Operation operation) {
        final Block block = new Block(operation, operation.getSourceLine());
        getBlockOperationInterpreter().interpret(block);
    }

    private boolean isVariableDeclaredInsideInitializationScope(final ForOperation operation) {
        final Optional<ForInitOperation> initOperation = operation.getInitOperation();
        return initOperation.isPresent() && isVariableDeclaration(initOperation.get());
    }

    private boolean isVariableDeclaration(final ForInitOperation operation) {
        return confirmType(VariableDeclarationOperation.class, operation);
    }
}
