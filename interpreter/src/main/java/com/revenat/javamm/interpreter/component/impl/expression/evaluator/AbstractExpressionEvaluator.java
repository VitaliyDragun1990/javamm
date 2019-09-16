
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

package com.revenat.javamm.interpreter.component.impl.expression.evaluator;

import com.revenat.javamm.code.component.ExpressionContext;
import com.revenat.javamm.interpreter.component.ExpressionContextAware;

import java.util.Objects;

/**
 * @author Vitaliy Dragun
 *
 */
abstract class AbstractExpressionEvaluator implements ExpressionContextAware {
    private ExpressionContext expressionContext;

    protected ExpressionContext getExpressionContext() {
        return Objects.requireNonNull(expressionContext, "expressionContext is not set");
    }

    @Override
    public void setExpressionContext(final ExpressionContext expressionContext) {
        this.expressionContext = expressionContext;
    }
}
