
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
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.compiler.error.JavammSyntaxError;

import java.util.List;

/**
 * Responsible for building particular kind of expression
 *
 * @author Vitaliy Dragun
 *
 */
public interface ExpressionBuilder {

    /**
     * Determines whether {@code this} expression builder can build expression from
     * specified list of tokens
     *
     * @param tokens list of tokens used for building an expression
     * @return {@code true} if {@code this} expression builder is capable of
     *         building an expression from specified tokens, {@code false} if it
     *         doesn't
     */
    boolean canBuild(List<String> tokens);

    /**
     * Builds an expression using provided expression tokens and source line where
     * assumed expression is defined
     *
     * @param expressionTokens tokens that form assumed expression
     * @param sourceLine       source line where assumed expression is defined
     * @return newly builded expression
     * @throws JavammSyntaxError if can not build expression due to syntax error
     *                           presents in the expression tokens
     */
    Expression build(List<String> expressionTokens, SourceLine sourceLine);
}
