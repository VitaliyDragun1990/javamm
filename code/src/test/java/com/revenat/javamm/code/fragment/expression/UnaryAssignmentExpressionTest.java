/*
 *
 *  Copyright (c) 2019. http://devonline.academy
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.revenat.javamm.code.fragment.expression;

import com.revenat.javamm.code.fragment.operator.UnaryOperator;
import com.revenat.javamm.code.test.doubles.VariableDummy;
import com.revenat.juinit.addons.ReplaceCamelCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;

import static com.revenat.javamm.code.fragment.operator.UnaryOperator.ARITHMETICAL_UNARY_PLUS;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("an unary assignment expression")
class UnaryAssignmentExpressionTest {

    @Test
    void shouldFailToCreateIfSpecifiedUnaryOperatorIsNotAssignmentOne() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
            () -> new TestUnaryAssignmentExpression(new VariableExpression(new VariableDummy()), ARITHMETICAL_UNARY_PLUS));

        assertThat(e.getMessage(), containsString("is not assignment operator"));
    }

    private static class TestUnaryAssignmentExpression extends UnaryAssignmentExpression {

        protected TestUnaryAssignmentExpression(final VariableExpression operand, final UnaryOperator operator) {
            super(operand, operator);
        }
    }
}