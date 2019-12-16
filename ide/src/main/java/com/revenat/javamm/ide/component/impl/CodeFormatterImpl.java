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

import com.revenat.javamm.ide.component.CodeFormatter;
import com.revenat.javamm.ide.component.impl.formatter.model.Lines;
import com.revenat.javamm.ide.component.impl.formatter.model.LinesFactory;
import com.revenat.javamm.ide.component.impl.formatter.policy.FormattingPolicyProvider;

import java.util.Collections;
import java.util.List;

/**
 * @author Vitaliy Dragun
 */
class CodeFormatterImpl implements CodeFormatter {

    @Override
    public List<String> format(final List<String> sourceCode) {
        final Lines lines = LinesFactory.buildFrom(sourceCode);
        lines.accept(FormattingPolicyProvider.getFormattingPolicy());
        return Collections.unmodifiableList(lines.lines());
    }
}
