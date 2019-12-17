
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

package com.revenat.javamm.vm;

import com.revenat.javamm.code.fragment.SourceCode;
import com.revenat.javamm.compiler.error.JavammSyntaxError;
import com.revenat.javamm.interpreter.error.JavammRuntimeError;
import com.revenat.javamm.interpreter.error.TerminateInterpreterException;

/**
 * Represents virtual machine responsible for compiling javamm language source
 * code into byte code and interpreting that byte code afterwards.
 *
 * @author Vitaliy Dragun
 */
public interface VirtualMachine {

    /**
     * Starts virtual machine with specified source code to process.
     *
     * @param sourceCodes source code to process
     * @throws JavammSyntaxError             if specified source code contains any
     *                                       kind of syntax errors
     * @throws JavammRuntimeError            if there is some kind of error during
     *                                       runtime
     * @throws TerminateInterpreterException if virtual machine interpreter
     *                                       component has been abruptly terminated
     */
    void run(SourceCode... sourceCodes);
}
