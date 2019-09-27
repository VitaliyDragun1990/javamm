
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

package com.revenat.javamm.interpreter.component.impl.calculator.bitwise;

import com.revenat.javamm.code.fragment.operator.UnaryOperator;
import com.revenat.javamm.code.util.TypeUtils;
import com.revenat.javamm.interpreter.component.UnaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.AbstractUnaryExpressionCalculator;

/**
 * {@linkplain UnaryExpressionCalculator Unary expression calculator}
 * implementation for 'bitwise unary inverse' ({@code ~}) operator
 *
 * @author Vitaliy Dragun
 *
 */
public class BitwiseInverseUnaryExpressionCalculator extends AbstractUnaryExpressionCalculator {

    public BitwiseInverseUnaryExpressionCalculator() {
        super(UnaryOperator.BITWISE_INVERSE);
    }

    @Override
    protected Object calculate(final Object value) {
        if (TypeUtils.confirmType(Integer.class, value)) {
            return inverseBytesFor(value);
        }
        throw createNotSupportedTypesError(value);
    }

    private Integer inverseBytesFor(final Object value) {
        final int i = (Integer) value;
        return ~i;
    }
}
