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

import com.revenat.javamm.ide.component.PairedTokensHelper;
import com.revenat.juinit.addons.ReplaceCamelCase;
import org.fxmisc.richtext.CodeArea;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("a paired token helper")
@ExtendWith(MockitoExtension.class)
class PairedTokensHelperImplTest {

    private final PairedTokensHelper pairedTokensHelper = new PairedTokensHelperImpl();

    @Mock
    private CodeArea codeArea;

    @ParameterizedTest
    @CsvSource({
        "[,]",
        "{,}",
        "(,)",
        "\",\"",
        "'''',''''"  // escaped single quotation mark '
    })
    void shouldInsertPairedToken(final String token, final String expectedPairedToken) {
        when(codeArea.getCaretPosition()).thenReturn(1);

        pairedTokensHelper.insertPairedTokenIfAny(codeArea, token);

        verify(codeArea).insertText(1, expectedPairedToken);
        verify(codeArea).moveTo(1);
    }
}