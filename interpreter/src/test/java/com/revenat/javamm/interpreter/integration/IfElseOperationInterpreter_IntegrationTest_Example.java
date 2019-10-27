
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

package com.revenat.javamm.interpreter.integration;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.revenat.javamm.code.fragment.ByteCode;
import com.revenat.javamm.code.fragment.Expression;
import com.revenat.javamm.code.fragment.Operation;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.expression.ConstantExpression;
import com.revenat.javamm.code.fragment.expression.PostfixNotationComplexExpression;
import com.revenat.javamm.code.fragment.operation.Block;
import com.revenat.javamm.code.fragment.operation.IfElseOperation;
import com.revenat.javamm.code.fragment.operator.BinaryOperator;
import com.revenat.javamm.interpreter.Interpreter;
import com.revenat.javamm.interpreter.InterpreterConfigurator;
import com.revenat.javamm.interpreter.error.JavammRuntimeError;

import java.util.List;

import static com.revenat.javamm.interpreter.test.helper.CustomAsserts.assertErrorMessageContains;

import static java.lang.String.format;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;

import com.revenat.juinit.addons.ReplaceCamelCase;

@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("if-else operation interpreter")
class IfElseOperationInterpreter_IntegrationTest_Example {

    private static final SourceLine SOURCE_LINE = new SourceLine("test", 2, List.of());

    private final Interpreter interpreter = new InterpreterConfigurator().getInterpreter();

    /*
     if ( 8 + 10 ) {
     }
     */
    @Test
    void shouldFailIfConditionExpressionResultIsNotBoolean() {
        final ByteCode byteCode = createByteCodeWithIfElseOperationForCondition(8, "+", 10);
        final String expectedConditionResultType = "integer";

        final JavammRuntimeError e = assertThrows(JavammRuntimeError.class, () -> interpreter.interpret(byteCode));

        assertErrorMessageContains(e,
                "Runtime error in '%s' [Line: %s]: Condition expression should be boolean. Current type is %s",
                SOURCE_LINE.getModuleName(), SOURCE_LINE.getLineNumber(), expectedConditionResultType);
    }

    private ByteCode createByteCodeWithIfElseOperationForCondition(final int operandA, final String operator, final int operandB) {
        return () -> new Block(createIfElseOperationWithCondition(operandA, operator, operandB), SOURCE_LINE);
    }

    private Operation createIfElseOperationWithCondition(final int operandA,
                                                         final String operator,
                                                         final int operandB) {
        final Expression condition = createConditionExpression(operandA, operator, operandB);
        final Block trueBlock = new Block(List.of(), SOURCE_LINE);
        return new IfElseOperation(SOURCE_LINE, condition, trueBlock);
    }

    private Expression createConditionExpression(final int operandA, final String operator, final int operandB) {
        return new PostfixNotationComplexExpression(
                List.of(
                        ConstantExpression.valueOf(operandA),
                        ConstantExpression.valueOf(operandB),
                        BinaryOperator.of(operator).get()
                ),
                format("%s %s %s", operandA, operator, operandB));
    }

}
