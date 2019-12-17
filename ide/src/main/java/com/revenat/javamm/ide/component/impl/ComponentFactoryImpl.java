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
import com.revenat.javamm.ide.component.CodeFormatter;
import com.revenat.javamm.ide.component.CodeTemplateHelper;
import com.revenat.javamm.ide.component.ComponentFactory;
import com.revenat.javamm.ide.component.NewLineHelper;
import com.revenat.javamm.ide.component.PairedTokensHelper;
import com.revenat.javamm.ide.component.SyntaxHighlighter;
import com.revenat.javamm.ide.component.VirtualMachineRunner;
import com.revenat.javamm.vm.VirtualMachine;
import com.revenat.javamm.vm.VirtualMachineBuilder;
import org.fxmisc.richtext.CodeArea;

import java.util.List;
import java.util.concurrent.ExecutorService;

import static java.util.Objects.requireNonNull;

/**
 * @author Vitaliy Dragun
 */
final class ComponentFactoryImpl implements ComponentFactory {

    private final ExecutorService executorService;

    private final CodeTemplateHelper codeTemplateHelper;

    private final NewLineHelper newLineHelper;

    private final PairedTokensHelper pairedTokensHelper;

    private final CodeFormatter codeFormatter;

    private ComponentFactoryImpl(final ExecutorService executorService,
                                 final CodeTemplateHelper codeTemplateHelper, final NewLineHelper newLineHelper,
                                 final PairedTokensHelper pairedTokensHelper, final CodeFormatter codeFormatter) {
        this.executorService = requireNonNull(executorService);
        this.codeTemplateHelper = requireNonNull(codeTemplateHelper);
        this.newLineHelper = requireNonNull(newLineHelper);
        this.pairedTokensHelper = requireNonNull(pairedTokensHelper);
        this.codeFormatter = requireNonNull(codeFormatter);
    }

    @Override
    public SyntaxHighlighter createSyntaxHighlighter(final CodeArea codeArea) {
        return new JavammAsyncSyntaxHighlighter(codeArea, executorService);
    }

    @Override
    public VirtualMachineRunner createVirtualMachineRunner(final Console console, final List<SourceCode> sourceCodes) {
        final VirtualMachine virtualMachine = new VirtualMachineBuilder()
            .setConsole(console)
            .build();
        return new VirtualMachineRunnerImpl(
            console,
            virtualMachine,
            sourceCodes,
            new SimpleThreadRunner(Thread::new));
    }

    @Override
    public CodeTemplateHelper getCodeTemplateHelper() {
        return codeTemplateHelper;
    }

    @Override
    public NewLineHelper getNewLineHelper() {
        return newLineHelper;
    }

    @Override
    public PairedTokensHelper getPairedTokensHelper() {
        return pairedTokensHelper;
    }

    @Override
    public CodeFormatter getCodeFormatter() {
        return codeFormatter;
    }

    @Override
    public void release() {
        executorService.shutdownNow();
    }

    static class Builder {
        private ExecutorService executorService;

        private CodeTemplateHelper codeTemplateHelper;

        private NewLineHelper newLineHelper;

        private PairedTokensHelper pairedTokensHelper;

        private CodeFormatter codeFormatter;

        Builder() {
        }

        Builder addExecutorService(final ExecutorService executor) {
            this.executorService = requireNonNull(executor);
            return this;
        }

        Builder addCodeTemplateHelper(final CodeTemplateHelper templateHelper) {
            this.codeTemplateHelper = requireNonNull(templateHelper);
            return this;
        }

        Builder addNewLineHelper(final NewLineHelper nlHelper) {
            this.newLineHelper = requireNonNull(nlHelper);
            return this;
        }

        Builder addPairedTokensHelper(final PairedTokensHelper ptHelper) {
            this.pairedTokensHelper = requireNonNull(ptHelper);
            return this;
        }

        Builder addCodeFormatter(final CodeFormatter cFormatter) {
            this.codeFormatter = requireNonNull(cFormatter);
            return this;
        }

        ComponentFactory build() {
            return new ComponentFactoryImpl(executorService, codeTemplateHelper,
                newLineHelper, pairedTokensHelper, codeFormatter);
        }
    }
}
