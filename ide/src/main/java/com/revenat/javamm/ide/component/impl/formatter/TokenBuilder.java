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

import static com.revenat.javamm.code.syntax.Delimiters.DOUBLE_QUOTATION;
import static com.revenat.javamm.code.syntax.Delimiters.SINGLE_QUOTATION;

/**
 * @author Vitaliy Dragun
 */
final class TokenBuilder {

    private static final String LINE_COMMENT_PREFIX = "//";

    private static final String MULTILINE_COMMENT_PREFIX = "/*";

    private static final String MULTILINE_COMMENT_SUFFIX = "*/";

    private boolean multilineStarted;

    private String stringQuote;

    private final StringBuilder builder;

    TokenBuilder(final boolean multilineStarted) {
        this.multilineStarted = multilineStarted;
        this.stringQuote = null;
        builder = new StringBuilder();
    }

    public boolean isInsideStringLiteral() {
        return DOUBLE_QUOTATION.equals(stringQuote) || SINGLE_QUOTATION.equals(stringQuote);
    }

    public void append(final char c) {
        builder.append(c);
    }

    public void append(final String s) {
        builder.append(s);
    }

    public boolean isStringLiteralEnded() {
        if (stringQuote != null && builder.indexOf(stringQuote) < builder.lastIndexOf(stringQuote)) {
            stringQuote = null;
            return true;
        }
        return false;
    }

    public String getContent() {
        return builder.toString();
    }

    public void clearContent() {
        builder.delete(0, builder.length());
    }

    public boolean isInsideMultilineComment() {
        return multilineStarted;
    }

    public boolean isMultilineCommentEnded() {
        if (builder.indexOf(MULTILINE_COMMENT_SUFFIX) != -1) {
            multilineStarted = false;
            return true;
        }
        return false;
    }

    public boolean isNotEmpty() {
        return builder.length() > 0;
    }

    public boolean isLineCommentStarted() {
        return builder.indexOf(LINE_COMMENT_PREFIX) != -1;
    }

    public boolean containsBeforeLineCommentToken() {
        return builder.indexOf(LINE_COMMENT_PREFIX) > 0;
    }

    public String getBeforeLineCommentToken() {
        return builder.substring(0, builder.indexOf(LINE_COMMENT_PREFIX));
    }

    public void clearBeforeLineToken() {
        builder.delete(0, builder.indexOf(LINE_COMMENT_PREFIX));
    }

    public boolean isMultilineCommentStarted() {
        multilineStarted = builder.indexOf(MULTILINE_COMMENT_PREFIX) != -1;
        return multilineStarted;
    }

    public boolean containsBeforeMultilineCommentToken() {
        return builder.indexOf(MULTILINE_COMMENT_PREFIX) > 0;
    }

    public String getBeforeMultilineCommentToken() {
        return builder.substring(0, builder.indexOf(MULTILINE_COMMENT_PREFIX));
    }

    public void clearBeforeMultilineToken() {
        builder.delete(0, builder.indexOf(MULTILINE_COMMENT_PREFIX));
    }

    public boolean isStringLiteralStarted() {
        if (builder.indexOf(DOUBLE_QUOTATION) != -1) {
            stringQuote = DOUBLE_QUOTATION;
            return true;
        } else if (builder.indexOf(SINGLE_QUOTATION) != -1) {
            stringQuote = SINGLE_QUOTATION;
            return true;
        } else {
            return false;
        }
    }

    public boolean containsBeforeStringLiteralToken() {
        return builder.indexOf(stringQuote) > 0;
    }

    public String getBeforeStringLiteralToken() {
        return builder.substring(0, builder.indexOf(stringQuote));
    }

    public void clearBeforeStringLiteralToken() {
        builder.delete(0, builder.indexOf(stringQuote));
    }

    public void endStringLiteral() {
        stringQuote = null;
    }
}
