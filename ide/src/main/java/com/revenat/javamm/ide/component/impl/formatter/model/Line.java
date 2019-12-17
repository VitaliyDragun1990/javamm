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

package com.revenat.javamm.ide.component.impl.formatter.model;

import com.revenat.javamm.code.util.StringIterator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.revenat.javamm.code.syntax.Delimiters.CLOSING_CURLY_BRACE;
import static com.revenat.javamm.code.syntax.Delimiters.OPENING_CURLY_BRACE;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

/**
 * @author Vitaliy Dragun
 */
public final class Line {

    private final int number;

    private final String originalContent;

    private final List<Token> tokens;

    private String significantContent;

    private String indentation;

    Line(final int number, final String originalContent) {
        this.number = number;
        this.originalContent = requireNonNull(originalContent);
        this.tokens = new ArrayList<>();
        this.indentation = "";
        this.significantContent = "";
    }

    public void addTokens(final List<Token> newTokens) {
        newTokens.forEach(this::addToken);
    }

    public void addToken(final Token token) {
        final Optional<Token> optionalLast = getLastToken();
        if (optionalLast.isPresent()) {
            final Token lastToken = optionalLast.get();
            lastToken.setNext(token);
            token.setPrevious(lastToken);
        }
        this.tokens.add(token);
    }

    public Optional<Token> getFirstToken() {
        return tokens.isEmpty() ? Optional.empty() : Optional.of(tokens.get(0));
    }

    private Optional<Token> getLastToken() {
        return tokens.isEmpty() ? Optional.empty() : Optional.of(tokens.get(tokens.size() - 1));
    }

    public void setIndentation(final String indentation) {
        this.indentation = requireNonNull(indentation);
    }

    public StringIterator getSignificantContent() {
        return new StringIterator(significantContent);
    }

    public void setSignificantContent(final String significantContent) {
        this.significantContent = requireNonNull(significantContent);
    }

    public String getOriginalContent() {
        return originalContent;
    }

    public boolean isEmpty() {
        return significantContent.isEmpty();
    }

    public boolean isOpenBlockLine() {
        return !isEmpty() && lastSignificantTokenIsOpeningCurlyBrace();
    }

    public boolean isClosingBlockLine() {
        return !isEmpty() && containsOnlyClosingCurlyBraceToken();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Line line = (Line) o;
        return number == line.number &&
            originalContent.equals(line.originalContent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, originalContent);
    }

    @Override
    public String toString() {
        final StringBuilder string = new StringBuilder(indentation);
        for (final Token token : tokens) {
            string.append(token.toString());
        }
        return string.toString();
    }

    private boolean lastSignificantTokenIsOpeningCurlyBrace() {
        final List<Token> significantTokens = tokens.stream()
            .filter(t -> t.isSignificant() || t.isStringLiteral())
            .collect(toList());

        if (significantTokens.isEmpty()) {
            return false;
        } else {
            final Token last = significantTokens.get(significantTokens.size() - 1);
            return last.isSignificant() && last.equalsTo(OPENING_CURLY_BRACE);
        }
    }

    private boolean containsOnlyClosingCurlyBraceToken() {
        final List<Token> significantTokens = tokens.stream()
            .filter(t -> t.isSignificant() || t.isStringLiteral())
            .collect(toList());

        if (significantTokens.size() == 1) {
            final Token single = significantTokens.get(0);
            return single.isSignificant() && single.equalsTo(CLOSING_CURLY_BRACE);
        }
        return false;
    }

}
