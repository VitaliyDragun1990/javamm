
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

package com.revenat.javamm.compiler.component.impl.operation.block.forr;

import com.revenat.javamm.code.fragment.Expression;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.operation.Block;
import com.revenat.javamm.code.fragment.operation.ForInitOperation;
import com.revenat.javamm.code.fragment.operation.ForOperation;
import com.revenat.javamm.code.fragment.operation.ForOperation.Builder;
import com.revenat.javamm.code.fragment.operation.ForUpdateOperation;
import com.revenat.javamm.compiler.component.impl.operation.ForOperationHeader;

/**
 * @author Vitaliy Dragun
 */
class ForOperationHeaderImpl implements ForOperationHeader {

    private ForInitOperation initOperation;

    private Expression condition;

    private ForUpdateOperation updateOperation;

    void setInitOperation(final ForInitOperation initOperation) {
        this.initOperation = initOperation;
    }

    void setCondition(final Expression condition) {
        this.condition = condition;
    }

    void setUpdateOperation(final ForUpdateOperation updateOperation) {
        this.updateOperation = updateOperation;
    }

    private void setHeaderValuesTo(final Builder builder) {
        setInitOperationIfPresent(builder);
        setConditionIfPresent(builder);
        setUpdateOperationIfPresent(builder);
    }

    @Override
    public ForOperation mergeWith(final Block forOperationBody, final SourceLine sourceLine) {
        final ForOperation.Builder builder = new Builder();
        builder.setSourceLine(sourceLine);
        builder.setBody(forOperationBody);
        setHeaderValuesTo(builder);
        return builder.build();
    }

    private void setUpdateOperationIfPresent(final Builder builder) {
        if (updateOperation != null) {
            builder.setUpdateOperation(updateOperation);
        }
    }

    private void setConditionIfPresent(final Builder builder) {
        if (condition != null) {
            builder.setCondition(condition);
        }
    }

    private void setInitOperationIfPresent(final Builder builder) {
        if (initOperation != null) {
            builder.setInitOperation(initOperation);
        }
    }

    @Override
    public String toString() {
        return "ForOperationHeaderImpl [initOperation=" + initOperation + ", condition=" + condition +
            ", updateOperation=" + updateOperation + "]";
    }
}
