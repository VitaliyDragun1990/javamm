
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
import com.revenat.javamm.code.fragment.operator.TernaryConditionalOperator;
import com.revenat.javamm.code.fragment.operator.UnaryOperator;
import com.revenat.javamm.compiler.component.OperatorPrecedenceResolver;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ARITHMETIC_ADDITION;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ARITHMETIC_DIVISION;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ARITHMETIC_MODULUS;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ARITHMETIC_MULTIPLICATION;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ARITHMETIC_SUBTRACTION;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ASSIGNMENT_ADDITION;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ASSIGNMENT_BITWISE_AND;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ASSIGNMENT_BITWISE_OR;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ASSIGNMENT_BITWISE_SHIFT_LEFT;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ASSIGNMENT_BITWISE_SHIFT_RIGHT;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ASSIGNMENT_BITWISE_SHIFT_RIGHT_ZERO_FILL;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ASSIGNMENT_BITWISE_XOR;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ASSIGNMENT_DIVISION;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ASSIGNMENT_MODULUS;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ASSIGNMENT_MULTIPLICATION;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ASSIGNMENT_SUBTRACTION;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.BITWISE_AND;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.BITWISE_OR;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.BITWISE_SHIFT_LEFT;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.BITWISE_SHIFT_RIGHT;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.BITWISE_SHIFT_RIGHT_ZERO_FILL;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.BITWISE_XOR;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.LOGICAL_AND;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.LOGICAL_OR;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.PREDICATE_EQUALS;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.PREDICATE_GREATER_THAN;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.PREDICATE_GREATER_THAN_OR_EQUALS;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.PREDICATE_LESS_THAN;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.PREDICATE_LESS_THAN_OR_EQUALS;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.PREDICATE_NOT_EQUALS;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.PREDICATE_TYPEOF;
import static com.revenat.javamm.code.fragment.operator.UnaryOperator.ARITHMETICAL_UNARY_MINUS;
import static com.revenat.javamm.code.fragment.operator.UnaryOperator.ARITHMETICAL_UNARY_PLUS;
import static com.revenat.javamm.code.fragment.operator.UnaryOperator.BITWISE_INVERSE;
import static com.revenat.javamm.code.fragment.operator.UnaryOperator.DECREMENT;
import static com.revenat.javamm.code.fragment.operator.UnaryOperator.INCREMENT;
import static com.revenat.javamm.code.fragment.operator.UnaryOperator.LOGICAL_NOT;

import static java.util.Map.entry;
import static java.util.Map.ofEntries;

/**
 * @author Vitaliy Dragun
 *
 */
public class OperatorPrecedenceResolverImpl implements OperatorPrecedenceResolver {
    private static final int MAX_PRECEDENCE = 20;

    private static final Map<Operator, Integer> OPERATOR_PRECEDENCE_REGISTRY = ofEntries(
            entry(INCREMENT, MAX_PRECEDENCE - 1),
            entry(DECREMENT, MAX_PRECEDENCE - 1),
            entry(ARITHMETICAL_UNARY_PLUS, MAX_PRECEDENCE - 1),
            entry(ARITHMETICAL_UNARY_MINUS, MAX_PRECEDENCE - 1),
            entry(BITWISE_INVERSE, MAX_PRECEDENCE - 1),
            entry(LOGICAL_NOT, MAX_PRECEDENCE - 1),
            //
            entry(ARITHMETIC_MULTIPLICATION, MAX_PRECEDENCE - 2),
            entry(ARITHMETIC_DIVISION, MAX_PRECEDENCE - 2),
            entry(ARITHMETIC_MODULUS, MAX_PRECEDENCE - 2),
            //
            entry(ARITHMETIC_ADDITION, MAX_PRECEDENCE - 3),
            entry(ARITHMETIC_SUBTRACTION, MAX_PRECEDENCE - 3),
            //
            entry(BITWISE_SHIFT_LEFT, MAX_PRECEDENCE - 4),
            entry(BITWISE_SHIFT_RIGHT, MAX_PRECEDENCE - 4),
            entry(BITWISE_SHIFT_RIGHT_ZERO_FILL, MAX_PRECEDENCE - 4),
            //
            entry(PREDICATE_GREATER_THAN, MAX_PRECEDENCE - 5),
            entry(PREDICATE_GREATER_THAN_OR_EQUALS, MAX_PRECEDENCE - 5),
            entry(PREDICATE_LESS_THAN, MAX_PRECEDENCE - 5),
            entry(PREDICATE_LESS_THAN_OR_EQUALS, MAX_PRECEDENCE - 5),
            entry(PREDICATE_TYPEOF, MAX_PRECEDENCE - 5),
            //
            entry(PREDICATE_NOT_EQUALS, MAX_PRECEDENCE - 6),
            entry(PREDICATE_EQUALS, MAX_PRECEDENCE - 6),
            //
            entry(BITWISE_AND, MAX_PRECEDENCE - 7),
            //
            entry(BITWISE_XOR, MAX_PRECEDENCE - 8),
            //
            entry(BITWISE_OR, MAX_PRECEDENCE - 9),
            //
            entry(LOGICAL_AND, MAX_PRECEDENCE - 10),
            //
            entry(LOGICAL_OR, MAX_PRECEDENCE - 11),
            //
            entry(TernaryConditionalOperator.OPERATOR, MAX_PRECEDENCE - 12),
            //
            entry(ASSIGNMENT_MULTIPLICATION, MAX_PRECEDENCE - 13),
            entry(ASSIGNMENT_DIVISION, MAX_PRECEDENCE - 13),
            entry(ASSIGNMENT_MODULUS, MAX_PRECEDENCE - 13),
            entry(ASSIGNMENT_ADDITION, MAX_PRECEDENCE - 13),
            entry(ASSIGNMENT_SUBTRACTION, MAX_PRECEDENCE - 13),
            entry(ASSIGNMENT_BITWISE_SHIFT_LEFT, MAX_PRECEDENCE - 13),
            entry(ASSIGNMENT_BITWISE_SHIFT_RIGHT, MAX_PRECEDENCE - 13),
            entry(ASSIGNMENT_BITWISE_SHIFT_RIGHT_ZERO_FILL, MAX_PRECEDENCE - 13),
            entry(ASSIGNMENT_BITWISE_AND, MAX_PRECEDENCE - 13),
            entry(ASSIGNMENT_BITWISE_XOR, MAX_PRECEDENCE - 13),
            entry(ASSIGNMENT_BITWISE_OR, MAX_PRECEDENCE - 13)
            );

    static {
        validateAllOperatorsSupport();
    }

    @Override
    public int getPrecedence(final Operator operator) {
        final Integer precedence = OPERATOR_PRECEDENCE_REGISTRY.get(operator);
        if (precedence == null) {
            throw new ConfigException("Precedence not defined for " + operator);
        }

        return precedence;
    }

    @Override
    public boolean hasLowerPrecedence(final Operator first, final Operator second) {
        return getPrecedence(first) < getPrecedence(second);
    }

    @SuppressWarnings("unlikely-arg-type")
    private static void validateAllOperatorsSupport() {
        Stream.of(UnaryOperator.values(), BinaryOperator.values())
            .flatMap(Arrays::stream)
            .forEach(operator -> {
                if (!OPERATOR_PRECEDENCE_REGISTRY.containsKey(operator)) {
                    throw new ConfigException("Precedence not defined for " + operator);
                }
            });
    }
}
