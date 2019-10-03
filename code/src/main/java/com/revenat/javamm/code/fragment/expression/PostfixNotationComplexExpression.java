
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

import java.util.List;

/**
 * Represents complex expression which is done using {@code Postfix} notation
 *
 * @author Vitaliy Dragun
 * @link https://en.wikipedia.org/wiki/Reverse_Polish_notation
 *
 */
public class PostfixNotationComplexExpression extends ComplexExpression {
    private final String originalExpression;

    public PostfixNotationComplexExpression(final List<Lexeme> lexemes, final String originalExpression) {
        super(lexemes);
        this.originalExpression = originalExpression;
    }

    @Override
    public String toString() {
        return originalExpression;
    }
}
