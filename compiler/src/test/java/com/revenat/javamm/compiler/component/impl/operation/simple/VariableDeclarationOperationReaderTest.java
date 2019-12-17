
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

import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.expression.NullValueExpression;
import com.revenat.javamm.code.fragment.operation.VariableDeclarationOperation;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;
import com.revenat.javamm.compiler.test.doubles.ExpressionDummy;
import com.revenat.javamm.compiler.test.doubles.ExpressionResolverSpy;
import com.revenat.javamm.compiler.test.doubles.VariableBuilderStub;
import com.revenat.javamm.compiler.test.doubles.VariableDummy;
import com.revenat.juinit.addons.ReplaceCamelCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.ListIterator;

import static com.revenat.javamm.compiler.test.helper.CustomAsserts.assertErrorMessageContains;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("a variable declaration operation reader")
class VariableDeclarationOperationReaderTest {
    private static final ListIterator<SourceLine> DUMMY_CODE = List.of(SourceLine.EMPTY_SOURCE_LINE).listIterator();

    private VariableBuilderStub variableBuilderStub;

    private ExpressionResolverSpy expressionResolverStub;

    private VariableDeclarationOperationReader operationReader;


    @BeforeEach
    void setUp() {
        variableBuilderStub = new VariableBuilderStub();
        variableBuilderStub.setVariableToBuild(new VariableDummy());

        expressionResolverStub = new ExpressionResolverSpy();
        expressionResolverStub.setExpressionToResolve(new ExpressionDummy());

        operationReader = new VariableDeclarationOperationReader(variableBuilderStub, expressionResolverStub);
    }

    private SourceLine sourceLine(final String code) {
        final String[] tokens = code.split(" ");
        return new SourceLine("test", 1, List.of(tokens));
    }

    private void assertNotConstant(final VariableDeclarationOperation operation) {
        assertFalse(operation.isConstant());
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
    void canNotReadVariableDeclarationWithoutVarKeyword() {
        assertFalse(operationReader.canRead(sourceLine("a = 10")));
    }

    @Test
    @Order(4)
    void canReadCorrectVariableDeclaration() {
        assertTrue(operationReader.canRead(sourceLine("var a = 10")));
    }

    @Test
    @Order(5)
    void shouldFailToReadDeclarationWithoutVariableName() {
        final JavammLineSyntaxError e =
            assertThrows(JavammLineSyntaxError.class, () -> operationReader.read(sourceLine("var"), DUMMY_CODE));

        assertErrorMessageContains(e, "Variable name is missing");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "var a +",
        "var + a",
        "var = a",
        "var a + 3",
        "var a + ="
    })
    @Order(6)
    void shouldFailToReadDeclarationWithMisplacedEqualSignIfAny(final String code) {
        final JavammLineSyntaxError e =
            assertThrows(JavammLineSyntaxError.class, () -> operationReader.read(sourceLine(code), DUMMY_CODE));

        assertErrorMessageContains(e, "'=' is missing or has invalid position");
    }

    @Test
    @Order(7)
    void shouldFailToReadDeclarationWithEqualSignButWithoutExpression() {
        final JavammLineSyntaxError e =
            assertThrows(JavammLineSyntaxError.class, () -> operationReader.read(sourceLine("var a ="), DUMMY_CODE));

        assertErrorMessageContains(e, "Variable expression is missing");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "var a",
        "var a = 10",
        "var a = 10 + 2 - 1"
    })
    @Order(8)
    void shouldReadCorrectVariableDeclaration(final String declaration) {
        final VariableDeclarationOperation operation = operationReader.read(sourceLine(declaration), DUMMY_CODE);

        assertNotNull(operation);
        assertNotConstant(operation);
    }

    @Test
    @Order(8)
    void shouldSpecifyNullValueExpressionIfVariableDeclarationDoesNotContainAnyExpression() {
        final VariableDeclarationOperation operation = operationReader.read(sourceLine("var a"), DUMMY_CODE);

        assertThat(operation.getExpression(), instanceOf(NullValueExpression.class));
    }

    @Test
    @Order(9)
    void shouldSpecifyCorrectExpressionForVariableDeclaration() {
        final String code = "var a = 10";

        operationReader.read(sourceLine(code), DUMMY_CODE);

        assertCorrectExpressionTokens("10");
    }
}
