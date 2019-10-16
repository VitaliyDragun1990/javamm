
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

import com.revenat.javamm.code.fragment.Expression;
import com.revenat.javamm.code.fragment.Lexeme;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.compiler.error.JavammSyntaxError;

import java.util.List;

/**
 * Responsible for resolving expressions
 *
 * @author Vitaliy Dragun
 *
 */
public interface ExpressionResolver {

    /**
     * Resolves any kind of supported expression using expression tokens and source
     * line where assumed expression is defined
     *
     * @param expressionTokens tokens that form assumed expression
     * @param sourceLine       source line where assumed expression is defined
     * @return resolved expression
     * @throws JavammSyntaxError if specified {@code expressionTokens} can not be
     *                           resolved to any supported expression
     */
    Expression resolve(List<String> expressionTokens, SourceLine sourceLine);

    /**
     * Resolves any kind of supported expression using {@linkplain Lexeme lexemes} and source
     * line where assumed expression is defined
     *
     * @param expressionTokens tokens that form assumed expression
     * @param sourceLine       source line where assumed expression is defined
     * @return resolved expression
     * @throws JavammSyntaxError if specified {@code expressionTokens} can not be
     *                           resolved to any supported expression
     */
    Expression resolveFromLexemes(List<Lexeme> lexems, SourceLine sourceLine);
}
