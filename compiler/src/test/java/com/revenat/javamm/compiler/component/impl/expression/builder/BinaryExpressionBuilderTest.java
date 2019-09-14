
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

package com.revenat.javamm.compiler.component.impl.expression.builder;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.revenat.javamm.code.fragment.Expression;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.expression.BinaryExpression;
import com.revenat.javamm.code.fragment.operator.BinaryOperator;
import com.revenat.javamm.compiler.component.SingleTokenExpressionBuilder;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;
import com.revenat.javamm.compiler.test.doubles.ExpressionDummy;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.revenat.juinit.addons.ReplaceCamelCase;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("a binary expression builder")
class BinaryExpressionBuilderTest implements SingleTokenExpressionBuilder {
    private static final SourceLine SOURCE_LINE = SourceLine.EMPTY_SOURCE_LINE;
    private static final Expression OPERAND_1 = new ExpressionDummy();
    private static final Expression OPERAND_2 = new ExpressionDummy();

    private BinaryExpressionBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new BinaryExpressionBuilder(this);
    }

    private List<String> tokensFrom(final String string) {
        return List.of(string.split(" "));
    }

    private void assertBuiltCorrectly(final BinaryExpression expression) {
        assertThat(expression.getOperand1(), sameInstance(OPERAND_1));
        assertThat(expression.getOperand2(), sameInstance(OPERAND_2));
        assertThat(expression.getOperator(), sameInstance(BinaryOperator.ARITHMETIC_ADDITION));
    }

    @Test
    @Order(1)
    void canBuildBinaryExpressionFromThreeTokensOnly() {
        assertFalse(builder.canBuild(tokensFrom("a +")));
        assertTrue(builder.canBuild(tokensFrom("a - 10")));
    }

    @Test
    @Order(2)
    void canBuildBinaryExpression() {
        final BinaryExpression expression = builder.build(tokensFrom("a + b"), SOURCE_LINE);

        assertBuiltCorrectly(expression);
    }

    @Test
    @Order(3)
    void shouldFailToBuildIfBinaryOperatorIsInvalid() {
        final JavammLineSyntaxError e = assertThrows(
                JavammLineSyntaxError.class,
                () -> builder.build(tokensFrom("a ~ b"), SOURCE_LINE));

        assertThat(e.getMessage(), containsString("Unsupported binary operator: ~"));
    }

    @Override
    public boolean canBuild(final List<String> tokens) {
        return true;
    }

    @Override
    public Expression build(final List<String> expressionTokens, final SourceLine sourceLine) {
        if (expressionTokens.get(0).contentEquals("a")) {
            return OPERAND_1;
        } else {
            return OPERAND_2;
        }
    }
}
