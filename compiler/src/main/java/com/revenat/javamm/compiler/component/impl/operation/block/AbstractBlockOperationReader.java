
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

package com.revenat.javamm.compiler.component.impl.operation.block;

import com.revenat.javamm.code.fragment.Operation;
import com.revenat.javamm.compiler.component.BlockOperationReader;
import com.revenat.javamm.compiler.component.BlockOperationReaderAware;
import com.revenat.javamm.compiler.component.impl.operation.AbstractOperationReader;

import static java.util.Objects.requireNonNull;

/**
 * @author Vitaliy Dragun
 */
public abstract class AbstractBlockOperationReader<T extends Operation> extends AbstractOperationReader<T>
    implements BlockOperationReaderAware {

    private BlockOperationReader blockOperationReader;

    protected BlockOperationReader getBlockOperationReader() {
        return requireNonNull(blockOperationReader, "blockOperationReader is not set");
    }

    @Override
    public void setBlockOperationReader(final BlockOperationReader blockOperationReader) {
        this.blockOperationReader = requireNonNull(blockOperationReader);
    }
}
