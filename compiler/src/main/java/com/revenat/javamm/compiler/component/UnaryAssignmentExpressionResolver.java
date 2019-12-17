
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

package com.revenat.javamm.compiler.component;

import com.revenat.javamm.code.fragment.Lexeme;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.expression.UnaryPostfixAssignmentExpression;
import com.revenat.javamm.code.fragment.expression.UnaryPrefixAssignmentExpression;
import com.revenat.javamm.code.fragment.expression.VariableExpression;
import com.revenat.javamm.code.fragment.operator.UnaryOperator;

import java.util.List;

/**
 * Updates given lexemes in order to change occurrences of combinations of
 * {@linkplain UnaryOperator unary operator lexeme} +
 * {@linkplain VariableExpression variable expression lexeme} and
 * {@linkplain VariableExpression variable expression lexeme} +
 * {@linkplain UnaryOperator unary operator lexeme} to
 * {@linkplain UnaryPrefixAssignmentExpression unary prefix assignment
 * expression} and {@linkplain UnaryPostfixAssignmentExpression unary postfix
 * assignment expression} accordingly.
 *
 * @author Vitaliy Dragun
 */
public interface UnaryAssignmentExpressionResolver {

    List<Lexeme> resolve(List<Lexeme> lexemes, SourceLine sourceLine);
}
