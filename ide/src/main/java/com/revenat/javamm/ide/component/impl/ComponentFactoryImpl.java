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
import com.revenat.javamm.ide.component.ComponentFactory;
import com.revenat.javamm.ide.component.SyntaxHighlighter;
import com.revenat.javamm.ide.component.VirtualMachineRunner;
import com.revenat.javamm.vm.VirtualMachine;
import com.revenat.javamm.vm.VirtualMachineBuilder;
import org.fxmisc.richtext.CodeArea;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Vitaliy Dragun
 */
public class ComponentFactoryImpl implements ComponentFactory {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    public SyntaxHighlighter createSyntaxHighlighter(final CodeArea codeArea) {
        return new JavammAsyncSyntaxHighlighter(codeArea, executorService);
    }

    @Override
    public VirtualMachineRunner createVirtualMachineRunner(final Console console, final List<SourceCode> sourceCodes) {
        final VirtualMachine virtualMachine = new VirtualMachineBuilder()
            .setConsole(console)
            .build();
        return new VirtualMachineRunnerImpl(virtualMachine, sourceCodes);
    }

    @Override
    public void release() {
        executorService.shutdownNow();
    }
}
