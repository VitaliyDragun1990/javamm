
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

import com.revenat.javamm.code.fragment.expression.TernaryConditionalExpression;
import com.revenat.javamm.interpreter.component.ExpressionEvaluator;
import com.revenat.javamm.interpreter.component.impl.error.JavammLineRuntimeError;

import static com.revenat.javamm.code.util.TypeUtils.confirmType;

/**
 * @author Vitaliy Dragun
 *
 */
public class TernaryConditionalExpressionEvaluator extends AbstractExpressionEvaluator
        implements ExpressionEvaluator<TernaryConditionalExpression> {

    @Override
    public Class<TernaryConditionalExpression> getExpressionClass() {
        return TernaryConditionalExpression.class;
    }

    @Override
    public Object evaluate(final TernaryConditionalExpression expression) {
        final Object predicateResult = expression.getPredicateOperand().getValue(getExpressionContext());

        if (isBoolean(predicateResult)) {
            return evaluate(expression, (Boolean) predicateResult);
        }
        throw new JavammLineRuntimeError("First operand of ?: operator should resolve to boolean value");
    }

    private Object evaluate(final TernaryConditionalExpression expression, final Boolean result) {
        if (result) {
            return expression.getTrueClauseOperand().getValue(getExpressionContext());
        } else {
            return expression.getFalseClauseOperand().getValue(getExpressionContext());
        }
    }

    private boolean isBoolean(final Object object) {
        return confirmType(Boolean.class, object);
    }
}
