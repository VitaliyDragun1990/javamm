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

package com.revenat.javamm.ide.component.impl.formatter.policy.parsing;

import com.revenat.javamm.code.util.StringIterator;
import com.revenat.javamm.ide.component.impl.formatter.model.Line;
import com.revenat.javamm.ide.component.impl.formatter.model.Token;

import java.util.List;

/**
 * @author Vitaliy Dragun
 */
public final class LineParser {

    private final TokenSplitter tokenSplitter;

    public LineParser(final TokenSplitter tokenSplitter) {
        this.tokenSplitter = tokenSplitter;
    }

    public void parseLine(final Line line, final TokenBuilder tokenBuilder) {
        final StringIterator lineContent = line.getSignificantContent();

        while (lineContent.hasNext()) {
            tokenBuilder.append(lineContent.next())
                .ifPresent(token -> addToLine(line, token));
        }
        tokenBuilder.lineEnded()
            .ifPresent(token -> addToLine(line, token));
    }

    private void addToLine(final Line line, final Token token) {
        if (token.isSignificant()) {
            final List<Token> tokens = tokenSplitter.splitByDelimiters(token);
            line.addTokens(tokens);
        } else {
            line.addToken(token);
        }
    }
}
