
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

import com.revenat.javamm.code.fragment.SourceCode;
import com.revenat.javamm.code.fragment.SourceLine;

import java.util.List;

/**
 * Responsible for reading {@link SourceCode} and parsing it into list of
 * {@link SourceLine}
 *
 * @author Vitaliy Dragun
 */
public interface SourceLineReader {

    /**
     * Reads source code and translates it into list of source lines with meaningful
     * tokens.
     *
     * @param sourceCode source code to read
     * @return list of source lines with meaningful tokens.
     */
    List<SourceLine> read(SourceCode sourceCode);
}
