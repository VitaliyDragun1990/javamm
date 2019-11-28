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

package com.revenat.javamm.ide.model;

import com.revenat.javamm.ide.util.TabReplaceUtils;

import java.util.Arrays;
import java.util.List;

import static com.revenat.javamm.ide.util.TabReplaceUtils.getLineWithTabs;
import static java.lang.String.join;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toUnmodifiableList;

/**
 * @author Vitaliy Dragun
 */
public final class CodeTemplate {

    public static final String CURSOR = "#";

    private static final String NEW_LINE = "\n";

    private final List<String> lines;

    public CodeTemplate(final String codeTemplate) {
        this(codeTemplate.split(NEW_LINE));
    }

    public CodeTemplate(final String... lines) {
        this.lines = Arrays.stream(lines)
            .map(TabReplaceUtils::replaceTabulations)
            .collect(toUnmodifiableList());
    }

    public String getFormattedCode(final int tabCount) {
        return lines.stream()
            .map(l -> getLineWithTabs(l, tabCount))
            .collect(joining(NEW_LINE));
    }

    @Override
    public String toString() {
        return join(NEW_LINE, lines);
    }
}
