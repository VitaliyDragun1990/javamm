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

package com.revenat.javamm.ide.ui.dialog;

import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

import java.io.File;
import java.util.Optional;

/**
 * Factory to present dialog windows to user to choose a file for some purpose
 *
 * @author Vitaliy Dragun
 */
public interface FileChooserFactory {

    /**
     * Shows dialog window to open existing file with javamm source code in it
     *
     * @return {@linkplain Optional optional} object with chosen file in it,
     * or empty optional if the user doesn't choose any
     */
    Optional<File> showOpenDialog(Window ownerWindow);

    /**
     * Shows dialog window to save javamm source code from code editor pane to some
     * file.
     *
     * @param initialFileName desired name of the file to be saved
     * @return {@linkplain Optional optional} object with chosen file in it,
     * or empty optional if the user cancel save operation
     */
    Optional<File> showSaveDialog(Window ownerWindow, String initialFileName);

    interface Builder {

        Builder setOpenFileDialogTitle(String openFileDialogTitle);

        Builder setSaveFileDialogTitle(String saveFileDialogTitle);

        Builder setExtensionFilters(ExtensionFilter... extensionFilters);

        Builder setInitialDirectory(File initialDirectory);

        FileChooserFactory build();
    }
}
