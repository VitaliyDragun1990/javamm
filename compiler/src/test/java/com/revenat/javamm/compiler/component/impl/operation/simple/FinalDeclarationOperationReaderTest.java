
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

package com.revenat.javamm.compiler.component.impl.operation.simple;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.expression.NullValueExpression;
import com.revenat.javamm.code.fragment.operation.VariableDeclarationOperation;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;
import com.revenat.javamm.compiler.test.doubles.ExpressionDummy;
import com.revenat.javamm.compiler.test.doubles.ExpressionResolverSpy;
import com.revenat.javamm.compiler.test.doubles.VariableBuilderStub;
import com.revenat.javamm.compiler.test.doubles.VariableDummy;

import java.util.List;
import java.util.ListIterator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.revenat.juinit.addons.ReplaceCamelCase;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("a final declaration operation reader")
class FinalDeclarationOperationReaderTest {
    private static final ListIterator<SourceLine> DUMMY_CODE = List.of(SourceLine.EMPTY_SOURCE_LINE).listIterator();

    private VariableBuilderStub variableBuilderStub;
    private ExpressionResolverSpy expressionResolverStub;

    private FinalDeclarationOperationReader operationReader;


    @BeforeEach
    void setUp() {
        variableBuilderStub = new VariableBuilderStub();
        variableBuilderStub.setVariableToBuild(new VariableDummy());

        expressionResolverStub = new ExpressionResolverSpy();
        expressionResolverStub.setExpressionToResolve(new ExpressionDummy());

        operationReader = new FinalDeclarationOperationReader(variableBuilderStub, expressionResolverStub);
    }

    private SourceLine sourceLine(final String code) {
        final String[] tokens = code.split(" ");
        return new SourceLine("test", 1, List.of(tokens));
    }

    private void assertErrorMessage(final JavammLineSyntaxError e, final String msg) {
        assertThat(e.getMessage(), containsString(msg));
    }

    private void assertConstant(final VariableDeclarationOperation operation) {
        assertTrue(operation.isConstant());
    }

    private void assertCorrectExpressionTokens(final String... varExpressionTokens) {
        assertThat(expressionResolverStub.getLastExpressionTokens(), equalTo(List.of(varExpressionTokens)));
    }

    @Test
    @Order(1)
    void canNotBeCreatedWithoutVariableBuilder() {
        assertThrows(NullPointerException.class, () -> new VariableDeclarationOperationReader(null, expressionResolverStub));
    }

    @Test
    @Order(2)
    void canNotBeCreatedWithoutExpressionResolver() {
        assertThrows(NullPointerException.class, () -> new VariableDeclarationOperationReader(variableBuilderStub, null));
    }

    @Test
    @Order(3)
    void canNotReadVariableDeclarationWithoutFinalKeyword() {
        assertFalse(operationReader.canRead(sourceLine("a = 10")));
    }

    @Test
    @Order(4)
    void canReadCorrectFinalDeclaration() {
        assertTrue(operationReader.canRead(sourceLine("final a = 10")));
    }

    @Test
    @Order(5)
    void shouldFailToReadDeclarationWithoutFinalName() {
       final JavammLineSyntaxError e =
               assertThrows(JavammLineSyntaxError.class, () -> operationReader.read(sourceLine("final"), DUMMY_CODE));

       assertErrorMessage(e, "Final name is missing");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "final a +",
            "final + a",
            "final = a",
            "final a + 3",
            "final a + ="
            })
    @Order(6)
    void shouldFailToReadDeclarationWithMisplacedEqualSignIfAny(final String code) {
        final JavammLineSyntaxError e =
                assertThrows(JavammLineSyntaxError.class, () -> operationReader.read(sourceLine(code), DUMMY_CODE));

        assertErrorMessage(e, "'=' is missing or has invalid position");
    }

    @Test
    @Order(7)
    void shouldFailToReadDeclarationWithEqualSignButWithoutExpression() {
        final JavammLineSyntaxError e =
                assertThrows(JavammLineSyntaxError.class, () -> operationReader.read(sourceLine("final a ="), DUMMY_CODE));

        assertErrorMessage(e, "Final expression is missing");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "final a",
            "final a = 10",
            "final a = 10 + 2 - 1"
    })
    @Order(8)
    void shouldReadCorrectFinaleDeclaration(final String declaration) {
        final VariableDeclarationOperation operation = operationReader.read(sourceLine(declaration), DUMMY_CODE);

        assertNotNull(operation);
        assertConstant(operation);
    }

    @Test
    @Order(8)
    void shouldSpecifyNullValueExpressionIfFinalDeclarationDoesNotContainAnyExpression() {
        final VariableDeclarationOperation operation = operationReader.read(sourceLine("final a"), DUMMY_CODE);

        assertThat(operation.getExpression(), instanceOf(NullValueExpression.class));
    }

    @Test
    @Order(9)
    void shouldSpecifyCorrectExpressionForFinalDeclaration() {
        final String code = "final a = 10";

        operationReader.read(sourceLine(code), DUMMY_CODE);

        assertCorrectExpressionTokens("10");
    }
}
