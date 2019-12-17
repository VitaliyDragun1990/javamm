
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

package com.revenat.javamm.code.fragment.expression;

import com.revenat.javamm.code.component.ExpressionContext;
import com.revenat.javamm.code.fragment.operator.UnaryOperator;

import java.util.List;

/**
 * Represents expression with assignment unary operator in postfix position
 *
 * @author Vitaliy Dragun
 */
public class UnaryPostfixAssignmentExpression extends UnaryAssignmentExpression {

    public UnaryPostfixAssignmentExpression(final VariableExpression operand, final UnaryOperator operator) {
        super(operand, operator);
    }

    @Override
    public Object getValue(final ExpressionContext expressionContext) {
        final Object oldValue = getOperand().getValue(expressionContext);

        final Object newValue = new PostfixNotationComplexExpression(List.of(getOperand(), getOperator()), toString())
            .getValue(expressionContext);
        getOperand().setValue(expressionContext, newValue);

        return oldValue;
    }

    @Override
    public String toString() {
        return getOperand().toString() + getOperator();
    }
}
