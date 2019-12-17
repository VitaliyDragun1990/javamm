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
import com.revenat.javamm.compiler.error.JavammSyntaxError;
import com.revenat.javamm.ide.component.VirtualMachineRunner.VirtualMachineRunCompletedListener;
import com.revenat.javamm.interpreter.error.JavammRuntimeError;
import com.revenat.javamm.interpreter.error.TerminateInterpreterException;
import com.revenat.javamm.vm.VirtualMachine;
import com.revenat.juinit.addons.ReplaceCamelCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.List;

import static com.revenat.javamm.ide.component.VirtualMachineRunner.CompletionStatus.INTERNAL_ERROR;
import static com.revenat.javamm.ide.component.VirtualMachineRunner.CompletionStatus.RUNTIME_ERROR;
import static com.revenat.javamm.ide.component.VirtualMachineRunner.CompletionStatus.SUCCESS;
import static com.revenat.javamm.ide.component.VirtualMachineRunner.CompletionStatus.SYNTAX_ERROR;
import static com.revenat.javamm.ide.component.VirtualMachineRunner.CompletionStatus.TERMINATED;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("a virtual machine runner")
class VirtualMachineRunnerImplTest {

    @Mock
    private Console consoleMock;

    @Mock
    private VirtualMachine virtualMachineMock;

    @Mock
    private VirtualMachineRunCompletedListener listenerMock;

    @Mock
    private ThreadRunner threadRunnerMock;

    private VirtualMachineRunnerImpl virtualMachineRunner;

    @BeforeEach
    void setUp() {
        virtualMachineRunner = new VirtualMachineRunnerImpl(consoleMock, virtualMachineMock, List.of(), threadRunnerMock);
    }

    @Test
    @Order(1)
    void shouldReturnStatusSuccessIfRunCompletesSuccessfully() {
        doAnswer(runInSameThread()).when(threadRunnerMock).run(any(), any());

        virtualMachineRunner.run(listenerMock);

        verify(listenerMock).onRunCompleted(SUCCESS);
        verify(consoleMock, never()).outPrintln(any());
        verify(consoleMock, never()).errPrintln(any());
    }

    private Answer<Void> runInSameThread() {
        return invocation -> {
            final Runnable job = invocation.getArgument(0);
            job.run();
            return null;
        };
    }

    @Test
    @Order(2)
    void shouldReturnStatusSyntaxErrorIfRunFailsWithSyntaxError() {
        doAnswer(runInSameThread()).when(threadRunnerMock).run(any(), any());
        doThrow(new JavammSyntaxError("Syntax error") {
        }).when(virtualMachineMock).run(any());

        virtualMachineRunner.run(listenerMock);

        verify(listenerMock).onRunCompleted(SYNTAX_ERROR);
        verify(consoleMock, never()).outPrintln(any());
        verify(consoleMock).errPrintln("Syntax error");
    }

    @Test
    @Order(3)
    void shouldReturnStatusRuntimeErrorIfRunFailsWithRuntimeError() {
        doAnswer(runInSameThread()).when(threadRunnerMock).run(any(), any());
        doThrow(new JavammRuntimeError("Test") {
        }).when(virtualMachineMock).run(any());

        virtualMachineRunner.run(listenerMock);

        verify(listenerMock).onRunCompleted(RUNTIME_ERROR);
        verify(consoleMock, never()).outPrintln(any());
        verify(consoleMock).errPrintln("Runtime error: Test");
    }

    @Test
    @Order(4)
    void shouldReturnStatusTerminatedIfRunWasTerminated() {
        doAnswer(runInSameThread()).when(threadRunnerMock).run(any(), any());
        doThrow(new TerminateInterpreterException()).when(virtualMachineMock).run(any());

        virtualMachineRunner.run(listenerMock);

        verify(listenerMock).onRunCompleted(TERMINATED);
        verify(consoleMock, never()).outPrintln(any());
        verify(consoleMock, never()).errPrintln(any());
    }

    @Test
    @Order(5)
    void shouldReturnStatusInternalErrorIfRunFailsWithRuntimeException() {
        doAnswer(runInSameThread()).when(threadRunnerMock).run(any(), any());
        doThrow(new InternalErrorException("Internal")).when(virtualMachineMock).run(any());

        virtualMachineRunner.run(listenerMock);

        verify(listenerMock).onRunCompleted(INTERNAL_ERROR);
        verify(consoleMock, never()).outPrintln(any());
        verify(consoleMock).errPrintln(format("Internal error: %s: Internal%s",
            InternalErrorException.class.getName(), System.lineSeparator()));
    }

    @Test
    @Order(6)
    void shouldAllowToTerminateVirtualMachineRunning() {
        virtualMachineRunner.run(listenerMock);

        virtualMachineRunner.terminate();

        verify(threadRunnerMock).terminate();
    }

    @Test
    @Order(7)
    void shouldAllowToCheckWhetherVirtualMachineIsRunning() {
        when(threadRunnerMock.isRunning()).thenReturn(true);

        virtualMachineRunner.run(listenerMock);

        assertTrue(virtualMachineRunner.isRunning());
    }

    private static final class InternalErrorException extends RuntimeException {

        private InternalErrorException(final String msg) {
            super(msg, null, false, false);
        }
    }
}