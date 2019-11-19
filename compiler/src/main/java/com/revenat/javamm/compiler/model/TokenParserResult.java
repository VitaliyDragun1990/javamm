
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

package com.revenat.javamm.compiler.model;

import com.revenat.javamm.compiler.component.TokenParser;

import java.util.List;

/**
 * Represents result of {@link TokenParser} processing
 *
 * @author Vitaliy Dragun
 *
 */
public class TokenParserResult {

    private final List<String> tokens;

    private final boolean isMultiLineCommentStarted;

    public TokenParserResult(final List<String> tokens, final boolean isMultiLineCommentStarted) {
        this.tokens = List.copyOf(tokens);
        this.isMultiLineCommentStarted = isMultiLineCommentStarted;
    }

    public TokenParserResult(final boolean isMultiLineCommentStarted) {
        this(List.of(), isMultiLineCommentStarted);
    }

    public boolean isNotEmpty() {
        return !tokens.isEmpty();
    }

    public List<String> getTokens() {
        return tokens;
    }

    public boolean isMultiLineCommentStarted() {
        return isMultiLineCommentStarted;
    }
}
