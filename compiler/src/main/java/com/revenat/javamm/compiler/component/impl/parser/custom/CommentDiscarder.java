
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

import static com.revenat.javamm.code.syntax.Delimiters.END_MULTI_LINE_COMMENT;
import static com.revenat.javamm.code.syntax.Delimiters.START_MULTI_LINE_COMMENT;
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

    private boolean multiLineCommentStarted;

    private Character stringLiteralQuote;

    CommentDiscarder(final String sourceLine, final boolean multiLineCommentStarted) {
        this.sourceLine = toChars(sourceLine).listIterator();
        commentFreeContent = new StringBuilder();
        commentedOutContent = new StringBuilder();
        this.multiLineCommentStarted = multiLineCommentStarted;
        stringLiteralQuote = null;
    }

    CommentFreeSourceLine discardComments() {
        while (sourceLine.hasNext()) {
            final Character current = sourceLine.next();

            if (isWithinMultiLineComment()) {
                commentedOutContent.append(current);
                if (isMultiLineCommentEndDetected()) {
                    endMultiLineComment();
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
                } else if (isMultiLineCommentStartDetected()) {
                    startMultiLineComment();
                } else if (isSingleLineCommentDetected()) {
                    discardSingleLineCommentContent();
                    break;
                }
            }
        }
        return new CommentFreeSourceLine(commentFreeContent.toString().trim(), isWithinMultiLineComment());
    }

    private void discardSingleLineCommentContent() {
        commentFreeContent.delete(commentFreeContent.length() - 2, commentFreeContent.length());
    }

    private boolean isSingleLineCommentDetected() {
        return commentFreeContent.indexOf(START_SINGLE_LINE_COMMENT) != -1;
    }

    private void startMultiLineComment() {
        discardSingleLineCommentContent();
        multiLineCommentStarted = true;
    }

    private boolean isMultiLineCommentStartDetected() {
        return commentFreeContent.indexOf(START_MULTI_LINE_COMMENT) != -1;
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

    private void endMultiLineComment() {
        multiLineCommentStarted = false;
        commentedOutContent.delete(0, commentedOutContent.length());
        commentFreeContent.append(' '); // to separate possible tokens which were separated before by multiline comment
    }

    private boolean isWithinMultiLineComment() {
        return multiLineCommentStarted;
    }

    private boolean isMultiLineCommentEndDetected() {
        return commentedOutContent.indexOf(END_MULTI_LINE_COMMENT) != -1;
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

        final boolean multiLineCommentStarted;

        protected CommentFreeSourceLine(final String content, final boolean multiLineCommentStarted) {
            this.content = content;
            this.multiLineCommentStarted = multiLineCommentStarted;
        }

        boolean isEmpty() {
            return content.isEmpty();
        }
    }
}
