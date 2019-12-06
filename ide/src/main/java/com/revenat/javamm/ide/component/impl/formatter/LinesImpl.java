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
import java.util.Optional;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

/**
 * @author Vitaliy Dragun
 */
final class LinesImpl implements Lines {

    private final List<Line> lines;

    LinesImpl(final List<String> lines) {
        this.lines = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            this.lines.add(new Line(i + 1, lines.get(i)));
        }
    }

    List<Line> getLines() {
        return List.copyOf(lines);
    }

    Optional<Line> getLastLine() {
        if (lines.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(lines.get(lines.size() - 1));
    }

    void remove(final Line line) {
        lines.remove(line);
    }

    @Override
    public void accept(final FormattingPolicy formattingPolicy) {
        formattingPolicy.apply(this);
    }

    @Override
    public List<String> lines() {
        return lines.stream()
            .map(Line::toString)
            .collect(toList());
    }
}
