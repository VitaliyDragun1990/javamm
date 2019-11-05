
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

import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.expression.CaseExpression;

import static java.util.Objects.requireNonNull;

/**
 * @author Vitaliy Dragun
 *
 */
public class CaseOperation extends AbstractOperation implements SwitchChildOperation {

    private final CaseExpression expression;

    private final Block body;

    public CaseOperation(final SourceLine sourceLine, final CaseExpression expression, final Block body) {
        super(sourceLine);
        this.expression = requireNonNull(expression);
        this.body = requireNonNull(body);
    }

    public CaseExpression getExpression() {
        return expression;
    }

    @Override
    public Block getBody() {
        return body;
    }

    @Override
    public int compareTo(final SwitchChildOperation o) {
        return o instanceof CaseOperation ? compareCaseOperations(this, (CaseOperation) o) : 1;
    }

    private int compareCaseOperations(final CaseOperation one, final CaseOperation another) {
        return one.expression.equals(another.expression) ? 0 : 1;
    }

    @Override
    public boolean isDefault() {
        return false;
    }
}
