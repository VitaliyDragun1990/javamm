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

package com.revenat.javamm.ide.ui.pane;

import com.revenat.javamm.ide.ui.listener.ActionListener;
import com.revenat.javamm.ide.ui.listener.ActionStateManager;
import javafx.scene.layout.VBox;

import static java.util.Objects.requireNonNull;

/**
 * Provides pane with buttons for every possible action that can be performed with the javamm IDE
 *
 * @author Vitaliy Dragun
 */
public final class ActionPane extends VBox implements ActionStateManager {

    private ActionListener actionListener;

    public void setActionListener(final ActionListener actionListener) {
        this.actionListener = requireNonNull(actionListener);
    }

    @Override
    public void enableNewAction() {

    }

    @Override
    public void disableNewAction() {

    }

    @Override
    public void enableOpenAction() {

    }

    @Override
    public void disableOpenAction() {

    }

    @Override
    public void enableSaveAction() {

    }

    @Override
    public void disableSaveAction() {

    }

    @Override
    public void enableExitAction() {

    }

    @Override
    public void disableExitAction() {

    }

    @Override
    public void enableUndoAction() {

    }

    @Override
    public void disableUndoAction() {

    }

    @Override
    public void enableRedoAction() {

    }

    @Override
    public void disableRedoAction() {

    }

    @Override
    public void enableFormatAction() {

    }

    @Override
    public void disableFormatAction() {

    }

    @Override
    public void enableRunAction() {

    }

    @Override
    public void disableRunAction() {

    }

    @Override
    public void enableTerminateAction() {

    }

    @Override
    public void disableTerminateAction() {

    }
}
