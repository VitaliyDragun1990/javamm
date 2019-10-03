
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
import com.revenat.javamm.code.fragment.Parenthesis;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.operator.BinaryOperator;
import com.revenat.javamm.code.fragment.operator.UnaryOperator;
import com.revenat.javamm.compiler.component.LexemeBuilder;
import com.revenat.javamm.compiler.component.SingleTokenExpressionBuilder;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.revenat.javamm.code.fragment.Parenthesis.OPENING_PARENTHESIS;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ARITHMETIC_ADDITION;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ARITHMETIC_SUBTRACTION;
import static com.revenat.javamm.code.util.TypeUtils.confirmType;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

/**
 * @author Vitaliy Dragun
 *
 */
public class LexemeBuilderImpl implements LexemeBuilder {
    private static final Set<Operator> AMBIGUOUS_OPERATORS = Set.of(
            ARITHMETIC_ADDITION,
            ARITHMETIC_SUBTRACTION
        );

    private final SingleTokenExpressionBuilder singleTokenExpressionBuilder;

    public LexemeBuilderImpl(final SingleTokenExpressionBuilder singleTokenExpressionBuilder) {
        this.singleTokenExpressionBuilder = singleTokenExpressionBuilder;
    }

    @Override
    public List<Lexeme> build(final List<String> tokens, final SourceLine sourceLine) {
        final List<Lexeme> lexemes = new ArrayList<>();

        for (int i = 0; i < tokens.size(); i++) {
            final Lexeme lexeme = buildLexeme(tokens.get(i), sourceLine);
            processLexem(lexeme, i, lexemes);
        }

        return List.copyOf(lexemes);
    }

    private Lexeme buildLexeme(final String token, final SourceLine sourceLine) {
        return
                tryToBuildOperatorFrom(token)
                .or(() -> tryToBuildParenthesisFrom(token))
                .or(() -> tryToBuildSingleTokenExpressionFrom(token, sourceLine))
                .orElseThrow(() -> syntaxError(token, sourceLine));
    }

    private void processLexem(final Lexeme lexeme, final int position, final List<Lexeme> lexemes) {
        if (isAmbigousOperator(lexeme)) {
            resolveAmbiguity((Operator) lexeme, position, lexemes);
        } else {
            lexemes.add(lexeme);
        }
    }

    private boolean isAmbigousOperator(final Lexeme lexeme) {
        return AMBIGUOUS_OPERATORS.contains(lexeme);
    }

    private void resolveAmbiguity(final Operator ambiguousOperator,
                                  final int position,
                                  final List<Lexeme> lexemes) {
        if (shouldBeUnaryOperator(lexemes, position)) {
            lexemes.add(unaryOperatorFor(ambiguousOperator.getCode()));
        } else {
            lexemes.add(binaryOperatorFor(ambiguousOperator.getCode()));
        }
    }

    private boolean shouldBeUnaryOperator(final List<Lexeme> lexemes, final int position) {
        final boolean isFirstLexeme = position == 0;
        final int previousLexemPosition = position - 1;

        return isFirstLexeme ||
               previousLexemIsAnOpeningParenthesis(previousLexemPosition, lexemes) ||
               previousLexemeIsAnOperator(previousLexemPosition, lexemes);
    }

    private boolean previousLexemIsAnOpeningParenthesis(final int previousLexemPosition,
            final List<Lexeme> lexemes) {
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

    @SuppressWarnings("unchecked")
    private Optional<Lexeme> tryToBuildOperatorFrom(final String token) {
        final Optional<Lexeme> binaryOperator = (Optional<Lexeme>) tryToBuildBinaryOperator(token);
        if (binaryOperator.isPresent()) {
            return binaryOperator;
        } else {
            return (Optional<Lexeme>) tryToBuildUnaryOperator(token);
        }
    }

    private Optional<? extends Lexeme> tryToBuildBinaryOperator(final String token) {
        return BinaryOperator.of(token);
    }

    private Optional<? extends Lexeme> tryToBuildUnaryOperator(final String token) {
        return UnaryOperator.of(token);
    }

    private Optional<? extends Lexeme> tryToBuildParenthesisFrom(final String token) {
        return Parenthesis.of(token);
    }

    private Optional<Lexeme> tryToBuildSingleTokenExpressionFrom(final String token, final SourceLine sourceLine) {
        if (singleTokenExpressionBuilder.canBuild(List.of(token))) {
            return Optional.of(singleTokenExpressionBuilder.build(List.of(token), sourceLine));
        }
        return Optional.empty();
    }

    private JavammLineSyntaxError syntaxError(final String token, final SourceLine sourceLine) {
        return new JavammLineSyntaxError(buildErrorMessage(token), sourceLine);
    }

    private String buildErrorMessage(final String token) {
        return format(
                "Unsupported token: %s (%s)",
                token,
                token.codePoints().mapToObj(v -> "0x" + format("%04x", v)).collect(joining(" ")));
    }
}
