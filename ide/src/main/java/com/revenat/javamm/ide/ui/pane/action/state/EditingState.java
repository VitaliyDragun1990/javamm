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

import static com.revenat.javamm.ide.ui.pane.action.state.ActionPaneState.StateName.INITIAL;
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
        actionStateManager.disableTerminateAction();
        initializeUndoRedo();


        actionStateManager.enableNewAction();
        actionStateManager.enableOpenAction();
        actionStateManager.enableExitAction();
        actionStateManager.enableFormatAction();
        actionStateManager.enableRunAction();
    }

    private void initializeUndoRedo() {
        if (isUndoActionEnabled()) {
            actionStateManager.enableUndoAction();
        } else {
            actionStateManager.disableUndoAction();
        }
        if (isRedoActionEnabled()) {
            actionStateManager.enableRedoAction();
        } else {
            actionStateManager.disableRedoAction();
        }
    }

    @SuppressWarnings("checkstyle:MethodLength")
    @Override
    public void onEvent(final ActionEvent actionEvent) {
        switch (actionEvent) {
            case NEW:
                onNew();
                break;
            case OPEN:
                onOpen();
                break;
            case EXIT:
                onExit();
                break;
            case SAVE:
                onSave();
                break;
            case FORMAT:
                onFormat();
                break;
            case RUN:
                onRun();
                break;
            case ALL_TABS_CLOSED:
                onAllTabsClosed();
                break;
            case TAB_CONTENT_CHANGED_WITH_ONLY_UNDO_AVAILABLE:
                onTabContentChangedWithUndoAvailable();
                break;
            case TAB_CONTENT_CHANGED_WITH_ONLY_REDO_AVAILABLE:
                onTabContentChangedWithOnlyRedoAvailable();
                break;
            case TAB_CONTENT_CHANGED_WITH_UNDO_REDO_AVAILABLE:
                onTabContentChangedWithUndoRedoAvailable();
                break;
            case TAB_CONTENT_UNCHANGED:
                onTabContentUnchanged();
                break;
            case TAB_CONTENT_SAVED:
                onTabContentSaved();
                break;
            case UNDO:
                onUndo();
                break;
            case REDO:
                onRedo();
                break;
        }
    }

    private void onNew() {
        actionListener.onNewAction();
    }

    private void onOpen() {
        actionListener.onOpenAction();
    }

    private void onExit() {
        actionListener.onExitAction();
    }

    private void onSave() {
        actionListener.onSaveAction();
    }

    private void onFormat() {
        actionListener.onFormatAction();
    }

    private void onRun() {
        setCurrentStateByName(RUNNING);
        currentState.initialize();

        actionListener.onRunAction();
    }

    private void onAllTabsClosed() {
        setCurrentStateByName(INITIAL);
        currentState.initialize();
    }

    private void onTabContentChangedWithUndoAvailable() {
        actionStateManager.enableSaveAction();
        actionStateManager.enableUndoAction();
        undoActionEnabled();
        actionStateManager.disableRedoAction();
        redoActionDisabled();
    }

    private void onTabContentChangedWithOnlyRedoAvailable() {
        actionStateManager.enableSaveAction();
        actionStateManager.disableUndoAction();
        undoActionDisabled();
        actionStateManager.enableRedoAction();
        redoActionEnabled();
    }

    private void onTabContentChangedWithUndoRedoAvailable() {
        actionStateManager.enableSaveAction();
        actionStateManager.enableUndoAction();
        undoActionEnabled();
        actionStateManager.enableRedoAction();
        redoActionEnabled();
    }

    private void onTabContentUnchanged() {
        actionStateManager.disableSaveAction();
        actionStateManager.disableUndoAction();
        undoActionDisabled();
        actionStateManager.disableRedoAction();
        redoActionDisabled();
    }

    private void onTabContentSaved() {
        actionStateManager.disableSaveAction();
    }

    private void onUndo() {
        if (!actionListener.onUndoAction()) {
            actionStateManager.disableUndoAction();
            undoActionDisabled();
        }
        actionStateManager.enableRedoAction();
        redoActionEnabled();
    }

    private void onRedo() {
        if (!actionListener.onRedoAction()) {
            actionStateManager.disableRedoAction();
            redoActionDisabled();
        }
        actionStateManager.enableUndoAction();
        undoActionEnabled();
    }
}
