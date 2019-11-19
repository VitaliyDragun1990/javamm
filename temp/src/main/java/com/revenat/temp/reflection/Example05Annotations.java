
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

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

/**
 * @author Vitaliy Dragun
 *
 */
@SuppressWarnings("CheckStyle")
public final class Example05Annotations {

    private Example05Annotations() {
    }

    public static void main(final String[] args) throws IllegalAccessException {
        final Example example = new Example();
        System.out.println(example.getList1());
        System.out.println(example.getList2());

        processAnnotations(example, SetEmptyList.class, Example05Annotations::setEmptyList);

        System.out.println(example.getList1());
        System.out.println(example.getList1().getClass());
        System.out.println(example.getList2());
        System.out.println(example.getList2().getClass());
    }

    public static <T extends Annotation> void processAnnotations(final Object instance, final Class<T> clazz,
            final AnnotationHandler<T> handler) throws IllegalAccessException {
        Class<?> currentClass = instance.getClass();
        while (currentClass != Object.class) {
            for (final Field field : currentClass.getDeclaredFields()) {
                final T annotation = field.getAnnotation(clazz);
                if (annotation != null) {
                    processAnnotation(instance, field, annotation, handler);
                }
            }
            currentClass = currentClass.getSuperclass();
        }
    }

    private static <T extends Annotation> void processAnnotation(final Object instance, final Field field, final T annotation,
            final AnnotationHandler<T> annotationHandler ) throws IllegalAccessException {
        ensureAccessible(field, instance);
        annotationHandler.handle(instance, field, annotation);
    }

    private static void ensureAccessible(final Field field, final Object instance) {
        if (!field.canAccess(instance)) {
            field.setAccessible(true);
        }
    }

    private static void setEmptyList(final Object instance, final Field field, final SetEmptyList annotation) throws IllegalAccessException {
        final List<?> emptyList = annotation.useNewStyle() ? List.of() : Collections.emptyList();
        field.set(instance, emptyList);
    }

    @FunctionalInterface
    public interface AnnotationHandler<T extends Annotation> {
        void handle(Object instance, Field field, T annotation) throws IllegalAccessException;
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface SetEmptyList {

        boolean useNewStyle() default true;
    }

    public static final class Example {

        @SetEmptyList
        private List<String> list1;

        @SetEmptyList(useNewStyle = false)
        private List<String> list2;

        public List<String> getList1() {
            return list1;
        }

        public List<String> getList2() {
            return list2;
        }
    }
}
