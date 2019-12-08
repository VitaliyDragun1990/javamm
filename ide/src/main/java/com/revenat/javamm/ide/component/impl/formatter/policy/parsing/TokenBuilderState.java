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

import static com.revenat.javamm.code.syntax.Delimiters.IGNORED_DELIMITERS;
import static com.revenat.javamm.ide.util.TabReplaceUtils.replaceTabulations;

/**
 * @author Vitaliy Dragun
 */
abstract class TokenBuilderState {

    static final TokenBuilderState NORMAL = new NormalTokenBuilderState();

    static final TokenBuilderState INSIDE_STRING_LITERAL = new InsideStringLiteralTokenBuilderState();

    static final TokenBuilderState INSIDE_MULTILINE_COMMENT = new InsideMultilineCommentTokenBuilderState();

    static final TokenBuilderState INSIDE_LINE_COMMENT = new InsideLineCommentBuilderState();

    abstract Optional<Token> append(TokenBuilder tokenBuilder, char c);

    abstract Optional<Token> lineEnded(TokenBuilder tokenBuilder);

    protected String asWhitespaceCharacters(final char delimiter) {
        if (delimiter == ' ') {
            return " ";
        } else if (delimiter == '\t') {
            return replaceTabulations(String.valueOf(delimiter));
        } else {
            return "";
        }
    }

    private static class NormalTokenBuilderState extends TokenBuilderState {

        @Override
        Optional<Token> append(final TokenBuilder tokenBuilder, final char c) {
            if (IGNORED_DELIMITERS.contains(c)) {
                final Optional<Token> optionalToken = tokenBuilder.getTokenIfPresent(Token.Type.NORMAL);
                tokenBuilder.appendDelimiter(asWhitespaceCharacters(c));
                return optionalToken;
            } else {
                tokenBuilder.appendContent(c);
                return tokenBuilder.getTokenIfPresentWithStateTransition();
            }
        }

        @Override
        Optional<Token> lineEnded(final TokenBuilder tokenBuilder) {
            return tokenBuilder.getTokenIfPresent(Token.Type.NORMAL);
        }
    }

    private static class InsideStringLiteralTokenBuilderState extends TokenBuilderState {

        @Override
        Optional<Token> append(final TokenBuilder tokenBuilder, final char c) {
            tokenBuilder.appendContent(c);
            final Optional<Token> token = tokenBuilder.getStringLiteralTokenIfPresent();
            token.ifPresent(t -> tokenBuilder.setBuilderState(NORMAL));
            return token;
        }

        @Override
        Optional<Token> lineEnded(final TokenBuilder tokenBuilder) {
            final Optional<Token> optionalToken = tokenBuilder.getTokenIfPresent(Token.Type.STRING_LITERAL);
            tokenBuilder.setBuilderState(NORMAL);
            return optionalToken;
        }
    }

    private static class InsideMultilineCommentTokenBuilderState extends TokenBuilderState {

        @Override
        Optional<Token> append(final TokenBuilder tokenBuilder, final char c) {
            tokenBuilder.appendContent(c);
            final Optional<Token> token = tokenBuilder.getMultilineCommentTokenIfPresent();
            token.ifPresent(t -> tokenBuilder.setBuilderState(NORMAL));
            return token;
        }

        @Override
        Optional<Token> lineEnded(final TokenBuilder tokenBuilder) {
            return tokenBuilder.getTokenIfPresent(Token.Type.STRING_LITERAL);
        }
    }

    private static class InsideLineCommentBuilderState extends TokenBuilderState {

        @Override
        Optional<Token> append(final TokenBuilder tokenBuilder, final char c) {
            tokenBuilder.appendContent(c);
            return Optional.empty();
        }

        @Override
        Optional<Token> lineEnded(final TokenBuilder tokenBuilder) {
            final Optional<Token> optionalToken = tokenBuilder.getTokenIfPresent(Token.Type.COMMENT);
            tokenBuilder.setBuilderState(NORMAL);
            return optionalToken;
        }
    }
}
