/*
 *
 *  Copyright (c) 2019. http://devonline.academy
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.revenat.javamm.ide.component.impl;

import com.revenat.javamm.code.component.Console;
import com.revenat.javamm.code.fragment.SourceCode;
import com.revenat.javamm.compiler.error.JavammSyntaxError;
import com.revenat.javamm.ide.component.VirtualMachineRunner;
import com.revenat.javamm.interpreter.error.JavammRuntimeError;
import com.revenat.javamm.interpreter.error.TerminateInterpreterException;
import com.revenat.javamm.vm.VirtualMachine;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;

/**
 * @author Vitaliy Dragun
 */
class VirtualMachineRunnerImpl implements VirtualMachineRunner {

    private final Console console;

    private final VirtualMachine virtualMachine;

    private final List<SourceCode> sourceCodes;

    private ThreadRunner threadRunner;

    VirtualMachineRunnerImpl(final Console console,
                             final VirtualMachine virtualMachine,
                             final List<SourceCode> sourceCodes,
                             final ThreadRunner threadRunner) {
        this.console = requireNonNull(console);
        this.virtualMachine = requireNonNull(virtualMachine);
        this.sourceCodes = unmodifiableList(sourceCodes);
        this.threadRunner = requireNonNull(threadRunner);
    }

    @Override
    public void run(final VirtualMachineRunCompletedListener listener) {
        requireNonNull(listener);
        threadRunner.run(() -> invokeVm(listener), "JavammVM-Run-Thread");
    }

    @Override
    public boolean isRunning() {
        return threadRunner.isRunning();
    }

    @Override
    public void terminate() {
        threadRunner.terminate();
    }

    private void invokeVm(final VirtualMachineRunCompletedListener listener) {
        CompletionStatus status = CompletionStatus.SUCCESS;
        try {
            virtualMachine.run(sourceCodes.toArray(new SourceCode[0]));
        } catch (final JavammSyntaxError e) {
            status = CompletionStatus.SYNTAX_ERROR;
            console.errPrintln(e.getMessage());
        } catch (final JavammRuntimeError e) {
            status = CompletionStatus.RUNTIME_ERROR;
            console.errPrintln(e.getMessage());
        } catch (final TerminateInterpreterException e) {
            status = CompletionStatus.TERMINATED;
        } catch (final RuntimeException e) {
            status = CompletionStatus.INTERNAL_ERROR;
            console.errPrintln(format("Internal error: %s", stackTraceToString(e)));
        } finally {
            listener.onRunCompleted(status);
        }
    }

    private String stackTraceToString(final Throwable th) {
        final StringWriter sw = new StringWriter();
        th.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
