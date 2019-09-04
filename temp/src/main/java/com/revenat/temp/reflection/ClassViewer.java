
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

package com.revenat.temp.reflection;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

/**
 * --add-opens java.base/java.lang=temp
 *
 * @author Vitaliy Dragun
 *
 */
public class ClassViewer {

    private final Object objectToInspect;

    public ClassViewer(final Object objectToInspect) {
        this.objectToInspect = objectToInspect;
    }

    public static void main(final String[] args) throws IllegalAccessException {
        new ClassViewer("Hello").printAll(System.out);
        new ClassViewer(1).printAll(System.out);
        new ClassViewer(true).printAll(System.out);
        new ClassViewer(new Object()).printAll(System.out);
    }

    public void printAll(final OutputStream out) throws IllegalAccessException {
        final PrintWriter writer = new PrintWriter(out);
        try {
            inspectClass(writer);
            inspectFields(writer);
            inspectConstructors(writer);
            inspectMethods(writer);
        } finally {
            writer.flush();
        }
    }

    private void inspectClass(final PrintWriter writer) {
        writer.println("###################### CLASS ################################");
        writer.println(objectToInspect.getClass().getName());
    }

    private void inspectFields(final PrintWriter writer) throws IllegalAccessException {
        writer.println("---------------------- FIELDS -------------------------------");
        for (final Field field :objectToInspect.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            writer.println(format("%s%s %s = %s",
                    getVisibilityModifier(field.getModifiers()),
                    getType(field.getType()),
                    field.getName(),
                    valueAsString(field.get(objectToInspect))));
        }
    }

    private void inspectConstructors(final PrintWriter writer) {
        writer.println("------------------- CONSTRUCTORS ----------------------------");
        for (final Constructor<?> constructor : objectToInspect.getClass().getDeclaredConstructors()) {
            writer.println(format("%s%s(%s) %s",
                    getVisibilityModifier(constructor.getModifiers()),
                    constructor.getName(),
                    getTypes(constructor.getParameterTypes()),
                    getThrows(constructor.getExceptionTypes())
                    ));
        }
    }

    private void inspectMethods(final PrintWriter writer) {
        writer.println("------------------- METHODS --------------------------------");
        for (final Method method : objectToInspect.getClass().getDeclaredMethods()) {
            writer.println(format("%s%s(%s) %s",
                    getVisibilityModifier(method.getModifiers()),
                    method.getName(),
                    getTypes(method.getParameterTypes()),
                    getThrows(method.getExceptionTypes())
                    ));
        }
    }

    private String getThrows(final Class<?>[] exceptionTypes) {
        final String result = getTypes(exceptionTypes);
        return result.isEmpty() ? "" : "throws " + result;
    }

    private String getTypes(final Class<?>[] classes) {
        return Arrays.stream(classes)
                .map(this::getType)
                .collect(joining(", "));
    }

    private String getType(final Class<?> clazz) {
        if (clazz.isArray()) {
            return clazz.getComponentType().getName() + "[]";
        } else {
            return clazz.getName();
        }
    }

    private String valueAsString(final Object value) {
        if (value.getClass().isArray()) {
            final Object[] array = new Object[Array.getLength(value)];
            for (int i = 0; i < array.length; i++) {
                array[i] = Array.get(value, i);
            }
            return Arrays.toString(array);
        } else {
            return String.valueOf(value);
        }
    }

    private String getVisibilityModifier(final int modifiers) {
        if (Modifier.isPublic(modifiers)) {
            return "public";
        } else if (Modifier.isProtected(modifiers)) {
            return "protected";
        } else if (Modifier.isPrivate(modifiers)) {
            return "private";
        } else {
            return "";
        }
    }
}
