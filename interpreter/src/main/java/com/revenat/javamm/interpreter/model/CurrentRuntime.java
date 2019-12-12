
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

package com.revenat.javamm.interpreter.model;

import com.revenat.javamm.code.fragment.Operation;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.function.DeveloperFunction;
import com.revenat.javamm.interpreter.component.FunctionInvoker;

import java.util.List;

/**
 * Represents current runtime environment in which interpreter operates at the
 * moment.
 *
 * @author Vitaliy Dragun
 *
 */
public interface CurrentRuntime {

    /**
     * Returns source line which interpreter process at the moment
     *
     * @throws NullPointerException if current source line has not been defined yet
     */
    SourceLine getCurrentSourceLine();

    /**
     * Sets current source line
     *
     * @throws NullPointerException if provided {@code currentSourceLine} is
     *                              {@code null}
     */
    void setCurrentSourceLine(SourceLine currentSourceLine);

    /**
     * Returns current {@linkplain LocalContext local context} that keeps local
     * variables for current module
     *
     * @throws NullPointerException if current local context has not been set
     */
    LocalContext getCurrentLocalContext();

    /**
     * Sets current local context
     *
     * @throws NullPointerException if specified {@code localContext} is
     *                              {@code null}
     */
    void setCurrentLocalContext(LocalContext localContext);

    /**
     * Sets {@linkplain Operation current operation} from the module which
     * interpreter process at the moment
     *
     * @throws NullPointerException if specified {@code operation} is {@code null}
     */
    default void setCurrentOperation(final Operation operation) {
        setCurrentSourceLine(operation.getSourceLine());
    }

    FunctionInvoker getCurrentFunctionInvoker();

    /**
     * Designates that flow of execution enters given {@linkplain DeveloperFunction
     * developer function}
     */
    void enterToFunction(DeveloperFunction developerFunction);

    /**
     * Designates that flow of execution exists from currently executed function
     */
    void exitFromFunction();

    /**
     * Returns current stack trace which represents flow of execution's path from
     * main function to currently executing line of code
     */
    List<StackTraceItem> getCurrentStackTrace();
}
