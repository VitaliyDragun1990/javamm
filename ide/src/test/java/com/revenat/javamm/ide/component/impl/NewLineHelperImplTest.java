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

import com.revenat.javamm.ide.component.NewLineHelper;
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

import static com.revenat.javamm.ide.util.TabReplaceUtils.replaceTabulations;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("a new line helper")
@ExtendWith(MockitoExtension.class)
class NewLineHelperImplTest {

    private static final String TAB = replaceTabulations("\t");

    private static final int CURRENT_PARAGRAPH = 1;
    
    @Mock
    private CodeArea codeArea;
    
    private NewLineHelper newLineHelper;

    @BeforeEach
    void setUp() {
        newLineHelper = new NewLineHelperImpl();
    }

    @Test
    @Order(1)
    void shouldInsertIntendedNewLineAndMoveCaretInsideBlock() {
        when(codeArea.getCurrentParagraph()).thenReturn(CURRENT_PARAGRAPH);
        when(codeArea.getText(CURRENT_PARAGRAPH - 1)).thenReturn("{");
        when(codeArea.getCaretPosition()).thenReturn(1);

        newLineHelper.insertNewLine(codeArea);

        verify(codeArea).replaceText(0, 1, "\n" + TAB);
    }

    @Test
    @Order(2)
    void shouldInsertIntendedNewLine() {
        when(codeArea.getCurrentParagraph()).thenReturn(CURRENT_PARAGRAPH);
        when(codeArea.getText(CURRENT_PARAGRAPH - 1)).thenReturn(TAB);
        when(codeArea.getCaretPosition()).thenReturn(1);

        newLineHelper.insertNewLine(codeArea);

        verify(codeArea).replaceText(0, 1, "\n" + TAB);
    }
}