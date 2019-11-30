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

import org.fxmisc.richtext.CodeArea;

/**
 * Responsible for auto insertion of a paired token for certain tokens
 *
 * @author Vitaliy Dragun
 */
public interface PairedTokensHelper {

    /**
     * Inserts paired token for specified {@code token} if such {@code token} has one
     *
     * @param codeArea {@linkplain CodeArea code area} where such insertion can take place
     * @param token    string token for which paired token may be inserted
     */
    void insertPairedTokenIfAny(CodeArea codeArea, String token);
}
