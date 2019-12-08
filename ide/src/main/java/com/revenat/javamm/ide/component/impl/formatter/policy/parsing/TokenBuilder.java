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

import com.revenat.javamm.ide.component.impl.formatter.model.Token;

import java.util.Optional;

import static com.revenat.javamm.code.syntax.Delimiters.DOUBLE_QUOTATION;
import static com.revenat.javamm.code.syntax.Delimiters.SINGLE_QUOTATION;
import static com.revenat.javamm.ide.component.impl.formatter.model.Token.Type.COMMENT;
import static com.revenat.javamm.ide.component.impl.formatter.model.Token.Type.NORMAL;
import static com.revenat.javamm.ide.component.impl.formatter.model.Token.Type.STRING_LITERAL;
import static com.revenat.javamm.ide.component.impl.formatter.policy.parsing.TokenBuilderState.INSIDE_LINE_COMMENT;
import static com.revenat.javamm.ide.component.impl.formatter.policy.parsing.TokenBuilderState.INSIDE_MULTILINE_COMMENT;
import static com.revenat.javamm.ide.component.impl.formatter.policy.parsing.TokenBuilderState.INSIDE_STRING_LITERAL;

/**
 * @author Vitaliy Dragun
 */
public class TokenBuilder {

    static final String LINE_COMMENT_PREFIX = "//";

    static final String MULTILINE_COMMENT_PREFIX = "/*";

    static final String MULTILINE_COMMENT_SUFFIX = "*/";

    private boolean insideMultilineComment;

    private String stringLiteralType;

    private final StringBuilder tokenBuffer;

    private final StringBuilder delimiterBuffer;

    private TokenBuilderState builderState;

    public TokenBuilder() {
        this.insideMultilineComment = false;
        this.tokenBuffer = new StringBuilder();
        this.delimiterBuffer = new StringBuilder();
        this.stringLiteralType = null;
        this.builderState = TokenBuilderState.NORMAL;
    }

    /**
     * Should be called for each character in a line
     *
     * @param c character in a line to build tokens from
     * @return optional token if any token can be build from specified character + previously specified ones
     */
    public Optional<Token> append(final char c) {
        return builderState.append(this, c);
    }

    /**
     * Should be called after all characters in the line have been processed.
     * Prepares token builder to process new line
     *
     * @return optional token if one can be build from previously specified characters
     */
    public Optional<Token> lineEnded() {
        final Optional<Token> optionalToken = builderState.lineEnded(this);
        clearContent();
        clearDelimiter();
        stringLiteralType = null;
        return optionalToken;
    }

    void setBuilderState(final TokenBuilderState builderState) {
        this.builderState = builderState;
    }

    void appendDelimiter(final String delimiter) {
        delimiterBuffer.append(delimiter);
    }

    void appendContent(final char c) {
        tokenBuffer.append(c);
    }

    Optional<Token> getTokenIfPresentWithStateTransition() {
        if (isLineCommentStarted()) {
            setBuilderState(INSIDE_LINE_COMMENT);
            return getTokenBeforeLineCommentIfAny();

        } else if (isMultilineCommentStarted()) {
            setBuilderState(INSIDE_MULTILINE_COMMENT);
            return getTokenBeforeMultilineCommentIfAny();

        } else if (isStringLiteralStarted()) {
            setBuilderState(INSIDE_STRING_LITERAL);
            return getTokenBeforeStringLiteralIfAny();
        }
        return Optional.empty();
    }

    Optional<Token> getTokenIfPresent(final Token.Type tokenType) {
        if (isNotEmpty()) {
            final Token token = new Token(tokenBuffer.toString(), tokenType, delimiterBuffer.toString());

            clearContent();
            clearDelimiter();
            return Optional.of(token);
        }
        return Optional.empty();
    }

    Optional<Token> getStringLiteralTokenIfPresent() {
        if (isStringLiteralEnded()) {
            final Token token = new Token(tokenBuffer.toString(), STRING_LITERAL, delimiterBuffer.toString());

            clearContent();
            clearDelimiter();
            return Optional.of(token);
        }
        return Optional.empty();
    }

    Optional<Token> getMultilineCommentTokenIfPresent() {
        if (isMultilineCommentEnded()) {
            final Token token = new Token(tokenBuffer.toString(), COMMENT, delimiterBuffer.toString());

            clearContent();
            clearDelimiter();
            return Optional.of(token);
        }
        return Optional.empty();
    }

    private boolean isLineCommentStarted() {
        return tokenBuffer.indexOf(LINE_COMMENT_PREFIX) != -1;
    }

    private Optional<Token> getTokenBeforeLineCommentIfAny() {
        if (containsContentBeforeLineComment()) {
            final String tokenContent = getContentBeforeLineComment();
            final String tokenDelimiter = delimiterBuffer.toString();

            delimiterBuffer.delete(0, delimiterBuffer.length());
            clearContentBeforeLineComment();
            return Optional.of(new Token(tokenContent, NORMAL, tokenDelimiter));
        }
        return Optional.empty();
    }

    private boolean isMultilineCommentStarted() {
        insideMultilineComment = tokenBuffer.indexOf(MULTILINE_COMMENT_PREFIX) != -1;
        return insideMultilineComment;
    }

    private Optional<Token> getTokenBeforeMultilineCommentIfAny() {
        if (containsContentBeforeMultiLineComment()) {
            final String tokenContent = getContentBeforeMultiLineComment();
            final String tokenDelimiter = delimiterBuffer.toString();

            delimiterBuffer.delete(0, delimiterBuffer.length());
            clearContentBeforeMultiLineComment();
            return Optional.of(new Token(tokenContent, NORMAL, tokenDelimiter));
        }
        return Optional.empty();
    }

    private boolean isStringLiteralStarted() {
        if (tokenBuffer.indexOf(DOUBLE_QUOTATION) != -1) {
            stringLiteralType = DOUBLE_QUOTATION;
            return true;
        } else if (tokenBuffer.indexOf(SINGLE_QUOTATION) != -1) {
            stringLiteralType = SINGLE_QUOTATION;
            return true;
        } else {
            return false;
        }
    }

    private Optional<Token> getTokenBeforeStringLiteralIfAny() {
        if (containsContentBeforeStringLiteral()) {
            final String tokenContent = getContentBeforeStringLiteral();
            final String tokenDelimiter = delimiterBuffer.toString();

            delimiterBuffer.delete(0, delimiterBuffer.length());
            clearContentBeforeStringLiteral();
            return Optional.of(new Token(tokenContent, NORMAL, tokenDelimiter));
        }
        return Optional.empty();
    }

    private boolean isNotEmpty() {
        return tokenBuffer.length() > 0;
    }

    private void clearContent() {
        tokenBuffer.delete(0, tokenBuffer.length());
    }

    private void clearDelimiter() {
        delimiterBuffer.delete(0, delimiterBuffer.length());
    }

    private boolean containsContentBeforeLineComment() {
        return tokenBuffer.indexOf(LINE_COMMENT_PREFIX) > 0;
    }

    private String getContentBeforeLineComment() {
        return tokenBuffer.substring(0, tokenBuffer.indexOf(LINE_COMMENT_PREFIX));
    }

    private void clearContentBeforeLineComment() {
        tokenBuffer.delete(0, tokenBuffer.indexOf(LINE_COMMENT_PREFIX));
    }

    private boolean containsContentBeforeMultiLineComment() {
        return tokenBuffer.indexOf(MULTILINE_COMMENT_PREFIX) > 0;
    }

    private String getContentBeforeMultiLineComment() {
        return tokenBuffer.substring(0, tokenBuffer.indexOf(MULTILINE_COMMENT_PREFIX));
    }

    private void clearContentBeforeMultiLineComment() {
        tokenBuffer.delete(0, tokenBuffer.indexOf(MULTILINE_COMMENT_PREFIX));
    }

    private boolean containsContentBeforeStringLiteral() {
        return tokenBuffer.indexOf(stringLiteralType) > 0;
    }

    private String getContentBeforeStringLiteral() {
        return tokenBuffer.substring(0, tokenBuffer.indexOf(stringLiteralType));
    }

    private void clearContentBeforeStringLiteral() {
        tokenBuffer.delete(0, tokenBuffer.indexOf(stringLiteralType));
    }

    private boolean isStringLiteralEnded() {
        if (tokenBuffer.indexOf(stringLiteralType) < tokenBuffer.lastIndexOf(stringLiteralType)) {
            stringLiteralType = null;
            return true;
        }
        return false;
    }

    private boolean isMultilineCommentEnded() {
        if (tokenBuffer.indexOf(MULTILINE_COMMENT_SUFFIX) != -1) {
            insideMultilineComment = false;
            return true;
        }
        return false;
    }
}
