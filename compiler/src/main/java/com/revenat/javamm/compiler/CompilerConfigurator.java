
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

import com.revenat.javamm.code.fragment.Operator;
import com.revenat.javamm.code.fragment.operator.TernaryConditionalOperator;
import com.revenat.javamm.code.fragment.operator.UnaryOperator;
import com.revenat.javamm.compiler.component.BlockOperationReader;
import com.revenat.javamm.compiler.component.ComplexExpressionBuilder;
import com.revenat.javamm.compiler.component.ComplexLexemeValidator;
import com.revenat.javamm.compiler.component.ExpressionBuilder;
import com.revenat.javamm.compiler.component.ExpressionOperationBuilder;
import com.revenat.javamm.compiler.component.ExpressionResolver;
import com.revenat.javamm.compiler.component.FunctionDefinitionsReader;
import com.revenat.javamm.compiler.component.FunctionNameBuilder;
import com.revenat.javamm.compiler.component.FunctionParametersBuilder;
import com.revenat.javamm.compiler.component.FunctionReader;
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
import com.revenat.javamm.compiler.component.impl.FunctionDefinitionsReaderImpl;
import com.revenat.javamm.compiler.component.impl.FunctionNameBuilderImpl;
import com.revenat.javamm.compiler.component.impl.FunctionParametersBuilderImpl;
import com.revenat.javamm.compiler.component.impl.FunctionReaderImpl;
import com.revenat.javamm.compiler.component.impl.LexemeAmbiguityResolverImpl;
import com.revenat.javamm.compiler.component.impl.LexemeBuilderImpl;
import com.revenat.javamm.compiler.component.impl.OperatorPrecedenceResolverImpl;
import com.revenat.javamm.compiler.component.impl.SourceLineReaderImpl;
import com.revenat.javamm.compiler.component.impl.UnaryAssignmentExpressionResolverImpl;
import com.revenat.javamm.compiler.component.impl.VariableBuilderImpl;
import com.revenat.javamm.compiler.component.impl.expression.builder.PostfixNotationComplexExpressionBuilder;
import com.revenat.javamm.compiler.component.impl.expression.builder.SingleTokenExpressionBuilderImpl;
import com.revenat.javamm.compiler.component.impl.operation.CaseValueExpressionResolver;
import com.revenat.javamm.compiler.component.impl.operation.ForInitOperationReader;
import com.revenat.javamm.compiler.component.impl.operation.ForOperationHeaderResolver;
import com.revenat.javamm.compiler.component.impl.operation.ForUpdateOperationReader;
import com.revenat.javamm.compiler.component.impl.operation.SwitchBodyEntryReader;
import com.revenat.javamm.compiler.component.impl.operation.SwitchBodyEntryValidator;
import com.revenat.javamm.compiler.component.impl.operation.SwitchBodyReader;
import com.revenat.javamm.compiler.component.impl.operation.block.DoWhileOperationReader;
import com.revenat.javamm.compiler.component.impl.operation.block.IfElseOperationReader;
import com.revenat.javamm.compiler.component.impl.operation.block.SimpleBlockOperationReader;
import com.revenat.javamm.compiler.component.impl.operation.block.WhileOperationReader;
import com.revenat.javamm.compiler.component.impl.operation.block.forr.ForOperationHeaderResolverImpl;
import com.revenat.javamm.compiler.component.impl.operation.block.forr.ForOperationReader;
import com.revenat.javamm.compiler.component.impl.operation.block.switchh.CaseEntryReader;
import com.revenat.javamm.compiler.component.impl.operation.block.switchh.CaseValueExpressionResolverImpl;
import com.revenat.javamm.compiler.component.impl.operation.block.switchh.DefaultEntryReader;
import com.revenat.javamm.compiler.component.impl.operation.block.switchh.SwitchBodyEntryValidatorImpl;
import com.revenat.javamm.compiler.component.impl.operation.block.switchh.SwitchBodyReaderImpl;
import com.revenat.javamm.compiler.component.impl.operation.block.switchh.SwitchOperationReader;
import com.revenat.javamm.compiler.component.impl.operation.simple.BreakOperationReader;
import com.revenat.javamm.compiler.component.impl.operation.simple.ContinueOperationReader;
import com.revenat.javamm.compiler.component.impl.operation.simple.FinalDeclarationOperationReader;
import com.revenat.javamm.compiler.component.impl.operation.simple.PrintlnOperationReader;
import com.revenat.javamm.compiler.component.impl.operation.simple.ReturnOperationReader;
import com.revenat.javamm.compiler.component.impl.operation.simple.VariableAssignmentOperationReader;
import com.revenat.javamm.compiler.component.impl.operation.simple.VariableDeclarationOperationReader;
import com.revenat.javamm.compiler.component.impl.parser.custom.TokenParserImpl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ARITHMETIC_ADDITION;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ARITHMETIC_DIVISION;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ARITHMETIC_MODULUS;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ARITHMETIC_MULTIPLICATION;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ARITHMETIC_SUBTRACTION;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ASSIGNMENT_ADDITION;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ASSIGNMENT_BITWISE_AND;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ASSIGNMENT_BITWISE_OR;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ASSIGNMENT_BITWISE_SHIFT_LEFT;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ASSIGNMENT_BITWISE_SHIFT_RIGHT;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ASSIGNMENT_BITWISE_SHIFT_RIGHT_ZERO_FILL;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ASSIGNMENT_BITWISE_XOR;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ASSIGNMENT_DIVISION;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ASSIGNMENT_MODULUS;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ASSIGNMENT_MULTIPLICATION;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.ASSIGNMENT_SUBTRACTION;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.BITWISE_AND;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.BITWISE_OR;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.BITWISE_SHIFT_LEFT;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.BITWISE_SHIFT_RIGHT;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.BITWISE_SHIFT_RIGHT_ZERO_FILL;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.BITWISE_XOR;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.LOGICAL_AND;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.LOGICAL_OR;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.PREDICATE_EQUALS;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.PREDICATE_GREATER_THAN;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.PREDICATE_GREATER_THAN_OR_EQUALS;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.PREDICATE_LESS_THAN;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.PREDICATE_LESS_THAN_OR_EQUALS;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.PREDICATE_NOT_EQUALS;
import static com.revenat.javamm.code.fragment.operator.BinaryOperator.PREDICATE_TYPEOF;
import static com.revenat.javamm.code.fragment.operator.UnaryOperator.ARITHMETICAL_UNARY_MINUS;
import static com.revenat.javamm.code.fragment.operator.UnaryOperator.ARITHMETICAL_UNARY_PLUS;
import static com.revenat.javamm.code.fragment.operator.UnaryOperator.BITWISE_INVERSE;
import static com.revenat.javamm.code.fragment.operator.UnaryOperator.DECREMENT;
import static com.revenat.javamm.code.fragment.operator.UnaryOperator.INCREMENT;
import static com.revenat.javamm.code.fragment.operator.UnaryOperator.LOGICAL_NOT;
import static java.util.Map.entry;
import static java.util.Map.ofEntries;

/**
 * Responsible for creating fully configured and ready to work with
 * {@link Compiler} component
 *
 * @author Vitaliy Dragun
 *
 */
public class CompilerConfigurator {
    public static final int MAX_PRECEDENCE = 20;

    public static final Map<Operator, Integer> OPERATOR_PRECEDENCE_REGISTRY = ofEntries(
        entry(INCREMENT, MAX_PRECEDENCE - 1),
        entry(DECREMENT, MAX_PRECEDENCE - 1),
        entry(ARITHMETICAL_UNARY_PLUS, MAX_PRECEDENCE - 1),
        entry(ARITHMETICAL_UNARY_MINUS, MAX_PRECEDENCE - 1),
        entry(BITWISE_INVERSE, MAX_PRECEDENCE - 1),
        entry(LOGICAL_NOT, MAX_PRECEDENCE - 1),
        //
        entry(ARITHMETIC_MULTIPLICATION, MAX_PRECEDENCE - 2),
        entry(ARITHMETIC_DIVISION, MAX_PRECEDENCE - 2),
        entry(ARITHMETIC_MODULUS, MAX_PRECEDENCE - 2),
        //
        entry(ARITHMETIC_ADDITION, MAX_PRECEDENCE - 3),
        entry(ARITHMETIC_SUBTRACTION, MAX_PRECEDENCE - 3),
        //
        entry(BITWISE_SHIFT_LEFT, MAX_PRECEDENCE - 4),
        entry(BITWISE_SHIFT_RIGHT, MAX_PRECEDENCE - 4),
        entry(BITWISE_SHIFT_RIGHT_ZERO_FILL, MAX_PRECEDENCE - 4),
        //
        entry(PREDICATE_GREATER_THAN, MAX_PRECEDENCE - 5),
        entry(PREDICATE_GREATER_THAN_OR_EQUALS, MAX_PRECEDENCE - 5),
        entry(PREDICATE_LESS_THAN, MAX_PRECEDENCE - 5),
        entry(PREDICATE_LESS_THAN_OR_EQUALS, MAX_PRECEDENCE - 5),
        entry(PREDICATE_TYPEOF, MAX_PRECEDENCE - 5),
        //
        entry(PREDICATE_NOT_EQUALS, MAX_PRECEDENCE - 6),
        entry(PREDICATE_EQUALS, MAX_PRECEDENCE - 6),
        //
        entry(BITWISE_AND, MAX_PRECEDENCE - 7),
        //
        entry(BITWISE_XOR, MAX_PRECEDENCE - 8),
        //
        entry(BITWISE_OR, MAX_PRECEDENCE - 9),
        //
        entry(LOGICAL_AND, MAX_PRECEDENCE - 10),
        //
        entry(LOGICAL_OR, MAX_PRECEDENCE - 11),
        //
        entry(TernaryConditionalOperator.OPERATOR, MAX_PRECEDENCE - 12),
        //
        entry(ASSIGNMENT_MULTIPLICATION, MAX_PRECEDENCE - 13),
        entry(ASSIGNMENT_DIVISION, MAX_PRECEDENCE - 13),
        entry(ASSIGNMENT_MODULUS, MAX_PRECEDENCE - 13),
        entry(ASSIGNMENT_ADDITION, MAX_PRECEDENCE - 13),
        entry(ASSIGNMENT_SUBTRACTION, MAX_PRECEDENCE - 13),
        entry(ASSIGNMENT_BITWISE_SHIFT_LEFT, MAX_PRECEDENCE - 13),
        entry(ASSIGNMENT_BITWISE_SHIFT_RIGHT, MAX_PRECEDENCE - 13),
        entry(ASSIGNMENT_BITWISE_SHIFT_RIGHT_ZERO_FILL, MAX_PRECEDENCE - 13),
        entry(ASSIGNMENT_BITWISE_AND, MAX_PRECEDENCE - 13),
        entry(ASSIGNMENT_BITWISE_XOR, MAX_PRECEDENCE - 13),
        entry(ASSIGNMENT_BITWISE_OR, MAX_PRECEDENCE - 13),

        entry(UnaryOperator.HASH_CODE, 0)
    );

    private final TokenParser tokenParser = new TokenParserImpl();

    private final SourceLineReader sourceLineReader = new SourceLineReaderImpl(tokenParser);

    private final VariableBuilder variableBuilder = new VariableBuilderImpl();

    private final OperatorPrecedenceResolver operatorPrecedenceResolver =
        new OperatorPrecedenceResolverImpl(OPERATOR_PRECEDENCE_REGISTRY);

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
            unaryAssignmentExpressionResolver,
            operatorPrecedenceResolver
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

    private final CaseValueExpressionResolver caseLabelExpressionResolver =
            new CaseValueExpressionResolverImpl(expressionResolver);

    private final SwitchBodyEntryValidator switchEntryValidator = new SwitchBodyEntryValidatorImpl();

    private final List<SwitchBodyEntryReader<?>> switchBodyEntryReaders = List.of(
            new CaseEntryReader(caseLabelExpressionResolver),
            new DefaultEntryReader()
    );

    private final SwitchBodyReader switchBodyReader =
            new SwitchBodyReaderImpl(switchBodyEntryReaders, switchEntryValidator);

    private final Set<OperationReader> operationReaders = Set.of(
            printlnOperationReader,
            variableDeclarationOperationReader,
            finalDeclarationOperationReader,
            variableAssignmentOperationReader,
            new IfElseOperationReader(expressionResolver),
            new WhileOperationReader(expressionResolver),
            new DoWhileOperationReader(expressionResolver),
            new ForOperationReader(forOperationHeaderResolver),
            new SimpleBlockOperationReader(),
            new ContinueOperationReader(),
            new BreakOperationReader(),
            new SwitchOperationReader(switchBodyReader, expressionResolver),
            new ReturnOperationReader(expressionResolver)
    );

    private final BlockOperationReader blockOperationReader =
            new BlockOperationReaderImpl(operationReaders, expressionOperationBuilder, expressionResolver);

    private final FunctionParametersBuilder functionParametersBuilder =
            new FunctionParametersBuilderImpl(variableBuilder);

    private final FunctionReader functionReader =
            new FunctionReaderImpl(functionNameBuilder, functionParametersBuilder, blockOperationReader);

    private final FunctionDefinitionsReader functionDefinitionsReader =
            new FunctionDefinitionsReaderImpl(functionReader);

    private final Compiler compiler =
            new CompilerImpl(sourceLineReader, functionNameBuilder, functionDefinitionsReader);

    public Compiler getCompiler() {
        return compiler;
    }
}
