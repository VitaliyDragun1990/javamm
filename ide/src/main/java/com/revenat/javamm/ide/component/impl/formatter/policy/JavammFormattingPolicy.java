/*
 *
 *  Copyright (c) 2019. http://devonline.academy
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.revenat.javamm.ide.component.impl.formatter.policy;

import com.revenat.javamm.ide.component.impl.formatter.FormattingPolicy;
import com.revenat.javamm.ide.component.impl.formatter.model.Lines;
import com.revenat.javamm.ide.component.impl.formatter.policy.parsing.LineParser;
import com.revenat.javamm.ide.component.impl.formatter.policy.parsing.TokenBuilder;
import com.revenat.javamm.ide.component.impl.formatter.policy.parsing.TokenSplitter;

import static com.revenat.javamm.code.syntax.Delimiters.SIGNIFICANT_TOKEN_DELIMITERS;
import static com.revenat.javamm.ide.util.TabReplaceUtils.TABULATION;

/**
 * @author Vitaliy Dragun
 */
final class JavammFormattingPolicy implements FormattingPolicy {

    private static final String WHITESPACE = " ";

    private final LineTrimmingFormattingPolicy lineTrimmingFormattingPolicy = new LineTrimmingFormattingPolicy();

    private final EmptyLinesNormalizationFormattingPolicy emptyLinesNormalizationFormattingPolicy =
        new EmptyLinesNormalizationFormattingPolicy();

    private final LineParsingFormattingPolicy lineParsingFormattingPolicy =
        new LineParsingFormattingPolicy(
            new LineParser(new TokenSplitter(SIGNIFICANT_TOKEN_DELIMITERS)),
            new TokenBuilder());

    private final TokenSeparationFormattingPolicy tokenSeparationFormattingPolicy =
        new TokenSeparationFormattingPolicy(WHITESPACE);

    private final LineIndentationFormattingPolicy lineIndentationFormattingPolicy =
        new LineIndentationFormattingPolicy(TABULATION);

    @Override
    public void apply(final Lines lines) {
        lineTrimmingFormattingPolicy.apply(lines);
        emptyLinesNormalizationFormattingPolicy.apply(lines);
        lineParsingFormattingPolicy.apply(lines);
        tokenSeparationFormattingPolicy.apply(lines);
        lineIndentationFormattingPolicy.apply(lines);
    }
}
