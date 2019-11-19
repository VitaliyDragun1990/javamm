
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

import com.revenat.javamm.compiler.component.TokenParser;
import com.revenat.javamm.compiler.component.impl.parser.custom.CommentDiscarder.CommentFreeSourceLine;
import com.revenat.javamm.compiler.component.impl.parser.custom.StringLiteralParser.LiteralParserResult;
import com.revenat.javamm.compiler.model.TokenParserResult;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vitaliy Dragun
 *
 */
public class TokenParserImpl implements TokenParser {

    private final StringLiteralParser stringLiteralParser;

    private final ByDelimiterSplitter byDelimiterSplitter;

    public TokenParserImpl() {
        stringLiteralParser = new StringLiteralParser();
        byDelimiterSplitter = new ByDelimiterSplitter();
    }

    @Override
    public TokenParserResult parseLine(final String sourceCodeLine, final boolean isMultiLineCommentStartedBefore) {
        final String trimmedLine = trimAllWhitespaceCharacters(sourceCodeLine);

        return trimmedLine.isEmpty() ? emptyResult(isMultiLineCommentStartedBefore) :
            parse(trimmedLine, isMultiLineCommentStartedBefore);
    }

    private TokenParserResult parse(final String line, final boolean isMultiLineCommentStartedBefore) {
        final CommentFreeSourceLine commentFreeLine = discardAllComments(line, isMultiLineCommentStartedBefore);

        return commentFreeLine.isEmpty() ? emptyResult(commentFreeLine.multiLineCommentStarted) :
            resultWithTokens(splitIntoTokens(commentFreeLine.content), commentFreeLine.multiLineCommentStarted);
    }

    private CommentFreeSourceLine discardAllComments(final String line, final boolean isMultiLineCommentStartedBefore) {
        return new CommentDiscarder(line, isMultiLineCommentStartedBefore).discardComments();
    }

    private List<String> splitIntoTokens(final String line) {
        final List<String> tokens = new ArrayList<>();

        final List<LiteralParserResult> results = stringLiteralParser.parse(line);
        for (final LiteralParserResult result : results) {
            result.getHeadFragment().ifPresent(fragment -> tokens.addAll(splitByDelimiters(fragment)));
            result.getLiteral().ifPresent(tokens::add);
            result.getTailFragment().ifPresent(tail -> tokens.addAll(splitByDelimiters(tail)));
        }

        return tokens;
    }

    private List<String> splitByDelimiters(final String line) {
        return byDelimiterSplitter.splitByDelimiters(line);
    }

    private String trimAllWhitespaceCharacters(final String sourceCodeLine) {
        return sourceCodeLine.replaceAll("(^\\h*)|(\\h*$)", "").trim();
    }

    private TokenParserResult emptyResult(final boolean isMultiLineCommentStartedBefore) {
        return new TokenParserResult(isMultiLineCommentStartedBefore);
    }

    private TokenParserResult resultWithTokens(final List<String> tokens,
                                               final boolean isMultiLineCommentStartedBefore) {
        return new TokenParserResult(tokens, isMultiLineCommentStartedBefore);
    }
}
