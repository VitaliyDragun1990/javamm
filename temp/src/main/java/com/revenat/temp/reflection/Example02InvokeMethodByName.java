
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

import com.revenat.javamm.code.fragment.SourceLine;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Vitaliy Dragun
 *
 */
@SuppressWarnings("CheckStyle")
public final class Example02InvokeMethodByName {

    private Example02InvokeMethodByName() {
    }

     public static void main(final String[] args)
             throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        final SourceLine sourceLine = new SourceLine("module1", 5, List.of("hello"));

        System.out.println(invokeMethodByName(sourceLine, "getModuleName"));
        System.out.println(sourceLine.getModuleName());
    }

    public static Object invokeMethodByName(final Object target, final String methodName)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        final Method method = target.getClass().getDeclaredMethod(methodName);
        return method.invoke(target);
    }
}
