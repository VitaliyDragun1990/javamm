
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
import com.revenat.javamm.code.fragment.Variable;
import com.revenat.javamm.code.fragment.expression.ConstantExpression;
import com.revenat.javamm.code.fragment.expression.NullValueExpression;
import com.revenat.javamm.code.fragment.expression.TypeExpression;
import com.revenat.javamm.code.fragment.expression.VariableExpression;
import com.revenat.javamm.compiler.component.SingleTokenExpressionBuilder;
import com.revenat.javamm.compiler.component.VariableBuilder;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;

import java.util.List;

import static com.revenat.javamm.code.syntax.Delimiters.SIGNIFICANT_TOKEN_DELIMITERS;
import static com.revenat.javamm.code.syntax.Delimiters.STRING_DELIMITERS;
import static com.revenat.javamm.code.syntax.Keywords.FALSE;
import static com.revenat.javamm.code.syntax.Keywords.NULL;
import static com.revenat.javamm.code.syntax.Keywords.TRUE;
import static com.revenat.javamm.code.syntax.SyntaxUtils.isValidSyntaxToken;

import static java.util.Objects.requireNonNull;


/**
 * @author Vitaliy Dragun
 *
 */
public class SingleTokenExpressionBuilderImpl implements SingleTokenExpressionBuilder {
    private final VariableBuilder variableBuilder;

    public SingleTokenExpressionBuilderImpl(final VariableBuilder variableBuilder) {
        this.variableBuilder = requireNonNull(variableBuilder);
    }

    @Override
    public boolean canBuild(final List<String> tokens) {
        return tokens.size() == 1 &&
                isValidSyntaxToken(tokens.get(0)) &&
                !SIGNIFICANT_TOKEN_DELIMITERS.contains(tokens.get(0));
    }

    /**
     * var a = integer
     * var a = 'Hello world'
     * var a = "Hello world"
     * var a = null
     * var a = true
     * var a = false
     * var a = 1
     * var a = 1.1
     * var a = b
     */
    @Override
    public Expression build(final List<String> expressionTokens, final SourceLine sourceLine) {
        final String expressionToken = expressionTokens.get(0);
        return buildExpression(expressionToken, sourceLine);
    }

    private Expression buildExpression(final String token, final SourceLine sourceLine) {
        if (isTypeLiteral(token)) {
            return TypeExpression.of(token);
        } else if (isNullLiteral(token)) {
            return NullValueExpression.getInstance();
        } else if (isConstantLiteral(token)) {
            return buildConstantExpression(token, sourceLine);
        } else {
            return buildVariableExpression(token, sourceLine);
        }
    }

    private Expression buildConstantExpression(final String token, final SourceLine sourceLine) {
        final Object result = buildConstantLiteral(token, sourceLine);
        return ConstantExpression.valueOf(result);
    }

    private Object buildConstantLiteral(final String token, final SourceLine sourceLine) {
        if (isStringLiteral(token)) {
            return buildStringLiteral(token, sourceLine);
        } else if (isBooleanLiteral(token)) {
            return Boolean.parseBoolean(token);
        } else if (isIntegerLiteral(token)) {
            return Integer.parseInt(token);
        } else {
            return Double.parseDouble(token);
        }
    }

    private Expression buildVariableExpression(final String name, final SourceLine sourceLine) {
        final Variable var = variableBuilder.build(name, sourceLine);
        return new VariableExpression(var);
    }

    private String buildStringLiteral(final String token, final SourceLine sourceLine) {
        final String openDelimiter = getOpenDelimiter(token);

        if (closeDelimiterIsMissing(token)) {
            throw new JavammLineSyntaxError(sourceLine, "%s expected at the end of the string token", openDelimiter);
        } else if (delimitersDoNotMatch(token, openDelimiter)) {
            throw new JavammLineSyntaxError(sourceLine,
                    "%s expected at the end of the string literal instead of %s", openDelimiter, getLastChar(token));
        }
        return stringWithoutDelimiters(token);
    }

    private boolean closeDelimiterIsMissing(final String token) {
        return token.length() == 1 || !STRING_DELIMITERS.contains(getLastChar(token));
    }

    private boolean delimitersDoNotMatch(final String token, final String openDelimiter) {
        return !token.endsWith(openDelimiter) && STRING_DELIMITERS.contains(getLastChar(token));
    }

    private String getOpenDelimiter(final String token) {
        return token.substring(0, 1);
    }

    private char getLastChar(final String token) {
        return token.charAt(token.length() - 1);
    }

    private String stringWithoutDelimiters(final String token) {
        return token.substring(1, token.length() - 1);
    }

    private boolean isTypeLiteral(final String token) {
        return TypeExpression.is(token);
    }

    private boolean isConstantLiteral(final String token) {
        return isBooleanLiteral(token) || isIntegerLiteral(token) || isDoubleLiteral(token) || isStringLiteral(token);
    }

    private boolean isNullLiteral(final String token) {
        return NULL.equals(token);
    }

    private boolean isStringLiteral(final String token) {
        return STRING_DELIMITERS.contains(token.charAt(0));
    }

    private boolean isBooleanLiteral(final String token) {
        return TRUE.equals(token) || FALSE.equals(token);
    }

    private boolean isIntegerLiteral(final String token) {
        try {
            Integer.parseInt(token);
            return true;
        } catch (final NumberFormatException e) {
            return false;
        }
    }

    private boolean isDoubleLiteral(final String token) {
        try {
            Double.parseDouble(token);
            return true;
        } catch (final NumberFormatException e) {
            return false;
        }
    }
}
