
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

package com.revenat.javamm.compiler.test.doubles;

import com.revenat.javamm.code.component.ExpressionContext;
import com.revenat.javamm.code.fragment.Expression;

public class ExpressionStub implements Expression {
    private final Object value;

    public ExpressionStub(final Object value) {
        this.value = value;
    }

    @Override
    public Object getValue(final ExpressionContext expressionContext) {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}