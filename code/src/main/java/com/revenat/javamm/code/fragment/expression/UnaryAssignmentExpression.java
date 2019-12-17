
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

import com.revenat.javamm.code.fragment.Expression;
import com.revenat.javamm.code.fragment.operator.UnaryOperator;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * Represents expression with assignment unary operator.
 *
 * @author Vitaliy Dragun
 */
public abstract class UnaryAssignmentExpression implements Expression {

    private final VariableExpression operand;

    private final UnaryOperator operator;

    protected UnaryAssignmentExpression(final VariableExpression operand, final UnaryOperator operator) {
        this.operand = requireNonNull(operand);
        this.operator = requireAssignmentOperator(operator);
    }

    public VariableExpression getOperand() {
        return operand;
    }

    public UnaryOperator getOperator() {
        return operator;
    }

    private UnaryOperator requireAssignmentOperator(final UnaryOperator operatorToCheck) {
        if (!operatorToCheck.isAssignment()) {
            throw new IllegalArgumentException(format("'%s' is not assignment operator", operatorToCheck));
        }
        return operatorToCheck;
    }
}
