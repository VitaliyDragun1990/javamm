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

import com.revenat.javamm.code.util.ExceptionUtils.WrappedCheckedException;
import com.revenat.juinit.addons.ReplaceCamelCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("exception utils")
class ExceptionUtilsTest {

    @Test
    void shouldWrapThrownExceptionIfItsChecked() {
        final WrappedCheckedException e = assertThrows(WrappedCheckedException.class,
            () -> ExceptionUtils.wrapCheckedException(() -> {
                throw new IOException("test checked exception");
            }));

        assertThat(e.getMessage(), containsString("test checked exception"));
        assertThat(e.getCause().getClass(), equalTo(IOException.class));
    }

    @Test
    void shouldNotWrapThrownExceptionIfItsUnchecked() {
        final RuntimeException e = assertThrows(RuntimeException.class,
            () -> ExceptionUtils.wrapCheckedException(() -> {
                throw new RuntimeException("test unchecked exception");
            }));

        assertThat(e.getMessage(), containsString("test unchecked exception"));
    }
}