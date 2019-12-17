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

package com.revenat.javamm.ide.component;

import com.revenat.javamm.code.component.Console;
import com.revenat.javamm.code.fragment.SourceCode;
import org.fxmisc.richtext.CodeArea;

import java.util.List;

/**
 * Responsible for building all kind of components
 *
 * @author Vitaliy Dragun
 */
public interface ComponentFactory extends Releasable {

    /**
     * Creates {@linkplain SyntaxHighlighter asynchronous syntax highlighter} for provided
     * {@linkplain CodeArea code area}
     *
     * @param codeArea code are for which asynchronous syntax highlighter should be created
     * @return instance of asynchronous syntax highlighter
     */
    SyntaxHighlighter createSyntaxHighlighter(CodeArea codeArea);

    /**
     * Creates new instance of {@linkplain VirtualMachineRunner virtual machine runner}
     *
     * @param console     {@linkplain Console console} for virtual machine to send messages to
     * @param sourceCodes list of {@linkplain SourceCode source code} which should be executed by virtual machine
     * @return new instance of {@linkplain VirtualMachineRunner virtual machine runner}
     */
    VirtualMachineRunner createVirtualMachineRunner(Console console, List<SourceCode> sourceCodes);

    /**
     * Returns {@linkplain CodeTemplateHelper code template helper}
     */
    CodeTemplateHelper getCodeTemplateHelper();

    /**
     * Returns instance of the {@linkplain NewLineHelper new line helper}
     */
    NewLineHelper getNewLineHelper();

    /**
     * Returns instance of the {@linkplain PairedTokensHelper paired tokens helper}
     */
    PairedTokensHelper getPairedTokensHelper();

    /**
     * Returns instance of the {@linkplain CodeFormatter code formatter}
     */
    CodeFormatter getCodeFormatter();
}
