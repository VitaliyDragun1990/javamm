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

package com.revenat.javamm.code.util;

/**
 * @author Vitaliy Dragun
 */
public final class ExceptionUtils {

    private ExceptionUtils() {
    }

    @SuppressWarnings("checkstyle:IllegalCatch")
    public static <T> T wrapCheckedException(final RunnableFragment<T> runnableFragment) {
        try {
            return runnableFragment.run();
        } catch (final RuntimeException e) {
            throw e;
        } catch (final Exception e) {
            throw new WrappedCheckedException(e);
        }
    }

    @FunctionalInterface
    public interface RunnableFragment<T> {

        T run() throws Exception;
    }

    static final class WrappedCheckedException extends RuntimeException {
        private WrappedCheckedException(final Exception cause) {
            super(cause);
        }
    }
}
