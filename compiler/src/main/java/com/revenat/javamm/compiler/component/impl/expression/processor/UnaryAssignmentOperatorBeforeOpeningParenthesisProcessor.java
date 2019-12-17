
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

import com.revenat.javamm.code.fragment.Lexeme;
import com.revenat.javamm.code.fragment.Parenthesis;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.expression.UnaryPrefixAssignmentExpression;
import com.revenat.javamm.code.fragment.expression.VariableExpression;
import com.revenat.javamm.code.fragment.operator.UnaryOperator;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;

import java.util.List;
import java.util.ListIterator;

import static com.revenat.javamm.code.util.LexemeUtils.isClosingParenthesis;
import static com.revenat.javamm.code.util.LexemeUtils.isExpression;
import static com.revenat.javamm.code.util.LexemeUtils.isOpeningParenthesis;
import static com.revenat.javamm.compiler.component.impl.util.SyntaxValidationUtils.requireVariableExpression;

class UnaryAssignmentOperatorBeforeOpeningParenthesisProcessor extends UnaryAssignmentOperatorProcessor {

    private final ListIterator<Lexeme> source;

    protected UnaryAssignmentOperatorBeforeOpeningParenthesisProcessor(final Lexeme operator,
                                                                       final Lexeme parenthesis,
                                                                       final ListIterator<Lexeme> source,
                                                                       final List<Lexeme> result,
                                                                       final SourceLine sourceLine) {
        super(operator, parenthesis, result, sourceLine);
        this.source = source;
    }

    Parenthesis getParenthesis() {
        return (Parenthesis) next;
    }

    UnaryOperator getOperator() {
        return (UnaryOperator) current;
    }

    protected void processEntry() {
        Lexeme next = getParenthesis();

        while (isOpeningParenthesis(next) && source.hasNext()) {
            result.add(next);
            next = source.next();
        }

        if (isNotExpression(next)) {
            throw syntaxError(sourceLine, "A variable expression is expected for unary operator: '%s'", current);
        } else {
            buildUnaryAssignmentExpression(next);
        }
    }

    private void buildUnaryAssignmentExpression(final Lexeme operatorArgument) {

        if (source.hasNext()) {
            final Lexeme assumedParenthesis = source.next();
            if (isExpressionBeforeClosingParenthesis(operatorArgument, assumedParenthesis)) {
                final VariableExpression operand = requireVariableExpression(operatorArgument,
                    getOperator(),
                    sourceLine);
                result.add(new UnaryPrefixAssignmentExpression(operand, getOperator()));
                source.previous();
                return;
            }
        }
        throw syntaxError("Invalid argument for '%s' operator", getOperator());
    }

    private boolean isNotExpression(final Lexeme lexeme) {
        return !isExpression(lexeme);
    }

    private boolean isExpressionBeforeClosingParenthesis(final Lexeme current, final Lexeme next) {
        return isExpression(current) && isClosingParenthesis(next);
    }

    private JavammLineSyntaxError syntaxError(final SourceLine sourceLine, final String message, final Object... args) {
        return new JavammLineSyntaxError(sourceLine, message, args);
    }
}
