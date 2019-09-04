
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

import com.revenat.javamm.code.fragment.SourceCode;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Vitaliy Dragun
 *
 */
public class Example04CreateMock {

    private Example04CreateMock() {
    }

    public static void main(final String[] args) {
        final SourceCode sourceCode = createMock(new Class[] {SourceCode.class}, new SourceCodeInvocationHandler(new Object()));

        System.out.println(sourceCode.getModuleName());
        System.out.println(sourceCode.getLines());
        System.out.println(sourceCode.hashCode());
        System.out.println(sourceCode.equals(sourceCode));
        System.out.println(sourceCode.toString());
        System.out.println(sourceCode.getClass());
    }

    @SuppressWarnings("unchecked")
    public static <T> T createMock(final Class<?>[] interfaces, final InvocationHandler invocationHandler) {
        return (T) Proxy.newProxyInstance(Example04CreateMock.class.getClassLoader(), interfaces, invocationHandler);
    }

    private abstract static class AbstractInvocationHandler implements InvocationHandler {
        private final Object monitor;
        private final Map<String, ResultProducer> methodHandlerMap;

        AbstractInvocationHandler(final Object monitor) {
            this.monitor = monitor;
            methodHandlerMap = new HashMap<>();
        }

        protected void addMethodHandler(final String methodName, final ResultProducer methodResult) {
            methodHandlerMap.put(methodName, methodResult);
        }

        @Override
        public final Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            return methodHandlerMap
                    .getOrDefault(method.getName(), (p, a) -> invokeMonitorMethod(method, args))
                    .produce(proxy, args);
        }

        private Object invokeMonitorMethod(final Method method, final Object[] args) {
            try {
                return method.invoke(monitor, args);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        @FunctionalInterface
        protected interface ResultProducer {
            Object produce(Object proxy, Object[] args);
        }

    }

    private static class SourceCodeInvocationHandler extends AbstractInvocationHandler {

        SourceCodeInvocationHandler(final Object monitor) {
            super(monitor);

            addMethodHandler("getModuleName", (p, args) -> "ProxyModuleName");
            addMethodHandler("getLines", (p, args) -> List.of("function main() {", "println('Hello world')", "}"));
            addMethodHandler("equals", (p, args) -> p == args[0]);
            addMethodHandler("hashCode", (p, args) -> System.identityHashCode(p));
            addMethodHandler("toString", (p, args) -> "SourceCode proxy");
        }
    }
}
