
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
import com.revenat.javamm.code.fragment.operation.SwitchBodyEntry;
import com.revenat.javamm.code.fragment.operation.SwitchCaseEntry;
import com.revenat.javamm.code.fragment.operation.SwitchOperation;
import com.revenat.javamm.interpreter.component.impl.operation.exception.BreakOperationException;

import java.util.List;
import java.util.Objects;

/**
 * @author Vitaliy Dragun
 */
public class SwitchOperationInterpreter extends AbstractBlockOperationInterpreter<SwitchOperation> {

    public SwitchOperationInterpreter(final ExpressionContext expressionContext) {
        super(expressionContext);
    }

    @Override
    public Class<SwitchOperation> getOperationClass() {
        return SwitchOperation.class;
    }

    @Override
    protected void interpretOperation(final SwitchOperation operation) {
        final Object switchConditionValue = operation.getCondition().getValue(expressionContext);
        interpretEntries(findMatchedEntries(switchConditionValue, operation.getEntries()));
    }

    private List<SwitchBodyEntry> findMatchedEntries(final Object switchValue, final List<SwitchBodyEntry> entries) {
        int defaultEntryPosition = entries.size();
        for (int i = 0; i < entries.size(); i++) {
            final SwitchBodyEntry current = entries.get(i);
            defaultEntryPosition = current.isDefault() ? i : defaultEntryPosition;
            if (areMatch(switchValue, current)) {
                return entries.subList(i, entries.size());
            }
        }
        return entries.subList(defaultEntryPosition, entries.size());
    }

    private void interpretEntries(final List<SwitchBodyEntry> entries) {
        for (final SwitchBodyEntry entry : entries) {
            try {
                interpretBlock(entry.getBody());
            } catch (final BreakOperationException e) {
                break;
            }
        }
    }

    private boolean areMatch(final Object switchConditionValue, final SwitchBodyEntry entry) {
        return !entry.isDefault() && areEqual(switchConditionValue, getCaseValue(entry));
    }

    private Object getCaseValue(final SwitchBodyEntry entry) {
        return ((SwitchCaseEntry) entry).getExpression().getValue();
    }

    private boolean areEqual(final Object switchConditionValue, final Object caseExpressionValue) {
        return Objects.equals(switchConditionValue, caseExpressionValue);
    }
}
