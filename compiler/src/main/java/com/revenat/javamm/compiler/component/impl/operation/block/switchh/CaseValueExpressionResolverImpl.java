
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

package com.revenat.javamm.compiler.component.impl.operation.block.switchh;

import com.revenat.javamm.code.fragment.Expression;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.expression.CaseValueExpression;
import com.revenat.javamm.code.util.TypeUtils;
import com.revenat.javamm.compiler.component.ExpressionResolver;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;
import com.revenat.javamm.compiler.component.impl.operation.CaseValueExpressionResolver;

import java.util.List;

/**
 * @author Vitaliy Dragun
 */
public class CaseValueExpressionResolverImpl implements CaseValueExpressionResolver {

    private final ExpressionResolver expressionResolver;

    public CaseValueExpressionResolverImpl(final ExpressionResolver expressionResolver) {
        this.expressionResolver = expressionResolver;
    }

    @Override
    public CaseValueExpression resolve(final List<String> expressionTokens, final SourceLine sourceLine) {
        requireSingleTokenOnly(expressionTokens, sourceLine);
        final Expression expression = expressionResolver.resolve(expressionTokens, sourceLine);
        return requireCaseLabelExpression(expression, sourceLine);
    }

    private void requireSingleTokenOnly(final List<String> expressionTokens, final SourceLine sourceLine) {
        if (expressionTokens.size() != 1) {
            throw new JavammLineSyntaxError(sourceLine, "A constant expected between 'case' and ':'");
        }
    }

    private CaseValueExpression requireCaseLabelExpression(final Expression expression, final SourceLine sourceLine) {
        if (TypeUtils.confirmType(CaseValueExpression.class, expression)) {
            return (CaseValueExpression) expression;
        }
        throw new JavammLineSyntaxError(sourceLine, "A constant expected between 'case' and ':'");
    }
}
