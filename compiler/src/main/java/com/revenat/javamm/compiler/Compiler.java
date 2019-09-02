
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

package com.revenat.javamm.compiler;

import com.revenat.javamm.code.fragment.ByteCode;
import com.revenat.javamm.code.fragment.SourceCode;
import com.revenat.javamm.compiler.error.JavammSyntaxError;

/**
 * Represents compiler component, responsible for compiling source code into
 * byte code.
 *
 * @author Vitaliy Dragun
 *
 */
public interface Compiler {

    /**
     * Compiles given {@link SourceCode} into {@link ByteCode}
     *
     * @param sourceCode source code to compile
     * @return byte code compiled from given source code
     * @throws JavammSyntaxError if given source code contains any syntax error
     */
    ByteCode compile(SourceCode... sourceCode) throws JavammSyntaxError;
}
