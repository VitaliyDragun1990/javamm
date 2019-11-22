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

package com.revenat.javamm.ide.ui.pane.action;

import com.revenat.javamm.ide.ui.listener.ActionListener;
import com.revenat.javamm.ide.ui.listener.ActionStateManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

/**
 * Manages state of the action pane control elements
 *
 * @author Vitaliy Dragun
 */
@Deprecated
public final class ActionPaneOld extends VBox implements ActionStateManager {

    @FXML
    private MenuItem miNew;

    @FXML
    private MenuItem miOpen;

    @FXML
    private MenuItem miSave;

    @FXML
    private MenuItem miExit;

    @FXML
    private MenuItem miUndo;

    @FXML
    private MenuItem miRedo;

    @FXML
    private MenuItem miFormat;

    @FXML
    private MenuItem miRun;

    @FXML
    private MenuItem miTerminate;

    @FXML
    private Button tbNew;

    @FXML
    private Button tbOpen;

    @FXML
    private Button tbSave;

    @FXML
    private Button tbUndo;

    @FXML
    private Button tbRedo;

    @FXML
    private Button tbFormat;

    @FXML
    private Button tbRun;

    @FXML
    private Button tbTerminate;

    private ActionListener actionListener;

    private Map<MenuItem, Boolean> controlElementsState;

    public ActionPaneOld() throws IOException {
        final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/javafx/action-pane.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
    }

    public void setActionListener(final ActionListener actionListener) {
        this.actionListener = requireNonNull(actionListener);
    }

    /**
     * Component lifecycle method. Will be called by javafx framework
     * after this component has been fully initialized
     */
    @FXML
    private void initialize() {
        bindMenuItemsToCorrespondingToolbarButtons();
        customizeButtonsTooltipWithHotKeyCombination();
        setInitialControlElementsState();
    }

    /**
     * Binds menu item disable properties with corresponding toolbars buttons
     * disable properties
     */
    private void bindMenuItemsToCorrespondingToolbarButtons() {
        miNew.disableProperty().bindBidirectional(tbNew.disableProperty());
        miOpen.disableProperty().bindBidirectional(tbOpen.disableProperty());
        miSave.disableProperty().bindBidirectional(tbSave.disableProperty());

        miUndo.disableProperty().bindBidirectional(tbUndo.disableProperty());
        miRedo.disableProperty().bindBidirectional(tbRedo.disableProperty());
        miFormat.disableProperty().bindBidirectional(tbFormat.disableProperty());

        miRun.disableProperty().bindBidirectional(tbRun.disableProperty());
        miTerminate.disableProperty().bindBidirectional(tbTerminate.disableProperty());
    }

    /**
     * Sets default state for main menu items and toolbars buttons
     */
    private void setInitialControlElementsState() {
        disableSaveAction();
        disableUndoAction();
        disableRedoAction();
        disableFormatAction();
        disableRunAction();
        disableTerminateAction();
    }

    /**
     * Completes all toolbar's buttons tooltip with hot key combination
     * from corresponding menu item
     */
    private void customizeButtonsTooltipWithHotKeyCombination() {
        customizeButtonTooltipWithHotKeyCombination(tbNew, miNew);
        customizeButtonTooltipWithHotKeyCombination(tbOpen, miOpen);
        customizeButtonTooltipWithHotKeyCombination(tbSave, miSave);

        customizeButtonTooltipWithHotKeyCombination(tbUndo, miUndo);
        customizeButtonTooltipWithHotKeyCombination(tbRedo, miRedo);
        customizeButtonTooltipWithHotKeyCombination(tbFormat, miFormat);

        customizeButtonTooltipWithHotKeyCombination(tbRun, miRun);
        customizeButtonTooltipWithHotKeyCombination(tbTerminate, miTerminate);
    }

    /**
     * Customizes specified button tooltip with hot key combination from given menu item
     *
     * @param btn button which tooltip should be customized
     * @param mi  menu item whose hot key combination should be used
     */
    private void customizeButtonTooltipWithHotKeyCombination(final Button btn, final MenuItem mi) {
        final String template = "%s (%s)";
        btn.getTooltip().setText(format(template,
            btn.getTooltip().getText(), mi.getAccelerator().getDisplayText()));
    }

    @FXML
    private void onNewAction(final ActionEvent event) {
        actionListener.onNewAction();
        enableFormatAction();
        enableRunAction();
    }

    @FXML
    private void onOpenAction(final ActionEvent event) {
        if (actionListener.onOpenAction()) {
            enableFormatAction();
            enableRunAction();
        }
    }

    @FXML
    private void onSaveAction(final ActionEvent event) {
        if (actionListener.onSaveAction()) {
            disableSaveAction();
        }
    }

    @FXML
    private void onExitAction(final ActionEvent event) {
        actionListener.onExitAction();
    }

    @FXML
    private void onUndoAction(final ActionEvent event) {
        actionListener.onUndoAction();
    }

    @FXML
    private void onRedoAction(final ActionEvent event) {
        actionListener.onRedoAction();
    }

    @FXML
    private void onFormatAction(final ActionEvent event) {
        actionListener.onFormatAction();
    }

    @FXML
    private void onRunAction(final ActionEvent event) {
        preserveCurrentStateAndDisable(miNew, miOpen, miSave, miExit, miUndo, miRedo, miFormat, miRun);
        enableTerminateAction();
        actionListener.onRunAction();
    }

    @FXML
    private void onTerminateAction(final ActionEvent event) {
        actionListener.onTerminateAction();
        restorePreservedState();
        disableTerminateAction();
    }

    private void preserveCurrentStateAndDisable(final MenuItem... items) {
        final List<MenuItem> menuItems = Arrays.asList(items);
        controlElementsState = menuItems.stream().collect(toMap(identity(), MenuItem::isDisable));
        menuItems.forEach(mi -> mi.setDisable(true));
    }

    private void restorePreservedState() {
        controlElementsState.forEach(MenuItem::setDisable);
        controlElementsState.clear();
    }

    @Override
    public void enableNewAction() {
        miNew.setDisable(false);
    }

    @Override
    public boolean isNewActionEnabled() {
        return !miNew.isDisable();
    }

    @Override
    public void disableNewAction() {
        miNew.setDisable(true);
    }

    @Override
    public void enableOpenAction() {
        miOpen.setDisable(false);
    }

    @Override
    public boolean isOpenActionEnabled() {
        return !miOpen.isDisable();
    }

    @Override
    public void disableOpenAction() {
        miOpen.setDisable(true);
    }

    @Override
    public void enableSaveAction() {
        miSave.setDisable(false);
    }

    @Override
    public boolean isSaveActionEnabled() {
        return !miSave.isDisable();
    }

    @Override
    public void disableSaveAction() {
        miSave.setDisable(true);
    }

    @Override
    public void enableExitAction() {
        miExit.setDisable(false);
    }

    @Override
    public boolean isExitActionEnabled() {
        return !miExit.isDisable();
    }

    @Override
    public void disableExitAction() {
        miExit.setDisable(true);
    }

    @Override
    public void enableUndoAction() {
        miUndo.setDisable(false);
    }

    @Override
    public boolean isUndoActionEnabled() {
        return !miUndo.isDisable();
    }

    @Override
    public void disableUndoAction() {
        miUndo.setDisable(true);
    }

    @Override
    public void enableRedoAction() {
        miRedo.setDisable(false);
    }

    @Override
    public boolean isRedoActionEnabled() {
        return !miRedo.isDisable();
    }

    @Override
    public void disableRedoAction() {
        miRedo.setDisable(true);
    }

    @Override
    public void enableFormatAction() {
        miFormat.setDisable(false);
    }

    @Override
    public boolean isFormatActionEnabled() {
        return !miFormat.isDisable();
    }

    @Override
    public void disableFormatAction() {
        miFormat.setDisable(true);
    }

    @Override
    public void enableRunAction() {
        miRun.setDisable(false);
    }

    @Override
    public boolean isRunActionEnabled() {
        return !miRun.isDisable();
    }

    @Override
    public void disableRunAction() {
        miRun.setDisable(true);
    }

    @Override
    public void enableTerminateAction() {
        miTerminate.setDisable(false);
    }

    @Override
    public boolean isTerminatedActionEnabled() {
        return !miTerminate.isDisable();
    }

    @Override
    public void disableTerminateAction() {
        miTerminate.setDisable(true);
    }

    public boolean isExitActionDisabled() {
        return miExit.isDisable();
    }
}
