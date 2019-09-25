
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

import com.revenat.javamm.code.fragment.expression.TypeExpression;
import com.revenat.javamm.code.fragment.operator.BinaryOperator;
import com.revenat.javamm.interpreter.component.BinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.AbstractBinaryExpressionCalculator;

import static com.revenat.javamm.code.util.TypeUtils.confirmType;

/**
 * {@linkplain BinaryExpressionCalculator Binary expression calculator}
 * implementation for 'predicate typeof' ({@code typeof}) operator
 *
 * @author Vitaliy Dragun
 *
 */
public class TypeOfBinaryExpressionCalculator extends AbstractBinaryExpressionCalculator {

    public TypeOfBinaryExpressionCalculator() {
        super(BinaryOperator.PREDICATE_TYPEOF);
    }

    @Override
    protected Object calculate(final Object value1, final Object value2) {
        if (value1 == null) {
            return false;
        } else if (confirmType(TypeExpression.class, value2)) {
            return value1.getClass().equals(((TypeExpression) value2).getType());
        }
        throw createNotSupportedTypesError(value1, value2);
    }
}
