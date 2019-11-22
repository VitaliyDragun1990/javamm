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

package com.revenat.javamm.ide.ui.pane.action.state;

import com.revenat.javamm.ide.ui.listener.ActionListener;
import com.revenat.javamm.ide.ui.listener.ActionStateManager;

import static com.revenat.javamm.ide.ui.pane.action.state.ActionPaneState.StateName.EDITING;
import static com.revenat.javamm.ide.ui.pane.action.state.ActionPaneState.StateName.RUNNING;

/**
 * @author Vitaliy Dragun
 */
class EditingState extends ActionPaneState {

    EditingState(final ActionStateManager actionStateManager, final ActionListener actionListener) {
        super(actionStateManager, actionListener);
    }

    @Override
    protected void initialize() {
        actionStateManager.disableSaveAction();
        actionStateManager.disableUndoAction();
        actionStateManager.disableRedoAction();
        actionStateManager.disableTerminateAction();

        actionStateManager.enableNewAction();
        actionStateManager.enableOpenAction();
        actionStateManager.enableExitAction();
        actionStateManager.enableFormatAction();
        actionStateManager.enableRunAction();
    }

    @Override
    public void onNew() {
        actionListener.onNewAction();
    }

    @Override
    public void onOpen() {
        actionListener.onOpenAction();
    }

    @Override
    public void onSave() {
        // not available in this state
    }

    @Override
    public void onExit() {
        actionListener.onExitAction();
    }

    @Override
    public void onUndo() {
        // not available in this state
    }

    @Override
    public void onRedo() {
        // not available in this state
    }

    @Override
    public void onFormat() {
        actionListener.onFormatAction();
    }

    @Override
    public void onRun() {
        setCurrentStateByName(RUNNING);
        currentState.initialize();

        actionListener.onRunAction();
    }

    @Override
    public void onRunCompleted() {
        // not available in this state
    }

    @Override
    public void onTerminate() {
        // not available in this state
    }
}
