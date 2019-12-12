
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

package com.revenat.javamm.compiler.component.impl.operation.simple;

import com.revenat.javamm.code.fragment.Expression;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.operation.PrintlnOperation;
import com.revenat.javamm.compiler.component.ExpressionResolver;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;
import com.revenat.javamm.compiler.component.impl.operation.AbstractOperationReader;
import com.revenat.javamm.compiler.component.impl.operation.ForInitOperationReader;
import com.revenat.javamm.compiler.component.impl.operation.ForUpdateOperationReader;

import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

import static com.revenat.javamm.compiler.component.impl.util.SyntaxValidationUtils.validateOpeningParenthesisAfterTokenInPosition;
import static java.util.Objects.requireNonNull;

/**
 * Responsible for reading {@code println} operation
 *
 * @author Vitaliy Dragun
 *
 */
public class PrintlnOperationReader extends AbstractOperationReader<PrintlnOperation>
    implements ForInitOperationReader, ForUpdateOperationReader {
    private final ExpressionResolver expressionResolver;

    public PrintlnOperationReader(final ExpressionResolver expressionResolver) {
        this.expressionResolver = requireNonNull(expressionResolver);
    }

    @Override
    public boolean canRead(final SourceLine sourceLine) {
        return "println".equals(sourceLine.getFirst());
    }

    @Override
    protected void validate(final SourceLine sourceLine) {
        validateOpeningParenthesisAfterTokenInPosition(sourceLine, "println", 0);
        validateLineEndsWithClosingParenthesis(sourceLine);
    }

    @Override
    protected PrintlnOperation get(final SourceLine sourceLine, final ListIterator<SourceLine> compiledCodeIterator) {
        return getExpression(sourceLine)
            .map(expression -> new PrintlnOperation(sourceLine, expression))
            .orElseGet(() -> new PrintlnOperation(sourceLine));
    }

    private Optional<Expression> getExpression(final SourceLine sourceLine) {
        if (sourceLine.getTokenCount() == 3) {
            return Optional.empty();
        }
        return Optional.of(expressionResolver.resolve(extractExpressionTokens(sourceLine), sourceLine));
    }

    private List<String> extractExpressionTokens(final SourceLine sourceLine) {
        return sourceLine.subList(2, sourceLine.getTokenCount() - 1);
    }

    private void validateLineEndsWithClosingParenthesis(final SourceLine sourceLine) {
        if (!")".equals(sourceLine.getLast())) {
            throw new JavammLineSyntaxError("')' expected at the end of the line", sourceLine);
        }
    }
}
