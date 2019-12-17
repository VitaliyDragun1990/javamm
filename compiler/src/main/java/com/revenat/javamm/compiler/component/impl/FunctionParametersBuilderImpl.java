
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

import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.Variable;
import com.revenat.javamm.compiler.component.FunctionParametersBuilder;
import com.revenat.javamm.compiler.component.VariableBuilder;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;

import java.util.ArrayList;
import java.util.List;

import static com.revenat.javamm.compiler.component.impl.util.SyntaxParseUtils.groupTokensByComma;
import static com.revenat.javamm.compiler.component.impl.util.SyntaxValidationUtils.MAX_ALLOWED_PARAMETER_COUNT;

/**
 * @author Vitaliy Dragun
 */
public class FunctionParametersBuilderImpl implements FunctionParametersBuilder {

    private final VariableBuilder variableBuilder;

    public FunctionParametersBuilderImpl(final VariableBuilder variableBuilder) {
        this.variableBuilder = variableBuilder;
    }

    @Override
    public List<Variable> build(final List<String> parameterTokens, final SourceLine sourceLine) {
        final List<Variable> parameters = new ArrayList<>();

        final List<List<String>> parameterGroups = groupTokensByComma(parameterTokens, sourceLine);
        for (final List<String> parameterGroup : parameterGroups) {
            final String parameterName = requireOnlyOneParameterInside(parameterGroup, sourceLine);
            parameters.add(variableBuilder.build(parameterName, sourceLine));
        }

        return requireAllowedParameterCount(parameters, sourceLine);
    }

    private List<Variable> requireAllowedParameterCount(final List<Variable> parameters,
                                                        final SourceLine sourceLine) {
        if (parameters.size() > MAX_ALLOWED_PARAMETER_COUNT) {
            throw new JavammLineSyntaxError(sourceLine,
                "Max allowed function parameter count is %d", MAX_ALLOWED_PARAMETER_COUNT);
        }
        return parameters;
    }

    private String requireOnlyOneParameterInside(final List<String> parameterGroup, final SourceLine sourceLine) {
        if (parameterGroup.size() != 1) {
            throw new JavammLineSyntaxError(sourceLine, "Expressions not allowed here");
        }

        return parameterGroup.get(0);
    }
}
