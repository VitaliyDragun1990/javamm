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
import org.fxmisc.richtext.CodeArea;

import java.util.Optional;

import static com.revenat.javamm.code.syntax.Delimiters.STRING_DELIMITERS;
import static com.revenat.javamm.code.syntax.SyntaxUtils.isLatinChar;
import static com.revenat.javamm.ide.model.CodeTemplate.CURSOR;
import static com.revenat.javamm.ide.util.TabReplaceUtils.getTabCount;

/**
 * @author Vitaliy Dragun
 */
class CodeTemplateHelperImpl implements CodeTemplateHelper {

    private final CodeTemplateStorage codeTemplateStorage;

    CodeTemplateHelperImpl(final CodeTemplateStorage codeTemplateStorage) {
        this.codeTemplateStorage = codeTemplateStorage;
    }

    @Override
    public boolean insertCodeTemplateAtCaretPosition(final CodeArea codeArea) {
        final int targetParagraph = codeArea.getCurrentParagraph() - 1;
        final String targetLine = codeArea.getText(targetParagraph);

        if (!targetLine.isBlank()) {
            final String lastToken = getLastToken(targetLine);
            if (!lastToken.isEmpty()) {
                final Optional<CodeTemplate> optionalTemplate = codeTemplateStorage.getTemplate(lastToken);
                if (optionalTemplate.isPresent()) {
                    insertCodeTemplate(targetLine, lastToken, optionalTemplate.get(), codeArea);
                    return true;
                }
            }
        }
        return false;
    }

    private void insertCodeTemplate(final String lineToInsert,
                                    final String keyToken,
                                    final CodeTemplate codeTemplate,
                                    final CodeArea codeArea) {
        final String template = codeTemplate.getFormattedCode(getTabCount(lineToInsert)).trim();
        final int caretPosition = codeArea.getCaretPosition();
        final int replaceFromPosition = caretPosition - keyToken.length() - 1;
        final int cursorOffset = template.indexOf(CURSOR);
        if (cursorOffset != -1) {
            codeArea.replaceText(replaceFromPosition, caretPosition, template.replace(CURSOR, ""));
            codeArea.moveTo(replaceFromPosition + cursorOffset);
        } else {
            codeArea.replaceText(replaceFromPosition, caretPosition, template);
        }
    }

    private String getLastToken(final String line) {
        final String strippedLine = stripPrecedingStringLiteralIfAny(line);
        final StringBuilder builder = new StringBuilder();
        for (int i = strippedLine.length() - 1; i >= 0; i--) {
            final char c = strippedLine.charAt(i);
            if (isLatinChar(c)) {
                builder.insert(0, c);
            } else {
                break;
            }
        }
        return builder.toString();
    }

    private String stripPrecedingStringLiteralIfAny(final String line) {
        final StringBuilder builder = new StringBuilder();
        Character stringDelimiter = null;

        for (int i = 0; i < line.length(); i++) {
            final Character ch = line.charAt(i);
            if (STRING_DELIMITERS.contains(ch)) {
                if (ch.equals(stringDelimiter)) { // string literal ends
                    stringDelimiter = null;
                    builder.delete(0, builder.length());
                } else if (stringDelimiter == null) { // new string literal begins
                    stringDelimiter = ch;
                }
            } else if (stringDelimiter == null) { // not inside string literal
                builder.append(ch);
            }
        }

        if (stringDelimiter != null) { // string literal without ending delimiter
            builder.delete(0, builder.length());
        }
        return builder.toString();
    }
}
