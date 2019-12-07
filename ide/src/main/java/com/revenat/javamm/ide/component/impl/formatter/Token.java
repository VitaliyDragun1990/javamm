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

import java.util.Collection;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * @author Vitaliy Dragun
 */
final class Token {

    private static final String NO_DELIMITER = "";

    private Token previous;

    private Token next;

    private final String content;

    private final Type type;

    private String delimiterBefore;

    Token(final String content, final Type type, final String delimiterBefore) {
        this.content = requireNonNull(content);
        this.type = requireNonNull(type);
        this.delimiterBefore = requireNonNull(delimiterBefore);
    }

    void setPrevious(final Token token) {
        previous = token;
    }

    void setNext(final Token token) {
        next = token;
    }

    void setDelimiterBefore(final String delimiterBefore) {
        this.delimiterBefore = delimiterBefore;
    }

    void deleteDelimiterBefore() {
        delimiterBefore = NO_DELIMITER;
    }

    Optional<Token> getNext() {
        return Optional.ofNullable(next);
    }

    boolean equalsAny(final Collection<String> tokenContent) {
        return tokenContent.contains(content);
    }

    boolean equalsTo(final String tokenContent) {
        return content.equals(tokenContent);
    }

    @Override
    public String toString() {
        return format("%s%s", delimiterBefore, content);
    }

    boolean isFirst() {
        return previous == null;
    }

    boolean isComment() {
        return type == Type.COMMENT;
    }

    boolean previousOneIsComment() {
        return previous != null && previous.type == Type.COMMENT;
    }

    public boolean previousOneEqualsAny(final Collection<String> tokenContent) {
        return previous != null && tokenContent.contains(previous.content);
    }

    boolean isStringLiteral() {
        return type == Type.STRING_LITERAL;
    }

    boolean isSignificant() {
        return type == Type.NORMAL;
    }

    enum Type {
        NORMAL,
        COMMENT,
        STRING_LITERAL
    }
}
