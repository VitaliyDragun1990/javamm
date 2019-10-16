
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

package com.revenat.javamm.interpreter.component.impl.operation.simple;

import com.revenat.javamm.code.component.ExpressionContext;
import com.revenat.javamm.code.fragment.Variable;
import com.revenat.javamm.code.fragment.operation.VariableDeclarationOperation;
import com.revenat.javamm.interpreter.component.impl.error.JavammLineRuntimeError;
import com.revenat.javamm.interpreter.component.impl.operation.AbstractOperationInterpreter;
import com.revenat.javamm.interpreter.model.LocalContext;

import static com.revenat.javamm.interpreter.model.CurrentRuntimeProvider.getCurrentRuntime;

/**
 * Interprets {@linkplain VariableDeclarationOperation variable declaration
 * operation}
 *
 * @author Vitaliy Dragun
 *
 */
public class VariableDeclarationOperationInterpreter
        extends AbstractOperationInterpreter<VariableDeclarationOperation> {

    public VariableDeclarationOperationInterpreter(final ExpressionContext expressionContext) {
        super(expressionContext);
    }

    @Override
    public Class<VariableDeclarationOperation> getOperationClass() {
        return VariableDeclarationOperation.class;
    }

    /**
     * Interprets {@linkplain VariableDeclarationOperation variable declaration
     * operation} by adding {@linkplain Variable variable} declaring by that
     * operation along with it's assigned value to the {@linkplain LocalContext
     * local context}
     *
     * @throws JavammLineRuntimeError if variable with same name has already been
     *                                declared in the {@linkplain LocalContext local
     *                                context}
     */
    @Override
    protected void interpretOperation(final VariableDeclarationOperation operation) {
        final LocalContext localContext = getCurrentRuntime().getCurrentLocalContext();
        final Variable variable = operation.getVariable();

        assertVariableIsNotDefinedInLocalContext(localContext, variable);

        defineVariableInLocalContext(variable, localContext, operation);
    }

    private void assertVariableIsNotDefinedInLocalContext(final LocalContext localContext, final Variable variable) {
        if (localContext.isVariableDefined(variable)) {
            throw new JavammLineRuntimeError("Variable '%s' already defined", variable);
        }
    }

    private void defineVariableInLocalContext(final Variable variable,
                                              final LocalContext localContext,
                                              final VariableDeclarationOperation operation) {
        final Object variableValue = operation.getExpression().getValue(expressionContext);

        if (operation.isConstant()) {
            localContext.setFinalValue(variable, variableValue);
        } else {
            localContext.setVariableValue(variable, variableValue);
        }
    }
}
