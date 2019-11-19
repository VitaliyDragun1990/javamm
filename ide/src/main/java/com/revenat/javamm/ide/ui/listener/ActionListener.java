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

package com.revenat.javamm.ide.ui.listener;

/**
 * @author Vitaliy Dragun
 */
public interface ActionListener {

    /**
     * Creates new empty file for javamm source code
     */
    void onNewAction();

    /**
     * Opens existent file with javamm source code
     *
     * @return {@code true} if 'open' action completes successfully, {@code false} otherwise
     */
    boolean onOpenAction();

    /**
     * Saves javamm source code into file
     *
     * @return {@code true} if 'save' action completes successfully, {@code false} otherwise
     */
    boolean onSaveAction();

    /**
     * Leaves javamm IDE
     *
     * @return {@code true} if 'exit' action completes successfully, {@code false} otherwise
     */
    boolean onExitAction();

    /**
     * Undoing last performing actions
     */
    void onUndoAction();

    /**
     * Redoing last performing actions
     */
    void onRedoAction();

    /**
     * Formats javamm source code in the currently active tab
     */
    void onFormatAction();

    /**
     * Runs javamm source code via javamm virtual machine
     */
    void onRunAction();

    /**
     * External termination of execution of javamm virtual machine
     */
    void onTerminateAction();
}
