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
import com.revenat.javamm.ide.component.impl.formatter.model.Line;
import com.revenat.javamm.ide.component.impl.formatter.model.Lines;
import com.revenat.javamm.ide.component.impl.formatter.model.LinesImpl;
import com.revenat.javamm.ide.component.impl.formatter.policy.parsing.LineParser;
import com.revenat.javamm.ide.component.impl.formatter.policy.parsing.TokenBuilder;

import static java.util.Objects.requireNonNull;

/**
 * @author Vitaliy Dragun
 */
final class LineParsingFormattingPolicy implements FormattingPolicy {

    private final LineParser lineParser;

    private final TokenBuilder tokenBuilder;

    LineParsingFormattingPolicy(final LineParser lineParse, final TokenBuilder tokenBuilder) {
        this.lineParser = requireNonNull(lineParse);
        this.tokenBuilder = requireNonNull(tokenBuilder);
    }

    @Override
    public void apply(final Lines lines) {
        for (final Line line : ((LinesImpl) lines).getLines()) {
            lineParser.parseLine(line, tokenBuilder);
        }
    }
}
