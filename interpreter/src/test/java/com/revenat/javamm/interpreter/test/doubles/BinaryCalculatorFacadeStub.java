
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

package com.revenat.javamm.interpreter.test.doubles;

import com.revenat.javamm.code.component.ExpressionContext;
import com.revenat.javamm.code.fragment.Expression;
import com.revenat.javamm.code.fragment.operator.BinaryOperator;
import com.revenat.javamm.interpreter.component.BinaryCalculatorFacade;

public class BinaryCalculatorFacadeStub implements BinaryCalculatorFacade {
    private Object calculatedValue;

    public void setCalculatedValue(final Object calculatedValue) {
        this.calculatedValue = calculatedValue;
    }

    @Override
    public Object calculate(final ExpressionContext expressionContext, final Expression operand1, final BinaryOperator operator,
            final Expression operand2) {
        return calculatedValue;
    }
}