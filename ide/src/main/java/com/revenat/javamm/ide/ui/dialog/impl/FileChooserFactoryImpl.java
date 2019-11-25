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

import com.revenat.javamm.ide.ui.dialog.FileChooserFactory;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

import java.io.File;
import java.util.Optional;
import java.util.Set;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

/**
 * @author Vitaliy Dragun
 */
public final class FileChooserFactoryImpl implements FileChooserFactory {

    private final String openFileDialogTitle;

    private final String saveFileDialogTitle;

    private final Set<ExtensionFilter> extensionFilters;

    private File initialDirectory;

    private FileChooserFactoryImpl(final String openFileDialogTitle,
                                   final String saveFileDialogTitle,
                                   final Set<ExtensionFilter> extensionFilters,
                                   final File initialDirectory) {
        this.openFileDialogTitle = requireNonNull(openFileDialogTitle);
        this.saveFileDialogTitle = requireNonNull(saveFileDialogTitle);
        this.extensionFilters = requireNonNull(extensionFilters);
        this.initialDirectory = initialDirectory;
    }

    @Override
    public Optional<File> showOpenDialog(final Window ownerWindow) {
        final FileChooser fileChooser = createFileChooser(openFileDialogTitle);

        final File file = fileChooser.showOpenDialog(ownerWindow);
        rememberFileDirectoryIfAny(file);

        return ofNullable(file);
    }

    @Override
    public Optional<File> showSaveDialog(final Window ownerWindow, final String initialFileName) {
        final FileChooser fileChooser = createFileChooser(saveFileDialogTitle);
        fileChooser.setInitialFileName(initialFileName);

        final File file = fileChooser.showSaveDialog(ownerWindow);
        rememberFileDirectoryIfAny(file);

        return ofNullable(file);
    }

    private FileChooser createFileChooser(final String title) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().setAll(extensionFilters);
        setInitialDirectoryIfAny(fileChooser);
        return fileChooser;
    }

    private void setInitialDirectoryIfAny(final FileChooser fileChooser) {
        if (initialDirectory != null && initialDirectory.exists()) {
            fileChooser.setInitialDirectory(initialDirectory);
        }
    }

    private void rememberFileDirectoryIfAny(final File file) {
        if (file != null) {
            final File fileDirectory = file.getParentFile();
            this.initialDirectory = fileDirectory != null ? fileDirectory : initialDirectory;
        }
    }

    public static class BuilderImpl implements FileChooserFactory.Builder {

        private static final String DEFAULT_OPEN_FILE_DIALOG_TITLE = "Open File";

        private static final String DEFAULT_SAVE_FILE_DIALOG_TITLE = "Save File";

        private static final ExtensionFilter ALL_FILES = new ExtensionFilter("All files (*.*)", "*.*");

        private String openFileDialogTitle;

        private String saveFileDialogTitle;

        private Set<ExtensionFilter> extensionFilters;

        private File initialDirectory;

        @Override
        public FileChooserFactory.Builder setOpenFileDialogTitle(final String openDialogTitle) {
            this.openFileDialogTitle = requireNonNull(openDialogTitle);
            return this;
        }

        @Override
        public FileChooserFactory.Builder setSaveFileDialogTitle(final String saveDialogTitle) {
            this.saveFileDialogTitle = requireNonNull(saveDialogTitle);
            return this;
        }

        @Override
        public FileChooserFactory.Builder setExtensionFilters(final ExtensionFilter... filters) {
            this.extensionFilters = Set.of(filters);
            return this;
        }

        @Override
        public FileChooserFactory.Builder setInitialDirectory(final File directory) {
            this.initialDirectory = requireNonNull(directory);
            return this;
        }

        @Override
        public FileChooserFactory build() {
            return new FileChooserFactoryImpl(
                ofNullable(openFileDialogTitle).orElse(DEFAULT_OPEN_FILE_DIALOG_TITLE),
                ofNullable(saveFileDialogTitle).orElse(DEFAULT_SAVE_FILE_DIALOG_TITLE),
                ofNullable(extensionFilters).orElse(Set.of(ALL_FILES)),
                initialDirectory);
        }
    }
}
