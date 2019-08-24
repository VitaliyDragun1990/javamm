
/*
 * Copyright (c) 2019. http://devonline.academy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.revenat.javamm.code.fragment;

import java.util.List;
import java.util.Objects;

/**
 * Represents line of the compiled code.
 *
 * @author Vitaliy Dragun
 *
 */
public class SourceLine implements CompiledCodeFragment, Comparable<SourceLine> {
    public static final SourceLine EMPTY_SOURCE_LINE = new SourceLine("<UNDEFINED>", 0, List.of());

    private final String moduleName;

    private final int lineNumber;

    private final List<String> tokens;

    /**
     * Creates new {@link SourceLine} instance.
     *
     * @param moduleName name of the file where this source line is declared
     * @param lineNumber number of the line this source line represents inside the
     *                   module
     * @param tokens     array of meaningful tokens represented by this source line.
     */
    public SourceLine(final String moduleName, final int lineNumber, final List<String> tokens) {
        super();
        this.moduleName = moduleName;
        this.lineNumber = lineNumber;
        this.tokens = tokens;
    }

    public String getCommand() {
        return String.join(" ", tokens);
    }

    public String getToken(final int index) {
        return tokens.get(index);
    }

    public String getFirst() {
        return getToken(0);
    }

    public String getLast() {
        return getToken(getTokenCount() - 1);
    }

    public int getTokenCount() {
        return tokens.size();
    }

    public List<String> subList(final int start, final int end) {
        return tokens.subList(start, end);
    }

    public List<String> subList(final int start) {
        return subList(start, getTokenCount());
    }

    public boolean contains(final String token) {
        return tokens.contains(token);
    }

    public int indexOf(final String token) {
        return tokens.indexOf(token);
    }


    @Override
    public int hashCode() {
        return Objects.hash(lineNumber, moduleName);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SourceLine other = (SourceLine) obj;
        return lineNumber == other.lineNumber && Objects.equals(moduleName, other.moduleName);
    }

    public String getModuleName() {
        return moduleName;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public List<String> getTokens() {
        return tokens;
    }

    @Override
    public int compareTo(final SourceLine o) {
        final int result = moduleName.compareTo(o.getModuleName());
        if (result != 0) {
            return result;
        } else {
            return Integer.compare(lineNumber, o.getLineNumber());
        }
    }

    @Override
    public String toString() {
        return String.format("[%s:%s] -> %s", moduleName, lineNumber, getCommand());
    }

}
