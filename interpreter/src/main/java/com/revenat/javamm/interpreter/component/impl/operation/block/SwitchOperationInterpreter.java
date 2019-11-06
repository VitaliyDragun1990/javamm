
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
import com.revenat.javamm.code.fragment.Expression;
import com.revenat.javamm.code.fragment.operation.CaseOperation;
import com.revenat.javamm.code.fragment.operation.SwitchChildOperation;
import com.revenat.javamm.code.fragment.operation.SwitchOperation;
import com.revenat.javamm.interpreter.component.CalculatorFacade;
import com.revenat.javamm.interpreter.component.impl.operation.exception.BreakOperationException;

import java.util.List;

import static com.revenat.javamm.code.fragment.operator.BinaryOperator.PREDICATE_EQUALS;

import static java.util.Objects.requireNonNull;

/**
 * @author Vitaliy Dragun
 *
 */
public class SwitchOperationInterpreter extends AbstractBlockOperationInterpreter<SwitchOperation> {

    private final CalculatorFacade calcualtorFacade;

    public SwitchOperationInterpreter(final ExpressionContext expressionContext,
                                      final CalculatorFacade calcualtorFacade) {
        super(expressionContext);
        this.calcualtorFacade = requireNonNull(calcualtorFacade);
    }

    @Override
    public Class<SwitchOperation> getOperationClass() {
        return SwitchOperation.class;
    }

    @Override
    protected void interpretOperation(final SwitchOperation operation) {
        final int matchPosition = findMatchedLabelPosition(operation);
        interpretOperations(getLablesBeginningAtPosition(matchPosition, operation));
    }

    private int findMatchedLabelPosition(final SwitchOperation operation) {
        final List<SwitchChildOperation> labels = operation.getChildOperations();

        int defaultLabelPosition = labels.size();
        for (int i = 0; i < labels.size(); i++) {
            final SwitchChildOperation currentLabel = labels.get(i);
            defaultLabelPosition = currentLabel.isDefault() ? i : defaultLabelPosition;
            if (areMatch(operation, currentLabel)) {
                return i;
            }
        }
        return defaultLabelPosition;
    }

    private List<SwitchChildOperation> getLablesBeginningAtPosition(final int biginningPosition,
                                                                    final SwitchOperation switchOperation) {
        return switchOperation
                .getChildOperations()
                .subList(biginningPosition, switchOperation.getChildOperations().size());
    }

    private void interpretOperations(final List<SwitchChildOperation> operations) {
        try {
            for (final SwitchChildOperation operation : operations) {
                interpretBlock(operation.getBody());
            }
        } catch (final BreakOperationException e) {
            // do nothing. Stop evaluating switch body
        }
    }

    private boolean areMatch(final SwitchOperation operation, final SwitchChildOperation label) {
        return !label.isDefault() && areEqual(operation.getCondition(), ((CaseOperation) label).getExpression());
    }

    private boolean areEqual(final Expression first, final Expression second) {
        return (Boolean) calcualtorFacade.calculate(expressionContext, first, PREDICATE_EQUALS, second);
    }
}
