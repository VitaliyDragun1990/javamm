
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
import com.revenat.javamm.interpreter.component.impl.error.JavammLineRuntimeError;

/**
 * {@linkplain BinaryExpressionCalculator Binary expression calculator}
 * implementation for 'predicate less than' ({@code <}) operator
 *
 * @author Vitaliy Dragun
 */
public class IsLessThanBinaryExpressionCalculator extends AbstractBinaryExpressionCalculator {
    private final IsGreaterThanOrEqualsBinaryExpressionCalculator oppositeCalculator;

    public IsLessThanBinaryExpressionCalculator() {
        super(BinaryOperator.PREDICATE_LESS_THAN);
        oppositeCalculator = new IsGreaterThanOrEqualsBinaryExpressionCalculator();
    }

    @Override
    protected Object calculate(final Object value1, final Object value2) {
        try {
            return calculateWithOppositeCalculator(value1, value2);
        } catch (final JavammLineRuntimeError e) {
            throw createNotSupportedTypesError(value1, value2);
        }
    }

    private boolean calculateWithOppositeCalculator(final Object value1, final Object value2) {
        return !oppositeCalculator.calculate(value1, value2);
    }
}
