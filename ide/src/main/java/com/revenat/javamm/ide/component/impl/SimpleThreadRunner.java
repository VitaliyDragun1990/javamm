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

import static java.util.Objects.requireNonNull;

/**
 * @author Vitaliy Dragun
 */
public class SimpleThreadRunner implements ThreadRunner {

    private final ThreadProvider threadProvider;

    private Thread runnerThread;

    public SimpleThreadRunner(final ThreadProvider threadProvider) {
        this.threadProvider = requireNonNull(threadProvider);
    }

    @Override
    public boolean isRunning() {
        return runnerThread != null && runnerThread.isAlive();
    }

    @Override
    public void terminate() {
        checkCondition(runnerThread != null, "Runner thread not found");
        runnerThread.interrupt();
    }

    @Override
    public void run(final Runnable job, final String jobThreadName) {
        checkCondition(runnerThread == null, "Runner thread already exists");
        runnerThread = threadProvider.createNewThread(job, jobThreadName);
        runnerThread.start();
    }

    private void checkCondition(final boolean conditionResult, final String failureMsg) {
        if (!conditionResult) {
            throw new IllegalStateException(failureMsg);
        }
    }

    @FunctionalInterface
    public interface ThreadProvider {

        Thread createNewThread(Runnable job, String threadName);
    }
}
