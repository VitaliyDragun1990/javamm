
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

package com.revenat.javamm.compiler.component.impl.operation.simple;

import com.revenat.javamm.code.fragment.Expression;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.expression.VariableExpression;
import com.revenat.javamm.code.fragment.operation.VariableAssignmentOperation;
import com.revenat.javamm.compiler.component.ExpressionResolver;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;
import com.revenat.javamm.compiler.component.impl.operation.AbstractOperationReader;

import java.util.List;
import java.util.ListIterator;

import static com.revenat.javamm.code.syntax.Keywords.ALL_KEYWORDS;
import static com.revenat.javamm.code.util.TypeUtils.confirmType;

/**
 * @author Vitaliy Dragun
 *
 */
public class VariableAssignmentOperationReader extends AbstractOperationReader<VariableAssignmentOperation> {

    private static final String ASSIGNMENT_OPERATOR = "=";

    private final ExpressionResolver expressionResolver;

    public VariableAssignmentOperationReader(final ExpressionResolver expressionResolver) {
        this.expressionResolver = expressionResolver;
    }

    /*
     * Example:
     * var1 = 5
     * array1 [4] = 5
     * array1 [4 + 2 * a] = 5
     */
    @Override
    public boolean canRead(final SourceLine sourceLine) {
        return isVariableNameValid(sourceLine) && isAssignmentOperatorPresent(sourceLine);
    }

    @Override
    protected void validate(final SourceLine sourceLine) {
        validateVariableAndExpressionArePresent(sourceLine);
    }

    @Override
    protected VariableAssignmentOperation get(final SourceLine sourceLine,
                                              final ListIterator<SourceLine> codeIterator) {
        final int assignmentOperatorIndex = sourceLine.indexOf(ASSIGNMENT_OPERATOR);
        final VariableExpression variableExpression = resolveVariableExpression(assignmentOperatorIndex, sourceLine);
        final Expression valueExpression = resolveValueExpression(assignmentOperatorIndex, sourceLine);

        return new VariableAssignmentOperation(sourceLine, variableExpression, valueExpression);
    }

    private VariableExpression resolveVariableExpression(final int assignmentOperatorIndex,
                                                         final SourceLine sourceLine) {
        final List<String> variableTokens = sourceLine.subList(0, assignmentOperatorIndex);
        final Expression expression = expressionResolver.resolve(variableTokens, sourceLine);
        return requireVariableExpression(expression, sourceLine);
    }

    private Expression resolveValueExpression(final int assignmentOperatorIndex, final SourceLine sourceLine) {
        final List<String> valueTokens = sourceLine.subList(assignmentOperatorIndex + 1);
        return expressionResolver.resolve(valueTokens, sourceLine);
    }

    private VariableExpression requireVariableExpression(final Expression expression, final SourceLine sourceLine) {
        if (confirmType(VariableExpression.class, expression)) {
            return (VariableExpression) expression;
        } else {
            throw new JavammLineSyntaxError("The assignment is possible to variable expression only", sourceLine);
        }
    }

    private boolean isAssignmentOperatorPresent(final SourceLine sourceLine) {
        return sourceLine.contains(ASSIGNMENT_OPERATOR);
    }

    private boolean isVariableNameValid(final SourceLine sourceLine) {
        return !ALL_KEYWORDS.contains(sourceLine.getFirst());
    }

    private void validateVariableAndExpressionArePresent(final SourceLine sourceLine) {
        if (sourceLine.getTokenCount() <= 2) {
            throw new JavammLineSyntaxError("Missing variable or expression", sourceLine);
        }
    }
}
