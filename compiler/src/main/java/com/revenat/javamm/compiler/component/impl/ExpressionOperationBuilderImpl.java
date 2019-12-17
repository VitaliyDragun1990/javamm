
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

package com.revenat.javamm.compiler.component.impl;

import com.revenat.javamm.code.fragment.Expression;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.expression.FunctionInvocationExpression;
import com.revenat.javamm.code.fragment.expression.PostfixNotationComplexExpression;
import com.revenat.javamm.code.fragment.expression.UnaryPostfixAssignmentExpression;
import com.revenat.javamm.code.fragment.expression.UnaryPrefixAssignmentExpression;
import com.revenat.javamm.code.fragment.operation.ExpressionOperation;
import com.revenat.javamm.compiler.component.ExpressionOperationBuilder;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;

import java.util.Set;

import static com.revenat.javamm.code.util.TypeUtils.confirmType;

/**
 * @author Vitaliy Dragun
 */
public class ExpressionOperationBuilderImpl implements ExpressionOperationBuilder {

    private static final Set<Class<? extends Expression>> UNARY_ASSIGNMENT_EXPRESSIONS = Set.of(
        UnaryPrefixAssignmentExpression.class,
        UnaryPostfixAssignmentExpression.class,
        FunctionInvocationExpression.class
    );

    @Override
    public ExpressionOperation build(final Expression expression, final SourceLine sourceLine) {
        return new ExpressionOperation(sourceLine, requireAssignmentExpression(expression, sourceLine));
    }

    private Expression requireAssignmentExpression(final Expression expression, final SourceLine sourceLine) {
        if (isAssignmentExpression(expression)) {
            return expression;
        } else {
            throw new JavammLineSyntaxError(sourceLine, "Expression '%s' is not allowed here", sourceLine.getCommand());
        }
    }

    private boolean isAssignmentExpression(final Expression expression) {
        return isUnaryAssignmentExpression(expression) || isBinaryAssignmentExpression(expression);
    }

    private boolean isUnaryAssignmentExpression(final Expression expression) {
        return UNARY_ASSIGNMENT_EXPRESSIONS.contains(expression.getClass());
    }

    private boolean isBinaryAssignmentExpression(final Expression expression) {
        return isPostfixExpression(expression) && toPostfix(expression).isBinaryAssignmentExpression();
    }

    private boolean isPostfixExpression(final Expression expression) {
        return confirmType(PostfixNotationComplexExpression.class, expression);
    }

    private PostfixNotationComplexExpression toPostfix(final Expression expression) {
        return (PostfixNotationComplexExpression) expression;
    }
}
