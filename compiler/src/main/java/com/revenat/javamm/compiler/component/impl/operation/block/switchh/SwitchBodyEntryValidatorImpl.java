
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

package com.revenat.javamm.compiler.component.impl.operation.block.switchh;

import com.revenat.javamm.code.fragment.Operation;
import com.revenat.javamm.code.fragment.operation.BreakOperation;
import com.revenat.javamm.code.fragment.operation.SwitchBodyEntry;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;
import com.revenat.javamm.compiler.component.impl.operation.SwitchBodyEntryValidator;

import java.util.ListIterator;

import static com.revenat.javamm.code.util.TypeUtils.confirmType;

/**
 * @author Vitaliy Dragun
 */
public class SwitchBodyEntryValidatorImpl implements SwitchBodyEntryValidator {

    @Override
    public void validate(final SwitchBodyEntry entry) {
        validateNoUnreachableCodeAfterBreakOperation(entry);
    }

    private void validateNoUnreachableCodeAfterBreakOperation(final SwitchBodyEntry entry) {
        final ListIterator<Operation> bodyOperations = getBodyOperations(entry);
        while (bodyOperations.hasNext()) {
            final Operation bodyOperation = bodyOperations.next();
            if (isBreakOperation(bodyOperation) && bodyOperations.hasNext()) {
                final Operation unreachableOperation = bodyOperations.next();
                throw new JavammLineSyntaxError(unreachableOperation.getSourceLine(), "Unreachable code");
            }
        }
    }

    private ListIterator<Operation> getBodyOperations(final SwitchBodyEntry operation) {
        return operation.getBody().getOperations().listIterator();
    }

    private boolean isBreakOperation(final Operation operation) {
        return confirmType(BreakOperation.class, operation);
    }
}
