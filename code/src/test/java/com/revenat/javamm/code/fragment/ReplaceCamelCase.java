
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

import java.lang.reflect.Method;

import org.junit.jupiter.api.DisplayNameGenerator;

/**
 * @author Vitaliy Dragun
 *
 */
public class ReplaceCamelCase extends DisplayNameGenerator.Standard {

    public ReplaceCamelCase() {
    }

    @Override
    public String generateDisplayNameForClass(final Class<?> testClass) {
        return replaceCamelCase(super.generateDisplayNameForClass(testClass));
    }

    @Override
    public String generateDisplayNameForNestedClass(final Class<?> nestedClass) {
        return replaceCamelCase(super.generateDisplayNameForNestedClass(nestedClass));
    }

    @Override
    public String generateDisplayNameForMethod(final Class<?> testClass, final Method testMethod) {
        return replaceCamelCase(testMethod.getName() + DisplayNameGenerator.parameterTypesAsString(testMethod));
    }

    protected String replaceCamelCase(final String camelCase) {
        return separateDigits(replaceCamelCaseWithWhitespace(camelCase));
    }

    private String replaceCamelCaseWithWhitespace(final String camelCase) {
        final StringBuilder result = new StringBuilder();
        result.append(camelCase.charAt(0));

        for (int i = 1; i < camelCase.length(); i++) {
            if (Character.isUpperCase(camelCase.charAt(i))) {
                result.append(' ');
                result.append(Character.toLowerCase(camelCase.charAt(i)));
            } else {
                result.append(camelCase.charAt(i));
            }
        }
        return result.toString();
    }

    private String separateDigits(final String name) {
        return name.replaceAll("([0-9]+)", " $1");
    }
}
