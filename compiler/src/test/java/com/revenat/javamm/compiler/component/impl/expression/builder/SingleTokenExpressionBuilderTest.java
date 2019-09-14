
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

package com.revenat.javamm.compiler.component.impl.expression.builder;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.revenat.javamm.code.fragment.Expression;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.Variable;
import com.revenat.javamm.code.fragment.expression.ConstantExpression;
import com.revenat.javamm.code.fragment.expression.NullValueExpression;
import com.revenat.javamm.code.fragment.expression.TypeExpression;
import com.revenat.javamm.code.fragment.expression.VariableExpression;
import com.revenat.javamm.compiler.component.SingleTokenExpressionBuilder;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;
import com.revenat.javamm.compiler.component.impl.expression.builder.SingleTokenExpressionBuilderImpl;
import com.revenat.javamm.compiler.test.doubles.ExpressionContextDummy;
import com.revenat.javamm.compiler.test.doubles.VariableBuilderStub;
import com.revenat.javamm.compiler.test.doubles.VariableDummy;

import java.util.List;

import static com.revenat.javamm.code.syntax.Keywords.BOOLEAN;
import static com.revenat.javamm.code.syntax.Keywords.DOUBLE;
import static com.revenat.javamm.code.syntax.Keywords.FALSE;
import static com.revenat.javamm.code.syntax.Keywords.INTEGER;
import static com.revenat.javamm.code.syntax.Keywords.NULL;
import static com.revenat.javamm.code.syntax.Keywords.STRING;
import static com.revenat.javamm.code.syntax.Keywords.TRUE;

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
@DisplayName("a single token expression builder")
class SingleTokenExpressionBuilderTest {
    private static final SourceLine DUMMY_SOURCE_LINE = SourceLine.EMPTY_SOURCE_LINE;
    private static final Variable DUMMY_VARIABLE = new VariableDummy();

    private VariableBuilderStub variableBuilderStub;
    private SingleTokenExpressionBuilder expresssionBuilder;

    @BeforeEach
    void setUp() {
        variableBuilderStub = new VariableBuilderStub();
        variableBuilderStub.setVariableToBuild(DUMMY_VARIABLE);
        expresssionBuilder = new SingleTokenExpressionBuilderImpl(variableBuilderStub);
    }

    private void assertSupport(final String... tokens) {
        assertTrue(expresssionBuilder.canBuild(List.of(tokens)));
    }

    private void assertNotSupport(final String... tokens) {
        assertFalse(expresssionBuilder.canBuild(List.of(tokens)));
    }

    private void assertFailToBuild(final String token) {
        assertThrows(JavammLineSyntaxError.class, () -> expresssionBuilder.build(List.of(token), DUMMY_SOURCE_LINE));
    }

    private void assertExpressionType(final String token, final Class<? extends Expression> expressionType) {
        final Expression expression = expresssionBuilder.build(List.of(token), DUMMY_SOURCE_LINE);
        assertThat(expression.getClass(), equalTo(expressionType));
    }

    private void assertConstantExpression(final String token, final Class<?> constantType) {
        final Expression expression = expresssionBuilder.build(List.of(token), DUMMY_SOURCE_LINE);
        assertThat(expression.getClass(), equalTo(ConstantExpression.class));
        assertThat(expression.getValue(new ExpressionContextDummy()).getClass(), equalTo(constantType));
    }

    @Test
    @Order(1)
    void canNotBuildExpressionsContainingMoreThan1Token() {
        assertNotSupport("1", "+", "1");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "#",
            "$",
            "{",
            "+",
            "переменная"
    })
    @Order(1)
    void canNotBuildExpressionsWithUnsupportedTokens(final String token) {
        assertNotSupport(token);
    }

    @ParameterizedTest
    @ValueSource(strings = { INTEGER, DOUBLE, BOOLEAN, STRING })
    @Order(2)
    void shouldSupportTypeLiterals(final String typeLiteral) {
        assertSupport(typeLiteral);
    }

    @ParameterizedTest
    @ValueSource(strings = { "'test'", "\"test\"" })
    @Order(3)
    void shouldSupportStringLiterals(final String stringLiteral) {
        assertSupport(stringLiteral);
    }

    @ParameterizedTest
    @ValueSource(strings = { TRUE, FALSE })
    @Order(4)
    void shouldSupportBooleanLiterals(final String booleanLiteral) {
        assertSupport(booleanLiteral);
    }

    @ParameterizedTest
    @ValueSource(strings = { "1", "100", "0" })
    @Order(5)
    void shouldSupportIntegerLiterals(final String intLiteral) {
        assertSupport(intLiteral);
    }

    @ParameterizedTest
    @ValueSource(strings = { "1.025", "0.25", ".25" })
    @Order(6)
    void shouldSupportDecimalLiterals(final String decimalLiteral) {
        assertSupport(decimalLiteral);
    }

    @ParameterizedTest
    @ValueSource(strings = { "a", "varA", "person1" })
    @Order(6)
    void shouldSupportValidVariableNames(final String variableName) {
        assertSupport(variableName);
    }

    @ParameterizedTest
    @ValueSource(strings = { INTEGER, DOUBLE, BOOLEAN, STRING })
    @Order(7)
    void shouldBuildTypeExpressionForTypeLiteralToken(final String token) {
        assertExpressionType(token, TypeExpression.class);
    }

    @ParameterizedTest
    @ValueSource(strings = { "10", "1", "99999999" })
    @Order(8)
    void shouldBuildConstantExpressionForIntegerLiteralToken(final String token) {
        assertConstantExpression(token, Integer.class);
    }

    @ParameterizedTest
    @ValueSource(strings = { "10.5", "0.255", ".255" })
    @Order(9)
    void shouldBuildConstantExpressionForDoubleLiteralToken(final String token) {
        assertConstantExpression(token, Double.class);
    }

    @ParameterizedTest
    @ValueSource(strings = { "'test'", "\"hello\"" })
    @Order(10)
    void shouldBuildConstantExpressionForStringLiteralToken(final String token) {
        assertConstantExpression(token, String.class);
    }

    @ParameterizedTest
    @ValueSource(strings = { TRUE, FALSE })
    @Order(11)
    void shouldBuildConstantExpressionForBooleanLiteralToken(final String token) {
        assertConstantExpression(token, Boolean.class);
    }

    @Test
    @Order(12)
    void shouldFailToBuildExpressionIfStringLiteralTokenMissesCloseDelimiter() {
        assertFailToBuild("'test");
    }

    @Test
    @Order(13)
    void shouldFailToBuildExpressionIfStringLiteralOpenAndCloseDelimetersMismatch() {
        assertFailToBuild("'test\"");
        assertFailToBuild("\"test'");
    }

    @Test
    @Order(14)
    void shouldBuildNullValueExpressionForNullLiteralToken() {
        assertExpressionType(NULL, NullValueExpression.class);
    }

    @ParameterizedTest
    @ValueSource(strings = { "a", "b", "varA"})
    @Order(15)
    void shouldBuildVariableExpressionForVariableNameToken(final String token) {
        assertExpressionType(token, VariableExpression.class);
    }
}
