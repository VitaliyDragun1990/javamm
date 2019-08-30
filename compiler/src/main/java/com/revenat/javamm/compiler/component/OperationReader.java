
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

package com.revenat.javamm.compiler.component;

import com.revenat.javamm.code.fragment.Operation;
import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;

import java.util.ListIterator;

/**
 * Responsible for transforming set of significant tokens into byte code
 * {@link Operation}.
 *
 * @author Vitaliy Dragun
 *
 */
public interface OperationReader {

    /**
     * Checks whether this operation reader can read particular {@link SourceLine}
     *
     * @param sourceLine source line to check
     */
    boolean canRead(SourceLine sourceLine);

    /**
     * Reads compiled code starting at specified source line and transform it into
     * particular byte code operation
     *
     * @param startingLine         source line to start
     * @param compiledCodeIterator iterator that points to a particular place in the
     *                             compiled code just after the source line where
     *                             particular byte code operation starts
     * @return byte code {@link Operation} obtained compiled code
     * @throws JavammLineSyntaxError if source line contains suntax error
     */
    Operation read(SourceLine startingLine, ListIterator<SourceLine> compiledCodeIterator);
}
