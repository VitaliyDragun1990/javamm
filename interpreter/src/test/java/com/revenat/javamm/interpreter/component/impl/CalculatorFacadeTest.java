
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
import com.revenat.javamm.code.exception.ConfigException;
import com.revenat.javamm.code.fragment.Expression;
import com.revenat.javamm.code.fragment.operator.BinaryOperator;
import com.revenat.javamm.code.fragment.operator.UnaryOperator;
import com.revenat.javamm.interpreter.component.BinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.CalculatorFacade;
import com.revenat.javamm.interpreter.component.UnaryExpressionCalculator;
import com.revenat.javamm.interpreter.test.doubles.BinaryExpressionCalculatorStub;
import com.revenat.javamm.interpreter.test.doubles.ExpressionContextDummy;
import com.revenat.javamm.interpreter.test.doubles.ExpressionDummy;
import com.revenat.javamm.interpreter.test.doubles.UnaryExpressionCalculatorStub;

import java.util.HashSet;
import java.util.Set;

import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ARITHMETIC_ADDITION;
import static com.revenat.javamm.code.fragment.operator.UnaryOperator.ARITHMETICAL_UNARY_PLUS;
import static com.revenat.javamm.interpreter.test.helper.CustomAsserts.assertErrorMessageContains;

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
class CalculatorFacadeTest {
    private static final ExpressionContext DUMMY_EXPRESSION_CONTEXT = new ExpressionContextDummy();
    private static final Expression OPERAND_1 = new ExpressionDummy();
    private static final Expression OPERAND_2 = new ExpressionDummy();

    private CalculatorFacade calculatorFacade;

    private Set<BinaryExpressionCalculator> calculatorsFor(final BinaryOperator...operators) {
        final Set<BinaryExpressionCalculator> calculators = new HashSet<>();

        for(final BinaryOperator operator : operators) {
            final BinaryExpressionCalculatorStub calculator = new BinaryExpressionCalculatorStub(operator);
            calculators.add(calculator);
        }

        return calculators;
    }

    private Set<UnaryExpressionCalculator> calculatorsFor(final UnaryOperator...operators) {
        final Set<UnaryExpressionCalculator> calculators = new HashSet<>();

        for(final UnaryOperator operator : operators) {
            final UnaryExpressionCalculatorStub calculator = new UnaryExpressionCalculatorStub(operator);
            calculators.add(calculator);
        }

        return calculators;
    }

    private Set<BinaryExpressionCalculator> calculatorsForAllOperatorsExcept(final BinaryOperator... excludedOperators) {
        final Set<BinaryOperator> excluded = Set.of(excludedOperators);
        final Set<BinaryExpressionCalculator> calculators = calculatorsFor(BinaryOperator.values());

        calculators.removeIf(c -> excluded.contains(c.getOperator()));

        return calculators;
    }

    private Set<UnaryExpressionCalculator> calculatorsForAllOperatorsExcept(final UnaryOperator... excludedOperators) {
        final Set<UnaryOperator> excluded = Set.of(excludedOperators);
        final Set<UnaryExpressionCalculator> calculators = calculatorsFor(UnaryOperator.values());

        calculators.removeIf(c -> excluded.contains(c.getOperator()));

        return calculators;
    }

    private BinaryExpressionCalculatorStub calculatorForOperatorWithExpectedResult(final BinaryOperator operator, final Object expectedResult) {
        final BinaryExpressionCalculatorStub binaryCalculatorStub = new BinaryExpressionCalculatorStub(operator);
        binaryCalculatorStub.setCalculationResult(expectedResult);
        return binaryCalculatorStub;
    }

    @Test
    @Order(1)
    void canNotBeCreatedWithoutCalculators() {
        assertThrows(NullPointerException.class, () -> new CalculatorFacadeImpl(null, null));
    }

    @Test
    @Order(2)
    void canBeCreatedWithCalculatorsForAllOperators() {
        calculatorFacade = new CalculatorFacadeImpl(calculatorsFor(BinaryOperator.values()), calculatorsFor(UnaryOperator.values()));
    }

    @Test
    @Order(3)
    void canoNotBeCreatedWithoutCalculatosForBinaryOperators() {
        final Set<BinaryExpressionCalculator> notAllBinaryCalculators = calculatorsForAllOperatorsExcept(ARITHMETIC_ADDITION);
        final Set<UnaryExpressionCalculator> allUnaryCalculators = calculatorsFor(UnaryOperator.values());

        final ConfigException e =
                assertThrows(ConfigException.class, () -> new CalculatorFacadeImpl(notAllBinaryCalculators, allUnaryCalculators));

        assertErrorMessageContains(e, "Missing calculator for binary operator: %s", ARITHMETIC_ADDITION);
    }

    @Test
    @Order(4)
    void canoNotBeCreatedWithoutCalculatosForUnaryOperators() {
        final Set<BinaryExpressionCalculator> allBinaryCalculators = calculatorsFor(BinaryOperator.values());
        final Set<UnaryExpressionCalculator> notAllUnaryCalculators = calculatorsForAllOperatorsExcept(ARITHMETICAL_UNARY_PLUS);

        final ConfigException e =
                assertThrows(ConfigException.class, () -> new CalculatorFacadeImpl(allBinaryCalculators, notAllUnaryCalculators));

        assertErrorMessageContains(e, "Missing calculator for unary operator: %s", ARITHMETICAL_UNARY_PLUS);
    }

    @Test
    @Order(5)
    void shouldCalculateBinaryExpression() {
        final Set<UnaryExpressionCalculator> allUnaryCalculators = calculatorsFor(UnaryOperator.values());
        final Set<BinaryExpressionCalculator> allBinaryCalculators = calculatorsForAllOperatorsExcept(ARITHMETIC_ADDITION);
        allBinaryCalculators.add(calculatorForOperatorWithExpectedResult(ARITHMETIC_ADDITION, 5));
        calculatorFacade = new CalculatorFacadeImpl(allBinaryCalculators, allUnaryCalculators);

        final Object result = calculatorFacade.calculate(
                                                         DUMMY_EXPRESSION_CONTEXT,
                                                         OPERAND_1,
                                                         ARITHMETIC_ADDITION,
                                                         OPERAND_2);

        assertThat(result, equalTo(5));
    }
}
