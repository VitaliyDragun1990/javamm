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

import com.revenat.javamm.ide.component.CodeTemplateHelper;
import com.revenat.javamm.ide.component.CodeTemplateStorage;
import com.revenat.javamm.ide.model.CodeTemplate;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.revenat.javamm.ide.model.CodeTemplate.CURSOR;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("a code template helper")
@ExtendWith(MockitoExtension.class)
class CodeTemplateHelperImplTest {

    private static final int CURRENT_PARAGRAPH = 1;

    @Mock
    private CodeTemplateStorage codeTemplateStorage;

    @Mock
    private CodeArea codeArea;

    private CodeTemplateHelper codeTemplateHelper;

    @BeforeEach
    void setup() {
        codeTemplateHelper = new CodeTemplateHelperImpl(codeTemplateStorage);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "",
        " ",
        "\t",
        "    "
    })
    @Order(1)
    void shouldNotInsertCodeTemplateIfCurrentLineIsBlank(String currentLine) {
        when(codeArea.getCurrentParagraph()).thenReturn(CURRENT_PARAGRAPH);
        when(codeArea.getText(CURRENT_PARAGRAPH - 1)).thenReturn(currentLine);

        assertFalse(codeTemplateHelper.insertCodeTemplateAtCaretPosition(codeArea));

//        verify(codeTemplateStorage, never()).getTemplate(anyString());
    }

    /**
     * @see com.revenat.javamm.code.syntax.Delimiters
     */
    @ParameterizedTest
    @ValueSource(strings = {
        "var ",
        "var\u00A0",
        "var\t",
        "var\r",
        "var\n"
    })
    @Order(2)
    void shouldNotInsertCodeTemplateIfLastTokenOnTheCurrentLineIsAmongIgnoredDelimiters(String currentLine) {
        when(codeArea.getCurrentParagraph()).thenReturn(CURRENT_PARAGRAPH);
        when(codeArea.getText(CURRENT_PARAGRAPH - 1)).thenReturn(currentLine);

        assertFalse(codeTemplateHelper.insertCodeTemplateAtCaretPosition(codeArea));
    }

    @Test
    @Order(3)
    void shouldNotInsertCodeTemplateIfNoTemplateFound() {
        when(codeArea.getCurrentParagraph()).thenReturn(CURRENT_PARAGRAPH);
        when(codeArea.getText(CURRENT_PARAGRAPH - 1)).thenReturn("test");
        when(codeTemplateStorage.getTemplate("test")).thenReturn(Optional.empty());

        assertFalse(codeTemplateHelper.insertCodeTemplateAtCaretPosition(codeArea));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "n",
        "var a = n",
        "'Hello ' + n",
        "\"Hello \" + n",
        "a + 'Hello ' + n",
        "a + \"Hello \" + n"
    })
    @Order(4)
    void shouldInsertCodeTemplateWithoutMovingCaret(String currentLine) {
        when(codeArea.getCurrentParagraph()).thenReturn(CURRENT_PARAGRAPH);
        when(codeArea.getText(CURRENT_PARAGRAPH - 1)).thenReturn(currentLine);
        when(codeTemplateStorage.getTemplate("n")).thenReturn(Optional.of(new CodeTemplate("null")));
        when(codeArea.getCaretPosition()).thenReturn(2);

        assertTrue(codeTemplateHelper.insertCodeTemplateAtCaretPosition(codeArea));

        verify(codeArea).replaceText(0, 2, "null");
        verify(codeArea, never()).moveTo(anyInt());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "v",
        "for (v",
        "'Hello ' + v",
        "\"Hello \" + v",
        "a + 'Hello ' + v",
        "a + \"Hello \" + v"
    })
    @Order(5)
    void shouldInsertCodeTemplateAndMoveTheCaret(String currentLine) {
        CodeTemplate codeTemplate = new CodeTemplate(format("var %s = ", CURSOR));
        when(codeArea.getCurrentParagraph()).thenReturn(CURRENT_PARAGRAPH);
        when(codeArea.getText(CURRENT_PARAGRAPH - 1)).thenReturn(currentLine);
        when(codeTemplateStorage.getTemplate("v")).thenReturn(Optional.of(codeTemplate));
        when(codeArea.getCaretPosition()).thenReturn(2);

        assertTrue(codeTemplateHelper.insertCodeTemplateAtCaretPosition(codeArea));

        verify(codeArea).replaceText(0, 2, "var  =");
        verify(codeArea).moveTo(4);
    }
}