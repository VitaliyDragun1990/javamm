
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
import com.revenat.javamm.code.fragment.operator.UnaryOperator;
import com.revenat.javamm.interpreter.component.BinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.CalculatorFacade;
import com.revenat.javamm.interpreter.component.UnaryExpressionCalculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.System.lineSeparator;
import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toUnmodifiableMap;

/**
 * @author Vitaliy Dragun
 *
 */
public class CalculatorFacadeImpl implements CalculatorFacade {

    private final Map<BinaryOperator, BinaryExpressionCalculator> binaryCalculatorRegistry;

    private final Map<UnaryOperator, UnaryExpressionCalculator> unaryCalculatorRegistry;

    public CalculatorFacadeImpl(final Set<BinaryExpressionCalculator> binaryExpressionCalculators,
            final Set<UnaryExpressionCalculator> unaryExpressionCalculators) {

        binaryCalculatorRegistry = buildBinaryExpressionCalculatorRegistry(requireNonNull(binaryExpressionCalculators));
        unaryCalculatorRegistry = buildUnaryExpressionCalculatorRegistry(unaryExpressionCalculators);
        assertAllOperatorsSupported();
    }

    @Override
    public Object calculate(final ExpressionContext expressionContext, final Expression operand1,
            final BinaryOperator operator, final Expression operand2) {
        final BinaryExpressionCalculator calculator = getBinaryCalculatorFor(operator);
        return calculator.calculate(expressionContext, operand1, operand2);
    }

    @Override
    public Object calculate(final ExpressionContext expressionContext, final UnaryOperator operator,
            final Expression operand) {
        final UnaryExpressionCalculator calculator = getUnaryCalculatorFor(operator);
        return calculator.calculate(expressionContext, operand);
    }

    private BinaryExpressionCalculator getBinaryCalculatorFor(final BinaryOperator operator) {
        return binaryCalculatorRegistry.get(operator);
    }

    private UnaryExpressionCalculator getUnaryCalculatorFor(final UnaryOperator operator) {
        return unaryCalculatorRegistry.get(operator);
    }

    private Map<BinaryOperator, BinaryExpressionCalculator> buildBinaryExpressionCalculatorRegistry(
            final Set<BinaryExpressionCalculator> binaryExpressionCalculators) {
        return binaryExpressionCalculators
                .stream()
                .collect(toUnmodifiableMap(BinaryExpressionCalculator::getOperator, identity()));
    }

    private Map<UnaryOperator, UnaryExpressionCalculator> buildUnaryExpressionCalculatorRegistry(
            final Set<UnaryExpressionCalculator> unaryExpressionCalculators) {
        return unaryExpressionCalculators
                .stream()
                .collect(toUnmodifiableMap(UnaryExpressionCalculator::getOperator, identity()));
    }

    private void assertAllOperatorsSupported() {
        final List<String> errorMessages = new ArrayList<>();

        final Collection<BinaryOperator> supportedBinary = binaryCalculatorRegistry.keySet();
        final Collection<UnaryOperator> supportedUnary = unaryCalculatorRegistry.keySet();

        errorMessages.addAll(assertBinaryOperatorsSupported(supportedBinary));
        errorMessages.addAll(assertAllUnaryOperatorsSupported(supportedUnary));

        processMessages(errorMessages);
    }

    private List<String> assertBinaryOperatorsSupported(final Collection<BinaryOperator> supportedBinary) {
        final List<String> errorMessages = new ArrayList<>();

        Arrays.stream(BinaryOperator.values()).forEach(operator -> {
            if (!supportedBinary.contains(operator)) {
                errorMessages.add("Missing calculator for binary operator: " + operator);
            }
        });

        return errorMessages;
    }

    private List<String> assertAllUnaryOperatorsSupported(final Collection<UnaryOperator> supportedUnary) {
        final List<String> errorMessages = new ArrayList<>();

        Arrays.stream(UnaryOperator.values()).forEach(operator -> {
            if (!supportedUnary.contains(operator)) {
                errorMessages.add("Missing calculator for unary operator: " + operator);
            }
        });

        return errorMessages;
    }

    private void processMessages(final List<String> errorMessages) {
        if (!errorMessages.isEmpty()) {
            final String compositeErrorMessage = createCompositeErrorMessage(errorMessages);
            throw new ConfigException(compositeErrorMessage);
        }
    }

    private String createCompositeErrorMessage(final List<String> errorMessages) {
        return lineSeparator() + errorMessages.stream().collect(joining(lineSeparator()));
    }
}
