
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
import com.revenat.javamm.compiler.component.ComplexExpressionBuilder;
import com.revenat.javamm.compiler.component.ComplexLexemeValidator;
import com.revenat.javamm.compiler.component.ExpressionBuilder;
import com.revenat.javamm.compiler.component.ExpressionOperationBuilder;
import com.revenat.javamm.compiler.component.ExpressionResolver;
import com.revenat.javamm.compiler.component.LexemeAmbiguityResolver;
import com.revenat.javamm.compiler.component.LexemeBuilder;
import com.revenat.javamm.compiler.component.OperationReader;
import com.revenat.javamm.compiler.component.OperatorPrecedenceResolver;
import com.revenat.javamm.compiler.component.SingleTokenExpressionBuilder;
import com.revenat.javamm.compiler.component.SourceLineReader;
import com.revenat.javamm.compiler.component.TokenParser;
import com.revenat.javamm.compiler.component.UnaryAssignmentExpressionResolver;
import com.revenat.javamm.compiler.component.VariableBuilder;
import com.revenat.javamm.compiler.component.impl.BlockOperationReaderImpl;
import com.revenat.javamm.compiler.component.impl.CompilerImpl;
import com.revenat.javamm.compiler.component.impl.ComplexLexemeValidatorImpl;
import com.revenat.javamm.compiler.component.impl.ExpressionOperationBuilderImpl;
import com.revenat.javamm.compiler.component.impl.ExpressionResolverImpl;
import com.revenat.javamm.compiler.component.impl.LexemeAmbiguityResolverImpl;
import com.revenat.javamm.compiler.component.impl.LexemeBuilderImpl;
import com.revenat.javamm.compiler.component.impl.OperatorPrecedenceResolverImpl;
import com.revenat.javamm.compiler.component.impl.SourceLineReaderImpl;
import com.revenat.javamm.compiler.component.impl.UnaryAssignmentExpressionResolverImpl;
import com.revenat.javamm.compiler.component.impl.VariableBuilderImpl;
import com.revenat.javamm.compiler.component.impl.expression.builder.PostfixNotationComplexExpressionBuilder;
import com.revenat.javamm.compiler.component.impl.expression.builder.SingleTokenExpressionBuilderImpl;
import com.revenat.javamm.compiler.component.impl.operation.ForInitOperationReader;
import com.revenat.javamm.compiler.component.impl.operation.ForOperationHeaderResolver;
import com.revenat.javamm.compiler.component.impl.operation.ForUpdateOperationReader;
import com.revenat.javamm.compiler.component.impl.operation.block.DoWhileOperationRedaer;
import com.revenat.javamm.compiler.component.impl.operation.block.ForOperationHeaderResolverImpl;
import com.revenat.javamm.compiler.component.impl.operation.block.ForOperationReader;
import com.revenat.javamm.compiler.component.impl.operation.block.IfElseOperationReader;
import com.revenat.javamm.compiler.component.impl.operation.block.SimpleBlockOperationReader;
import com.revenat.javamm.compiler.component.impl.operation.block.WhileOperationReader;
import com.revenat.javamm.compiler.component.impl.operation.simple.FinalDeclarationOperationReader;
import com.revenat.javamm.compiler.component.impl.operation.simple.PrintlnOperationReader;
import com.revenat.javamm.compiler.component.impl.operation.simple.VariableAssignmentOperationReader;
import com.revenat.javamm.compiler.component.impl.operation.simple.VariableDeclarationOperationReader;
import com.revenat.javamm.compiler.component.impl.parser.custom.TokenParserImpl;

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

    private final OperatorPrecedenceResolver operatorPrecedenceResolver = new OperatorPrecedenceResolverImpl();

    private final ComplexExpressionBuilder complexExpressionBuilder =
            new PostfixNotationComplexExpressionBuilder(operatorPrecedenceResolver);

    private final SingleTokenExpressionBuilder singleTokenExpressionBuilder =
            new SingleTokenExpressionBuilderImpl(variableBuilder);

    private final Set<ExpressionBuilder> expressionBuilders = Set.of(
            singleTokenExpressionBuilder
    );

    private final LexemeAmbiguityResolver lexemeAmbiguityResolver = new LexemeAmbiguityResolverImpl();

    private final LexemeBuilder lexemeBuilder = new LexemeBuilderImpl(singleTokenExpressionBuilder,
                                                                      lexemeAmbiguityResolver);

    private final ComplexLexemeValidator lexemeValidator = new ComplexLexemeValidatorImpl(operatorPrecedenceResolver);

    private final UnaryAssignmentExpressionResolver unaryAssignmentExpressionResolver =
            new UnaryAssignmentExpressionResolverImpl();

    private final ExpressionResolver expressionResolver = new ExpressionResolverImpl(
            expressionBuilders,
            complexExpressionBuilder,
            lexemeBuilder,
            lexemeValidator,
            unaryAssignmentExpressionResolver
    );

    private final PrintlnOperationReader printlnOperationReader = new PrintlnOperationReader(expressionResolver);

    private final VariableDeclarationOperationReader variableDeclarationOperationReader =
                new VariableDeclarationOperationReader(variableBuilder, expressionResolver);

    private final FinalDeclarationOperationReader finalDeclarationOperationReader =
                new FinalDeclarationOperationReader(variableBuilder, expressionResolver);

    private final VariableAssignmentOperationReader variableAssignmentOperationReader =
               new VariableAssignmentOperationReader(expressionResolver);

    private final Set<ForInitOperationReader> initOperationReaders = Set.of(
            printlnOperationReader,
            variableDeclarationOperationReader,
            finalDeclarationOperationReader,
            variableAssignmentOperationReader
    );

    private final Set<ForUpdateOperationReader> updateOperationReaders = Set.of(
            printlnOperationReader,
            variableAssignmentOperationReader
    );

    private final ExpressionOperationBuilder expressionOperationBuilder = new ExpressionOperationBuilderImpl();

    private final ForOperationHeaderResolver forOperationHeaderResolver =
                new ForOperationHeaderResolverImpl(initOperationReaders, expressionResolver, updateOperationReaders,
                        expressionOperationBuilder);

    private final Set<OperationReader> operationReaders = Set.of(
            printlnOperationReader,
            variableDeclarationOperationReader,
            finalDeclarationOperationReader,
            variableAssignmentOperationReader,
            new IfElseOperationReader(expressionResolver),
            new WhileOperationReader(expressionResolver),
            new DoWhileOperationRedaer(expressionResolver),
            new ForOperationReader(forOperationHeaderResolver),
            new SimpleBlockOperationReader()
    );

    private final BlockOperationReader blockOperationReader =
            new BlockOperationReaderImpl(operationReaders, expressionOperationBuilder, expressionResolver);

    private final Compiler compiler = new CompilerImpl(sourceLineReader, blockOperationReader);

    public Compiler getCompiler() {
        return compiler;
    }
}
