
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

package com.revenat.javamm.code.fragment.operator;

import com.revenat.javamm.code.fragment.Lexeme;
import com.revenat.javamm.code.fragment.Operator;

/**
 * @author Vitaliy Dragun
 *
 */
public final class TernaryConditionalOperator implements Operator {

    public static final Operator OPERATOR = new TernaryConditionalOperator();

    public static final Lexeme SEPARATOR = new TernarySeparator();

    private static final String CODE = "?"; // TODO: remove ???

    private static final String CONTITIONAL = "?";

    private static final String SEPARATOR_SYMBOL = ":";

    private TernaryConditionalOperator() {
    }

    public static boolean isContitional(final String token) {
        return CONTITIONAL.equals(token);
    }

    public static boolean isSeparator(final String token) {
        return SEPARATOR_SYMBOL.equals(token);
    }

    @Override
    public String getType() {
        return "ternary";
    }

    @Override
    public String getCode() {
        return CODE;
    }

    @Override
    public boolean isAssignment() {
        return false;
    }

    @Override
    public String toString() {
        return CODE;
    }

    private static final class TernarySeparator implements Lexeme {

        @Override
        public String toString() {
            return SEPARATOR_SYMBOL;
        }
    }
}
