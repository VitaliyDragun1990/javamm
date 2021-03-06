
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

package com.revenat.javamm.code.fragment.operation;

import com.revenat.javamm.code.fragment.Expression;
import com.revenat.javamm.code.fragment.SourceLine;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * @author Vitaliy Dragun
 */
public class ReturnOperation extends AbstractOperation {

    private final Expression expression;

    public ReturnOperation(final Expression expression, final SourceLine sourceLine) {
        super(sourceLine);
        this.expression = requireNonNull(expression);
    }

    public ReturnOperation(final SourceLine sourceLine) {
        super(sourceLine);
        this.expression = null;
    }

    public Optional<Expression> getExpression() {
        return Optional.ofNullable(expression);
    }
}
