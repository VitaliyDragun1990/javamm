
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

import com.revenat.javamm.code.fragment.FunctionName;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.Variable;
import com.revenat.javamm.code.fragment.function.DeveloperFunction;
import com.revenat.javamm.code.fragment.operation.Block;
import com.revenat.javamm.compiler.component.BlockOperationReader;
import com.revenat.javamm.compiler.component.FunctionNameBuilder;
import com.revenat.javamm.compiler.component.FunctionParametersBuilder;
import com.revenat.javamm.compiler.component.FunctionReader;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;

import java.util.List;
import java.util.ListIterator;

import static com.revenat.javamm.code.syntax.Delimiters.OPENING_PARENTHESIS;
import static com.revenat.javamm.code.syntax.Keywords.FUNCTION;
import static com.revenat.javamm.compiler.component.impl.util.SyntaxParseUtils.getTokensBetweenBrackets;
import static com.revenat.javamm.compiler.component.impl.util.SyntaxValidationUtils.validateClosingParenthesisBeforeOpeningCurlyBrace;
import static com.revenat.javamm.compiler.component.impl.util.SyntaxValidationUtils.validateThatLineEndsWithOpeningCurlyBrace;
import static java.util.Objects.requireNonNull;

/**
 * @author Vitaliy Dragun
 */
public class FunctionReaderImpl implements FunctionReader {

    private final FunctionNameBuilder functionNameBuilder;

    private final FunctionParametersBuilder functionParametersBuilder;

    private final BlockOperationReader blockOperationReader;

    public FunctionReaderImpl(final FunctionNameBuilder functionNameBuilder,
                              final FunctionParametersBuilder functionParametersBuilder,
                              final BlockOperationReader blockOperationReader) {
        this.functionNameBuilder = requireNonNull(functionNameBuilder);
        this.functionParametersBuilder = requireNonNull(functionParametersBuilder);
        this.blockOperationReader = requireNonNull(blockOperationReader);
    }

    @Override
    public DeveloperFunction read(final ListIterator<SourceLine> sourceCode) {
        final SourceLine functionDefinitionLine = requireValidFunctionDefinition(sourceCode);

        final List<Variable> parameters = getFunctionParameters(functionDefinitionLine);
        final FunctionName functionName = getFunctionName(functionDefinitionLine, parameters);
        final Block functionBody = getFunctionBody(sourceCode, functionDefinitionLine);

        return buildFunction(functionName, parameters, functionBody);
    }

    private List<Variable> getFunctionParameters(final SourceLine functionDefinitionLine) {
        final List<String> parameterTokens = getTokensBetweenBrackets("(", ")", functionDefinitionLine, true);
        return functionParametersBuilder.build(parameterTokens, functionDefinitionLine);
    }

    private FunctionName getFunctionName(final SourceLine functionDefinitionLine, final List<Variable> parameters) {
        final String nameToken = functionDefinitionLine.getToken(1);
        return functionNameBuilder.build(nameToken, parameters, functionDefinitionLine);
    }

    private Block getFunctionBody(final ListIterator<SourceLine> sourceCode, final SourceLine functionDefinitionLine) {
        return blockOperationReader.read(functionDefinitionLine, sourceCode);
    }

    private DeveloperFunction buildFunction(final FunctionName name,
                                            final List<Variable> parameters,
                                            final Block body) {
        return new DeveloperFunction.Builder()
            .setName(name)
            .setBody(body)
            .setParameters(parameters)
            .build();
    }

    private SourceLine requireValidFunctionDefinition(final ListIterator<SourceLine> sourceCode) {
        final SourceLine line = sourceCode.next();
        validateThatFunctionKeywordIsFirstTokenInLine(line);
        validateThatLineEndsWithOpeningCurlyBrace(line);
        validateThatOpeningParenthesisPresent(line);
        validateThatFunctionNamePresent(line);
        validateThatOpeningParenthesisRightAfterFunctionName(line);
        validateClosingParenthesisBeforeOpeningCurlyBrace(line);
        return line;
    }

    private void validateThatOpeningParenthesisRightAfterFunctionName(final SourceLine line) {
        if (!OPENING_PARENTHESIS.equals(line.getToken(2))) {
            throw syntaxError(line, "'%s' expected after function name", OPENING_PARENTHESIS);
        }
    }

    private void validateThatFunctionNamePresent(final SourceLine line) {
        if (OPENING_PARENTHESIS.equals(line.getToken(1))) {
            throw syntaxError(line, "Function name expected between '%s' and '%s'", FUNCTION, OPENING_PARENTHESIS);
        }
    }

    private void validateThatOpeningParenthesisPresent(final SourceLine line) {
        if (line.getTokenCount() < 3) {
            throw syntaxError(line, "Missing '%s'", OPENING_PARENTHESIS);
        }
    }

    private void validateThatFunctionKeywordIsFirstTokenInLine(final SourceLine line) {
        if (!FUNCTION.equals(line.getFirst())) {
            throw new JavammLineSyntaxError(line, "'%s' expected at the beginning of the line", FUNCTION);
        }
    }

    private JavammLineSyntaxError syntaxError(final SourceLine sourceLine, final String message, final Object... args) {
        return new JavammLineSyntaxError(sourceLine, message, args);
    }
}
