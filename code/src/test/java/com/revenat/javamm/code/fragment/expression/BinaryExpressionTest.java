
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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.revenat.javamm.code.fragment.Expression;
import com.revenat.javamm.code.fragment.operator.BinaryOperator;
import com.revenat.javamm.code.test.doubles.ExpressionDummy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.revenat.juinit.addons.ReplaceCamelCase;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("a binary expression")
class BinaryExpressionTest {
    private static final Expression OPERAND_1 = new ExpressionDummy();
    private static final Expression OPERAND_2 = new ExpressionDummy();
    private static final BinaryOperator OPERATOR = BinaryOperator.ARITHMETIC_ADDITION;

    @Test
    @Order(1)
    void canNotBeCreatedWithoutFirstOperand() {
        assertCanNotBeCreated(() -> new BinaryExpression(null, OPERATOR, OPERAND_2));
    }

    @Test
    @Order(2)
    void canNotBeCreatedWithoutOperator() {
        assertCanNotBeCreated(() -> new BinaryExpression(OPERAND_1, null, OPERAND_2));
    }

    @Test
    @Order(3)
    void canNotBeCreatedWithoutSecondOperand() {
        assertCanNotBeCreated(() -> new BinaryExpression(OPERAND_1, OPERATOR, null));
    }

    @Test
    @Order(4)
    void canBeCreatedWithTwoOperandsAndOperator() {
        new BinaryExpression(OPERAND_1, OPERATOR, OPERAND_2);
    }

    @Test
    @Order(5)
    void shouldProvideItsOperandsAndOperator() {
        final BinaryExpression expression = new BinaryExpression(OPERAND_1, OPERATOR, OPERAND_2);

        assertThat(expression.getOperand1(), equalTo(OPERAND_1));
        assertThat(expression.getOperand2(), equalTo(OPERAND_2));
        assertThat(expression.getOperator(), equalTo(OPERATOR));
    }

    private void assertCanNotBeCreated(final Runnable expression) {
        assertThrows(NullPointerException.class, () -> expression.run());
    }

}
