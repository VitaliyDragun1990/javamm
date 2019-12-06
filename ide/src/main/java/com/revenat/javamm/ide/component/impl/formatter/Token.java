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

    Token(final String content, final Type type, String delimiterBefore) {
        this.content = requireNonNull(content);
        this.type = requireNonNull(type);
        this.delimiterBefore = requireNonNull(delimiterBefore);
    }

    void setPrevious(Token token) {
        previous = token;
    }

    void setNext(Token token) {
        next = token;
    }

    boolean isType(Type type) {
        return this.type == type;
    }

    void setDelimiterBefore(final String delimiterBefore) {
        this.delimiterBefore = delimiterBefore;
    }

    boolean hasContentEquals(final Collection<String> contents) {
        return contents.contains(content);
    }

    void deleteDelimiterBefore() {
        delimiterBefore = NO_DELIMITER;
    }

    public boolean previousTokenHasContentEquals(final Collection<String> contents) {
        return previous != null && contents.contains(previous.content);
    }

    Optional<Token> getNext() {
        return Optional.ofNullable(next);
    }

    boolean previousTokenHasType(final Type type) {
        return previous != null && previous.type == type;
    }

    @Override
    public String toString() {
        return format("%s%s", delimiterBefore, content);
    }

    boolean isFirst() {
        return previous == null;
    }

    enum Type {
        NORMAL,
        LINE_COMMENT,
        MULTI_LINE_COMMENT,
        STRING_LITERAL
    }
}
