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

package com.revenat.javamm.ide.component.impl.formatter;

import java.util.Optional;
import java.util.Set;

import static com.revenat.javamm.ide.component.impl.formatter.Token.Type.LINE_COMMENT;
import static com.revenat.javamm.ide.component.impl.formatter.Token.Type.MULTI_LINE_COMMENT;
import static com.revenat.javamm.ide.component.impl.formatter.Token.Type.NORMAL;
import static java.util.Objects.requireNonNull;

/**
 * Applies custom token separation policy (type and size of the delimiters before and after each token in the line)
 *
 * @author Vitaliy Dragun
 */
final class TokenSeparationFormattingPolicy implements FormattingPolicy {

    private static final Set<String> MANDATORY_DELIMITER_AFTER = Set.of("if", "else", "switch", "while", "for");

    private static final Set<String> MANDATORY_NO_DELIMITER_AFTER = Set.of("(", "[", "{");

    private static final Set<String> MANDATORY_NO_DELIMITER_BEFORE = Set.of(")", "[", "]", "}", ",", ";", ":");

    private static final Set<String> PREFERABLY_NO_DELIMITER_BEFORE = Set.of("(");

    private final String delimiter;

    TokenSeparationFormattingPolicy(final String delimiter) {
        this.delimiter = requireNonNull(delimiter);
    }

    @Override
    public void apply(final Lines lines) {
        final LinesImpl linesImpl = (LinesImpl) lines;
        for (final Line line : linesImpl.getLines()) {
            if (!line.isEmpty()) {
                apply(line);
            }
        }
    }

    private void apply(final Line line) {
        Optional<Token> optionalToken = line.getFirstToken();
        while (optionalToken.isPresent()) {
            final Token token = optionalToken.get();

            if (token.isFirst()) {
                token.deleteDelimiterBefore();
            } else if (token.isType(LINE_COMMENT) || token.isType(MULTI_LINE_COMMENT)) {
                // preserve user specific before delimiter
            } else if (token.previousTokenHasType(LINE_COMMENT) || token.previousTokenHasType(MULTI_LINE_COMMENT)) {
                // preserve user specific delimiter
            } else if (token.hasContentEquals(MANDATORY_NO_DELIMITER_BEFORE)) {
                token.deleteDelimiterBefore();
            } else if (token.hasContentEquals(PREFERABLY_NO_DELIMITER_BEFORE)) {
                if (token.previousTokenHasContentEquals(MANDATORY_DELIMITER_AFTER)) {
                    token.setDelimiterBefore(delimiter);
                } else {
                    token.deleteDelimiterBefore();
                }
            } else {
                token.setDelimiterBefore(delimiter);
            }

            if (token.previousTokenHasContentEquals(MANDATORY_NO_DELIMITER_AFTER) &&
                !(token.isType(LINE_COMMENT) || token.isType(MULTI_LINE_COMMENT))) {
                token.deleteDelimiterBefore();
            }
            optionalToken = token.getNext();
        }
    }

    public static void main(final String[] args) {
        final Line line = new Line(1, "for(var i=1;i<10;i++){");
        line.addToken("for", NORMAL, "");
        line.addToken("(", NORMAL, "");
        line.addToken("var", NORMAL, "");
        line.addToken("i", NORMAL, " ");
        line.addToken("=", NORMAL, "");
        line.addToken("1", NORMAL, "");
        line.addToken(";", NORMAL, "");
        line.addToken("/* test */", MULTI_LINE_COMMENT, "");
        line.addToken("i", NORMAL, "");
        line.addToken("<", NORMAL, "");
        line.addToken("10", NORMAL, "");
        line.addToken(";", NORMAL, "");
        line.addToken("++", NORMAL, "");
        line.addToken("i", NORMAL, "");
        line.addToken(")", NORMAL, "");
        line.addToken("{", NORMAL, "");
        line.addToken("// test", LINE_COMMENT, "   ");
        System.out.println(line);

        new TokenSeparationFormattingPolicy(" ").apply(line);

        System.out.println(line);
    }
}
