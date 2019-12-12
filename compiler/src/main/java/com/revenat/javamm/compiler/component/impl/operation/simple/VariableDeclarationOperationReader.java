
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
import com.revenat.javamm.code.fragment.Variable;
import com.revenat.javamm.code.fragment.expression.NullValueExpression;
import com.revenat.javamm.code.fragment.operation.VariableDeclarationOperation;
import com.revenat.javamm.compiler.component.ExpressionResolver;
import com.revenat.javamm.compiler.component.VariableBuilder;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;
import com.revenat.javamm.compiler.component.impl.operation.AbstractOperationReader;
import com.revenat.javamm.compiler.component.impl.operation.ForInitOperationReader;

import java.util.ListIterator;
import java.util.Optional;

import static com.revenat.javamm.code.syntax.Keywords.VAR;

import static java.util.Objects.requireNonNull;

/**
 * Responsible for reading variable declaration operation
 *
 * @author Vitaliy Dragun
 *
 */
public class VariableDeclarationOperationReader extends AbstractOperationReader<VariableDeclarationOperation>
    implements ForInitOperationReader {
    private static final int VARIABLE_NAME_POSITION = 1;

    private static final int ASSIGNMENT_OPERATOR_POSITION = 2;

    private static final int EXPRESSION_STARTING_POSITION = 3;

    private final VariableBuilder variableBuilder;

    private final ExpressionResolver expressionResolver;

    public VariableDeclarationOperationReader(final VariableBuilder variableBuilder,
                                              final ExpressionResolver expressionResolver) {
        this.variableBuilder = requireNonNull(variableBuilder);
        this.expressionResolver = requireNonNull(expressionResolver);
    }

    @Override
    public boolean canRead(final SourceLine sourceLine) {
        return VAR.equals(sourceLine.getFirst());
    }

    /**
     * Valid:
     * var a
     * var a = ${expression}
     * ---------------------
     * Invalid:
     * var
     * var a =
     * var a +
     * var + a
     * var = a
     * var a + 3
     * var a 3 =
     */
    @Override
    protected void validate(final SourceLine sourceLine) {
        assertVariableNamePresent(sourceLine);
        assertAssignmentOperatorPosition(sourceLine);
        assertVariableExpression(sourceLine);
    }

    @Override
    protected VariableDeclarationOperation get(final SourceLine sourceLine,
                                               final ListIterator<SourceLine> compiledCodeIterator) {
        final Variable variable = variableBuilder.build(sourceLine.getToken(VARIABLE_NAME_POSITION), sourceLine);
        final Expression expression = getExpression(sourceLine);
        return new VariableDeclarationOperation(sourceLine, isConstant(), variable, expression);
    }

    private Expression getExpression(final SourceLine sourceLine) {
        if (sourceLine.getTokenCount() == 2) {
            return NullValueExpression.getInstance();
        } else {
            return expressionResolver.resolve(sourceLine.subList(EXPRESSION_STARTING_POSITION), sourceLine);
        }
    }

    protected boolean isConstant() {
        return false;
    }

    private void assertVariableNamePresent(final SourceLine sourceLine) {
        if (sourceLine.getTokenCount() == 1) {
            throw new JavammLineSyntaxError(missingNameErrorMessage(), sourceLine);
        }
    }

    private void assertAssignmentOperatorPosition(final SourceLine sourceLine) {
        if (sourceLine.getTokenCount() > 2 &&
                !sourceLine.getToken(ASSIGNMENT_OPERATOR_POSITION).equals("=")) {
            throw new JavammLineSyntaxError("'=' is missing or has invalid position", sourceLine);
        }
    }

    private void assertVariableExpression(final SourceLine sourceLine) {
        if (sourceLine.getTokenCount() == 3 &&
                sourceLine.getToken(ASSIGNMENT_OPERATOR_POSITION).equals("=")) {
            throw new JavammLineSyntaxError(missingExpressionErrorMessage(), sourceLine);
        }
    }

    protected String missingNameErrorMessage() {
        return "Variable name is missing";
    }

    protected String missingExpressionErrorMessage() {
        return "Variable expression is missing";
    }
}
