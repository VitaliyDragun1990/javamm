
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

package com.revenat.javamm.interpreter;

import com.revenat.javamm.code.component.Console;
import com.revenat.javamm.code.component.ExpressionContext;
import com.revenat.javamm.interpreter.component.BlockOperationInterpreter;
import com.revenat.javamm.interpreter.component.CalculatorFacade;
import com.revenat.javamm.interpreter.component.DeveloperFunctionInvoker;
import com.revenat.javamm.interpreter.component.ExpressionEvaluator;
import com.revenat.javamm.interpreter.component.ExpressionUpdater;
import com.revenat.javamm.interpreter.component.FunctionInvokerBuilder;
import com.revenat.javamm.interpreter.component.OperationInterpreter;
import com.revenat.javamm.interpreter.component.impl.BlockOperationInterpreterImpl;
import com.revenat.javamm.interpreter.component.impl.CalculatorFacadeImpl;
import com.revenat.javamm.interpreter.component.impl.DeveloperFunctionInvokerImpl;
import com.revenat.javamm.interpreter.component.impl.ExpressionContextImpl;
import com.revenat.javamm.interpreter.component.impl.FunctionInvokerBuilderImpl;
import com.revenat.javamm.interpreter.component.impl.InterpreterImpl;
import com.revenat.javamm.interpreter.component.impl.RuntimeBuilderImpl;
import com.revenat.javamm.interpreter.component.impl.calculator.HashCodeUnaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.arithmetic.binary.AdditionBinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.arithmetic.binary.DivisionBinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.arithmetic.binary.ModulusBinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.arithmetic.binary.MultiplicationBinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.arithmetic.binary.SubtractionBinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.arithmetic.unary.DecrementUnaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.arithmetic.unary.IncrementUnaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.arithmetic.unary.MinusUnaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.arithmetic.unary.PlusUnaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.bitwise.binary.BitwiseAndBinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.bitwise.binary.BitwiseOrBinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.bitwise.binary.BitwiseShiftLeftBinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.bitwise.binary.BitwiseShiftRightBinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.bitwise.binary.BitwiseShiftRightZeroFillBinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.bitwise.binary.BitwiseXorBinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.bitwise.unary.BitwiseInverseUnaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.logical.bianry.LogicalAndBinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.logical.bianry.LogicalOrBinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.logical.unary.LogicalNotUnaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.predicate.IsEqualsBinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.predicate.IsGreaterThanBinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.predicate.IsGreaterThanOrEqualsBinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.predicate.IsLessThanBinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.predicate.IsLessThanOrEqualsBinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.predicate.IsNotEqualsBinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.predicate.TypeOfBinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.expression.evaluator.FunctionInvocationExpressionEvaluator;
import com.revenat.javamm.interpreter.component.impl.expression.evaluator.PostfixNotationComplexExpressionEvaluator;
import com.revenat.javamm.interpreter.component.impl.expression.evaluator.TernaryConditionalExpressionEvaluator;
import com.revenat.javamm.interpreter.component.impl.expression.evaluator.VariableExpressionEvaluator;
import com.revenat.javamm.interpreter.component.impl.expression.updater.VariableExpressionUpdater;
import com.revenat.javamm.interpreter.component.impl.operation.block.DoWhileOperationInterpreter;
import com.revenat.javamm.interpreter.component.impl.operation.block.ForOperationInterpreter;
import com.revenat.javamm.interpreter.component.impl.operation.block.IfElseOperationInterpreter;
import com.revenat.javamm.interpreter.component.impl.operation.block.SimpleBlockOperationInterpreter;
import com.revenat.javamm.interpreter.component.impl.operation.block.SwitchOperationInterpreter;
import com.revenat.javamm.interpreter.component.impl.operation.block.WhileOperationInterpreter;
import com.revenat.javamm.interpreter.component.impl.operation.simple.BreakOperationInterpreter;
import com.revenat.javamm.interpreter.component.impl.operation.simple.ContinueOperationInterpreter;
import com.revenat.javamm.interpreter.component.impl.operation.simple.ExpressionOperationInterpreter;
import com.revenat.javamm.interpreter.component.impl.operation.simple.PrintlnOperationInterpreter;
import com.revenat.javamm.interpreter.component.impl.operation.simple.ReturnOperationInterpreter;
import com.revenat.javamm.interpreter.component.impl.operation.simple.VariableAssignmentOperationInterpreter;
import com.revenat.javamm.interpreter.component.impl.operation.simple.VariableDeclarationOperationInterpreter;

import java.util.Set;

/**
 * Responsible for creating fully configured and ready to work
 * {@link Interpreter}
 *
 * @author Vitaliy Dragun
 */
public class InterpreterConfigurator {

    public static final int MAX_STACK_SIZE = 10;

    private final Interpreter interpreter;

    public InterpreterConfigurator() {
        this(Console.DEFAULT);
    }

    @SuppressWarnings({"checkstyle:FinalParameters", "checkstyle:MethodLength"})
    public InterpreterConfigurator(final Console console) {
        final CalculatorFacade calculatorFacade = new CalculatorFacadeImpl(
            Set.of(
                AdditionBinaryExpressionCalculator.createNormalCalculator(),
                AdditionBinaryExpressionCalculator.createAssignmentCalculator(),
                SubtractionBinaryExpressionCalculator.createNormalCalculator(),
                SubtractionBinaryExpressionCalculator.createAssignmentCalculator(),
                MultiplicationBinaryExpressionCalculator.createNormalCalculator(),
                MultiplicationBinaryExpressionCalculator.createAssignmentCalculator(),
                DivisionBinaryExpressionCalculator.createNormalCalculator(),
                DivisionBinaryExpressionCalculator.createAssignmentCalculator(),
                ModulusBinaryExpressionCalculator.createNormalCalculator(),
                ModulusBinaryExpressionCalculator.createAssignmentCalculator(),

                BitwiseAndBinaryExpressionCalculator.createNormalCalculator(),
                BitwiseAndBinaryExpressionCalculator.createAssignmentCalculator(),
                BitwiseOrBinaryExpressionCalculator.createNormalCalculator(),
                BitwiseOrBinaryExpressionCalculator.createAssignmentCalculator(),
                BitwiseXorBinaryExpressionCalculator.createNormalCalculator(),
                BitwiseXorBinaryExpressionCalculator.createAssignmentCalculator(),
                BitwiseShiftLeftBinaryExpressionCalculator.createNormalCalculator(),
                BitwiseShiftLeftBinaryExpressionCalculator.createAssignmentCalculator(),
                BitwiseShiftRightBinaryExpressionCalculator.createNormalCalculator(),
                BitwiseShiftRightBinaryExpressionCalculator.createAssignmentCalculator(),
                BitwiseShiftRightZeroFillBinaryExpressionCalculator.createNormalCalculator(),
                BitwiseShiftRightZeroFillBinaryExpressionCalculator.createAssignmentCalculator(),

                new LogicalAndBinaryExpressionCalculator(),
                new LogicalOrBinaryExpressionCalculator(),

                new IsEqualsBinaryExpressionCalculator(),
                new IsNotEqualsBinaryExpressionCalculator(),
                new IsGreaterThanBinaryExpressionCalculator(),
                new IsGreaterThanOrEqualsBinaryExpressionCalculator(),
                new IsLessThanBinaryExpressionCalculator(),
                new IsLessThanOrEqualsBinaryExpressionCalculator(),
                new TypeOfBinaryExpressionCalculator()

            ),
            Set.of(
                new IncrementUnaryExpressionCalculator(),
                new DecrementUnaryExpressionCalculator(),
                new PlusUnaryExpressionCalculator(),
                new MinusUnaryExpressionCalculator(),

                new BitwiseInverseUnaryExpressionCalculator(),

                new LogicalNotUnaryExpressionCalculator(),
                new HashCodeUnaryExpressionCalculator()
            ));

        final Set<ExpressionEvaluator<?>> expressionEvaluators = Set.of(
            new VariableExpressionEvaluator(),
            new PostfixNotationComplexExpressionEvaluator(calculatorFacade),
            new TernaryConditionalExpressionEvaluator(calculatorFacade),
            new FunctionInvocationExpressionEvaluator()
        );

        final Set<ExpressionUpdater<?>> expressionUpdaters = Set.of(
            new VariableExpressionUpdater()
        );

        final ExpressionContext expressionContext = new ExpressionContextImpl(expressionEvaluators, expressionUpdaters);

        final Set<OperationInterpreter<?>> operationInterpreters = Set.of(
            new PrintlnOperationInterpreter(expressionContext, console),
            new VariableDeclarationOperationInterpreter(expressionContext),
            new VariableAssignmentOperationInterpreter(expressionContext),
            new ExpressionOperationInterpreter(expressionContext),
            new IfElseOperationInterpreter(expressionContext, calculatorFacade),
            new WhileOperationInterpreter(expressionContext, calculatorFacade),
            new DoWhileOperationInterpreter(expressionContext, calculatorFacade),
            new ForOperationInterpreter(expressionContext, calculatorFacade),
            new SimpleBlockOperationInterpreter(expressionContext),
            new ContinueOperationInterpreter(expressionContext),
            new BreakOperationInterpreter(expressionContext),
            new SwitchOperationInterpreter(expressionContext),
            new ReturnOperationInterpreter(expressionContext)
        );

        final BlockOperationInterpreter blockOperationInterpreter
            = new BlockOperationInterpreterImpl(operationInterpreters);

        final RuntimeBuilderImpl runtimeBuilderImpl = new RuntimeBuilderImpl(MAX_STACK_SIZE);

        final DeveloperFunctionInvoker developerFunctionInvoker
            = new DeveloperFunctionInvokerImpl(runtimeBuilderImpl, blockOperationInterpreter, expressionContext);

        final FunctionInvokerBuilder functionInvokerBuilder = new FunctionInvokerBuilderImpl(developerFunctionInvoker);

        this.interpreter = new InterpreterImpl(functionInvokerBuilder, runtimeBuilderImpl);
    }

    public Interpreter getInterpreter() {
        return interpreter;
    }
}
