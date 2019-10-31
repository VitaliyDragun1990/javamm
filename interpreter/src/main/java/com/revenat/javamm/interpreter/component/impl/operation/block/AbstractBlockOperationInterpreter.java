
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

package com.revenat.javamm.interpreter.component.impl.operation.block;

import com.revenat.javamm.code.component.ExpressionContext;
import com.revenat.javamm.code.fragment.Operation;
import com.revenat.javamm.code.fragment.operation.Block;
import com.revenat.javamm.interpreter.component.BlockOperationInterpreter;
import com.revenat.javamm.interpreter.component.BlockOperationInterpreterAware;
import com.revenat.javamm.interpreter.component.impl.operation.AbstractOperationInterpreter;

import static java.util.Objects.requireNonNull;

/**
 * @author Vitaliy Dragun
 *
 */
abstract class AbstractBlockOperationInterpreter<T extends Operation> extends AbstractOperationInterpreter<T>
        implements BlockOperationInterpreterAware {

    private BlockOperationInterpreter blockOperationInterpreter;

    protected AbstractBlockOperationInterpreter(final ExpressionContext expressionContext) {
        super(expressionContext);
    }

    protected BlockOperationInterpreter getBlockOperationInterpreter() {
        return requireNonNull(blockOperationInterpreter, "blockOperationInterpreter is not set");
    }

    @Override
    public void setBlockOperationInterpreter(final BlockOperationInterpreter blockOperationInterpreter) {
        this.blockOperationInterpreter = requireNonNull(blockOperationInterpreter);
    }

    protected void interpretBlock(final Block block) {
        final BlockScopeLocalContextController contextController = new BlockScopeLocalContextController();
        try {
            contextController.setChildLocalContextForNestedBlock();
            getBlockOperationInterpreter().interpret(block);
        } finally {
            contextController.disposeChildLocalContext();
        }
    }
}
