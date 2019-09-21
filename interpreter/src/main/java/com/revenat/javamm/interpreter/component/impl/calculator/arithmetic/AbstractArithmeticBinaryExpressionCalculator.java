
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

package com.revenat.javamm.interpreter.component.impl.calculator.arithmetic;

import com.revenat.javamm.code.fragment.operator.BinaryOperator;
import com.revenat.javamm.interpreter.component.impl.calculator.AbstractBinaryExpressionCalculator;

/**
 * @author Vitaliy Dragun
 *
 */
abstract class AbstractArithmeticBinaryExpressionCalculator extends AbstractBinaryExpressionCalculator {

    protected AbstractArithmeticBinaryExpressionCalculator(final BinaryOperator operator) {
        super(operator);
    }

    protected boolean areIntegers(final Object value1, final Object value2) {
        return value1 instanceof Integer && value2 instanceof Integer;
    }

    protected boolean areDoubles(final Object value1, final Object value2) {
        return value1 instanceof Double && value2 instanceof Double;
    }

    protected boolean areNumbers(final Object value1, final Object value2) {
        return value1 instanceof Number && value2 instanceof Number;
    }
}
