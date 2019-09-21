
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

package com.revenat.javamm.interpreter.test.doubles;

import com.revenat.javamm.code.component.ExpressionContext;

public class ExpressionSpy extends ExpressionStub {
    private int getValueCount = 0;

    public ExpressionSpy(final Object value) {
        super(value);
    }

    public int getGetValueCount() {
        return getValueCount;
    }

    @Override
        public Object getValue(final ExpressionContext expressionContext) {
            getValueCount++;
            return super.getValue(expressionContext);
        }
}