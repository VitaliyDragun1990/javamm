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

package com.revenat.javamm.ide.ui.dialog.impl;

import com.revenat.javamm.ide.ui.dialog.SimpleDialogFactory;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;


/**
 * @author Vitaliy Dragun
 */
public class SimpleDialogFactoryImpl implements SimpleDialogFactory {

    @Override
    public void showInfoDialog(final String message) {
        final Alert infoAlert = new Alert(AlertType.INFORMATION);
        infoAlert.setHeaderText("Information");
        infoAlert.setContentText(message);
        infoAlert.showAndWait();
    }

    @Override
    public void showErrorDialog(final String message) {
        final Alert errorAlert = new Alert(AlertType.ERROR);
        errorAlert.setHeaderText("Error");
        errorAlert.setContentText(message);
        errorAlert.showAndWait();
    }

    @Override
    public ButtonData showYesNoCancelDialog(final String title, final String question) {
        final Alert chooseAlert = new Alert(AlertType.CONFIRMATION);
        chooseAlert.setHeaderText(title);
        chooseAlert.setContentText(question);

        final ButtonType okButton = new ButtonType("Yes", ButtonData.YES);
        final ButtonType noButton = new ButtonType("No", ButtonData.NO);
        final ButtonType cancelButton = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

        chooseAlert.getButtonTypes().setAll(okButton, noButton, cancelButton);

        return chooseAlert.showAndWait().map(ButtonType::getButtonData).orElseThrow();
    }
}
