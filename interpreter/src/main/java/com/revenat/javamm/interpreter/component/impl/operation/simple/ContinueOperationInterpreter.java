
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

package com.revenat.javamm.interpreter.component.impl.operation.simple;

import com.revenat.javamm.code.component.ExpressionContext;
import com.revenat.javamm.code.fragment.operation.ContinueOperation;
import com.revenat.javamm.interpreter.component.impl.operation.AbstractOperationInterpreter;
import com.revenat.javamm.interpreter.component.impl.operation.exception.ContinueOperationException;

/**
 * @author Vitaliy Dragun
 */
public class ContinueOperationInterpreter extends AbstractOperationInterpreter<ContinueOperation> {

    public ContinueOperationInterpreter(final ExpressionContext expressionContext) {
        super(expressionContext);
    }

    @Override
    public Class<ContinueOperation> getOperationClass() {
        return ContinueOperation.class;
    }

    @Override
    protected void interpretOperation(final ContinueOperation operation) {
        throw new ContinueOperationException();
    }
}
