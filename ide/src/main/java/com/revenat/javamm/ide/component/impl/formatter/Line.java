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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.revenat.javamm.code.syntax.Delimiters.CLOSING_CURLY_BRACE;
import static com.revenat.javamm.code.syntax.Delimiters.OPENING_CURLY_BRACE;
import static com.revenat.javamm.ide.component.impl.formatter.Token.Type.NORMAL;
import static com.revenat.javamm.ide.component.impl.formatter.Token.Type.STRING_LITERAL;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

/**
 * @author Vitaliy Dragun
 */
final class Line {

    private final int number;

    private final String originalContent;

    private String significantContent;

    private final List<Token> tokens;

    private String indentation;

    Line(final int number, final String originalContent) {
        this.number = number;
        this.originalContent = requireNonNull(originalContent);
        this.tokens = new ArrayList<>();
        this.indentation = "";
        this.significantContent = "";
    }

    void addToken(String content, Token.Type tokenType, String delimiterBefore) {
        final Token newToken = new Token(content, tokenType, delimiterBefore);
        final Optional<Token> optionalLast = getLastToken();
        if (optionalLast.isPresent()) {
            Token lastToken = optionalLast.get();
            lastToken.setNext(newToken);
            newToken.setPrevious(lastToken);
        }
        this.tokens.add(newToken);
    }

    Optional<Token> getFirstToken() {
        return tokens.isEmpty() ? Optional.empty() : Optional.of(tokens.get(0));
    }

    private Optional<Token> getLastToken() {
        return tokens.isEmpty() ? Optional.empty() : Optional.of(tokens.get(tokens.size() - 1));
    }

    void setIndentation(final String indentation) {
        this.indentation = indentation;
    }

    void setSignificantContent(String significantContent) {
        this.significantContent = requireNonNull(significantContent);
    }

    String getSignificantContent() {
        return significantContent;
    }

    String getOriginalContent() {
        return originalContent;
    }

    boolean isEmpty() {
        return significantContent.isEmpty();
    }

    boolean isOpenBlockLine() {
        return !isEmpty() && isLastSignificantTokenContains(OPENING_CURLY_BRACE);
    }

    boolean isClosingBlockLine() {
        return !isEmpty() && isOnlyOneSignificantTokenWithContent(CLOSING_CURLY_BRACE);
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
        StringBuilder string = new StringBuilder(indentation);
        for (Token token : tokens) {
            string.append(token.toString());
        }
        return string.toString();
    }

    private boolean isLastSignificantTokenContains(String content) {
        final List<Token> significantTokens = tokens.stream()
            .filter(t -> t.isType(NORMAL) || t.isType(STRING_LITERAL))
            .collect(toList());

        if (significantTokens.isEmpty()) {
            return false;
        } else {
            Token last = significantTokens.get(significantTokens.size() - 1);
            return last.isType(NORMAL) && last.hasContentEquals(List.of(content));
        }
    }

    boolean isOnlyOneSignificantTokenWithContent(final String content) {
        final List<Token> significantTokens = tokens.stream()
            .filter(t -> t.isType(NORMAL) || t.isType(STRING_LITERAL))
            .collect(toList());

        if (significantTokens.size() == 1) {
            Token single = significantTokens.get(0);
            return single.isType(NORMAL) && single.hasContentEquals(List.of(content));
        }
        return false;
    }
}
