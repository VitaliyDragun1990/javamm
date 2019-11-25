
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

import com.revenat.javamm.ide.component.ComponentFactoryProvider;
import com.revenat.javamm.ide.component.VirtualMachineRunner;
import com.revenat.javamm.ide.component.VirtualMachineRunner.CompleteStatus;
import com.revenat.javamm.ide.component.VirtualMachineRunner.VirtualMachineRunCompletedListener;
import com.revenat.javamm.ide.ui.dialog.DialogFactoryProvider;
import com.revenat.javamm.ide.ui.dialog.FileChooserFactory;
import com.revenat.javamm.ide.ui.listener.ActionListener;
import com.revenat.javamm.ide.ui.listener.TabCloseConfirmationListener;
import com.revenat.javamm.ide.ui.pane.PaneManager;
import com.revenat.javamm.ide.ui.pane.action.ActionPane;
import com.revenat.javamm.ide.ui.pane.code.CodeTab;
import com.revenat.javamm.ide.ui.pane.code.CodeTabPane;
import com.revenat.javamm.ide.ui.pane.code.exception.SaveFileException;
import com.revenat.javamm.ide.ui.pane.console.ConsolePane;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.revenat.javamm.ide.ui.dialog.DialogFactoryProvider.getSimpleDialogFactory;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

/**
 * @author Vitaliy Dragun
 *
 */
public class MainWindowController implements ActionListener, VirtualMachineRunCompletedListener,
    TabCloseConfirmationListener {

    private static final String INFO_VM_IS_RUNNING = "Javamm VM is running.\nWait for VM to complete or terminate it! ";

    @FXML
    private ActionPane actionPane;

    @FXML
    private CodeTabPane codeTabPane;

    @FXML
    private SplitPane spWork;

    private final PaneManager paneManager = new PaneManager();

    private VirtualMachineRunner virtualMachineRunner;

    private final FileChooserFactory fileChooserFactory = buildFileChooserFactory();

    private FileChooserFactory buildFileChooserFactory() {
        return DialogFactoryProvider.createFileChooserFactoryBuilder()
            .setOpenFileDialogTitle("Open javamm source code file")
            .setSaveFileDialogTitle("Save javamm source code file")
            .setExtensionFilters(
                new FileChooser.ExtensionFilter("Source code files (*.javamm)", "*.javamm"),
                new FileChooser.ExtensionFilter("All files (*.*)", "*.*")
            )
            .setInitialDirectory(new File("E:\\idea-workspace\\javamm\\ide\\src\\main\\resources\\examples"))
            .build();
    }

    /**
     * Controller lifecycle method. Will be called by javafx framework
     * after this controller has been fully initialized
     */
    @FXML
    private void initialize() {
        actionPane.setActionListener(this);
        codeTabPane.setTabChangeListener(actionPane);
        codeTabPane.setTabCloseConfirmationListener(this);
    }

    @Override
    public void onNewAction() {
        codeTabPane.newCodeEditor();
    }

    @Override
    public boolean onOpenAction() {
        final Optional<File> selectedFileOptional = fileChooserFactory.showOpenDialog(getStage());
        if (selectedFileOptional.isPresent()) {
            final File selectedFile = selectedFileOptional.get();
            try {
                return codeTabPane.presentTabWithFileContent(selectedFile);
            } catch (final IOException e) {
                getSimpleDialogFactory().showErrorDialog(format("Can't load file: %s -> %s",
                    selectedFile.getAbsolutePath(), e.getMessage()));
            }
        }
        return false;
    }

    @Override
    public boolean onSaveAction() {
        return saveChanges(codeTabPane.getSelectedTab());
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
        Platform.runLater(() -> {
            actionPane.onRunCompleted(status);
            getConsolePane().displayMessage(status);
        });
    }

    @Override
    public void onTerminateAction() {
        virtualMachineRunner.terminate();
    }

    @Override
    public boolean onExitAction() {
        if (isVirtualMachineRunning()) {
            getSimpleDialogFactory().showInfoDialog(INFO_VM_IS_RUNNING);
            return false;
        }

        for (final CodeTab changedTab : findChangedTabs(codeTabPane.getTabs())) {
            selectTab(changedTab);
            if (isTabCloseEventCancelled(changedTab)) {
                return false;
            }
        }

        getStage().close();
        return true;
    }

    private void selectTab(final CodeTab changedTab) {
        codeTabPane.getSelectionModel().select(changedTab);
    }

    private List<CodeTab> findChangedTabs(final ObservableList<Tab> tabs) {
        return tabs.stream().map(tab -> (CodeTab) tab).filter(CodeTab::isChanged).collect(toList());
    }

    private Stage getStage() {
        return (Stage) actionPane.getScene().getWindow();
    }

    @Override
    public boolean isTabCloseEventCancelled(final CodeTab codeTab) {
        if (isVirtualMachineRunning()) {
            getSimpleDialogFactory().showInfoDialog(INFO_VM_IS_RUNNING);
            return true;
        } else if (codeTab.isChanged()) {
            return askUserToSaveChanges(codeTab);
        }
        return false;
    }

    private boolean askUserToSaveChanges(final CodeTab codeTab) {
        final ButtonData result = getSimpleDialogFactory().showYesNoCancelDialog(
            "Source code has unsaved changes",
            "Save changes before close?"
        );
        return handleUserDecision(result, codeTab);
    }

    private boolean handleUserDecision(final ButtonData result, final CodeTab codeTab) {
        if (result == ButtonData.YES) {
            return !saveChanges(codeTab);
        } else if (result == ButtonData.NO) {
            return false;
        } else {
            return true;
        }
    }

    private boolean saveChanges(final CodeTab codeTab) {
        try {
            return codeTab.saveChanges(() -> fileChooserFactory.showSaveDialog(getStage(), codeTab.getModuleName()));
        } catch (final SaveFileException e) {
            getSimpleDialogFactory().showErrorDialog(format("Can't save file: %s -> %s",
                e.getFile().getAbsolutePath(), e.getMessage()));
        }
        return false;
    }

    private boolean isVirtualMachineRunning() {
        return virtualMachineRunner != null && virtualMachineRunner.isRunning();
    }

    private VirtualMachineRunner createVirtualMachineRunner() {
        return ComponentFactoryProvider.getComponentFactory().createVirtualMachineRunner(
            getConsolePane().getNewConsole(),
            codeTabPane.getAllSourceCode()
        );
    }

    private ConsolePane getConsolePane() {
        return paneManager.provideConsolePane(spWork);
    }
}
