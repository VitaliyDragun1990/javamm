
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

import com.revenat.javamm.code.fragment.expression.FunctionInvocationExpression;
import com.revenat.javamm.interpreter.component.ExpressionEvaluator;
import com.revenat.javamm.interpreter.component.FunctionInvoker;

import static com.revenat.javamm.interpreter.model.CurrentRuntimeProvider.getCurrentRuntime;

/**
 * @author Vitaliy Dragun
 */
public class FunctionInvocationExpressionEvaluator implements ExpressionEvaluator<FunctionInvocationExpression> {

    @Override
    public Class<FunctionInvocationExpression> getExpressionClass() {
        return FunctionInvocationExpression.class;
    }

    @Override
    public Object evaluate(final FunctionInvocationExpression expression) {
        final FunctionInvoker functionInvoker = getFunctionInvoker();
        return functionInvoker.invoke(expression.getFunctionName(), expression.getArguments());
    }

    private FunctionInvoker getFunctionInvoker() {
        return getCurrentRuntime().getCurrentFunctionInvoker();
    }
}
