
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

package com.revenat.javamm.compiler.component.impl.expression.processor;

import com.revenat.javamm.code.fragment.Lexeme;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;

import java.util.List;
import java.util.ListIterator;

/**
 * @author Vitaliy Dragun
 */
public abstract class UnaryAssignmentOperatorProcessor {
    protected final Lexeme current;

    protected final Lexeme next;

    protected final List<Lexeme> result;

    protected final SourceLine sourceLine;

    protected UnaryAssignmentOperatorProcessor(final Lexeme current, final Lexeme next, final List<Lexeme> result,
                                               final SourceLine sourceLine) {
        this.current = current;
        this.next = next;
        this.result = result;
        this.sourceLine = sourceLine;
    }

    public static void processOperatorBeforeExpression(final Lexeme operator,
                                                       final Lexeme expression,
                                                       final List<Lexeme> result,
                                                       final SourceLine sourceLine) {
        new UnaryAssignmentOperatorBeforeExpressionProcessor(operator, expression, result, sourceLine).processEntry();
    }

    public static void processExpressionBeforeOperator(final Lexeme expression,
                                                       final Lexeme operator,
                                                       final List<Lexeme> result,
                                                       final SourceLine sourceLine) {
        new ExpressionBeforeUnaryAssignmentOperatorProcessor(expression, operator, result, sourceLine).processEntry();
    }

    public static void processOperatorBeforeOpeningParenthesis(final Lexeme operator,
                                                               final Lexeme parenthesis,
                                                               final ListIterator<Lexeme> source,
                                                               final List<Lexeme> result,
                                                               final SourceLine sourceLine) {
        new UnaryAssignmentOperatorBeforeOpeningParenthesisProcessor(operator, parenthesis, source, result, sourceLine)
            .processEntry();
    }

    public static void processPossibleOperatorAfterClosingParenthesis(final Lexeme expression,
                                                                      final Lexeme parenthesis,
                                                                      final ListIterator<Lexeme> source,
                                                                      final List<Lexeme> result,
                                                                      final SourceLine sourceLine) {
        new PossibleUnaryAssignmentOperatorAfterClosingParenthesisProcessor(expression,
            parenthesis,
            source,
            result,
            sourceLine).processEntry();
    }

    protected JavammLineSyntaxError syntaxError(final String message,
                                                final Object... args) {
        return new JavammLineSyntaxError(sourceLine, message, args);
    }
}
