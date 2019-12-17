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

package com.revenat.javamm.ide.component.impl;

import com.revenat.juinit.addons.ReplaceCamelCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static com.revenat.javamm.ide.test.helper.CustomAsserts.assertErrorMessageContains;
import static com.revenat.javamm.ide.test.helper.ThreadUtils.waitForThreadToDie;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("a simple thread runner")
class SimpleThreadRunnerTest {

    private static final String ANY_THREAD_NAME = "Test Thread";

    private static final Runnable ANY_RUNNABLE = () -> {
    };

    private final Thread workingThreadStub = new Thread(ANY_RUNNABLE, ANY_THREAD_NAME);

    private SimpleThreadRunner threadRunner;

    @BeforeEach
    void setUp() {
        threadRunner = new SimpleThreadRunner((job, name) -> workingThreadStub);
    }

    @Test
    @Order(1)
    void shouldFailIfAlreadyInvoked() {
        threadRunner.run(ANY_RUNNABLE, ANY_THREAD_NAME);

        final IllegalStateException e =
            assertThrows(IllegalStateException.class, () -> threadRunner.run(ANY_RUNNABLE, ANY_THREAD_NAME));

        assertErrorMessageContains(e, "Runner thread already exists");
    }

    @Test
    @Order(2)
    void canBeTerminatedSeveralTimesWithoutFailure() throws InterruptedException {
        threadRunner.run(waitUntilInterrupted(), ANY_THREAD_NAME);

        threadRunner.terminate();

        waitForThreadToDie(workingThreadStub);
        assertDoesNotThrow(threadRunner::terminate);
    }

    @Test
    @Order(3)
    void shouldFailToTerminatedIfNotStartedBefore() {
        final IllegalStateException e
            = assertThrows(IllegalStateException.class, threadRunner::terminate);

        assertErrorMessageContains(e, "Runner thread not found");
    }

    @Test
    @Order(4)
    void shouldReturnFalseIfNotStartedYet() {
        assertFalse(threadRunner.isRunning());
    }

    @Test
    @Order(5)
    void shouldReturnFalseIfStartedAndAlreadyFinished() throws InterruptedException {
        threadRunner.run(ANY_RUNNABLE, ANY_THREAD_NAME);
        waitForThreadToDie(workingThreadStub);

        assertFalse(threadRunner.isRunning());
    }

    @Test
    @Order(6)
    void shouldReturnTrueIfRunning() {
        threadRunner.run(ANY_RUNNABLE, ANY_THREAD_NAME);
        assertTrue(threadRunner.isRunning());
    }

    @Test
    @Order(7)
    void shouldReturnFalseIfNotRunning() {
        assertFalse(threadRunner.isRunning());
    }

    private Runnable waitUntilInterrupted() {
        return () -> {
            synchronized (this) {
                try {
                    wait();
                } catch (final InterruptedException e) {
                    // do nothing
                }
            }
        };
    }

}