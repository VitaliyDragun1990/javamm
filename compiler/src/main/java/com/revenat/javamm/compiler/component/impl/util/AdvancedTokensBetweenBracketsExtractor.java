
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * @author Vitaliy Dragun
 *
 */
class AdvancedTokensBetweenBracketsExtractor {

    private final String openingBracket;

    private final String closingBracket;

    AdvancedTokensBetweenBracketsExtractor(final String openingBracket, final String closingBracket) {
        this.openingBracket = requireNonNull(openingBracket);
        this.closingBracket = requireNonNull(closingBracket);
    }

    List<String> extract(final Iterator<String> tokens,
                         final SourceLine sourceLine,
                         final boolean allowEmptyResult) {
        final List<String> result = new ArrayList<>();

        while (tokens.hasNext()) {
            final String token = tokens.next();
            if (openingBracket.equals(token)) {
                result.add(token);
                result.addAll(extract(tokens, sourceLine, true));
                result.add(closingBracket);
            } else if (closingBracket.equals(token)) {
                return checkIfEmptyAllowed(result, allowEmptyResult, sourceLine);
            } else {
                result.add(token);
            }
        }

        throw new JavammLineSyntaxError(sourceLine, "Missing '%s'", closingBracket);
    }

    private List<String> checkIfEmptyAllowed(final List<String> result,
                                             final boolean allowEmptyResult,
                                             final SourceLine sourceLine) {
        if (emptyResultWhileNotAllowed(result, allowEmptyResult)) {
            throw new JavammLineSyntaxError(sourceLine, "An expression is expected between '%s' and '%s'",
                    openingBracket, closingBracket);
        }
        return result;
    }

    private boolean emptyResultWhileNotAllowed(final List<String> result, final boolean allowEmptyResult) {
        return result.isEmpty() && !allowEmptyResult;
    }
}
