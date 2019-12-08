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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Comparator.comparing;

/**
 * Splits tokens by provided delimiters
 *
 * @author Vitaliy Dragun
 */
public final class TokenSplitter {

    private final List<String> delimiters;

    public TokenSplitter(final Collection<String> delimiters) {
        this.delimiters = sortByLengthReversed(delimiters);
    }

    private static List<String> sortByLengthReversed(final Collection<String> delimiters) {
        final List<String> result = new ArrayList<>(delimiters);
        result.sort(comparing(String::length).reversed());
        return result;
    }

    /**
     * Tries to splits specified {@code token} into several tokens using provided delimiters
     *
     * @param token token to split further
     * @return {@linkplain List list of tokens} if specified {@code token} was successfully split by delimiters,
     * or if specified {@code token} could't be split further, {@linkplain List list} with single original token in it
     */
    public List<Token> splitByDelimiters(final Token token) {
        final List<String> contents = splitByDelimiters(token.getContent());
        if (contents.size() > 1) {
            return createTokensFrom(contents, token.getDelimiter());
        }
        return List.of(token);
    }

    private List<String> splitByDelimiters(final String token) {
        final List<String> tokens = new ArrayList<>();

        for (final String delimiter : delimiters) {
            if (token.contains(delimiter)) {
                final int startIdx = token.indexOf(delimiter);
                final String headToken = token.substring(0, startIdx);
                if (headToken.length() > 1) {
                    tokens.addAll(splitByDelimiters(headToken));
                } else if (headToken.length() == 1) {
                    tokens.add(headToken);
                }

                tokens.add(delimiter);

                final String tailToken = token.substring(startIdx + delimiter.length());
                if (tailToken.length() > 1) {
                    tokens.addAll(splitByDelimiters(tailToken));
                } else if (tailToken.length() == 1) {
                    tokens.add(tailToken);
                }

                return tokens;
            }
        }

        tokens.add(token);
        return tokens;
    }

    private List<Token> createTokensFrom(final List<String> content, final String originalDelimiter) {
        final List<Token> tokens = new ArrayList<>(content.size());
        tokens.add(new Token(content.get(0), Token.Type.NORMAL, originalDelimiter));
        for (int i = 1; i < content.size(); i++) {
            tokens.add(new Token(content.get(i), Token.Type.NORMAL, Token.NO_DELIMITER));
        }
        return tokens;
    }
}
