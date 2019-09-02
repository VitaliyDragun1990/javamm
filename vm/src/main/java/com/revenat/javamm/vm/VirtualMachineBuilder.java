
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

import com.revenat.javamm.code.fragment.ByteCode;
import com.revenat.javamm.code.fragment.SourceCode;
import com.revenat.javamm.compiler.Compiler;
import com.revenat.javamm.compiler.CompilerConfigurator;
import com.revenat.javamm.interpreter.Interpreter;
import com.revenat.javamm.interpreter.InterpreterConfigurator;

import static java.util.Objects.requireNonNull;

/**
 * Responsible for building fully configured and ready to work javamm virtual
 * machine
 *
 * @author Vitaliy Dragun
 *
 */
public class VirtualMachineBuilder {

    public VirtualMachine build() {
        return new VirtualMachineImpl(
                buildCompilerConfiguration().getCompiler(),
                buildInterpreterConfigurator().getInterpreter());
    }

    protected CompilerConfigurator buildCompilerConfiguration() {
        return new CompilerConfigurator();
    }

    protected InterpreterConfigurator buildInterpreterConfigurator() {
        return new InterpreterConfigurator();
    }

    private static class VirtualMachineImpl implements VirtualMachine {
        private final Compiler compiler;

        private final Interpreter interpreter;

        private VirtualMachineImpl(final Compiler compiler, final Interpreter interpreter) {
            this.compiler = requireNonNull(compiler);
            this.interpreter = requireNonNull(interpreter);
        }

        @Override
        public void run(final SourceCode... sourceCodes) {
            final ByteCode byteCode = compiler.compile(sourceCodes);
            interpreter.interpret(byteCode);
        }
    }
}
