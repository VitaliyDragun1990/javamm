
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

import com.revenat.javamm.code.component.ExpressionContext;
import com.revenat.javamm.code.exception.ConfigException;
import com.revenat.javamm.code.fragment.Expression;
import com.revenat.javamm.code.fragment.operator.BinaryOperator;
import com.revenat.javamm.interpreter.component.BinaryCalculatorFacade;
import com.revenat.javamm.interpreter.component.BinaryExpressionCalculator;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toUnmodifiableMap;

/**
 * @author Vitaliy Dragun
 *
 */
public class BinaryCalculatorFacadeImpl implements BinaryCalculatorFacade {
    private final Map<BinaryOperator, BinaryExpressionCalculator> binaryExpressionCalculatorMap;

    public BinaryCalculatorFacadeImpl(final Set<BinaryExpressionCalculator> binaryExpressionCalculators) {
        binaryExpressionCalculatorMap = getBinaryExpressionCalculatorMap(requireNonNull(binaryExpressionCalculators));
        // validateAllOperatorsSupported(binaryExpressionCalculatorMap.keySet(), BinaryOperator.values());
    }

    @Override
    public Object calculate(final ExpressionContext expressionContext, final Expression operand1,
            final BinaryOperator operator, final Expression operand2) {
        final BinaryExpressionCalculator calculator = getBinaryCalculatorFor(operator);
        return calculator.calculate(expressionContext, operand1, operand2);
    }

    private BinaryExpressionCalculator getBinaryCalculatorFor(final BinaryOperator operator) {
        return binaryExpressionCalculatorMap.get(operator);
    }

    private Map<BinaryOperator, BinaryExpressionCalculator> getBinaryExpressionCalculatorMap(
            final Set<BinaryExpressionCalculator> binaryExpressionCalculators) {
        return binaryExpressionCalculators
                .stream()
                .collect(toUnmodifiableMap(BinaryExpressionCalculator::getOperator, identity()));
    }

    private void validateAllOperatorsSupported(final Set<BinaryOperator> currentlySupported,
            final BinaryOperator[] allBianryOperators) {
        Arrays.stream(allBianryOperators)
                .forEach(operator -> {
                    if (!currentlySupported.contains(operator)) {
                        throw new ConfigException("Missing calculator for binary operator: " + operator);
                    }
                });
    }

}
