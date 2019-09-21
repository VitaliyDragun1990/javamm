
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

package com.revenat.javamm.compiler.component.impl.util;

import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;
import static com.revenat.javamm.code.syntax.Keywords.ALL_KEYWORDS;

import static java.lang.Character.isLetter;

/**
 * @author Vitaliy Dragun
 *
 */
public final class SyntaxValidationUtils {

    private SyntaxValidationUtils() {
    }

    /**
     * Validates that first character of the provided {@code name} argument is a
     * letter.
     *
     * @throws JavammLineSyntaxError if validation fails
     */
    public static void validateThatFirstCharacterIsLetter(final LanguageFeature feature, final String name,
            final SourceLine sourceLine) {
        if (!isLetter(name.charAt(0))) {
            throw new JavammLineSyntaxError(sourceLine, "The %s name must start with letter: '%s'", feature.getName(),
                    name);
        }
    }

    /**
     * Validates that provided {@code name} argument is not a reserved keyword
     *
     * @throws JavammLineSyntaxError if validation fails
     */
    public static void validateThatNameIsNotKeyword(final LanguageFeature feature, final String name,
            final SourceLine sourceLine) {
        if (ALL_KEYWORDS.contains(name)) {
            throw new JavammLineSyntaxError(sourceLine, "The keyword '%s' can not be used as %s name", name,
                    feature.getName());
        }
    }

    public enum LanguageFeature {

        VARIABLE,

        FUNCTION;

        public String getName() {
            return name().toLowerCase();
        }
    }
}
