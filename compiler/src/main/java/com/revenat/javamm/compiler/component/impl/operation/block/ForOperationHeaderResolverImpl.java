
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

package com.revenat.javamm.compiler.component.impl.operation.block;

import com.revenat.javamm.code.fragment.Expression;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.operation.ExpressionOperation;
import com.revenat.javamm.code.fragment.operation.ForInitOperation;
import com.revenat.javamm.code.fragment.operation.ForUpdateOperation;
import com.revenat.javamm.compiler.component.ExpressionOperationBuilder;
import com.revenat.javamm.compiler.component.ExpressionResolver;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;
import com.revenat.javamm.compiler.component.impl.operation.ForInitOperationReader;
import com.revenat.javamm.compiler.component.impl.operation.ForOperationHeader;
import com.revenat.javamm.compiler.component.impl.operation.ForOperationHeaderResolver;
import com.revenat.javamm.compiler.component.impl.operation.ForUpdateOperationReader;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.revenat.javamm.compiler.component.impl.util.SyntaxParseUtils.getTokensBetweenBrackets;

import static java.util.Collections.emptyListIterator;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;

/**
 * @author Vitaliy Dragun
 *
 */
public class ForOperationHeaderResolverImpl implements ForOperationHeaderResolver {

    private static final int EXPECTED_NUMBER_OF_SEMICOLONS = 2;

    private static final String SEMICOLON = ";";

    private static final String OPENING_PARENTHESIS = "(";

    private static final String CLOSING_PARENTHESIS = ")";

    private final Collection<ForInitOperationReader> initOperationReaders;

    private final ExpressionResolver expressionResolver;

    private final Collection<ForUpdateOperationReader> updateOperationReaders;

    private final ExpressionOperationBuilder expressionOperationBuilder;

    public ForOperationHeaderResolverImpl(final Set<ForInitOperationReader> initOperationReaders,
                                          final ExpressionResolver expressionResolver,
                                          final Set<ForUpdateOperationReader> updateOperationReaders,
                                          final ExpressionOperationBuilder expressionOperationBuilder) {
        this.initOperationReaders = List.copyOf(initOperationReaders);
        this.expressionResolver = requireNonNull(expressionResolver);
        this.updateOperationReaders = List.copyOf(updateOperationReaders);
        this.expressionOperationBuilder = requireNonNull(expressionOperationBuilder);
    }

    @Override
    public ForOperationHeader resolve(final SourceLine line) {
        final ForOperationHeaderImpl header = new ForOperationHeaderImpl();

        final ForOperationHeaderTokens headerTokens = getHeaderTokensFrom(line);
        resolve(header, headerTokens, line);

        return header;
    }

    private void resolve(final ForOperationHeaderImpl header,
                         final ForOperationHeaderTokens headerTokens,
                         final SourceLine line) {
        setInitOperationIfPresent(header, headerTokens, line);
        setConditionIfPresent(header, headerTokens, line);
        setUpdateOperationIfPresent(header, headerTokens, line);
    }

    private void setUpdateOperationIfPresent(final ForOperationHeaderImpl header,
                                             final ForOperationHeaderTokens headerTokens,
                                             final SourceLine line) {
        if (headerTokens.isUpdateTokensPresent()) {
            header.setUpdateOperation(
                    resolveUpdateOperation(headerTokens.getUpdateTokens(), line.getModuleName(), line.getLineNumber())
            );
        }
    }

    private void setConditionIfPresent(final ForOperationHeaderImpl header,
                                       final ForOperationHeaderTokens headerTokens,
                                       final SourceLine line) {
        if (headerTokens.isConditionTokensPresent()) {
            header.setCondition(expressionResolver.resolve(headerTokens.getConditionTokens(), line));
        }
    }

    private void setInitOperationIfPresent(final ForOperationHeaderImpl header,
                                           final ForOperationHeaderTokens headerTokens,
                                           final SourceLine line) {
        if (headerTokens.isInitTokensPresent()) {
            header.setInitOperation(
                    resolveInitOperation(headerTokens.getInitTokens(), line.getModuleName(), line.getLineNumber())
            );
        }
    }

    private ForUpdateOperation resolveUpdateOperation(final List<String> tokens,
                                                      final String moduleName,
                                                      final int lineNumber) {
        final SourceLine sourceLine = new SourceLine(moduleName, lineNumber, tokens);
        return updateOperationReaders.stream()
                .filter(reader -> reader.canRead(sourceLine))
                .findFirst()
                .map(reader -> reader.read(sourceLine, emptyListIterator()))
                .orElseGet(() -> readAsExpressionOperation(tokens, sourceLine));
    }

    private ForInitOperation resolveInitOperation(final List<String> tokens,
                                                  final String moduleName,
                                                  final int lineNumber) {
        final SourceLine sourceLine = new SourceLine(moduleName, lineNumber, tokens);
        return initOperationReaders.stream()
                .filter(reader -> reader.canRead(sourceLine))
                .findFirst()
                .map(reader -> reader.read(sourceLine, emptyListIterator()))
                .orElseGet(() -> readAsExpressionOperation(tokens, sourceLine));
    }

    private ExpressionOperation readAsExpressionOperation(final List<String> tokens, final SourceLine sourceLine) {
        final Expression expression = expressionResolver.resolve(tokens, sourceLine);
        return expressionOperationBuilder.build(expression, sourceLine);
    }

    private ForOperationHeaderTokens getHeaderTokensFrom(final SourceLine sourceLine) {
        final List<String> expressionTokens = getExpressionTokens(sourceLine);
        final int firstSemicolonIndex = expressionTokens.indexOf(SEMICOLON);
        final int lastSemicolonIndex = expressionTokens.lastIndexOf(SEMICOLON);

        return new ForOperationHeaderTokens(expressionTokens.subList(0, firstSemicolonIndex),
                                            expressionTokens.subList(firstSemicolonIndex + 1, lastSemicolonIndex),
                                            expressionTokens.subList(lastSemicolonIndex + 1, expressionTokens.size()));
    }

    private List<String> getExpressionTokens(final SourceLine sourceLine) {
        final List<String> expressionTokens =
                getTokensBetweenBrackets(OPENING_PARENTHESIS, CLOSING_PARENTHESIS, sourceLine, false);
        return requireCorrectNumberOfSemicolonsWithinExpression(expressionTokens, sourceLine);
    }

    private List<String> requireCorrectNumberOfSemicolonsWithinExpression(final List<String> expressionTokens,
                                                                        final SourceLine sourceLine) {
        final int numberOfDelimiters = calculateNumberOfSemicolonsAmong(expressionTokens);
        validateNumberOfSemicolons(sourceLine, numberOfDelimiters);
        return expressionTokens;
    }

    private int calculateNumberOfSemicolonsAmong(final List<String> expressionTokens) {
        return (int) expressionTokens.stream()
            .filter(SEMICOLON::equals)
            .count();
    }

    private void validateNumberOfSemicolons(final SourceLine sourceLine, final int numberOfDelimiters) {
        if (numberOfDelimiters > EXPECTED_NUMBER_OF_SEMICOLONS) {
            throw new JavammLineSyntaxError(sourceLine, "Redundant '%s'", SEMICOLON);
        } else if (numberOfDelimiters < EXPECTED_NUMBER_OF_SEMICOLONS) {
            throw new JavammLineSyntaxError(sourceLine, "Missing '%s'", SEMICOLON);
        }
    }

    private static final class ForOperationHeaderTokens {
        private final List<String> initTokens;

        private final List<String> conditionTokens;

        private final List<String> updateTokens;

        private ForOperationHeaderTokens(final List<String> initTokens,
                                         final List<String> conditionTokens,
                                         final List<String> updateTokens) {
            this.initTokens = unmodifiableList(initTokens);
            this.conditionTokens = unmodifiableList(conditionTokens);
            this.updateTokens = unmodifiableList(updateTokens);
        }

        public boolean isInitTokensPresent() {
            return !initTokens.isEmpty();
        }

        public List<String> getInitTokens() {
            return initTokens;
        }

        public boolean isConditionTokensPresent() {
            return !conditionTokens.isEmpty();
        }

        public List<String> getConditionTokens() {
            return conditionTokens;
        }

        public boolean isUpdateTokensPresent() {
            return !updateTokens.isEmpty();
        }

        public List<String> getUpdateTokens() {
            return updateTokens;
        }
    }
}
