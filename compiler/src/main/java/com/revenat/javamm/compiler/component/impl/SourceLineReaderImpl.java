
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
import java.util.Objects;

public class SourceLineReaderImpl implements SourceLineReader {
    private final TokenParser tokenParser;

    public SourceLineReaderImpl(final TokenParser tokenParser) {
        this.tokenParser = Objects.requireNonNull(tokenParser);
    }

    @Override
    public List<SourceLine> read(final SourceCode sourceCode) {
        return readLines(sourceCode.getLines(), sourceCode.getModuleName());
    }

    private List<SourceLine> readLines(final List<String> sourceCodeLines, final String moduleName) {
        final List<SourceLine> result = new ArrayList<>();

        boolean isMultiLineCommentStarted = false;

        for (int i = 0; i < sourceCodeLines.size(); i++) {
            final TokenParserResult parserResult = parseLine(sourceCodeLines.get(i), isMultiLineCommentStarted);

            if (parserResult.isNotEmpty()) {
                result.add(new SourceLine(moduleName, i + 1, parserResult.getTokens()));
            }

            isMultiLineCommentStarted = parserResult.isMultiLineCommentStarted();
        }

        return List.copyOf(result);
    }

    private TokenParserResult parseLine(final String codeLine, final boolean isMultiLineCommentStarted) {
        return tokenParser.parseLine(codeLine, isMultiLineCommentStarted);
    }
}
