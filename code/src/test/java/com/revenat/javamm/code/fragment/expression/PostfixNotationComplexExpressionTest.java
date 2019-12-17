
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

package com.revenat.javamm.code.fragment.expression;

import com.revenat.javamm.code.fragment.Lexeme;
import com.revenat.javamm.code.fragment.operator.BinaryOperator;
import com.revenat.javamm.code.test.helper.MockUtils;
import com.revenat.juinit.addons.ReplaceCamelCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.stream.Stream;

import static com.revenat.javamm.code.fragment.expression.ConstantExpression.valueOf;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ARITHMETIC_ADDITION;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ASSIGNMENT_ADDITION;
import static com.revenat.javamm.code.fragment.operator.UnaryOperator.DECREMENT;
import static com.revenat.javamm.code.fragment.operator.UnaryOperator.INCREMENT;
import static com.revenat.javamm.code.test.helper.MockUtils.variable;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("a postfix notation complex expression")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PostfixNotationComplexExpressionTest {

    @Test
    @Order(1)
    void shouldBeBinaryAssignmentExpressionIfMainExpressionInvolvesBinaryAssignmentOperator() {
        final PostfixNotationComplexExpression expression = createPostfixExpression("b += 1", MockUtils.variable("b"), ConstantExpression.valueOf(1), BinaryOperator.ASSIGNMENT_ADDITION);

        assertTrue(expression.isBinaryAssignmentExpression());
    }

    @ParameterizedTest
    @ArgumentsSource(NotBinaryAssignmentExpressionProvider.class)
    @Order(2)
    @DisplayName("should not be binary assignment expression if main expression does not involve binary assignment operator")
    void shouldNotBeBinaryAssignmentExpression(final PostfixNotationComplexExpression expression) {
        assertFalse(expression.isBinaryAssignmentExpression());
    }

    private PostfixNotationComplexExpression createPostfixExpression(final String originalExpression, final Lexeme... lexemesInPostfixNotation) {
        return new PostfixNotationComplexExpression(List.of(lexemesInPostfixNotation), originalExpression);
    }

    static class NotBinaryAssignmentExpressionProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(final ExtensionContext context) {
            return Stream.of(
                arguments(
                    createPostfixExpression("a ++ --", variable("a"), INCREMENT, DECREMENT)),
                arguments(
                    createPostfixExpression("a++", variable("a"), INCREMENT)),
                arguments(
                    createPostfixExpression("a + 1", variable("a"), valueOf(1), ARITHMETIC_ADDITION)),
                arguments(
                    createPostfixExpression("2 + a", valueOf(1), variable("a"), ARITHMETIC_ADDITION)),
                arguments(
                    createPostfixExpression("2 + a += 3", valueOf(2), variable("a"), ASSIGNMENT_ADDITION, ARITHMETIC_ADDITION))
            );
        }


        private PostfixNotationComplexExpression createPostfixExpression(final String originalExpression, final Lexeme... lexemesInPostfixNotation) {
            return new PostfixNotationComplexExpression(List.of(lexemesInPostfixNotation), originalExpression);
        }
    }
}
