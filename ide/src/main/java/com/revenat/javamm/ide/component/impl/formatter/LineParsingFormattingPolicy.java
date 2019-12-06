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

import static com.revenat.javamm.code.syntax.Delimiters.DOUBLE_QUOTATION;
import static com.revenat.javamm.code.syntax.Delimiters.IGNORED_DELIMITERS;
import static com.revenat.javamm.code.syntax.Delimiters.SINGLE_QUOTATION;
import static com.revenat.javamm.ide.component.impl.formatter.Token.Type.LINE_COMMENT;
import static com.revenat.javamm.ide.component.impl.formatter.Token.Type.MULTI_LINE_COMMENT;
import static com.revenat.javamm.ide.component.impl.formatter.Token.Type.NORMAL;
import static com.revenat.javamm.ide.component.impl.formatter.Token.Type.STRING_LITERAL;
import static com.revenat.javamm.ide.util.TabReplaceUtils.replaceTabulations;
import static java.util.Objects.requireNonNull;

/**
 * @author Vitaliy Dragun
 */
final class LineParsingFormattingPolicy implements FormattingPolicy {

    private static final String LINE_COMMENT_PREFIX = "//";

    private static final String MULTILINE_COMMENT_PREFIX = "/*";

    private static final String MULTILINE_COMMENT_SUFFIX = "*/";

    private final TokenSplitter tokenSplitter;

    LineParsingFormattingPolicy(final TokenSplitter tokenSplitter) {
        this.tokenSplitter = requireNonNull(tokenSplitter);
    }

    @Override
    public void apply(final Lines lines) {
        final LinesImpl linesImpl = (LinesImpl) lines;

        boolean multilineStarted = false;
        String stringQuote = null;
        for (final Line line : linesImpl.getLines()) {
            final StringBuilder tokenBuilder = new StringBuilder();
            final StringBuilder delimiterBuilder = new StringBuilder();
            final StringIterator lineContent = new StringIterator(line.getSignificantContent());

            while (lineContent.hasNext()) {
                final char c = lineContent.next();

                if (isInsideStringLiteral(stringQuote)) {
                    tokenBuilder.append(c);

                    if (isStringLiteralEnded(tokenBuilder, stringQuote)) {
                        line.addToken(tokenBuilder.toString(), STRING_LITERAL, delimiterBuilder.toString());
                        tokenBuilder.delete(0, tokenBuilder.length());
                        delimiterBuilder.delete(0, delimiterBuilder.length());
                        stringQuote = null;
                    }
                } else if (multilineStarted) {
                    tokenBuilder.append(c);

                    if (isMultilineCommentEnded(tokenBuilder)) {
                        line.addToken(tokenBuilder.toString(), MULTI_LINE_COMMENT, delimiterBuilder.toString());
                        tokenBuilder.delete(0, tokenBuilder.length());
                        delimiterBuilder.delete(0, delimiterBuilder.length());
                        multilineStarted = false;
                    }
                } else {
                    if (IGNORED_DELIMITERS.contains(c) && tokenBuilder.length() > 0) {
                        final String token = tokenBuilder.toString();
                        final List<String> tokens = tokenSplitter.splitByDelimiters(token);
                        line.addToken(tokens.get(0), NORMAL, delimiterBuilder.toString());
                        for (int i = 1; i < tokens.size(); i++) {
                            line.addToken(tokens.get(i), NORMAL, "");
                        }

                        tokenBuilder.delete(0, tokenBuilder.length());
                        delimiterBuilder.delete(0, delimiterBuilder.length());
                        delimiterBuilder.append(asWhitespaceCharacters(c));
                    } else if (IGNORED_DELIMITERS.contains(c)) {
                        delimiterBuilder.append(asWhitespaceCharacters(c));
                    } else if (!IGNORED_DELIMITERS.contains(c)) {
                        tokenBuilder.append(c);
                    }

                    if (isLineCommentStarted(tokenBuilder)) {
                        final int commentStartIndex = tokenBuilder.indexOf(LINE_COMMENT_PREFIX);
                        if (commentStartIndex != 0) {
                            final String beforeCommentToken = tokenBuilder.substring(0, commentStartIndex);
                            final List<String> tokens = tokenSplitter.splitByDelimiters(beforeCommentToken);
                            line.addToken(tokens.get(0), NORMAL, delimiterBuilder.toString());
                            for (int i = 1; i < tokens.size(); i++) {
                                line.addToken(tokens.get(i), NORMAL, "");
                            }

                            delimiterBuilder.delete(0, delimiterBuilder.length());
                            tokenBuilder.delete(0, beforeCommentToken.length());
                        }
                        final String commentTokenBody = lineContent.tail();
                        tokenBuilder.append(commentTokenBody);
                        line.addToken(tokenBuilder.toString(), LINE_COMMENT, delimiterBuilder.toString());
                        tokenBuilder.delete(0, tokenBuilder.length());
                    } else if (isMultilineCommentStarted(tokenBuilder)) {
                        final int commentStartIndex = tokenBuilder.indexOf(MULTILINE_COMMENT_PREFIX);
                        if (commentStartIndex != 0) {
                            final String beforeCommentToken = tokenBuilder.substring(0, commentStartIndex);
                            final List<String> tokens = tokenSplitter.splitByDelimiters(beforeCommentToken);
                            line.addToken(tokens.get(0), NORMAL, delimiterBuilder.toString());
                            for (int i = 1; i < tokens.size(); i++) {
                                line.addToken(tokens.get(i), NORMAL, "");
                            }

                            delimiterBuilder.delete(0, delimiterBuilder.length());
                            tokenBuilder.delete(0, beforeCommentToken.length());
                        }
                        multilineStarted = true;
                    } else if (isStringLiteralStarted(tokenBuilder)) {
                        final int stringStartIndex = getStringStartIndex(tokenBuilder);
                        stringQuote = tokenBuilder.substring(stringStartIndex, stringStartIndex + 1);
                        if (stringStartIndex != 0) {
                            final String beforeStringToken = tokenBuilder.substring(0, stringStartIndex);
                            final List<String> tokens = tokenSplitter.splitByDelimiters(beforeStringToken);
                            line.addToken(tokens.get(0), NORMAL, delimiterBuilder.toString());
                            for (int i = 1; i < tokens.size(); i++) {
                                line.addToken(tokens.get(i), NORMAL, "");
                            }

                            delimiterBuilder.delete(0, delimiterBuilder.length());
                            tokenBuilder.delete(0, beforeStringToken.length());
                        }
                    }
                }
            }

            if (tokenBuilder.length() > 0) {
                if (isInsideStringLiteral(stringQuote)) { // string literal not ended with closing quote
                    line.addToken(tokenBuilder.toString(), STRING_LITERAL, delimiterBuilder.toString());
                    stringQuote = null;
                } else if (multilineStarted) {
                    line.addToken(tokenBuilder.toString(), MULTI_LINE_COMMENT, delimiterBuilder.toString());
                } else {
                    final String token = tokenBuilder.toString();
                    final List<String> tokens = tokenSplitter.splitByDelimiters(token);
                    line.addToken(tokens.get(0), NORMAL, delimiterBuilder.toString());
                    for (int i = 1; i < tokens.size(); i++) {
                        line.addToken(tokens.get(i), NORMAL, "");
                    }
                }
                tokenBuilder.delete(0, tokenBuilder.length());
                delimiterBuilder.delete(0, delimiterBuilder.length());
            }
        }
    }

    private int getStringStartIndex(final StringBuilder tokenBuilder) {
        final int doubleQuoteIndex = tokenBuilder.indexOf(DOUBLE_QUOTATION);
        return doubleQuoteIndex != -1 ? doubleQuoteIndex : tokenBuilder.indexOf(SINGLE_QUOTATION);
    }

    private boolean isStringLiteralStarted(final StringBuilder tokenBuilder) {
        return tokenBuilder.indexOf(DOUBLE_QUOTATION) != -1 ||
            tokenBuilder.indexOf(SINGLE_QUOTATION) != -1;
    }

    private boolean isMultilineCommentStarted(final StringBuilder tokenBuilder) {
        return tokenBuilder.indexOf(MULTILINE_COMMENT_PREFIX) != -1;
    }

    private boolean isLineCommentStarted(final StringBuilder tokenBuilder) {
        return tokenBuilder.indexOf(LINE_COMMENT_PREFIX) != -1;
    }

    private boolean isMultilineCommentEnded(final StringBuilder tokenBuilder) {
        return tokenBuilder.indexOf(MULTILINE_COMMENT_SUFFIX) != -1;
    }

    private boolean isStringLiteralEnded(final StringBuilder tokenBuilder, final String stringQuote) {
        if (stringQuote != null) {
            return tokenBuilder.indexOf(stringQuote) < tokenBuilder.lastIndexOf(stringQuote);
        }
        return false;
    }

    private boolean isInsideStringLiteral(final String stringQuote) {
        return DOUBLE_QUOTATION.equals(stringQuote) || SINGLE_QUOTATION.equals(stringQuote);
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
