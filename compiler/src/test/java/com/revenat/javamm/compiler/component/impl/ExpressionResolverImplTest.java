
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

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.revenat.javamm.code.fragment.Expression;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.compiler.component.ExpressionBuilder;
import com.revenat.javamm.compiler.component.ExpressionResolver;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.revenat.juinit.addons.ReplaceCamelCase;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("a expression resolver")
class ExpressionResolverImplTest {
    private static final Expression DUMMY_EXPRESSION = new Expression() {};
    private static final SourceLine SOURCE_LINE = new SourceLine("test", 1, List.of("println", "(", "'test'", ")"));
    private static final List<String> EXCEPTION_TOKENS = List.of("'test'");

    private ExpressionBuilderStub expressionBuilderStub;

    private ExpressionResolver expressionResolver;

    @BeforeEach
    void setUp() {
        expressionBuilderStub = new ExpressionBuilderStub();
    }

    @Test
    void shouldFailIfCanNotResolveTokensForAnySupportedExpression() {
        expressionResolver = new ExpressionResolverImpl(Set.of(expressionBuilderStub));

        assertThrows(JavammLineSyntaxError.class, () -> expressionResolver.resolve(EXCEPTION_TOKENS, SOURCE_LINE));
    }

    @Test
    void shouldResolveSupportedExpressionFromTokens() {
        expressionBuilderStub.setCanBuild(true);
        expressionResolver = new ExpressionResolverImpl(Set.of(expressionBuilderStub));

        final Expression expression = expressionResolver.resolve(EXCEPTION_TOKENS, SOURCE_LINE);

        assertThat(expression, sameInstance(DUMMY_EXPRESSION));
    }

    private class ExpressionBuilderStub implements ExpressionBuilder {
        private boolean canBuild = false;

        void setCanBuild(final boolean canBuild) {
            this.canBuild = canBuild;
        }

        @Override
        public boolean canBuild(final List<String> tokens) {
            return canBuild;
        }

        @Override
        public Expression build(final List<String> expressionTokens, final SourceLine sourceLine) {
            return DUMMY_EXPRESSION;
        }
    }
}
