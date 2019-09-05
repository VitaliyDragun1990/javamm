
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

package com.revenat.javamm.interpreter.component;

import com.revenat.javamm.code.fragment.Expression;

/**
 * Responsible for evaluating expressions
 *
 * @author Vitaliy Dragun
 * @param <T> type of expression to evaluate
 *
 */
public interface ExpressionEvaluator<T extends Expression> {

    /**
     * Returns class of particular expression {@code this} expression evaluator can
     * work with
     */
    Class<T> getExpressionClass();

    /**
     * Evaluates provided expression
     *
     * @param expression expression to evaluate
     * @return result of the expression evaluation
     */
    Object evaluate(T expression);
}