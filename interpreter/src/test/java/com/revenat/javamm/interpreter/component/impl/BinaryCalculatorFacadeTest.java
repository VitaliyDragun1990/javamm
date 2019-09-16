
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

package com.revenat.javamm.interpreter.component.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.revenat.javamm.code.component.ExpressionContext;
import com.revenat.javamm.code.fragment.Expression;
import com.revenat.javamm.code.fragment.operator.BinaryOperator;
import com.revenat.javamm.interpreter.component.BinaryCalculatorFacade;
import com.revenat.javamm.interpreter.test.doubles.BinaryExpressionCalculatorStub;
import com.revenat.javamm.interpreter.test.doubles.ExpressionContextDummy;
import com.revenat.javamm.interpreter.test.doubles.ExpressionDummy;

import java.util.Set;

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
@DisplayName("a binary calculator facade")
class BinaryCalculatorFacadeTest {
    private static final BinaryOperator BINARY_OPERATOR = BinaryOperator.ARITHMETIC_SUBTRACTION;
    private static final ExpressionContext DUMMY_EXPRESSION_CONTEXT = new ExpressionContextDummy();
    private static final Expression OPERAND_1 = new ExpressionDummy();
    private static final Expression OPERAND_2 = new ExpressionDummy();

    private BinaryExpressionCalculatorStub binaryCalculatorStub;

    private BinaryCalculatorFacade calculatorFacade;


    @BeforeEach
    void setUp() {
        binaryCalculatorStub = new BinaryExpressionCalculatorStub();
        binaryCalculatorStub.setSupportedOperator(BINARY_OPERATOR);

        calculatorFacade = new BinaryCalculatorFacadeImpl(Set.of(binaryCalculatorStub));
    }

    @Test
    @Order(1)
    void canNotBeCreatedWithoutCalculators() {
        assertThrows(NullPointerException.class, () -> new BinaryCalculatorFacadeImpl(null));
    }

    @Test
    @Order(2)
    void shouldCalculateBinaryExpression() {
        final int expectedResult = 5;
        binaryCalculatorStub.setCalculationResult(expectedResult);

        final Object result = calculatorFacade.calculate(DUMMY_EXPRESSION_CONTEXT, OPERAND_1, BINARY_OPERATOR, OPERAND_2);

        assertThat(result, equalTo(expectedResult));
    }
}
