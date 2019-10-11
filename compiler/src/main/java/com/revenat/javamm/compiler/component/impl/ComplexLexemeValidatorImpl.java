
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
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.compiler.component.ComplexLexemeValidator;
import com.revenat.javamm.compiler.component.OperatorPrecedenceResolver;

import java.util.List;

/**
 * @author Vitaliy Dragun
 *
 */
public class ComplexLexemeValidatorImpl implements ComplexLexemeValidator {

    private final OperatorPrecedenceResolver operatorPrecedenceResolver;

    public ComplexLexemeValidatorImpl(final OperatorPrecedenceResolver operatorPrecedenceResolver) {
        this.operatorPrecedenceResolver = operatorPrecedenceResolver;
    }

    @Override
    public void validate(final List<Lexeme> lexemes, final SourceLine sourceLine) {
        requireNotEmpty(lexemes);
        LexemesOrderValidator.validator(operatorPrecedenceResolver, lexemes, sourceLine).validate();
    }

    private void requireNotEmpty(final List<Lexeme> lexemes) {
        if (lexemes.isEmpty()) {
            throw new IllegalArgumentException("Found no lexemes to validate");
        }
    }
}
