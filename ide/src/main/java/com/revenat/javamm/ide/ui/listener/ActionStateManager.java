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
public interface ActionStateManager {

    void enableNewAction();

    void disableNewAction();


    void enableOpenAction();

    void disableOpenAction();


    void enableSaveAction();

    void disableSaveAction();


    void enableExitAction();

    void disableExitAction();


    void enableUndoAction();

    void disableUndoAction();


    void enableRedoAction();

    void disableRedoAction();


    void enableFormatAction();

    void disableFormatAction();


    void enableRunAction();

    void disableRunAction();


    void enableTerminateAction();

    void disableTerminateAction();

}
