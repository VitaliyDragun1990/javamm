
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
import com.revenat.javamm.code.exception.JavammError;
import com.revenat.javamm.code.fragment.Expression;

import static com.revenat.javamm.code.util.TypeUtils.confirmType;

import static java.lang.String.format;

/**
 * Represents ternary operator expression: predicate, true case and
 * false case
 *
 * @author Vitaliy Dragun
 *
 */
public class TernaryConditionalExpression implements Expression {

    private final Expression predicateOperand;

    private final Expression trueClauseOperand;

    private final Expression falseClauseOperand;

    public TernaryConditionalExpression(final Expression predicateOperand,
                                        final Expression trueClauseOperand,
                                        final Expression falseClauseOperand) {
        this.predicateOperand = predicateOperand;
        this.trueClauseOperand = trueClauseOperand;
        this.falseClauseOperand = falseClauseOperand;
    }

    public Expression getPredicateOperand() {
        return predicateOperand;
    }

    public Expression getTrueClauseOperand() {
        return trueClauseOperand;
    }

    public Expression getFalseClauseOperand() {
        return falseClauseOperand;
    }

    @Override
    public Object getValue(final ExpressionContext expressionContext) {
        final Object predicateResult = predicateOperand.getValue(expressionContext);

        if (isBoolean(predicateResult)) {
            return evaluate(expressionContext, (Boolean) predicateResult);
        }
        throw new JavammError("First operand of ?: operator should resolve to boolean value");
    }

    private Object evaluate(final ExpressionContext expressionContext, final Boolean result) {
        if (result) {
            return trueClauseOperand.getValue(expressionContext);
        } else {
            return falseClauseOperand.getValue(expressionContext);
        }
    }

    private boolean isBoolean(final Object object) {
        return confirmType(Boolean.class, object);
    }

    @Override
    public String toString() {
        return format("%s ? %s : %s", predicateOperand, trueClauseOperand, falseClauseOperand);
    }
}
