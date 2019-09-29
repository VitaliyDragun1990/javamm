
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

package com.revenat.javamm.compiler.test.doubles;

import com.revenat.javamm.code.fragment.Lexeme;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.compiler.component.LexemeBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LexemeBuilderStub implements LexemeBuilder {
    private final Map<List<String>, List<Lexeme>> lexemesToBuild = new HashMap<>();

    public void setLexemesToBuild(final List<String> tokens, final List<Lexeme> lexemes) {
        lexemesToBuild.put(tokens, lexemes);
    }

    @Override
    public List<Lexeme> build(final List<String> tokens, final SourceLine sourceLine) {
        return lexemesToBuild.get(tokens);
    }
}