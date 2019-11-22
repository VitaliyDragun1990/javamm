
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

import com.revenat.javamm.code.component.Console;
import com.revenat.javamm.ide.component.ComponentFactoryProvider;
import com.revenat.javamm.ide.component.VirtualMachineRunner;
import com.revenat.javamm.ide.component.VirtualMachineRunner.CompleteStatus;
import com.revenat.javamm.ide.component.VirtualMachineRunner.VirtualMachineRunCompletedListener;
import com.revenat.javamm.ide.ui.listener.ActionListener;
import com.revenat.javamm.ide.ui.pane.action.ActionPane;
import com.revenat.javamm.ide.ui.pane.code.CodeTabPane;
import com.revenat.javamm.ide.ui.pane.console.ConsolePane;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;

/**
 * @author Vitaliy Dragun
 *
 */
public class MainWindowController implements ActionListener, VirtualMachineRunCompletedListener {

    @FXML
    private ActionPane actionPane;

    @FXML
    private CodeTabPane codeTabPane;

    @FXML
    private ConsolePane consolePane;

    private VirtualMachineRunner virtualMachineRunner;

    /**
     * Controller lifecycle method. Will be called by javafx framework
     * after this controller has been fully initialized
     */
    @FXML
    private void initialize() {
        actionPane.setActionListener(this);
    }

    @FXML
    public void onCloseAction(final ActionEvent actionEvent) {
        System.exit(0);
    }

    @Override
    public void onNewAction() {
        codeTabPane.newCodeEditor();
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
        if (actionPane.isExitActionDisabled()) {
            return false;
        }
        getStage().close();
        return true;
    }

    protected Stage getStage() {
        return (Stage) actionPane.getScene().getWindow();
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
        virtualMachineRunner = createVirtualMachineRunner();
        virtualMachineRunner.run(this);
    }

    @Override
    public void onRunCompleted(final CompleteStatus status) {
        Platform.runLater(() -> actionPane.onRunCompleted(status));
    }

    @Override
    public void onTerminateAction() {
        virtualMachineRunner.terminate();
    }

    private VirtualMachineRunner createVirtualMachineRunner() {
        return ComponentFactoryProvider.getComponentFactory().createVirtualMachineRunner(
            Console.DEFAULT,
            codeTabPane.getAllSourceCode()
        );
    }
}
