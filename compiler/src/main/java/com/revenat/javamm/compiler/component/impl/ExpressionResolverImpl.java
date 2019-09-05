
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

import com.revenat.javamm.code.fragment.Expression;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.compiler.component.ExpressionBuilder;
import com.revenat.javamm.compiler.component.ExpressionResolver;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static java.lang.String.join;

/**
 * @author Vitaliy Dragun
 *
 */
public class ExpressionResolverImpl implements ExpressionResolver {
    private final Collection<ExpressionBuilder> expressionBuilders;

    public ExpressionResolverImpl(final Set<ExpressionBuilder> expressionBuilders) {
        this.expressionBuilders = List.copyOf(expressionBuilders);
    }

    @Override
    public Expression resolve(final List<String> expressionTokens, final SourceLine sourceLine) {
        final ExpressionBuilder builder = getAppropriateExpressionBuilder(expressionTokens, sourceLine);
        return builder.build(expressionTokens, sourceLine);
    }

    private ExpressionBuilder getAppropriateExpressionBuilder(final List<String> expressionTokens,
            final SourceLine sourceLine) {
        return expressionBuilders.stream()
                .filter(b -> b.canBuild(expressionTokens))
                .findFirst()
                .orElseThrow(() -> syntaxError(expressionTokens, sourceLine));
    }

    private JavammLineSyntaxError syntaxError(final List<String> expressionTokens, final SourceLine sourceLine) {
        return new JavammLineSyntaxError("Unsupported expression: " + join("", expressionTokens), sourceLine);
    }
}
