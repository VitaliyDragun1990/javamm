
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

import com.revenat.javamm.code.fragment.FunctionName;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.function.DeveloperFunction;
import com.revenat.javamm.compiler.component.FunctionDefinitionsReader;
import com.revenat.javamm.compiler.component.FunctionReader;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import static java.util.Objects.requireNonNull;

/**
 * @author Vitaliy Dragun
 *
 */
public class FunctionDefinitionsReaderImpl implements FunctionDefinitionsReader {

    private final FunctionReader functionReader;

    public FunctionDefinitionsReaderImpl(final FunctionReader functionReader) {
        this.functionReader = requireNonNull(functionReader);
    }

    @Override
    public List<DeveloperFunction> read(final List<SourceLine> sourceLines) {
        final List<DeveloperFunction> definedFunctions = new ArrayList<>();

        final ListIterator<SourceLine> sourceCode = sourceLines.listIterator();
        while (sourceCode.hasNext()) {
            final DeveloperFunction function = functionReader.read(sourceCode);
            definedFunctions.add(requireUniqueName(function, definedFunctions));
        }

        return definedFunctions;
    }

    private DeveloperFunction requireUniqueName(final DeveloperFunction function,
                                                final List<DeveloperFunction> definedFunctions) {
        if (functionWithSameNameAlreadyDefined(function.getName(), definedFunctions)) {
            throw duplicateFunctionDefinitionError(function);
        }
        return function;
    }

    private boolean functionWithSameNameAlreadyDefined(final FunctionName name,
                                                       final List<DeveloperFunction> definedFunctions) {
        return definedFunctions.stream()
                .anyMatch(f -> f.getName().equals(name));
    }

    private JavammLineSyntaxError duplicateFunctionDefinitionError(final DeveloperFunction function) {
        return new JavammLineSyntaxError(function.getDeclarationSourceLine(), "Function '%s' is already defined",
                function.getName());
    }
}
