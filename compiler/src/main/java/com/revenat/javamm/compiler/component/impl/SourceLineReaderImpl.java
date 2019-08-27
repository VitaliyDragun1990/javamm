
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

package com.revenat.javamm.compiler.component.impl;

import com.revenat.javamm.code.fragment.SourceCode;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.compiler.component.SourceLineReader;
import com.revenat.javamm.compiler.component.TokenParser;
import com.revenat.javamm.compiler.model.TokenParserResult;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vitaliy Dragun
 *
 */
public class SourceLineReaderImpl implements SourceLineReader {
    private final TokenParser tokenParser;

    public SourceLineReaderImpl(final TokenParser tokenParser) {
        this.tokenParser = tokenParser;
    }

    @Override
    public List<SourceLine> read(final SourceCode sourceCode) {
        final List<String> lines = sourceCode.getLines();
        final String moduleName = sourceCode.getModuleName();

        return List.copyOf(readLines(moduleName, lines));
    }

    private List<SourceLine> readLines(final String moduleName, final List<String> lines) {
        final List<SourceLine> sourceLines = new ArrayList<>();

        boolean withinComment = false;
        for (int i = 0; i < lines.size(); i++) {
            final int lineNumber = i + 1;
            final String sourceCodeLine = lines.get(i);

            final TokenParserResult parserResult = parseLine(sourceCodeLine, withinComment);

            if (parserResult.isNotEmpty()) {
                sourceLines.add(new SourceLine(moduleName, lineNumber, parserResult.getTokens()));
            }

            withinComment = parserResult.isMultilineCommentStarted();
        }

        return sourceLines;
    }

    private TokenParserResult parseLine(final String sourceCodeLine, final boolean withinComment) {
        if (withinComment) {
            return tokenParser.parseLineWithStartedMultilineComment(sourceCodeLine);
        } else {
            return tokenParser.parseLine(sourceCodeLine);
        }
    }
}
