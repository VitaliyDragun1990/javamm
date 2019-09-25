
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
 * implementation for 'predicate equals' ({@code ==}) operator
 *
 * @author Vitaliy Dragun
 *
 */
public class IsEqualsBinaryExpressionCalculator extends AbstractBinaryExpressionCalculator {

    public IsEqualsBinaryExpressionCalculator() {
        super(BinaryOperator.PREDICATE_EQUALS);
    }

    @Override
    protected Boolean calculate(final Object value1, final Object value2) {
        if (booleanAgainstNotBoolean(value1, value2)) {
            throw createNotSupportedTypesError(value1, value2);
        } else {
            return calculateEquals(value1, value2);
        }
    }

    private boolean booleanAgainstNotBoolean(final Object value1, final Object value2) {
        return (confirmType(Boolean.class, value1) && !confirmType(Boolean.class, value2)) ||
                (!confirmType(Boolean.class, value1) && confirmType(Boolean.class, value2));
    }

    private boolean calculateEquals(final Object value1, final Object value2) {
        if (confirmType(Number.class, value1, value2)) {
            return calculateEqualsForNumbers(value1, value2);
        } else if (areNotNull(value1, value2)) {
            return value1.equals(value2);
        } else {
            return value1 == value2;
        }
    }

    private boolean areNotNull(final Object value1, final Object value2) {
        return value1 != null && value2 != null;
    }

    private boolean calculateEqualsForNumbers(final Object value1, final Object value2) {
        return ((Number) value1).doubleValue() == ((Number) value2).doubleValue();
    }
}
