
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

package com.revenat.javamm.compiler.component.impl.parser.custom;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import static com.revenat.javamm.code.syntax.Delimiters.END_MULTILINE_COMMENT;
import static com.revenat.javamm.code.syntax.Delimiters.START_MULTILINE_COMMENT;
import static com.revenat.javamm.code.syntax.Delimiters.START_SINGLE_LINE_COMMENT;

/**
 * Discards all comments from source line
 *
 * @author Vitaliy Dragun
 *
 */
class CommentDiscarder {

    private final ListIterator<Character> sourceLine;

    private final StringBuilder commentFreeContent;

    private final StringBuilder commentedOutContent;

    private boolean multilineCommentStarted;

    private Character stringLiteralQuote;


    CommentDiscarder(final String sourceLine, final boolean multilineCommentStarted) {
        this.sourceLine = toChars(sourceLine).listIterator();
        commentFreeContent = new StringBuilder();
        commentedOutContent = new StringBuilder();
        this.multilineCommentStarted = multilineCommentStarted;
        endStringLiteral();
    }

    CommentFreeSourceLine discardComments() {
        while (sourceLine.hasNext()) {
            final Character current = sourceLine.next();

            if (isWithinMultilineComment()) {
                commentedOutContent.append(current);
                if (isMultilineCommentEndDetected()) {
                    endMultilineComment();
                }
            } else if (isWithinStringLiteral()) {
                commentFreeContent.append(current);
                if (isStringLiteralEndDetected(current)) {
                    endStringLiteral();
                }
            } else {
                commentFreeContent.append(current);
                if (isStringLiteralQuote(current)) {
                    startStringLiteral(current);
                } else if (isMultilineCommentStartDetected()) {
                    startMultilineComment();
                } else if (isSingleLineCommentDetected()) {
                    discardSingleLineCommentCotent();
                    break;
                }
            }
        }
        return new CommentFreeSourceLine(commentFreeContent.toString(), isWithinMultilineComment());
    }

    private void discardSingleLineCommentCotent() {
        commentFreeContent.delete(commentFreeContent.length() - 2, commentFreeContent.length());
    }

    private boolean isSingleLineCommentDetected() {
        return commentFreeContent.indexOf(START_SINGLE_LINE_COMMENT) != -1;
    }

    private void startMultilineComment() {
        discardSingleLineCommentCotent();
        multilineCommentStarted = true;
    }

    private boolean isMultilineCommentStartDetected() {
        return commentFreeContent.indexOf(START_MULTILINE_COMMENT) != -1;
    }

    private void startStringLiteral(final Character c) {
        stringLiteralQuote = c;
    }

    private void endStringLiteral() {
        stringLiteralQuote = null;
    }

    private boolean isStringLiteralEndDetected(final Character c) {
        return c.equals(stringLiteralQuote);
    }

    private void endMultilineComment() {
        multilineCommentStarted = false;
        commentedOutContent.delete(0, commentedOutContent.length());
    }

    private boolean isWithinMultilineComment() {
        return multilineCommentStarted;
    }

    private boolean isMultilineCommentEndDetected() {
        return commentedOutContent.indexOf(END_MULTILINE_COMMENT) != -1;
    }

    private boolean isStringLiteralQuote(final Character c) {
        return c.equals('"') || c.equals('\'');
    }

    private boolean isWithinStringLiteral() {
        return stringLiteralQuote != null;
    }

    private List<Character> toChars(final String line) {
        final List<Character> list = new ArrayList<>();

        for (final char c : line.toCharArray()) {
            list.add(c);
        }
        return list;
    }

    static class CommentFreeSourceLine {
        final String content;

        final boolean multilineCommentStarted;

        protected CommentFreeSourceLine(final String content, final boolean multilineCommentStarted) {
            this.content = content;
            this.multilineCommentStarted = multilineCommentStarted;
        }
    }
}
