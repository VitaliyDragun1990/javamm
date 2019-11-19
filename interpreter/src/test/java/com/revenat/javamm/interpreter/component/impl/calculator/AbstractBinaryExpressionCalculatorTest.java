
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

package com.revenat.javamm.interpreter.component.impl.calculator;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.revenat.javamm.code.component.ExpressionContext;
import com.revenat.javamm.code.fragment.Expression;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.operator.BinaryOperator;
import com.revenat.javamm.interpreter.component.BinaryExpressionCalculator;
import com.revenat.javamm.interpreter.test.doubles.ExpressionContextDummy;
import com.revenat.javamm.interpreter.test.doubles.ExpressionStub;
import com.revenat.javamm.interpreter.test.helper.TestCurrentRuntimeManager;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;

import com.revenat.juinit.addons.ReplaceCamelCase;

/**
 * Abstract parent class for testing all {@linkplain BinaryExpressionCalculator
 * binary expression calculator} implementations
 *
 * @author Vitaliy Dragun
 *
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
public abstract class AbstractBinaryExpressionCalculatorTest {
    static final ExpressionContext EXPRESSION_CONTEXT_DUMMY = new ExpressionContextDummy();

    protected BinaryExpressionCalculator calculator;

    @BeforeAll
    static void setupCurrentRuntimeStub() {
        TestCurrentRuntimeManager.setFakeCurrentRuntime(SourceLine.EMPTY_SOURCE_LINE);
    }

    @AfterAll
    static void releaseCurrentRuntime() {
        TestCurrentRuntimeManager.releaseFakeCurrentRuntime();
    }

    @BeforeEach
    void setUp() {
        calculator = createCalculatorUnderTest();
    }

    protected abstract BinaryExpressionCalculator createCalculatorUnderTest();

    protected Expression expressionWithValue(final Object value) {
        return new ExpressionStub(value);
    }

    protected Object calculate(final Object operand1, final Object operand2) {
        return calculator.calculate(EXPRESSION_CONTEXT_DUMMY, expressionWithValue(operand1), expressionWithValue(operand2));
    }

    protected Object calculate(final Expression operand1, final Expression operand2) {
        return calculator.calculate(EXPRESSION_CONTEXT_DUMMY, operand1, operand2);
    }

    protected void assertCalculatorSupportsOperator(final BinaryExpressionCalculator calc, final BinaryOperator operator) {
        assertThat(calc.getOperator(), is(operator));
    }
}
