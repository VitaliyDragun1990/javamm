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

package com.revenat.javamm.ide.component.impl.formatter.policy;

import com.revenat.javamm.ide.component.impl.formatter.FormattingPolicy;
import com.revenat.javamm.ide.component.impl.formatter.model.Line;
import com.revenat.javamm.ide.component.impl.formatter.model.Lines;
import com.revenat.javamm.ide.component.impl.formatter.model.LinesImpl;

import static com.revenat.javamm.code.syntax.SyntaxUtils.trimAllWhitespaceCharacters;

/**
 * @author Vitaliy Dragun
 */
final class LineTrimmingFormattingPolicy implements FormattingPolicy {

    @Override
    public void apply(final Lines lines) {
        final LinesImpl linesImpl = (LinesImpl) lines;
        for (final Line line : linesImpl.getLines()) {
            line.setSignificantContent(trimAllWhitespaceCharacters(line.getOriginalContent()));
        }
    }
}
