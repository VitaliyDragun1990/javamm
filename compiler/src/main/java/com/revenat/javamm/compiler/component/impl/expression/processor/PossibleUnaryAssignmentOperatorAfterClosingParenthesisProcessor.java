
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
import com.revenat.javamm.code.fragment.Parenthesis;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.expression.UnaryPostfixAssignmentExpression;
import com.revenat.javamm.code.fragment.expression.VariableExpression;
import com.revenat.javamm.code.fragment.operator.UnaryOperator;

import java.util.List;
import java.util.ListIterator;

import static com.revenat.javamm.code.util.LexemeUtils.isClosingParenthesis;
import static com.revenat.javamm.code.util.LexemeUtils.isOpeningParenthesis;
import static com.revenat.javamm.code.util.LexemeUtils.isUnaryAssignmentOperator;
import static com.revenat.javamm.compiler.component.impl.util.SyntaxValidationUtils.requireVariableExpression;

class PossibleUnaryAssignmentOperatorAfterClosingParenthesisProcessor
    extends UnaryAssignmentOperatorProcessor {

    private final ListIterator<Lexeme> source;

    protected PossibleUnaryAssignmentOperatorAfterClosingParenthesisProcessor(final Lexeme expression,
                                                                              final Lexeme parenthesis,
                                                                              final ListIterator<Lexeme> source,
                                                                              final List<Lexeme> result,
                                                                              final SourceLine sourceLine) {
        super(expression, parenthesis, result, sourceLine);
        this.source = source;
    }

    Expression getExpression() {
        return (Expression) current;
    }

    Parenthesis getParenthesis() {
        return (Parenthesis) next;
    }

    protected void processEntry() {
        final int expressionPosition = result.size();
        result.add(getParenthesis());

        if (source.hasNext()) {
            Lexeme next = source.next();

            while (source.hasNext() && isClosingParenthesis(next)) {
                result.add(next);
                next = source.next();
            }

            if (isUnaryAssignmentOperator(next) && isOpeningParenthesis(result.get(expressionPosition - 1))) {
                final UnaryOperator operator = (UnaryOperator) next;
                final VariableExpression operand = requireVariableExpression(getExpression(), operator, sourceLine);
                result.add(expressionPosition, new UnaryPostfixAssignmentExpression(operand, operator));

            } else if (isUnaryAssignmentOperator(next)) {
                throw syntaxError("Invalid argument for '%s' operator", next);

            } else {
                result.add(next);
                result.add(expressionPosition, getExpression());
            }
        } else {
            result.add(expressionPosition, getExpression());
        }
    }
}
