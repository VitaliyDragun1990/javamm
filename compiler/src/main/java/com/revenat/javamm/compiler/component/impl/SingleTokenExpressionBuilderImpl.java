
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
import com.revenat.javamm.code.fragment.expression.ConstantExpression;
import com.revenat.javamm.code.fragment.expression.NullValueExpression;
import com.revenat.javamm.code.fragment.expression.TypeExpression;
import com.revenat.javamm.compiler.component.SingleTokenExpressionBuilder;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;

import java.util.List;

import static com.revenat.javamm.code.syntax.Delimiters.SIGNIFICANT_TOKEN_DELIMITERS;
import static com.revenat.javamm.code.syntax.Delimiters.STRING_DELIMITERS;
import static com.revenat.javamm.code.syntax.Keywords.FALSE;
import static com.revenat.javamm.code.syntax.Keywords.NULL;
import static com.revenat.javamm.code.syntax.Keywords.TRUE;
import static com.revenat.javamm.code.syntax.SyntaxUtils.isValidSyntaxToken;

import static java.lang.String.format;


/**
 * @author Vitaliy Dragun
 *
 */
public class SingleTokenExpressionBuilderImpl implements SingleTokenExpressionBuilder {

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
            throw syntaxError(sourceLine, "Invalid constant: %s", token);
        }
    }

    private Expression buildConstantExpression(final String token, final SourceLine sourceLine) {
        final String result = isString(token) ? buildStringLiteral(token, sourceLine) : token;
        return ConstantExpression.valueOf(result);
    }

    private String buildStringLiteral(final String token, final SourceLine sourceLine) {
        final String openDelimiter = getOpenDelimiter(token);

        if (closeDelimiterIsMissing(token)) {
            throw syntaxError(sourceLine, "%s expected at the end of the string token", openDelimiter);
        } else if (delimitersDoNotMatch(token, openDelimiter)) {
            throw syntaxError(sourceLine,
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

    private JavammLineSyntaxError syntaxError(final SourceLine sourceLine, final String template, final Object...args) {
        return new JavammLineSyntaxError(format(template, args), sourceLine);
    }

    private String stringWithoutDelimiters(final String token) {
        return token.substring(1, token.length() - 1);
    }

    private boolean isTypeLiteral(final String token) {
        return TypeExpression.is(token);
    }

    private boolean isConstantLiteral(final String token) {
        return isBoolean(token) || isInteger(token) || isDouble(token) || isString(token);
    }

    private boolean isNullLiteral(final String token) {
        return NULL.equals(token);
    }

    private boolean isString(final String token) {
        return STRING_DELIMITERS.contains(token.charAt(0));
    }

    private boolean isBoolean(final String token) {
        return TRUE.equals(token) || FALSE.equals(token);
    }

    private boolean isInteger(final String token) {
        try {
            Integer.parseInt(token);
            return true;
        } catch (final NumberFormatException e) {
            return false;
        }
    }

    private boolean isDouble(final String token) {
        try {
            Double.parseDouble(token);
            return true;
        } catch (final NumberFormatException e) {
            return false;
        }
    }
}
