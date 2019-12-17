
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

package com.revenat.javamm.code.fragment.operation;

import com.revenat.javamm.code.fragment.Expression;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.Variable;
import com.revenat.javamm.code.test.doubles.ExpressionDummy;
import com.revenat.javamm.code.test.doubles.VariableDummy;
import com.revenat.juinit.addons.ReplaceCamelCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("a variable declaration operation")
class VariableDeclarationOperationTest {
    private static final SourceLine DUMMY_SOURCE_LINE = SourceLine.EMPTY_SOURCE_LINE;

    private static final Variable DUMMY_VARIABLE = new VariableDummy();

    private static final Expression DUMMY_EXPRESSION = new ExpressionDummy();

    private VariableDeclarationOperation variableDeclarationOperation;

    @Test
    @Order(1)
    void canNotBeCreatedWithoutSourceLine() {
        assertThrows(NullPointerException.class, () -> new VariableDeclarationOperation(null, false, DUMMY_VARIABLE, DUMMY_EXPRESSION));
    }

    @Test
    @Order(2)
    void canNotBeCreatedWithoutVariable() {
        assertThrows(NullPointerException.class, () -> new VariableDeclarationOperation(DUMMY_SOURCE_LINE, false, null, DUMMY_EXPRESSION));
    }

    @Test
    @Order(3)
    void canNotBeCreatedWithoutExpression() {
        assertThrows(NullPointerException.class, () -> new VariableDeclarationOperation(DUMMY_SOURCE_LINE, false, DUMMY_VARIABLE, null));
    }

    @Test
    @Order(4)
    void shouldContainExpression() {
        variableDeclarationOperation = new VariableDeclarationOperation(DUMMY_SOURCE_LINE, false, DUMMY_VARIABLE, DUMMY_EXPRESSION);

        assertThat(variableDeclarationOperation.getExpression(), notNullValue());
    }

    @Test
    @Order(5)
    void shouldContainVariable() {
        variableDeclarationOperation = new VariableDeclarationOperation(DUMMY_SOURCE_LINE, false, DUMMY_VARIABLE, DUMMY_EXPRESSION);

        assertThat(variableDeclarationOperation.getVariable(), notNullValue());
    }

    @Test
    @Order(6)
    void canBeCreatedForConstantVariable() {
        variableDeclarationOperation = new VariableDeclarationOperation(DUMMY_SOURCE_LINE, true, DUMMY_VARIABLE, DUMMY_EXPRESSION);

        assertTrue(variableDeclarationOperation.isConstant());
    }
}
