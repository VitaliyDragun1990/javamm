
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

package com.revenat.javamm.interpreter.component.impl;

import com.revenat.javamm.code.exception.ConfigException;
import com.revenat.javamm.code.fragment.Operation;
import com.revenat.javamm.code.fragment.operation.Block;
import com.revenat.javamm.interpreter.component.BlockOperationInterpreter;
import com.revenat.javamm.interpreter.component.OperationInterpreter;

import java.util.Map;
import java.util.Set;
import java.util.function.BinaryOperator;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toUnmodifiableMap;

/**
 * @author Vitaliy Dragun
 *
 */
@SuppressWarnings("rawtypes")
public class BlockOperationInterpreterImpl implements BlockOperationInterpreter {
    private final Map<Class<? extends Operation>, OperationInterpreter> interpreterMap;

    /**
     * Creates new block operation interpreter with set of operation interpreters
     * for defined operations
     *
     * @throws ConfigException if provided set of operation interpreters contains
     *                         several interpreters for the same operation
     */
    public BlockOperationInterpreterImpl(final Set<OperationInterpreter<?>> operationInterpreters) {
        this.interpreterMap = buildOperationInterpreterMap(operationInterpreters);
    }

    private Map<Class<? extends Operation>, OperationInterpreter> buildOperationInterpreterMap(
            final Set<OperationInterpreter<?>> operationInterpreters) {
        return operationInterpreters.stream()
                .collect(toUnmodifiableMap(OperationInterpreter::getOperationClass, identity(), checkDuplicates()));
    }

    private BinaryOperator<OperationInterpreter> checkDuplicates() {
        return (i1, i2) -> {
            throw new ConfigException(String.format(
                    "Duplicate of OperationInterpreter was found for operation:=%s, interpreter1=%s, interpreter2=%s",
                    i1.getOperationClass().getName(), i1, i2));
        };
    }

    @SuppressWarnings("unchecked")
    @Override
    public void interpret(final Block block) {
        for (final Operation operation : block.getOperations()) {
            final OperationInterpreter operationInterpreter = findInterpreterFor(operation);
            operationInterpreter.interpret(operation);
        }
    }

    private OperationInterpreter findInterpreterFor(final Operation operation) {
        final OperationInterpreter interpreter = interpreterMap.get(operation.getClass());
        if (interpreter != null) {
            return interpreter;
        } else {
            throw new ConfigException("There is no OperationInterpreter for " + operation.getClass());
        }
    }

}
