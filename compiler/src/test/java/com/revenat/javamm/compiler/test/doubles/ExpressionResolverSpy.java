
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

package com.revenat.javamm.compiler.test.doubles;

import com.revenat.javamm.code.fragment.Expression;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.compiler.component.ExpressionResolver;

import java.util.List;

public class ExpressionResolverSpy implements ExpressionResolver {
    private Expression expressionToResolve;

    private List<String> lastExpressionTokens = List.of();

    public List<String> getLastExpressionTokens() {
        return lastExpressionTokens;
    }

    public void setExpressionToResolve(final Expression expressionToResolve) {
        this.expressionToResolve = expressionToResolve;
    }

    @Override
    public Expression resolve(final List<String> expressionTokens, final SourceLine sourceLine) {
        lastExpressionTokens = expressionTokens;
        return expressionToResolve;
    }
}