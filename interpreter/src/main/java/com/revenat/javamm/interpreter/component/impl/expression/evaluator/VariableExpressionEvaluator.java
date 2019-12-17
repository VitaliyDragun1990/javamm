
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

package com.revenat.javamm.interpreter.component.impl.expression.evaluator;

import com.revenat.javamm.code.fragment.Variable;
import com.revenat.javamm.code.fragment.expression.VariableExpression;
import com.revenat.javamm.interpreter.component.ExpressionEvaluator;
import com.revenat.javamm.interpreter.component.impl.error.JavammLineRuntimeError;
import com.revenat.javamm.interpreter.model.LocalContext;

import static com.revenat.javamm.interpreter.model.CurrentRuntimeProvider.getCurrentRuntime;

/**
 * Responsible for evaluating {@linkplain VariableExpression variable
 * expressions}
 *
 * @author Vitaliy Dragun
 */
public class VariableExpressionEvaluator implements ExpressionEvaluator<VariableExpression> {

    @Override
    public Class<VariableExpression> getExpressionClass() {
        return VariableExpression.class;
    }

    /**
     * Evaluates provided {@linkplain VariableExpression variable expression}
     *
     * @throws JavammLineRuntimeError if variable from provided
     *                                {@linkplain VariableExpression expression} can
     *                                not be found in the {@linkplain LocalContext
     *                                local context}
     */
    @Override
    public Object evaluate(final VariableExpression expression) {
        final LocalContext localContext = getCurrentRuntime().getCurrentLocalContext();
        final Variable variable = expression.getVariable();

        return getVariableValueIfDefined(variable, localContext);
    }

    private Object getVariableValueIfDefined(final Variable variable, final LocalContext localContext) {
        if (localContext.isVariableDefined(variable)) {
            return localContext.getVariableValue(variable);
        } else {
            throw new JavammLineRuntimeError("Variable '%s' is not defined", variable);
        }
    }
}
