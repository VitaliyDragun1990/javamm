
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

package com.revenat.javamm.code.fragment.operator;

import com.revenat.juinit.addons.ReplaceCamelCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("a binary operator")
class BinaryOperatorTest {
    private static final String BINARY = "binary";

    static Stream<String> allSupportedTokensProvider() {
        return Stream.of(BinaryOperator.values())
            .map(BinaryOperator::getCode);
    }

    private void assertCanBeCreatedFrom(final String token) {
        assertThat(BinaryOperator.of(token).isPresent(), is(true));
    }

    private void assertCanNotBeCreatedFrom(final String token) {
        assertThat(BinaryOperator.of(token).isPresent(), is(false));
    }

    private void assertBinaryType(final BinaryOperator operator) {
        assertThat(operator.getType(), equalTo(BINARY));
    }

    private void assertAssignmentOperator(final BinaryOperator operator) {
        assertTrue(operator.isAssignment());
    }

    private void assertNotAssignmentOperator(final BinaryOperator operator) {
        assertFalse(operator.isAssignment());
    }

    @ParameterizedTest
    @MethodSource("allSupportedTokensProvider")
    @Order(1)
    void canBeCreatedForEverySupportedToken(final String operatorToken) {
        assertCanBeCreatedFrom(operatorToken);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "@",
        "~",
        "?"
    })
    @Order(2)
    void canNotBeCreatedFromUnsupportedToken(final String unsupportedToken) {
        assertCanNotBeCreatedFrom(unsupportedToken);
    }

    @ParameterizedTest
    @EnumSource(BinaryOperator.class)
    @Order(3)
    void shouldConfirmItsBinaryStatus(final BinaryOperator operator) {
        assertBinaryType(operator);
    }

    @ParameterizedTest
    @EnumSource(value = BinaryOperator.class, mode = Mode.MATCH_ALL, names = "^ASSIGNMENT.+$")
    @Order(4)
    void shouldBeAssignmentIfItsNameStartsWithKeyword(final BinaryOperator operator) {
        assertAssignmentOperator(operator);
    }

    @ParameterizedTest
    @EnumSource(value = BinaryOperator.class, mode = Mode.MATCH_ALL, names = "^((?!ASSIGNMENT).)+$")
    @Order(5)
    void shouldNotBeAssignmentIfItsNameDoesNotStartWithAssignmentKeyword(final BinaryOperator operator) {
        assertNotAssignmentOperator(operator);
    }

}
