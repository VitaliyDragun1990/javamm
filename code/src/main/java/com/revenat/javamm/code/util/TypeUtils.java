
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

/**
 * @author Vitaliy Dragun
 */
public final class TypeUtils {

    private TypeUtils() {
    }

    public static String getType(final Object value) {
        return value != null ? getType(value.getClass()) : "null";
    }

    public static String getType(final Class<?> value) {
        return value != null ? value.getSimpleName().toLowerCase() : "null";
    }

    /**
     * Checks whether provided {@code values} all have specified {@code type}
     *
     * @param type   specific type to check values for
     * @param values some values to check for type
     * @return {@code true} if all specified {@code values} have specified
     * {@code type}, {@code false} otherwise.
     * @apiNote if any value from the provided {@code values} is null, {@code false}
     * will be returned
     */
    public static boolean confirmType(final Class<?> type, final Object... values) {
        for (final Object value : values) {
            if (value == null || !type.isAssignableFrom(value.getClass())) {
                return false;
            }
        }
        return true;
    }
}
