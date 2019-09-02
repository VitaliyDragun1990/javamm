
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

import com.revenat.javamm.compiler.error.JavammSyntaxError;
import com.revenat.javamm.interpreter.error.JavammRuntimeError;
import com.revenat.javamm.vm.VirtualMachine;
import com.revenat.javamm.vm.VirtualMachineBuilder;

import java.io.IOException;

/**
 * @author Vitaliy Dragun
 *
 */
public final class JmmVmLauncher {

    private JmmVmLauncher() {
    }

    public static void main(final String[] args) throws IOException {
        final VirtualMachine vm = new VirtualMachineBuilder().build();

        try {
            vm.run(new FileSourceCode("src/main/resources/test.javamm"));
        } catch (final JavammSyntaxError | JavammRuntimeError e) {
            System.err.println(e.getMessage());
        }
    }
}
