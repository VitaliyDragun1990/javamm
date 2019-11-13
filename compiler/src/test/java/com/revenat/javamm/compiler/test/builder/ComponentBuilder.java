
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

package com.revenat.javamm.compiler.test.builder;

import com.revenat.javamm.compiler.component.ComplexExpressionBuilder;
import com.revenat.javamm.compiler.component.ComplexLexemeValidator;
import com.revenat.javamm.compiler.component.ExpressionBuilder;
import com.revenat.javamm.compiler.component.ExpressionResolver;
import com.revenat.javamm.compiler.component.FunctionNameBuilder;
import com.revenat.javamm.compiler.component.LexemeAmbiguityResolver;
import com.revenat.javamm.compiler.component.LexemeBuilder;
import com.revenat.javamm.compiler.component.OperatorPrecedenceResolver;
import com.revenat.javamm.compiler.component.SingleTokenExpressionBuilder;
import com.revenat.javamm.compiler.component.UnaryAssignmentExpressionResolver;
import com.revenat.javamm.compiler.component.VariableBuilder;
import com.revenat.javamm.compiler.component.impl.ComplexLexemeValidatorImpl;
import com.revenat.javamm.compiler.component.impl.ExpressionResolverImpl;
import com.revenat.javamm.compiler.component.impl.FunctionNameBuilderImpl;
import com.revenat.javamm.compiler.component.impl.LexemeAmbiguityResolverImpl;
import com.revenat.javamm.compiler.component.impl.LexemeBuilderImpl;
import com.revenat.javamm.compiler.component.impl.OperatorPrecedenceResolverImpl;
import com.revenat.javamm.compiler.component.impl.UnaryAssignmentExpressionResolverImpl;
import com.revenat.javamm.compiler.component.impl.VariableBuilderImpl;
import com.revenat.javamm.compiler.component.impl.expression.builder.PostfixNotationComplexExpressionBuilder;
import com.revenat.javamm.compiler.component.impl.expression.builder.SingleTokenExpressionBuilderImpl;

import java.util.Set;

/**
 * Creates fully configured components for integration testing
 *
 * @author Vitaliy Dragun
 *
 */
public final class ComponentBuilder {
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

    private final FunctionNameBuilder functionNameBuilder = new FunctionNameBuilderImpl();

    private final LexemeBuilder lexemeBuilder = new LexemeBuilderImpl(singleTokenExpressionBuilder,
                                                                      functionNameBuilder,
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

    public ExpressionResolver buildExpressionResolver() {
        return expressionResolver;
    }

    public LexemeBuilder buildLexemeBuilder() {
        return lexemeBuilder;
    }
}
