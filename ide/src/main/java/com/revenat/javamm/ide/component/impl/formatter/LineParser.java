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

import com.revenat.javamm.code.util.StringIterator;

import java.util.List;

import static com.revenat.javamm.code.syntax.Delimiters.IGNORED_DELIMITERS;
import static com.revenat.javamm.ide.util.TabReplaceUtils.replaceTabulations;

/**
 * @author Vitaliy Dragun
 */
final class LineParser {

    private final TokenSplitter tokenSplitter;

    LineParser(final TokenSplitter tokenSplitter) {
        this.tokenSplitter = tokenSplitter;
    }

    boolean parseLine(final Line line, final boolean isMultilineStartedBefore) {
        final StringBuilder delimiterBuilder = new StringBuilder();

        final TokenBuilder tokenBuilder = new TokenBuilder(isMultilineStartedBefore);
        final StringIterator lineContent = new StringIterator(line.getSignificantContent());

        while (lineContent.hasNext()) {
            final char c = lineContent.next();

            if (tokenBuilder.isInsideStringLiteral()) {
                tokenBuilder.append(c);

                if (tokenBuilder.isStringLiteralEnded()) {
                    line.addStringLiteralToken(tokenBuilder.getContent(), delimiterBuilder.toString());
                    tokenBuilder.clearContent();
                    delimiterBuilder.delete(0, delimiterBuilder.length());
                }
            } else if (tokenBuilder.isInsideMultilineComment()) {
                tokenBuilder.append(c);

                if (tokenBuilder.isMultilineCommentEnded()) {
                    line.addCommentToken(tokenBuilder.getContent(), delimiterBuilder.toString());
                    tokenBuilder.clearContent();
                    delimiterBuilder.delete(0, delimiterBuilder.length());
                }
            } else {
                if (IGNORED_DELIMITERS.contains(c) && tokenBuilder.isNotEmpty()) {
                    final String token = tokenBuilder.getContent();
                    final List<String> tokens = tokenSplitter.splitByDelimiters(token);
                    line.addSignificantTokens(tokens, delimiterBuilder.toString());

                    tokenBuilder.clearContent();
                    delimiterBuilder.delete(0, delimiterBuilder.length());
                    delimiterBuilder.append(asWhitespaceCharacters(c));
                } else if (IGNORED_DELIMITERS.contains(c)) {
                    delimiterBuilder.append(asWhitespaceCharacters(c));
                } else if (!IGNORED_DELIMITERS.contains(c)) {
                    tokenBuilder.append(c);
                }

                if (tokenBuilder.isLineCommentStarted()) {
                    if (tokenBuilder.containsBeforeLineCommentToken()) {
                        final String beforeCommentToken = tokenBuilder.getBeforeLineCommentToken();
                        final List<String> tokens = tokenSplitter.splitByDelimiters(beforeCommentToken);
                        line.addSignificantTokens(tokens, delimiterBuilder.toString());

                        delimiterBuilder.delete(0, delimiterBuilder.length());
                        tokenBuilder.clearBeforeLineToken();
                    }
                    final String commentTokenBody = lineContent.tail();
                    tokenBuilder.append(commentTokenBody);
                    line.addCommentToken(tokenBuilder.getContent(), delimiterBuilder.toString());
                    tokenBuilder.clearContent();
                } else if (tokenBuilder.isMultilineCommentStarted()) {
                    if (tokenBuilder.containsBeforeMultilineCommentToken()) {
                        final String beforeCommentToken = tokenBuilder.getBeforeMultilineCommentToken();
                        final List<String> tokens = tokenSplitter.splitByDelimiters(beforeCommentToken);
                        line.addSignificantTokens(tokens, delimiterBuilder.toString());

                        delimiterBuilder.delete(0, delimiterBuilder.length());
                        tokenBuilder.clearBeforeMultilineToken();
                    }
                } else if (tokenBuilder.isStringLiteralStarted() && tokenBuilder.containsBeforeStringLiteralToken()) {
                    final String beforeLiteralToken = tokenBuilder.getBeforeStringLiteralToken();
                    final List<String> tokens = tokenSplitter.splitByDelimiters(beforeLiteralToken);
                    line.addSignificantTokens(tokens, delimiterBuilder.toString());

                    delimiterBuilder.delete(0, delimiterBuilder.length());
                    tokenBuilder.clearBeforeStringLiteralToken();
                }
            }
        }

        if (tokenBuilder.isNotEmpty()) {
            if (tokenBuilder.isInsideStringLiteral()) {
                line.addStringLiteralToken(tokenBuilder.getContent(), delimiterBuilder.toString());
                tokenBuilder.endStringLiteral();
            } else if (tokenBuilder.isInsideMultilineComment()) {
                line.addCommentToken(tokenBuilder.getContent(), delimiterBuilder.toString());
            } else {
                final String token = tokenBuilder.getContent();
                final List<String> tokens = tokenSplitter.splitByDelimiters(token);
                line.addSignificantTokens(tokens, delimiterBuilder.toString());
            }
            tokenBuilder.clearContent();
            delimiterBuilder.delete(0, delimiterBuilder.length());
        }
        return tokenBuilder.isInsideMultilineComment();
    }

    private String asWhitespaceCharacters(final char delimiter) {
        if (delimiter == ' ') {
            return " ";
        } else if (delimiter == '\t') {
            return replaceTabulations(String.valueOf(delimiter));
        } else {
            return "";
        }
    }
}
