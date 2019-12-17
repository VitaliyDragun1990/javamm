
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

package com.revenat.javamm.code.test.doubles;

import com.revenat.javamm.code.component.ExpressionContext;
import com.revenat.javamm.code.fragment.Expression;
import com.revenat.javamm.code.fragment.UpdatableExpression;

public class ExpressionContextSpy implements ExpressionContext {
    private Expression lastGetValueExpression = null;

    private UpdatableExpression lastSetValueExpression = null;

    private Object lastUpdateValue = null;

    @Override
    public Object getValue(final Expression expression) {
        this.lastGetValueExpression = expression;
        return null;
    }

    @Override
    public void setValue(final UpdatableExpression updatableExpression, final Object updatedValue) {
        this.lastSetValueExpression = updatableExpression;
        this.lastUpdateValue = updatedValue;
    }

    public Expression lastGetValueExpression() {
        return lastGetValueExpression;
    }

    public UpdatableExpression lastSetValueExpression() {
        return lastSetValueExpression;
    }

    public Object lastUpdateValue() {
        return lastUpdateValue;
    }
}