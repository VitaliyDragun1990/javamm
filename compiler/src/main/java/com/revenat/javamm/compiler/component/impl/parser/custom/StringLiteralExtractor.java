
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

/**
 * Checks whether string literal (text fragments surrounded by either single
 * quote ' or by double quote '' notation) is present in the specified source
 * line and extracts such literal if any.
 *
 * @author Vitaliy Dragun
 *
 */
class StringLiteralExtractor {

    private static final String SINGLE_QUOTE_NOTATION = "'";

    private static final String DOUBLE_QUOTE_NOTATION = "\"";

    boolean isStringLiteralPresent(final String line) {
        final int singleIndex = line.indexOf(SINGLE_QUOTE_NOTATION);
        final int doubleIndex = line.indexOf(DOUBLE_QUOTE_NOTATION);

        return singleIndex >= 0 || doubleIndex >= 0;
    }

    StringLiteralHolder extract(final String line) {
        final int singleIndex = line.indexOf(SINGLE_QUOTE_NOTATION);
        final int doubleIndex = line.indexOf(DOUBLE_QUOTE_NOTATION);

        if (singleIndex >= 0 && doubleIndex >= 0) {
            return singleIndex < doubleIndex ? quotedLiteralFrom(line, SINGLE_QUOTE_NOTATION) :
                quotedLiteralFrom(line, DOUBLE_QUOTE_NOTATION);
        } else if (singleIndex >= 0) {
            return quotedLiteralFrom(line, SINGLE_QUOTE_NOTATION);
        } else if (doubleIndex >= 0) {
            return quotedLiteralFrom(line, DOUBLE_QUOTE_NOTATION);
        } else {
            throw new IllegalArgumentException("Can not extract string literal from line which does not contain it. " +
                    "Check literal precence before calling extract() with isStringLiteralPresent() method");
        }
    }

    private StringLiteralHolder quotedLiteralFrom(final String line, final String quotedNotation) {
        final int fromPosition = getFromPosition(line, quotedNotation);
        final int toPosition = getToPosition(line, fromPosition, quotedNotation);
        final String quotedString = getQuotedString(line, fromPosition, toPosition);
        final String beforeLiteralFragment = line.substring(0, fromPosition);
        final String afterLiteralFragment = line.substring(toPosition + 1, line.length());
        return new StringLiteralHolder(quotedString, beforeLiteralFragment, afterLiteralFragment);
    }

    private int getFromPosition(final String line, final String quoteNotation) {
        return line.indexOf(quoteNotation);
    }

    private int getToPosition(final String line, final int fromPosition, final String quoteNotation) {
        int toPosition = line.indexOf(quoteNotation, fromPosition + 1);
        if (toPosition == -1) {
            toPosition = line.length() - 1;
        }
        return toPosition;
    }

    private String getQuotedString(final String line, final int fromPosition, final int toPosition) {
        return line.substring(fromPosition, toPosition + 1);
    }

    static class StringLiteralHolder {

        final String literal;

        final String beforeLiteralFragment;

        final String afterLiteralFragment;

        StringLiteralHolder(final String literal,
                            final String beforeLiteralFragment,
                            final String afterLiteralFragment) {
            this.literal = literal;
            this.beforeLiteralFragment = beforeLiteralFragment;
            this.afterLiteralFragment = afterLiteralFragment;
        }
    }
}
