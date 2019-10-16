
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

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

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
        this.predicateOperand = requireNonNull(predicateOperand);
        this.trueClauseOperand = requireNonNull(trueClauseOperand);
        this.falseClauseOperand = requireNonNull(falseClauseOperand);
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
    public String toString() {
        return format("%s ? %s : %s", predicateOperand, trueClauseOperand, falseClauseOperand);
    }
}
