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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

/**
 * Provides pane with buttons for every possible action that can be performed with the javamm IDE
 *
 * @author Vitaliy Dragun
 */
public final class ActionPane extends VBox implements ActionStateManager {

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

    public ActionPane() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/javafx/action-pane.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
    }

    public void setActionListener(final ActionListener actionListener) {
        this.actionListener = requireNonNull(actionListener);
    }

    @FXML
    private void onNewAction(ActionEvent event) {
        System.out.println("onNewAction");
    }

    @FXML
    private void onOpenAction(ActionEvent event) {
        System.out.println("onOpenAction");
    }

    @FXML
    private void onSaveAction(ActionEvent event) {
        System.out.println("onSaveAction");
    }

    @FXML
    private void onExitAction(ActionEvent event) {
        System.out.println("onExitAction");
    }

    @FXML
    private void onUndoAction(ActionEvent event) {
        System.out.println("onUndoAction");
    }

    @FXML
    private void onRedoAction(ActionEvent event) {
        System.out.println("onRedoAction");
    }

    @FXML
    private void onFormatAction(ActionEvent event) {
        System.out.println("onFormatAction");
    }

    @FXML
    private void onRunAction(ActionEvent event) {
        System.out.println("onRunAction");
    }

    @FXML
    private void onTerminateAction(ActionEvent event) {
        System.out.println("onTerminateAction");
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
