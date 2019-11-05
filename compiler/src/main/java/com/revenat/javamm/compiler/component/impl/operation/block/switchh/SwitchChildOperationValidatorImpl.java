
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
import com.revenat.javamm.code.fragment.operation.CaseOperation;
import com.revenat.javamm.code.fragment.operation.SwitchChildOperation;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;
import com.revenat.javamm.compiler.component.impl.operation.SwitchChildOperationValidator;

import java.util.List;
import java.util.ListIterator;

import static com.revenat.javamm.code.util.TypeUtils.confirmType;

/**
 * @author Vitaliy Dragun
 *
 */
public class SwitchChildOperationValidatorImpl implements SwitchChildOperationValidator {

    @Override
    public void validate(final List<SwitchChildOperation> switchChildOperations) {
        validateNoDuplicateSwitchChildOperations(switchChildOperations);
        validateNoUnreachableCodeAfterBreakOperation(switchChildOperations);
    }

    private void validateNoDuplicateSwitchChildOperations(final List<SwitchChildOperation> operations) {
        for (int i = 0; i < operations.size() - 1; i++) {
            final SwitchChildOperation current = operations.get(i);
            validateNoSubsequentEqualOperation(current, i, operations);
        }
    }

    private void validateNoSubsequentEqualOperation(final SwitchChildOperation current,
                                                    final int currentIndex,
                                                    final List<SwitchChildOperation> allOperations) {
        for (int j = currentIndex + 1; j < allOperations.size(); j++) {
            final SwitchChildOperation next = allOperations.get(j);
            validateNotEquals(current, next);
        }
    }

    private void validateNotEquals(final SwitchChildOperation first, final SwitchChildOperation second) {
        if (areEquals(first, second)) {
            throw duplicateLabelException(second);
        }
    }

    private JavammLineSyntaxError duplicateLabelException(final SwitchChildOperation violator) {
        if (violator.isDefault()) {
            return new JavammLineSyntaxError(violator.getSourceLine(), "Duplicate default label");
        } else {
            return new JavammLineSyntaxError(violator.getSourceLine(),
                    "Duplicate case label '%s'", ((CaseOperation) violator).getExpression());
        }
    }

    private boolean areEquals(final SwitchChildOperation first, final SwitchChildOperation second) {
        return first.compareTo(second) == 0;
    }

    private void validateNoUnreachableCodeAfterBreakOperation(final List<SwitchChildOperation> operations) {
        for (final SwitchChildOperation operation : operations) {
            validateNoUnreachableCodeAfterBreakOperation(operation);
        }
    }

    private void validateNoUnreachableCodeAfterBreakOperation(final SwitchChildOperation operation) {
        final ListIterator<Operation> bodyOperations = getBodyOperations(operation);
        while (bodyOperations.hasNext()) {
            final Operation bodyOperation = bodyOperations.next();
            if (isBreakOperation(bodyOperation) && bodyOperations.hasNext()) {
                final Operation unreachableOperation = bodyOperations.next();
                throw new JavammLineSyntaxError(unreachableOperation.getSourceLine(), "Unreachable code");
            }
        }
    }

    private ListIterator<Operation> getBodyOperations(final SwitchChildOperation operation) {
        return operation.getBody().getOperations().listIterator();
    }

    private boolean isBreakOperation(final Operation operation) {
        return confirmType(BreakOperation.class, operation);
    }
}
