
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
import com.revenat.javamm.code.fragment.operation.ReturnOperation;
import com.revenat.javamm.compiler.component.ExpressionResolver;
import com.revenat.javamm.compiler.component.impl.operation.AbstractOperationReader;

import java.util.ListIterator;
import java.util.Optional;

import static com.revenat.javamm.code.syntax.Keywords.RETURN;

import static java.util.Objects.requireNonNull;

/**
 * @author Vitaliy Dragun
 *
 */
public class ReturnOperationReader extends AbstractOperationReader<ReturnOperation> {

    private final ExpressionResolver expressionResolver;

    public ReturnOperationReader(final ExpressionResolver expressionResolver) {
        this.expressionResolver = requireNonNull(expressionResolver);
    }

    @Override
    public boolean canRead(final SourceLine sourceLine) {
        return RETURN.equals(sourceLine.getFirst());
    }

    @Override
    protected ReturnOperation get(final SourceLine sourceLine, final ListIterator<SourceLine> codeIterator) {
        return buildReturnOperation(sourceLine);
    }

    private ReturnOperation buildReturnOperation(final SourceLine sourceLine) {
        if (isExpressionAbsent(sourceLine)) {
            return new ReturnOperation(sourceLine);
        }
        return new ReturnOperation(getExpression(sourceLine), sourceLine);
    }

    private boolean isExpressionAbsent(final SourceLine sourceLine) {
        return sourceLine.getTokenCount() == 1;
    }

    private Expression getExpression(final SourceLine sourceLine) {
        return expressionResolver.resolve(sourceLine.subList(1), sourceLine);
    }
}
