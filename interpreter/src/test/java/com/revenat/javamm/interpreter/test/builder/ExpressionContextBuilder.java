
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

package com.revenat.javamm.interpreter.test.builder;

import com.revenat.javamm.code.component.ExpressionContext;
import com.revenat.javamm.interpreter.component.ExpressionEvaluator;
import com.revenat.javamm.interpreter.component.ExpressionUpdater;
import com.revenat.javamm.interpreter.component.impl.ExpressionContextImpl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Vitaliy Dragun
 */
public class ExpressionContextBuilder {
    private final Set<ExpressionEvaluator<?>> evaluators;

    private final Set<ExpressionUpdater<?>> updaters;

    public ExpressionContextBuilder() {
        this.evaluators = new HashSet<>();
        this.updaters = new HashSet<>();
    }

    public ExpressionContextBuilder withEvaluators(final ExpressionEvaluator<?>... evaluators) {
        this.evaluators.addAll(Arrays.asList(evaluators));
        return this;
    }

    public ExpressionContextBuilder withUpdaters(final ExpressionUpdater<?>... updaters) {
        this.updaters.addAll(Arrays.asList(updaters));
        return this;
    }

    public ExpressionContext build() {
        return new ExpressionContextImpl(Set.copyOf(evaluators), Set.copyOf(updaters));
    }
}
