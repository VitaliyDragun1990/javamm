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

package com.revenat.javamm.ide.component.impl.formatter;

import static java.util.Objects.requireNonNull;

/**
 * @author Vitaliy Dragun
 */
final class LineIndentationFormattingPolicy implements FormattingPolicy {

    private final String indentation;

    LineIndentationFormattingPolicy(final String indentation) {
        this.indentation = requireNonNull(indentation);
    }

    @Override
    public void apply(final Lines lines) {
        final LinesImpl linesImpl = (LinesImpl) lines;

        int indentationLevel = 0;
        for (final Line line : linesImpl.getLines()) {
            if (line.isOpenBlockLine()) {
                line.setIndentation(indentation.repeat(indentationLevel));
                indentationLevel++;
            } else if (line.isClosingBlockLine()) {
                indentationLevel = Math.max(0, indentationLevel - 1);
                line.setIndentation(indentation.repeat(indentationLevel));
            } else {
                line.setIndentation(indentation.repeat(indentationLevel));
            }
        }
    }
}
