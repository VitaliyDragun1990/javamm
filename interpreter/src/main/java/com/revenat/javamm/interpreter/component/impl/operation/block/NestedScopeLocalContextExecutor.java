
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

import com.revenat.javamm.interpreter.model.LocalContext;

import static com.revenat.javamm.interpreter.model.CurrentRuntimeProvider.getCurrentRuntime;

final class NestedScopeLocalContextExecutor {

    private final LocalContext parentContext;

    private final LocalContext childContext;

    private NestedScopeLocalContextExecutor() {
        parentContext = getCurrentRuntime().getCurrentLocalContext();
        childContext = parentContext.createChildLocalContext();
    }

    /**
     * Creates nested scope with {@linkplain LocalContext child local context} for
     * provided {@linkplain Runnable action} and destroys such child local context
     * when specified {@code action} finishes
     */
    static void executeInsideNestedScope(final Runnable action) {
        final NestedScopeLocalContextExecutor scopeExecutor = new NestedScopeLocalContextExecutor();
        try {
            scopeExecutor.setChildLocalContextForNestedBlock();
            action.run();
        } finally {
            scopeExecutor.disposeChildLocalContext();
        }
    }

    private void setChildLocalContextForNestedBlock() {
        getCurrentRuntime().setCurrentLocalContext(childContext);
    }

    private void disposeChildLocalContext() {
        getCurrentRuntime().setCurrentLocalContext(parentContext);
    }
}
