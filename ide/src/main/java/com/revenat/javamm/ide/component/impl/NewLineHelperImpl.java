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
import com.revenat.javamm.ide.model.CodeTemplate;
import org.fxmisc.richtext.CodeArea;

import static com.revenat.javamm.code.syntax.Delimiters.OPENING_CURLY_BRACE;
import static com.revenat.javamm.code.syntax.Delimiters.STRING_DELIMITERS;
import static com.revenat.javamm.ide.model.CodeTemplate.CURSOR;
import static com.revenat.javamm.ide.util.TabReplaceUtils.getLineWithTabs;
import static com.revenat.javamm.ide.util.TabReplaceUtils.getTabCount;

/**
 * @author Vitaliy Dragun
 */
public class NewLineHelperImpl implements NewLineHelper {

    private final CodeTemplate BLOCK_CODE_TEMPLATE = new CodeTemplate("\t" + CURSOR, "");

    @Override
    public void insertNewLine(final CodeArea codeArea) {
        final String previousLine = codeArea.getText(codeArea.getCurrentParagraph() - 1);
        final int caretPosition = codeArea.getCaretPosition();

        final int previousLineTabCount = getTabCount(previousLine);
        if (openBlockLine(previousLine)) {
            insertFirstNewLineInBlock(previousLineTabCount, caretPosition, codeArea);
        } else {
            insertNewLine(previousLineTabCount, caretPosition, codeArea);
        }
    }

    private void insertFirstNewLineInBlock(final int previousLineTabCount, final int caretPosition, final CodeArea codeArea) {
        String newLineContent = "\n" + BLOCK_CODE_TEMPLATE.getFormattedCode(previousLineTabCount);
        int cursorIndex = newLineContent.indexOf(CURSOR);
        codeArea.replaceText(caretPosition - 1, caretPosition, newLineContent.replaceAll(CURSOR, ""));
        codeArea.moveTo(caretPosition - 1 + cursorIndex);
    }

    private void insertNewLine(final int previousLineTabCount, final int caretPosition, final CodeArea codeArea) {
        String newLineContent = "\n" + getLineWithTabs("", previousLineTabCount);
        codeArea.replaceText(caretPosition - 1, caretPosition, newLineContent);
    }

    private boolean openBlockLine(final String line) {
        return !endsWithUnclosedStringLiteral(line) && line.endsWith(OPENING_CURLY_BRACE);
    }

    private boolean endsWithUnclosedStringLiteral(final String line) {
        Character stringDelimiter = null;

        for (int i = 0; i < line.length(); i++) {
            final Character ch = line.charAt(i);
            if (STRING_DELIMITERS.contains(ch)) {
                if (ch.equals(stringDelimiter)) { // string literal ends
                    stringDelimiter = null;
                } else if (stringDelimiter == null) { // new string literal begins
                    stringDelimiter = ch;
                }
            }
        }

        return stringDelimiter != null;
    }
}
