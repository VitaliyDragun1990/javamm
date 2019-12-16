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
import com.revenat.juinit.addons.ReplaceCamelCase;
import org.fxmisc.richtext.CodeArea;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("a component factory")
class ComponentFactoryTest {

    @Mock
    private ExecutorService executorServiceMock;

    @Mock
    private Console consoleMock;

    @Mock
    private CodeArea codeAreaMock;

    private List<SourceCode> sourceCodes;

    private ComponentFactory componentFactory;

    @BeforeEach
    void setUp() {
        sourceCodes = List.of();

        componentFactory = new ComponentFactoryImpl.Builder()
            .addExecutorService(executorServiceMock)
            .addCodeFormatter(ComponentFactoryProvider.CODE_FORMATTER)
            .addPairedTokensHelper(ComponentFactoryProvider.PAIRED_TOKENS_HELPER)
            .addNewLineHelper(ComponentFactoryProvider.NEW_LINE_HELPER)
            .addCodeTemplateHelper(ComponentFactoryProvider.CODE_TEMPLATE_HELPER)
            .build();
    }

    @Test
    @Order(1)
    void shouldProvideASingletonInstanceOfPairedTokensHelper() {
        assertSame(componentFactory.getPairedTokensHelper(), componentFactory.getPairedTokensHelper());
    }

    @Test
    @Order(2)
    void shouldProvideASingletonInstanceOfNewLineHelper() {
        assertSame(componentFactory.getNewLineHelper(), componentFactory.getNewLineHelper());
    }

    @Test
    @Order(3)
    void shouldProvideASingletonInstanceOfCodeTemplateHelperHelper() {
        assertSame(componentFactory.getCodeTemplateHelper(), componentFactory.getCodeTemplateHelper());
    }

    @Test
    @Order(4)
    void shouldProvideASingletonInstanceOfCodeFormatter() {
        assertSame(componentFactory.getCodeFormatter(), componentFactory.getCodeFormatter());
    }

    @Test
    @Order(5)
    void shouldProvideNewInstanceOfVirtualMachineRunnerForEachRequest() {
        assertNotSame(componentFactory.createVirtualMachineRunner(consoleMock, sourceCodes),
            componentFactory.createVirtualMachineRunner(consoleMock, sourceCodes));
    }

    @Test
    @Order(6)
    void shouldProvideNewInstanceOfSyntaxHighlighterForEachRequest() {
        assertNotSame(componentFactory.createSyntaxHighlighter(codeAreaMock),
            componentFactory.createSyntaxHighlighter(codeAreaMock));
    }

    @Test
    @Order(7)
    void shouldReleaseAllHeldResourcesOnRelease() {
        componentFactory.release();

        verify(executorServiceMock).shutdownNow();
    }
}