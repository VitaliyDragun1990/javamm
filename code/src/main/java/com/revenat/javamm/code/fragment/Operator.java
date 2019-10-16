
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

package com.revenat.javamm.code.fragment;

/**
 * Special type of {@linkplain Lexeme lexeme}. Represents any supported operator
 *
 * @author Vitaliy Dragun
 *
 */
public interface Operator extends Lexeme {

    /**
     * Returns type of an operator: {@code unary}, {@code binary} or {@code ternary}
     */
    String getType();

    /**
     * Returns code of an operator
     */
    String getCode();

    /**
     * Returns {@code true} if an operator is an assignment operator, {@code false}
     * otherwise
     */
    boolean isAssignment();
}
