
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

package com.revenat.javamm.code.fragment.function;

import com.revenat.javamm.code.fragment.FunctionName;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * @author Vitaliy Dragun
 */
public abstract class OverloadableFunctionName implements FunctionName {

    private final String name;

    private final String uniqueName;

    public OverloadableFunctionName(final String name, final int argumentCount) {
        this.name = requireNonNull(name);
        this.uniqueName = FunctionUniqueNameGenerator.generate(name, argumentCount);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof FunctionName)) {
            return false;
        }
        final FunctionName other = (FunctionName) obj;
        if (other instanceof OverloadableFunctionName) {
            return uniqueName.contentEquals(((OverloadableFunctionName) obj).uniqueName);
        } else {
            return name.equals(other.getName());
        }
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public int compareTo(final FunctionName other) {
        if (other instanceof OverloadableFunctionName) {
            return uniqueName.compareTo(((OverloadableFunctionName) other).uniqueName);
        } else {
            return name.compareTo(other.getName());
        }
    }

    @Override
    public String toString() {
        return uniqueName;
    }

    private static final class FunctionUniqueNameGenerator {

        private static final List<String> ALPHABET = generateAlphabet();

        private static String generate(final String name, final int argumentCount) {
            return format("%s(%s)", name, argumentPlaceholdersFor(argumentCount));
        }

        private static String argumentPlaceholdersFor(final int argumentCount) {
            final int[] index = new int[1];
            return Stream.generate(() -> {
                if (index[0] == ALPHABET.size()) {
                    index[0] = 0;
                }
                return ALPHABET.get(index[0]++);
            }).limit(argumentCount)
                .collect(Collectors.joining(","));
        }

        private static List<String> generateAlphabet() {
            final List<String> list = new ArrayList<>();
            for (char ch = 'a'; ch <= 'z'; ch++) {
                list.add(String.valueOf(ch));
            }
            return List.copyOf(list);
        }
    }
}
