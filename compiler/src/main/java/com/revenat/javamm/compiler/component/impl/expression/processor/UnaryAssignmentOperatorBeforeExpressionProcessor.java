
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

package com.revenat.javamm.compiler.component.impl.expression.processor;

import com.revenat.javamm.code.fragment.Expression;
import com.revenat.javamm.code.fragment.Lexeme;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.expression.UnaryPrefixAssignmentExpression;
import com.revenat.javamm.code.fragment.expression.VariableExpression;
import com.revenat.javamm.code.fragment.operator.UnaryOperator;

import java.util.List;

import static com.revenat.javamm.compiler.component.impl.util.SyntaxValidationUtils.requireVariableExpression;

class UnaryAssignmentOperatorBeforeExpressionProcessor extends UnaryAssignmentOperatorProcessor {

    protected UnaryAssignmentOperatorBeforeExpressionProcessor(final Lexeme operator,
                                                               final Lexeme expression,
                                                               final List<Lexeme> result,
                                                               final SourceLine sourceLine) {
        super(operator, expression, result, sourceLine);
    }

    UnaryOperator getOperator() {
        return (UnaryOperator) current;
    }

    Expression getExpression() {
        return (Expression) next;
    }

    public void processEntry() {
        final VariableExpression operand = requireVariableExpression(getExpression(), getOperator(), sourceLine);
        result.add(new UnaryPrefixAssignmentExpression(operand, getOperator()));
    }
}
