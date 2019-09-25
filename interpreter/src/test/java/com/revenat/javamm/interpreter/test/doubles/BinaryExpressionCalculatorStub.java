
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
import com.revenat.javamm.interpreter.component.BinaryExpressionCalculator;

public class BinaryExpressionCalculatorStub implements BinaryExpressionCalculator {
    private BinaryOperator supportedOperator = null;
    private Object calculationResult = null;

    public BinaryExpressionCalculatorStub() {
    }

    public BinaryExpressionCalculatorStub(final BinaryOperator supportedOperator) {
        this.supportedOperator = supportedOperator;
    }

    public void setSupportedOperator(final BinaryOperator supportedOperator) {
        this.supportedOperator = supportedOperator;
    }

    public void setCalculationResult(final Object calculationResult) {
        this.calculationResult = calculationResult;
    }

    @Override
    public BinaryOperator getOperator() {
        return supportedOperator;
    }

    @Override
    public Object calculate(final ExpressionContext expressionContext, final Expression operand1, final Expression operand2) {
        return calculationResult;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((supportedOperator == null) ? 0 : supportedOperator.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BinaryExpressionCalculatorStub other = (BinaryExpressionCalculatorStub) obj;
        if (supportedOperator != other.supportedOperator) {
            return false;
        }
        return true;
    }
}