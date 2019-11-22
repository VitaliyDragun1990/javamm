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

package com.revenat.javamm.ide.component;

/**
 * Responsible for managing lifecycle of {@linkplain com.revenat.javamm.vm.VirtualMachine virtual machine}
 * component
 *
 * @author Vitaliy Dragun
 */
public interface VirtualMachineRunner {

    /**
     * Runs {@linkplain com.revenat.javamm.vm.VirtualMachine virtual machine}
     *
     * @param listener {@linkplain com.revenat.javamm.vm.VirtualMachine virtual machine} lifecycle listener
     */
    void run(VirtualMachineRunCompletedListener listener);

    /**
     * Allows to check {@linkplain com.revenat.javamm.vm.VirtualMachine virtual machine} current status
     *
     * @return {@code true} if {@linkplain com.revenat.javamm.vm.VirtualMachine virtual machine} is running,
     * {@code false} otherwise
     */
    boolean isRunning();

    /**
     * Terminates {@linkplain com.revenat.javamm.vm.VirtualMachine virtual machine} execution
     */
    void terminate();

    /**
     * {@linkplain com.revenat.javamm.vm.VirtualMachine virtual machine} lifecycle listener
     */
    interface VirtualMachineRunCompletedListener {

        /**
         * Called after {@linkplain com.revenat.javamm.vm.VirtualMachine virtual machine}
         * completes its execution
         *
         * @param status indicates in what way {@linkplain com.revenat.javamm.vm.VirtualMachine virtual machine}
         *               completes its execution
         */
        void onRunCompleted(CompleteStatus status);
    }

    enum CompleteStatus {

        SUCCESS,

        SYNTAX_ERROR,

        RUNTIME_ERROR,

        INTERNAL_ERROR,

        TERMINATED;
    }
}
