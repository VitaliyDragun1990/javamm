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

/**
 * @author Vitaliy Dragun
 */
class InitialState extends ActionPaneState {

    InitialState(final ActionStateManager actionStateManager, final ActionListener actionListener) {
        super(actionStateManager, actionListener);
    }

    @Override
    protected void initialize() {
        actionStateManager.disableSaveAction();
        actionStateManager.disableUndoAction();
        actionStateManager.disableRedoAction();
        actionStateManager.disableFormatAction();
        actionStateManager.disableRunAction();
        actionStateManager.disableTerminateAction();

        actionStateManager.enableNewAction();
        actionStateManager.enableOpenAction();
        actionStateManager.enableExitAction();
    }

    @Override
    public void onNew() {
        actionListener.onNewAction();

        setCurrentStateByName(EDITING);
        currentState.initialize();
    }

    @Override
    public void onOpen() {
        if (actionListener.onOpenAction()) {
            setCurrentStateByName(EDITING);
            currentState.initialize();
        }
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
        // not available in this state
    }

    @Override
    public void onRun() {
        // not available in this state
    }

    @Override
    public void onTerminate() {
        // not available in this state
    }
}
