
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

package com.revenat.javamm.code.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author Vitaliy Dragun
 *
 */
public final class SortedOperatorMapBuilder {

    private final Collection<String> operators;

    public SortedOperatorMapBuilder(final Collection<String> operators) {
        this.operators = operators;
    }

    /*
        "+"     ->      "+=", "++"
        ">"     ->      ">>>=", ">>>", ">>=", ">=", ">>"
     */
    public Map<String, SortedSet<String>> buildUnmodifiableMapWithSortedValues(final Comparator<String> comparator) {
        final Map<String, SortedSet<String>> operatorMap = new HashMap<>();

        for (final String operator : operators) {
            final String key = String.valueOf(operator.charAt(0));
            final SortedSet<String> set = operatorMap.computeIfAbsent(key, k -> new TreeSet<>(comparator));
            if (!key.equals(operator)) {
                set.add(operator);
            }
        }
        return unmodifiableMap(operatorMap);
    }

    private Map<String, SortedSet<String>> unmodifiableMap(final Map<String, SortedSet<String>> map) {
        for (final Map.Entry<String, SortedSet<String>> entry : map.entrySet()) {
            map.put(entry.getKey(), Collections.unmodifiableSortedSet(entry.getValue()));
        }
        return Map.copyOf(map);
    }
}