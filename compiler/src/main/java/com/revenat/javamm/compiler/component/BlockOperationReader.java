
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

import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.operation.Block;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;

import java.util.ListIterator;

/**
 * Responsible for reading compiled code and transforming it into byte code
 * block
 *
 * @author Vitaliy Dragun
 *
 */
public interface BlockOperationReader {

    /**
     * Reads compiled code starting at specified source line and transforms it into
     * byte code block
     *
     * @param blockStartingLine    source line where particular byte code block
     *                             starts
     * @param compiledCodeIterator iterator that points to a specified place in the
     *                             compiled code just after source line where
     *                             particular byte code block starts
     * @return byte code {@link Block}
     * @throws JavammLineSyntaxError if some source line can not be read due to
     *                               syntax error
     */
    Block read(SourceLine blockStartingLine, ListIterator<SourceLine> compiledCodeIterator);

    /**
     * Reads compiled code starting at specified source line and transforms it into
     * byte code block. Expects that block to be read must end with {@code '}' }
     * closing curly brace which designates block end.
     *
     * @param blockStartingLine    source line where particular byte code block
     *                             starts
     * @param compiledCodeIterator iterator that points to a specified place in the
     *                             compiled code just after source line where
     *                             particular byte code block starts
     * @return byte code {@link Block}
     * @throws JavammLineSyntaxError if some source line can not be read due to
     *                               syntax error
     */
    Block readWithExpectedClosingCurlyBrace(SourceLine blockStartingLine,
            ListIterator<SourceLine> compiledCodeIterator);
}
