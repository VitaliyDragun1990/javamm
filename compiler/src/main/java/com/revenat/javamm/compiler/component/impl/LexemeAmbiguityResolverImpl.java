
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

package com.revenat.javamm.compiler.component.impl;

import com.revenat.javamm.code.fragment.Lexeme;
import com.revenat.javamm.code.fragment.Operator;
import com.revenat.javamm.code.fragment.operator.BinaryOperator;
import com.revenat.javamm.code.fragment.operator.UnaryOperator;
import com.revenat.javamm.compiler.component.LexemeAmbiguityResolver;

import java.util.List;
import java.util.Set;

import static com.revenat.javamm.code.fragment.Parenthesis.OPENING_PARENTHESIS;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ARITHMETIC_ADDITION;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ARITHMETIC_SUBTRACTION;
import static com.revenat.javamm.code.fragment.operator.UnaryOperator.ARITHMETICAL_UNARY_MINUS;
import static com.revenat.javamm.code.fragment.operator.UnaryOperator.ARITHMETICAL_UNARY_PLUS;
import static com.revenat.javamm.code.util.TypeUtils.confirmType;

/**
 * @author Vitaliy Dragun
 *
 */
public class LexemeAmbiguityResolverImpl implements LexemeAmbiguityResolver {
    private static final Set<Operator> AMBIGUOUS_OPERATORS = Set.of(
            ARITHMETIC_ADDITION,
            ARITHMETIC_SUBTRACTION,
            ARITHMETICAL_UNARY_PLUS,
            ARITHMETICAL_UNARY_MINUS
    );

    /**
     * Processes specified {@linkplain Lexeme lexeme}, its {@code position} between
     * all {@code lexemes} to determine whether this lexeme is ambiguous, and if so,
     * returns appropriate lexeme instead to resolve such ambiguity.
     *
     * @param lexeme   {@linkplain Lexeme lexeme} to process
     * @param position position of lexeme to resolve among all already resolved
     *                 lexemes
     * @param lexemes  all lexemes that already been resolved
     * @return new resolved lexeme if ambiguity was found, otherwise same lexeme
     *         which was specified to resolve
     */
    @Override
    public Lexeme resolve(final Lexeme lexeme, final int position, final List<Lexeme> lexemes) {
        if (isAmbigousOperator(lexeme)) {
            return resolveAmbiguity((Operator) lexeme, position, lexemes);
        } else {
            return lexeme;
        }
    }

    private boolean isAmbigousOperator(final Lexeme lexeme) {
        return AMBIGUOUS_OPERATORS.contains(lexeme);
    }

    private Lexeme resolveAmbiguity(final Operator ambiguousOperator, final int position, final List<Lexeme> lexemes) {
        if (shouldBeUnaryOperator(lexemes, position)) {
            return unaryOperatorFor(ambiguousOperator.getCode());
        } else {
            return binaryOperatorFor(ambiguousOperator.getCode());
        }
    }

    private boolean shouldBeUnaryOperator(final List<Lexeme> lexemes, final int position) {
        final boolean isFirstLexeme = position == 0;
        final int previousLexemPosition = position - 1;

        return isFirstLexeme || previousLexemIsAnOpeningParenthesis(previousLexemPosition, lexemes) ||
                previousLexemeIsAnOperator(previousLexemPosition, lexemes);
    }

    private boolean previousLexemIsAnOpeningParenthesis(final int previousLexemPosition, final List<Lexeme> lexemes) {
        return lexemes.get(previousLexemPosition) == OPENING_PARENTHESIS;
    }

    private boolean previousLexemeIsAnOperator(final int previousLexemPosition, final List<Lexeme> lexemes) {
        return confirmType(Operator.class, lexemes.get(previousLexemPosition));
    }

    private UnaryOperator unaryOperatorFor(final String code) {
        return UnaryOperator.of(code).get();
    }

    private BinaryOperator binaryOperatorFor(final String code) {
        return BinaryOperator.of(code).get();
    }
}
