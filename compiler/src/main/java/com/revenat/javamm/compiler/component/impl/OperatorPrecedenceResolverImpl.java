
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

import com.revenat.javamm.code.exception.ConfigException;
import com.revenat.javamm.code.fragment.Operator;
import com.revenat.javamm.code.fragment.operator.BinaryOperator;
import com.revenat.javamm.code.fragment.operator.UnaryOperator;
import com.revenat.javamm.compiler.component.OperatorPrecedenceResolver;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author Vitaliy Dragun
 *
 */
public class OperatorPrecedenceResolverImpl implements OperatorPrecedenceResolver {
    private final Map<Operator, Integer> operatorPrecedenceRegistry;

    public OperatorPrecedenceResolverImpl(final Map<Operator, Integer> operatorPrecedenceRegistry) {
        validateAllOperatorsSupport(operatorPrecedenceRegistry);
        this.operatorPrecedenceRegistry = Map.copyOf(operatorPrecedenceRegistry);
    }

    @Override
    public int getPrecedence(final Operator operator) {
        final Integer precedence = operatorPrecedenceRegistry.get(operator);
        if (precedence == null) {
            throw new ConfigException("Precedence not defined for " + operator);
        }

        return precedence;
    }

    @Override
    public boolean hasLowerPrecedence(final Operator first, final Operator second) {
        return getPrecedence(first) < getPrecedence(second);
    }

    private static void validateAllOperatorsSupport(final Map<Operator, Integer> registry) {
        Stream.of(UnaryOperator.values(), BinaryOperator.values())
            .flatMap(Arrays::stream)
            .forEach(operator -> {
                if (!registry.containsKey(operator)) {
                    throw new ConfigException("Precedence not defined for " + operator);
                }
            });
    }
}
