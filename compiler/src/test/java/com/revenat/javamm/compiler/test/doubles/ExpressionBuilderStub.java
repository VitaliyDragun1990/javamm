
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
import com.revenat.javamm.compiler.component.ExpressionBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpressionBuilderStub implements ExpressionBuilder {
    private final Map<List<String>, Expression> expressionsToBuild = new HashMap<>();

    private boolean canBuild = false;

    public void setCanBuild(final boolean canBuild) {
        this.canBuild = canBuild;
    }

    public void setExpressionToBuild(final List<String> tokens, final Expression expressionToBuild) {
        expressionsToBuild.put(tokens, expressionToBuild);
    }

    @Override
    public boolean canBuild(final List<String> tokens) {
        return canBuild;
    }

    @Override
    public Expression build(final List<String> expressionTokens, final SourceLine sourceLine) {
        return expressionsToBuild.get(expressionTokens);
    }
}