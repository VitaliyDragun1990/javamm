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

import com.revenat.javamm.code.syntax.Delimiters;
import com.revenat.javamm.ide.component.PairedTokensHelper;
import org.fxmisc.richtext.CodeArea;

import java.util.Map;
import java.util.Set;

import static com.revenat.javamm.code.syntax.Delimiters.CLOSING_CURLY_BRACE;
import static com.revenat.javamm.code.syntax.Delimiters.CLOSING_PARENTHESIS;
import static com.revenat.javamm.code.syntax.Delimiters.CLOSING_SQUARE_BRACKET;
import static com.revenat.javamm.code.syntax.Delimiters.DOUBLE_QUOTATION;
import static com.revenat.javamm.code.syntax.Delimiters.OPENING_CURLY_BRACE;
import static com.revenat.javamm.code.syntax.Delimiters.OPENING_PARENTHESIS;
import static com.revenat.javamm.code.syntax.Delimiters.OPENING_SQUARE_BRACKET;
import static com.revenat.javamm.code.syntax.Delimiters.SINGLE_QUOTATION;
import static java.util.Map.entry;

/**
 * @author Vitaliy Dragun
 */
public class PairedTokensHelperImpl implements PairedTokensHelper {

    private static final Map<String, String> PAIRED_TOKENS = Map.ofEntries(
        entry(OPENING_PARENTHESIS, CLOSING_PARENTHESIS),
        entry(OPENING_CURLY_BRACE, CLOSING_CURLY_BRACE),
        entry(OPENING_SQUARE_BRACKET, CLOSING_SQUARE_BRACKET),
        entry(DOUBLE_QUOTATION, DOUBLE_QUOTATION),
        entry(SINGLE_QUOTATION, SINGLE_QUOTATION)
    );

    private boolean isPairedToken(final String token) {
        return PAIRED_TOKENS.containsKey(token);
    }

    @Override
    public void insertPairedTokenIfAny(final CodeArea codeArea, final String token) {
        if (isPairedToken(token)) {
            insertPairedToken(token, codeArea);
        }
    }

    private void insertPairedToken(final String token, final CodeArea codeArea) {
        final int caretPosition = codeArea.getCaretPosition();

        codeArea.insertText(caretPosition, PAIRED_TOKENS.get(token));
        codeArea.moveTo(caretPosition);
    }
}
