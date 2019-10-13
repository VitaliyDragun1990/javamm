
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

package com.revenat.javamm.interpreter.component.impl.calculator.predicate;

import com.revenat.javamm.code.fragment.operator.BinaryOperator;
import com.revenat.javamm.interpreter.component.BinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.AbstractBinaryExpressionCalculator;

import static com.revenat.javamm.code.util.TypeUtils.confirmType;

/**
 * {@linkplain BinaryExpressionCalculator Binary expression calculator}
 * implementation for 'predicate greater than or equals' ({@code >=}) operator
 *
 * @author Vitaliy Dragun
 *
 */
public class IsGreaterThanOrEqualsBinaryExpressionCalculator extends AbstractBinaryExpressionCalculator {

    public IsGreaterThanOrEqualsBinaryExpressionCalculator() {
        super(BinaryOperator.PREDICATE_GREATER_THAN_OR_EQUALS);
    }

    @Override
    protected Boolean calculate(final Object value1, final Object value2) {
        if (areNumbers(value1, value2)) {
            return calculateForNumbers(value1, value2);
        }
        throw createNotSupportedTypesError(value1, value2);
    }

    private boolean calculateForNumbers(final Object value1, final Object value2) {
        return ((Number) value1).doubleValue() >= ((Number) value2).doubleValue();
    }

    private boolean areNumbers(final Object value1, final Object value2) {
        return confirmType(Number.class, value1, value2);
    }
}
