
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

package com.revenat.javamm.compiler.component.impl.parser;

import com.revenat.javamm.compiler.component.TokenParser;
import com.revenat.javamm.compiler.component.impl.parser.CommentDiscarder.CommentFreeSourceLine;
import com.revenat.javamm.compiler.component.impl.parser.StringLiteralExtractor.StringLiteralHolder;
import com.revenat.javamm.compiler.model.TokenParserResult;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vitaliy Dragun
 *
 */
public class TokenParserImpl implements TokenParser {

    private final StringLiteralExtractor stringLiteralExtractor;

    private final LineSplitter lineSplitter;

    public TokenParserImpl() {
        stringLiteralExtractor = new StringLiteralExtractor();
        lineSplitter = new LineSplitter();
    }

    @Override
    public TokenParserResult parseLine(final String sourceCodeLine, final boolean isMultilineCommentStartedBefore) {
        final String trimmedLine = trimAllWhitespaceCharacters(sourceCodeLine);

        return parseTrimmed(trimmedLine, isMultilineCommentStartedBefore);
    }

    private TokenParserResult parseTrimmed(final String line, final boolean isMultilineCommentStartedBefore) {
        if (line.isEmpty()) {
            return new TokenParserResult(List.of(), isMultilineCommentStartedBefore);
        } else {
            return parseIntoTokens(line, isMultilineCommentStartedBefore);
        }
    }

    private TokenParserResult parseIntoTokens(final String line, final boolean isMultilineCommentStartedBefore) {
        final CommentFreeSourceLine commentFreeLine =
                new CommentDiscarder(line, isMultilineCommentStartedBefore).discardComments();

        final List<String> tokens = new ArrayList<>();
        split(commentFreeLine.content, tokens);

        return new TokenParserResult(tokens, commentFreeLine.multilineCommentStarted);
    }

    private void split(final String line, final List<String> tokens) {
        if (line.isEmpty()) {
            return;
        }

        if (stringLiteralExtractor.isStringLiteralPresent(line)) {
            extractStringLiteral(line, tokens);
        } else {
            splitByDelimiters(line, tokens);
        }
    }

    private void extractStringLiteral(final String line, final List<String> tokens) {
        final StringLiteralHolder literalHolder = stringLiteralExtractor.extract(line);

        splitByDelimiters(literalHolder.beforeLiteralFragment, tokens);
        tokens.add(literalHolder.literal);
        split(literalHolder.afterLiteralFragment, tokens);
    }

    private void splitByDelimiters(final String line, final List<String> tokens) {
        tokens.addAll(lineSplitter.splitByDelimiters(line));
    }

    private String trimAllWhitespaceCharacters(final String sourceCodeLine) {
        return sourceCodeLine.replaceAll("(^\\h*)|(\\h*$)", "").trim();
    }
}
