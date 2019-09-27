
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

import com.revenat.javamm.code.component.ExpressionContext;
import com.revenat.javamm.interpreter.component.BlockOperationInterpreter;
import com.revenat.javamm.interpreter.component.CalculatorFacade;
import com.revenat.javamm.interpreter.component.ExpressionEvaluator;
import com.revenat.javamm.interpreter.component.ExpressionUpdater;
import com.revenat.javamm.interpreter.component.OperationInterpreter;
import com.revenat.javamm.interpreter.component.RuntimeBuilder;
import com.revenat.javamm.interpreter.component.impl.BlockOperationInterpreterImpl;
import com.revenat.javamm.interpreter.component.impl.CalculatorFacadeImpl;
import com.revenat.javamm.interpreter.component.impl.ExpressionContextImpl;
import com.revenat.javamm.interpreter.component.impl.InterpreterImpl;
import com.revenat.javamm.interpreter.component.impl.RuntimeBuilderImpl;
import com.revenat.javamm.interpreter.component.impl.calculator.arithmetic.AdditionBinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.arithmetic.DecrementUnaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.arithmetic.DivisionBinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.arithmetic.IncrementUnaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.arithmetic.MinusUnaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.arithmetic.ModulusBinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.arithmetic.MultiplicationBinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.arithmetic.PlusUnaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.arithmetic.SubtractionBinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.bitwise.BitwiseAndBinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.bitwise.BitwiseInverseUnaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.bitwise.BitwiseOrBinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.bitwise.BitwiseShiftLeftBinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.bitwise.BitwiseShiftRightBinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.bitwise.BitwiseShiftRightZeroFillBinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.bitwise.BitwiseXorBinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.logical.LogicalAndBinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.logical.LogicalNotUnaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.logical.LogicalOrBinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.predicate.IsEqualsBinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.predicate.IsGreaterThanBinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.predicate.IsGreaterThanOrEqualsBinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.predicate.IsLessThanBinaryExpressionCaluclator;
import com.revenat.javamm.interpreter.component.impl.calculator.predicate.IsLessThanOrEqualsBinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.predicate.IsNotEqualsBinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.calculator.predicate.TypeOfBinaryExpressionCalculator;
import com.revenat.javamm.interpreter.component.impl.expression.evaluator.BinaryExpressionEvaluator;
import com.revenat.javamm.interpreter.component.impl.expression.evaluator.VariableExpressionEvaluator;
import com.revenat.javamm.interpreter.component.impl.operation.simple.PrintlnOperationInterpreter;
import com.revenat.javamm.interpreter.component.impl.operation.simple.VariableDeclarationOperationInterpreter;

import java.util.Set;

/**
 * Responsible for creating fully configured and ready to work
 * {@link Interpreter}
 *
 * @author Vitaliy Dragun
 *
 */
public class InterpreterConfigurator {
    private final CalculatorFacade binaryCalculatorFacade = new CalculatorFacadeImpl(
            Set.of(
                    new AdditionBinaryExpressionCalculator(),
                    new SubtractionBinaryExpressionCalculator(),
                    new MultiplicationBinaryExpressionCalculator(),
                    new DivisionBinaryExpressionCalculator(),
                    new ModulusBinaryExpressionCalculator(),

                    new BitwiseAndBinaryExpressionCalculator(),
                    new BitwiseOrBinaryExpressionCalculator(),
                    new BitwiseXorBinaryExpressionCalculator(),
                    new BitwiseShiftLeftBinaryExpressionCalculator(),
                    new BitwiseShiftRightBinaryExpressionCalculator(),
                    new BitwiseShiftRightZeroFillBinaryExpressionCalculator(),

                    new LogicalAndBinaryExpressionCalculator(),
                    new LogicalOrBinaryExpressionCalculator(),

                    new IsEqualsBinaryExpressionCalculator(),
                    new IsNotEqualsBinaryExpressionCalculator(),
                    new IsGreaterThanBinaryExpressionCalculator(),
                    new IsGreaterThanOrEqualsBinaryExpressionCalculator(),
                    new IsLessThanBinaryExpressionCaluclator(),
                    new IsLessThanOrEqualsBinaryExpressionCalculator(),
                    new TypeOfBinaryExpressionCalculator()
    ),
            Set.of(
                    new IncrementUnaryExpressionCalculator(),
                    new DecrementUnaryExpressionCalculator(),
                    new PlusUnaryExpressionCalculator(),
                    new MinusUnaryExpressionCalculator(),

                    new BitwiseInverseUnaryExpressionCalculator(),

                    new LogicalNotUnaryExpressionCalculator()
                    ));

    private final Set<ExpressionEvaluator<?>> expressionEvaluators = Set.of(
            new VariableExpressionEvaluator(),
            new BinaryExpressionEvaluator(binaryCalculatorFacade)
    );

    private final Set<ExpressionUpdater<?>> expressionUpdaters = Set.of();

    private final ExpressionContext expressionContext =
            new ExpressionContextImpl(expressionEvaluators, expressionUpdaters);

    private final Set<OperationInterpreter<?>> operationInterpreters = Set.of(
            new PrintlnOperationInterpreter(expressionContext),
            new VariableDeclarationOperationInterpreter(expressionContext)
    );

    private final BlockOperationInterpreter blockOperationInterpreter = new BlockOperationInterpreterImpl(
            operationInterpreters);

    private final RuntimeBuilder runtimeBuilder = new RuntimeBuilderImpl();

    private final Interpreter interpreter = new InterpreterImpl(blockOperationInterpreter, runtimeBuilder);

    public Interpreter getInterpreter() {
        return interpreter;
    }
}
