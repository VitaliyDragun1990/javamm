
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

package com.revenat.javamm.code.fragment.expression;

import com.revenat.javamm.code.fragment.Expression;
import com.revenat.javamm.code.fragment.FunctionName;

import java.util.List;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

/**
 * @author Vitaliy Dragun
 *
 */
public class FunctionInvocationExpression implements Expression {

    private final FunctionName functionName;

    private final List<Expression> arguments;

    public FunctionInvocationExpression(final FunctionName functionName, final List<Expression> arguments) {
        this.functionName = requireNonNull(functionName);
        this.arguments = List.copyOf(arguments);
    }

    public FunctionName getFunctionName() {
        return functionName;
    }

    public List<Expression> getArguments() {
        return arguments;
    }

    @Override
    public String toString() {
        return String.format("%s(%s)",
                functionName.getName(),
                argumentList(arguments));
    }

    private String argumentList(final List<Expression> args) {
        return args.stream().map(Object::toString).collect(joining(","));
    }
}
