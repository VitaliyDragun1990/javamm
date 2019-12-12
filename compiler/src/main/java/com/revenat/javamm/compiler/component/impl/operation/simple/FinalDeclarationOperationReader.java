
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

package com.revenat.javamm.compiler.component.impl.operation.simple;

import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.compiler.component.ExpressionResolver;
import com.revenat.javamm.compiler.component.VariableBuilder;

import java.util.Optional;

import static com.revenat.javamm.code.syntax.Keywords.FINAL;

/**
 * Responsible for reading final declaration operation
 *
 * @author Vitaliy Dragun
 *
 */
public class FinalDeclarationOperationReader extends VariableDeclarationOperationReader {

    public FinalDeclarationOperationReader(final VariableBuilder variableBuilder,
                                           final ExpressionResolver expressionResolver) {
        super(variableBuilder, expressionResolver);
    }

    @Override
    public boolean canRead(final SourceLine sourceLine) {
        return FINAL.equals(sourceLine.getFirst());
    }

    @Override
    protected boolean isConstant() {
        return true;
    }

    @Override
    protected String missingNameErrorMessage() {
        return "Final name is missing";
    }

    @Override
    protected String missingExpressionErrorMessage() {
        return "Final expression is missing";
    }
}
