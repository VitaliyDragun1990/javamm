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

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

/**
 * @author Vitaliy Dragun
 */
final class EmptyLinesNormalizationFormattingPolicy implements FormattingPolicy {

    @Override
    public void apply(final Lines lines) {
        final LinesImpl linesImpl = (LinesImpl) lines;

        final List<Line> linesToRemove = new ArrayList<>();
        final ListIterator<Line> iter = linesImpl.getLines().listIterator();
        while (iter.hasNext()) {
            final Line current = iter.next();
            if (isEmptyLineAfterAnotherEmptyLine(current, iter) || isLastLineAndEmpty(current, iter)) {
                linesToRemove.add(current);
            }
        }

        linesToRemove.forEach(linesImpl::remove);
        removeLastLineIfEmpty(linesImpl);
    }

    private boolean isEmptyLineAfterAnotherEmptyLine(final Line current, final ListIterator<Line> lines) {
        if (current.isEmpty()) {
            final Optional<Line> previous = getPreviousLineIfAny(lines);
            return previous.isPresent() && previous.get().isEmpty();
        }
        return false;
    }

    private Optional<Line> getPreviousLineIfAny(final ListIterator<Line> lines) {
        // position iterator pointer before current line
        lines.previous();
        if (lines.hasPrevious()) {
            final Line previous = lines.previous();
            // position iterator pointer before current line
            lines.next();
            // position iterator pointer after current line
            lines.next();
            return Optional.of(previous);
        }
        // position iterator pointer after current line
        lines.next();
        return Optional.empty();
    }

    private boolean isLastLineAndEmpty(final Line current, final ListIterator<Line> lines) {
        return !lines.hasNext() && current.isEmpty();
    }

    private void removeLastLineIfEmpty(final LinesImpl lines) {
        lines.getLastLine().ifPresent(line -> {
            if (line.isEmpty()) {
                lines.remove(line);
            }
        });
    }
}
