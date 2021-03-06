
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

package com.revenat.javamm.cmd;

import com.revenat.javamm.code.fragment.SourceCode;
import com.revenat.javamm.compiler.error.JavammSyntaxError;
import com.revenat.javamm.interpreter.error.JavammRuntimeError;
import com.revenat.javamm.vm.VirtualMachine;
import com.revenat.javamm.vm.VirtualMachineBuilder;

import java.util.Arrays;

import static com.revenat.javamm.code.util.ExceptionUtils.wrapCheckedException;

/**
 * @author Vitaliy Dragun
 */
public final class JavammCMDLauncher {

    private JavammCMDLauncher() {
    }

    public static void main(final String... args) {
        final VirtualMachine vm = new VirtualMachineBuilder().build();

        try {
            vm.run(sourceCodeFrom(args));
        } catch (final JavammSyntaxError | JavammRuntimeError e) {
            System.err.println(e.getMessage());
        }
    }

    private static SourceCode[] sourceCodeFrom(final String[] args) {
        return Arrays.stream(args)
            .map(arg -> wrapCheckedException(() -> new FileSourceCode(arg)))
            .toArray(SourceCode[]::new);
    }
}
