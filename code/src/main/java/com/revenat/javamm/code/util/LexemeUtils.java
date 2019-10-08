
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

package com.revenat.javamm.code.util;

import com.revenat.javamm.code.fragment.Expression;
import com.revenat.javamm.code.fragment.Lexeme;
import com.revenat.javamm.code.fragment.Parenthesis;
import com.revenat.javamm.code.fragment.operator.UnaryOperator;

import java.util.Set;

import static com.revenat.javamm.code.fragment.operator.UnaryOperator.DECREMENT;
import static com.revenat.javamm.code.fragment.operator.UnaryOperator.INCREMENT;
import static com.revenat.javamm.code.util.TypeUtils.confirmType;

/**
 * @author Vitaliy Dragun
 *
 */
public final class LexemeUtils {
    private static final Set<UnaryOperator> UNARY_ASSIGNMENT_OPERATORS = Set.of(
            INCREMENT,
            DECREMENT
    );

    private LexemeUtils() {
    }

    public static boolean isClosingParenthesis(final Lexeme next) {
        return next == Parenthesis.CLOSING_PARENTHESIS;
    }

    public static boolean isOpeningParenthesis(final Lexeme next) {
        return next == Parenthesis.OPENING_PARENTHESIS;
    }

    public static boolean isExpression(final Lexeme lexeme) {
        return confirmType(Expression.class, lexeme);
    }

    public static boolean isUnaryAssignmentOperator(final Lexeme lexeme) {
        return UNARY_ASSIGNMENT_OPERATORS.contains(lexeme);
    }
}
