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
class RunningState extends ActionPaneState {

    RunningState(final ActionStateManager actionStateManager, final ActionListener actionListener) {
        super(actionStateManager, actionListener);
    }

    @Override
    protected void initialize() {
        actionStateManager.disableNewAction();
        actionStateManager.disableOpenAction();
        actionStateManager.disableExitAction();
        actionStateManager.disableSaveAction();
        actionStateManager.disableUndoAction();
        actionStateManager.disableRedoAction();
        actionStateManager.disableFormatAction();
        actionStateManager.disableRunAction();

        actionStateManager.enableTerminateAction();
    }

    @Override
    public void onEvent(final ActionEvent actionEvent) {
        switch (actionEvent) {
            case RUN_COMPLETED:
                onRunCompleted();
                break;
            case TERMINATED:
                onTerminate();
                break;
        }
    }

    private void onTerminate() {
        actionListener.onTerminateAction();

        setCurrentStateByName(EDITING);
        currentState.initialize();
    }

    private void onRunCompleted() {
        setCurrentStateByName(EDITING);
        currentState.initialize();
    }
}
