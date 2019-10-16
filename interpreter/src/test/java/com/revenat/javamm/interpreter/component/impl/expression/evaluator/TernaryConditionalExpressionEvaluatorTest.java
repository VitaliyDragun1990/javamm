
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

package com.revenat.javamm.interpreter.component.impl.expression.evaluator;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.revenat.javamm.code.component.ExpressionContext;
import com.revenat.javamm.code.fragment.Expression;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.expression.TernaryConditionalExpression;
import com.revenat.javamm.interpreter.component.impl.error.JavammLineRuntimeError;
import com.revenat.javamm.interpreter.test.helper.CustomAsserts;
import com.revenat.javamm.interpreter.test.helper.TestCurrentRuntimeManager;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.revenat.juinit.addons.ReplaceCamelCase;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("a ternary conditional expression evaluator")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TernaryConditionalExpressionEvaluatorTest {

    private static final Object ANY_NON_BOOLEAN_VALUE = "non boolean";

    private static final String TRUE_CLAUSE_VALUE = "true";

    private static final String FALSE_CLAUSE_VALUE = "false";

    private TernaryConditionalExpressionEvaluator expressionEvaluator;

    @BeforeEach
    void setUp() {
        expressionEvaluator = new TernaryConditionalExpressionEvaluator();
        expressionEvaluator.setExpressionContext(mock(ExpressionContext.class));
    }

    @BeforeAll
    static void setupFakeRuntime() {
        TestCurrentRuntimeManager.setFakeCurrentRuntime(SourceLine.EMPTY_SOURCE_LINE);
    }

    @AfterAll
    static void releaseFakeRuntime() {
        TestCurrentRuntimeManager.releaseFakeCurrentRuntime();
    }

    @Test
    @Order(1)
    void shouldDefineClassForExpressionItCanEvaluate() {
        assertThat(expressionEvaluator.getExpressionClass(), is(TernaryConditionalExpression.class));
    }

    @Test
    @Order(2)
    void shouldReturnTrueClauseValueIfTernaryExpressionPredicateEvaluatesToTrue() {
        final TernaryConditionalExpression ternaryExpression = createTernaryExpression(true, TRUE_CLAUSE_VALUE, FALSE_CLAUSE_VALUE);

        final Object result = expressionEvaluator.evaluate(ternaryExpression);

        assertThat(result, is(TRUE_CLAUSE_VALUE));
    }

    @Test
    @Order(3)
    void shouldReturnFalseClauseValueIfTernaryExpressionPredicateEvaluatesToFalse() {
        final TernaryConditionalExpression ternaryExpression = createTernaryExpression(false, TRUE_CLAUSE_VALUE, FALSE_CLAUSE_VALUE);

        final Object result = expressionEvaluator.evaluate(ternaryExpression);

        assertThat(result, is(FALSE_CLAUSE_VALUE));
    }

    @Test
    @Order(4)
    void shouldFailIfTernaryExpressionPredicateClauseEvaluatesToNonBooleanValue() {
        final TernaryConditionalExpression ternaryExpression =
                createTernaryExpression(ANY_NON_BOOLEAN_VALUE, TRUE_CLAUSE_VALUE, FALSE_CLAUSE_VALUE);

        final JavammLineRuntimeError e = assertThrows(JavammLineRuntimeError.class, () -> expressionEvaluator.evaluate(ternaryExpression));

        CustomAsserts.assertErrorMessageContains(e, "First operand of ?: operator should resolve to boolean value");
    }

    private TernaryConditionalExpression createTernaryExpression(final Object predicateValue,
                                                                 final Object trueClauseValue,
                                                                 final Object falseClauseValue) {
        final Expression predicateClause = mock(Expression.class);
        final Expression trueClause = mock(Expression.class);
        final Expression falseClause = mock(Expression.class);

        when(predicateClause.getValue(Mockito.any())).thenReturn(predicateValue);
        when(trueClause.getValue(Mockito.any())).thenReturn(trueClauseValue);
        when(falseClause.getValue(Mockito.any())).thenReturn(falseClauseValue);

        return new TernaryConditionalExpression(predicateClause, trueClause, falseClause);
    }
}
