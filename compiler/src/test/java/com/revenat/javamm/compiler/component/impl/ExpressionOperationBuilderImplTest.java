
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
import com.revenat.javamm.code.fragment.Variable;
import com.revenat.javamm.code.fragment.expression.PostfixNotationComplexExpression;
import com.revenat.javamm.code.fragment.expression.UnaryPostfixAssignmentExpression;
import com.revenat.javamm.code.fragment.expression.UnaryPrefixAssignmentExpression;
import com.revenat.javamm.code.fragment.expression.VariableExpression;
import com.revenat.javamm.code.fragment.operation.ExpressionOperation;
import com.revenat.javamm.code.fragment.operator.UnaryOperator;
import com.revenat.javamm.compiler.component.ExpressionOperationBuilder;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;
import com.revenat.javamm.compiler.test.helper.CustomAsserts;
import com.revenat.juinit.addons.ReplaceCamelCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("an expression operation builder")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ExpressionOperationBuilderImplTest {

    private static final SourceLine SOURCE_LINE = new SourceLine("test", 5, List.of());

    private final ExpressionOperationBuilder expressionOperationBuilder = new ExpressionOperationBuilderImpl();

    @Test
    @Order(1)
    void shouldBuildExpressionOperationForUnaryPrefixAssignmentExpression() {
        final Expression expression = createUnaryPrefixAssignmentExpression();

        final ExpressionOperation result = expressionOperationBuilder.build(expression, SOURCE_LINE);

        assertThat(result.getExpression(), sameInstance(expression));
    }

    @Test
    @Order(2)
    void shouldBuildExpressionOperationForUnaryPostfixAssignmentExpression() {
        final Expression expression = createUnaryPostfixAssignmentExpression();

        final ExpressionOperation result = expressionOperationBuilder.build(expression, SOURCE_LINE);

        assertThat(result.getExpression(), sameInstance(expression));
    }

    @Test
    @Order(3)
    void shouldBuildExpressionOperationForComplexBinaryAssignmentExpression() {
        final Expression expression = createComplexBinaryAssignmentExpression();

        final ExpressionOperation result = expressionOperationBuilder.build(expression, SOURCE_LINE);

        assertThat(result.getExpression(), sameInstance(expression));
    }

    @Test
    @Order(4)
    void shouldFailToBuildExpressionOperationIfProvidedExpressionIsNotAssignmentOne() {
        final Expression expression = createNotAssignmentExpression();

        final JavammLineSyntaxError e = assertThrows(JavammLineSyntaxError.class,
            () -> expressionOperationBuilder.build(expression, SOURCE_LINE));

        CustomAsserts.assertErrorMessageContains(e, "Expression '%s' is not allowed here", SOURCE_LINE.getCommand());
    }

    private Expression createNotAssignmentExpression() {
        final PostfixNotationComplexExpression expression = mock(PostfixNotationComplexExpression.class);
        when(expression.isBinaryAssignmentExpression()).thenReturn(false);
        return expression;
    }

    private Expression createComplexBinaryAssignmentExpression() {
        final PostfixNotationComplexExpression expression = mock(PostfixNotationComplexExpression.class);
        when(expression.isBinaryAssignmentExpression()).thenReturn(true);
        return expression;
    }

    private Expression createUnaryPrefixAssignmentExpression() {
        final VariableExpression variableExpression = new VariableExpression(mock(Variable.class));
        return new UnaryPrefixAssignmentExpression(variableExpression, UnaryOperator.DECREMENT);
    }

    private Expression createUnaryPostfixAssignmentExpression() {
        final VariableExpression variableExpression = new VariableExpression(mock(Variable.class));
        return new UnaryPostfixAssignmentExpression(variableExpression, UnaryOperator.DECREMENT);
    }
}
