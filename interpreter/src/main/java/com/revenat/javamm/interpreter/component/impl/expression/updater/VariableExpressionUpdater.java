
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

package com.revenat.javamm.interpreter.component.impl.expression.updater;

import com.revenat.javamm.code.fragment.Variable;
import com.revenat.javamm.code.fragment.expression.VariableExpression;
import com.revenat.javamm.interpreter.component.ExpressionUpdater;
import com.revenat.javamm.interpreter.component.impl.error.JavammLineRuntimeError;
import com.revenat.javamm.interpreter.model.CurrentRuntimeProvider;
import com.revenat.javamm.interpreter.model.LocalContext;

/**
 * Responsible for updating already defined {@linkplain Variable variable}
 *
 * @author Vitaliy Dragun
 *
 */
public class VariableExpressionUpdater implements ExpressionUpdater<VariableExpression> {

    @Override
    public Class<VariableExpression> getExpressionClass() {
        return VariableExpression.class;
    }

    @Override
    public void update(final VariableExpression expression, final Object updatedValue) {
        final Variable variable = expression.getVariable();
        updateVariableIfDefined(variable, updatedValue);
    }

    private void updateVariableIfDefined(final Variable variable, final Object updatedValue) {
        final LocalContext localContext = CurrentRuntimeProvider.getCurrentRuntime().getCurrentLocalContext();

        if (localContext.isVariableDefined(variable)) {
            localContext.setVariableValue(variable, updatedValue);
        } else {
            throw new JavammLineRuntimeError("Variable '%s' is not defined", variable);
        }
    }
}
