
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

package com.revenat.javamm.compiler.component.impl.expression.builder;

import com.revenat.javamm.code.fragment.Expression;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.expression.BinaryExpression;
import com.revenat.javamm.code.fragment.operator.BinaryOperator;
import com.revenat.javamm.compiler.component.ExpressionBuilder;
import com.revenat.javamm.compiler.component.SingleTokenExpressionBuilder;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;

import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Builds binary expressions
 *
 * @author Vitaliy Dragun
 *
 */
public class BinaryExpressionBuilder implements ExpressionBuilder {
    private final SingleTokenExpressionBuilder singleTokenExpressionBuilder;

    public BinaryExpressionBuilder(final SingleTokenExpressionBuilder singleTokenExpressionBuilder) {
        this.singleTokenExpressionBuilder = requireNonNull(singleTokenExpressionBuilder);
    }

    @Override
    public boolean canBuild(final List<String> tokens) {
        return tokens.size() == 3;
    }

    @Override
    public BinaryExpression build(final List<String> expressionTokens, final SourceLine sourceLine) {
        final Expression operand1 = singleTokenExpressionBuilder.build(List.of(expressionTokens.get(0)), sourceLine);
        final BinaryOperator operator = getBinaryOperator(expressionTokens.get(1), sourceLine);
        final Expression operand2 = singleTokenExpressionBuilder.build(List.of(expressionTokens.get(2)), sourceLine);

        return new BinaryExpression(operand1, operator, operand2);
    }

    private BinaryOperator getBinaryOperator(final String code, final SourceLine sourceLine) {
        return BinaryOperator.of(code)
                .orElseThrow(() -> new JavammLineSyntaxError(sourceLine, "Unsupported binary operator: %s", code));
    }
}
