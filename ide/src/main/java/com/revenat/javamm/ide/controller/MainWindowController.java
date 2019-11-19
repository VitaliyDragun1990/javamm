
/*
 * Copyright (c) 2019. http://devonline.academy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.revenat.javamm.ide.controller;

import com.revenat.javamm.ide.ui.listener.ActionListener;
import com.revenat.javamm.ide.ui.pane.ActionPane;
import com.revenat.javamm.ide.ui.pane.CodeTabPane;
import com.revenat.javamm.ide.ui.pane.ConsolePane;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

/**
 * @author Vitaliy Dragun
 *
 */
public class MainWindowController implements ActionListener {

    @FXML
    private ActionPane actionPane;

    @FXML
    private CodeTabPane codeTabPane;

    @FXML
    private ConsolePane consolePane;

    @FXML
    public void onCloseAction(final ActionEvent actionEvent) {
        System.exit(0);
    }

    @Override
    public void onNewAction() {

    }

    @Override
    public boolean onOpenAction() {
        return false;
    }

    @Override
    public boolean onSaveAction() {
        return false;
    }

    @Override
    public boolean onExitAction() {
        return false;
    }

    @Override
    public void onUndoAction() {

    }

    @Override
    public void onRedoAction() {

    }

    @Override
    public void onFormatAction() {

    }

    @Override
    public void onRunAction() {

    }

    @Override
    public void onTerminateAction() {

    }
}
