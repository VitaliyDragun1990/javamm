
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

package com.revenat.javamm.code.fragment.expression;

import com.revenat.javamm.code.component.ExpressionContext;
import com.revenat.javamm.code.fragment.Expression;

import java.util.Objects;
import java.util.Set;

/**
 * Represents constant literal expression:
 * boolean: true / false
 * integer
 * double
 * string
 *
 * @author Vitaliy Dragun
 *
 */
public final class ConstantExpression implements Expression {

    private static final Set<Class<?>> SUPPORTED_CLASSES =
            Set.of(Boolean.class, Integer.class, Double.class, String.class);

    private static final int POOL_SIZE = 12;

    private static final int MIN_POOLED_VALUE = -1;

    private static final int MAX_POOLED_VALUE = 10;

    private static final ConstantExpression TRUE_EXPRESSION = new ConstantExpression(true);

    private static final ConstantExpression FALSE_EXPRESSION = new ConstantExpression(false);

    private static final ConstantExpression[] INTEGER_POOL = new ConstantExpression[POOL_SIZE];

    static {
        for (int i = 0; i < POOL_SIZE; i++) {
            INTEGER_POOL[i] = new ConstantExpression(i - 1);

        }
    }

    private final Object value;

    private ConstantExpression(final Object value) {
        if (value == null) {
            throw new IllegalArgumentException("null value is not allowed. Use NullValueExpression instead");
        } else if (!SUPPORTED_CLASSES.contains(value.getClass())) {
            throw new IllegalArgumentException("Unsupported value type: " + value.getClass().getName());
        } else {
            this.value = value;
        }
    }

    public static ConstantExpression valueOf(final Object value) {
        if (value instanceof Boolean) {
            return ((boolean) value) ? TRUE_EXPRESSION : FALSE_EXPRESSION;
        } else if (value instanceof Integer) {
            final int val = (int) value;
            if (val >= MIN_POOLED_VALUE && val <= MAX_POOLED_VALUE) {
                return INTEGER_POOL[val - MIN_POOLED_VALUE];
            }
        }
        return new ConstantExpression(value);
    }

    @Override
    public Object getValue(final ExpressionContext expressionContext) {
        return value;
    }


    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ConstantExpression other = (ConstantExpression) obj;
        return Objects.equals(value, other.value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
