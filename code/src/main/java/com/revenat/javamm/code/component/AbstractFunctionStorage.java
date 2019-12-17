
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

package com.revenat.javamm.code.component;

import com.revenat.javamm.code.fragment.Function;
import com.revenat.javamm.code.fragment.FunctionName;
import com.revenat.javamm.code.fragment.FunctionStorage;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * @author Vitaliy Dragun
 */
public abstract class AbstractFunctionStorage<T extends Function> implements FunctionStorage<T> {

    private final Map<FunctionName, T> functionRegistry;

    protected AbstractFunctionStorage(final Map<FunctionName, T> functionRegistry) {
        this.functionRegistry = Map.copyOf(functionRegistry);
    }

    @Override
    public Optional<T> getFunction(final FunctionName name) {
        return Optional.ofNullable(functionRegistry.get(requireNonNull(name)));
    }

    @Override
    public Collection<T> getAllFunctions() {
        return functionRegistry.values();
    }
}
