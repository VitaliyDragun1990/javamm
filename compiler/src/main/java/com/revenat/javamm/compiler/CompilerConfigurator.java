
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

package com.revenat.javamm.compiler;

import com.revenat.javamm.compiler.component.BlockOperationReader;
import com.revenat.javamm.compiler.component.ExpressionResolver;
import com.revenat.javamm.compiler.component.OperationReader;
import com.revenat.javamm.compiler.component.SingleTokenExpressionBuilder;
import com.revenat.javamm.compiler.component.SourceLineReader;
import com.revenat.javamm.compiler.component.TokenParser;
import com.revenat.javamm.compiler.component.VariableBuilder;
import com.revenat.javamm.compiler.component.impl.BlockOperationReaderImpl;
import com.revenat.javamm.compiler.component.impl.CompilerImpl;
import com.revenat.javamm.compiler.component.impl.ExpressionResolverImpl;
import com.revenat.javamm.compiler.component.impl.SingleTokenExpressionBuilderImpl;
import com.revenat.javamm.compiler.component.impl.SourceLineReaderImpl;
import com.revenat.javamm.compiler.component.impl.TokenParserImpl;
import com.revenat.javamm.compiler.component.impl.VariableBuilderImpl;
import com.revenat.javamm.compiler.component.impl.operation.simple.PrintlnOperationReader;
import com.revenat.javamm.compiler.component.impl.operation.simple.VariableDeclarationOperationReader;

import java.util.Set;

/**
 * Responsible for creating fully configured and ready to work with
 * {@link Compiler} component
 *
 * @author Vitaliy Dragun
 *
 */
public class CompilerConfigurator {
    private final TokenParser tokenParser = new TokenParserImpl();

    private final SourceLineReader sourceLineReader = new SourceLineReaderImpl(tokenParser);

    private final VariableBuilder variableBuilder = new VariableBuilderImpl();

    private final SingleTokenExpressionBuilder singleTokenExpressionBuilder =
            new SingleTokenExpressionBuilderImpl(variableBuilder);

    private final ExpressionResolver expressionResolver = new ExpressionResolverImpl(
            Set.of(
                    singleTokenExpressionBuilder
            )
    );

    private final Set<OperationReader> operationReaders = Set.of(
            new PrintlnOperationReader(expressionResolver),
            new VariableDeclarationOperationReader(variableBuilder, expressionResolver)
    );

    private final BlockOperationReader blockOperationReader = new BlockOperationReaderImpl(operationReaders);

    private final Compiler compiler = new CompilerImpl(sourceLineReader, blockOperationReader);

    public Compiler getCompiler() {
        return compiler;
    }
}
