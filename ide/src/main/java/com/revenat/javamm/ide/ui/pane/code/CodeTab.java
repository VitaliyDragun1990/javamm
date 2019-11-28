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

package com.revenat.javamm.ide.ui.pane.code;

import com.revenat.javamm.code.fragment.SourceCode;
import com.revenat.javamm.ide.component.Releasable;
import com.revenat.javamm.ide.model.StringSourceCode;
import com.revenat.javamm.ide.ui.listener.TabChangeListener;
import com.revenat.javamm.ide.ui.listener.TabCloseConfirmationListener;
import com.revenat.javamm.ide.ui.pane.code.exception.SaveFileException;
import javafx.scene.control.Tab;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * Represents named tab with {@linkplain CodeEditorPane code editor pane}
 *
 * @author Vitaliy Dragun
 */
public class CodeTab extends Tab implements Releasable {

    private String moduleName;

    private final TabChangeListener tabChangeListener;

    private boolean changed;

    CodeTab(final String moduleName,
            final CodeEditorPane content,
            final TabChangeListener tabChangeListener,
            final TabCloseConfirmationListener tabCloseConfirmationListener) {
        super(requireNonNull(moduleName), requireNonNull(content));
        this.moduleName = moduleName;
        this.tabChangeListener = requireNonNull(tabChangeListener);

        content.setCodeChangeListener((observable, oldValue, newValue) -> setChanged());

        setCloseRequestHandler(tabCloseConfirmationListener);
    }

    public String getModuleName() {
        return moduleName;
    }

    void requestFocus() {
        getCodeEditorPane().requestFocus();
    }

    /**
     * Saves changes of the source code from {@code this} tab's {@linkplain CodeEditorPane code editor pane}.
     * If such code tab backed by already opened file, then all changes will be saved to that file, otherwise
     * provided {@code fileSupplier} will be used to obtain target file to save source code from this code editor
     * pane.
     *
     * @param fileSupplier means to obtain target file to save changes to
     * @return {@code true} if save operation succeeded, {@code false} otherwise
     * @throws SaveFileException if any error occurs while trying to save changes to file
     */
    public boolean saveChanges(final Supplier<Optional<File>> fileSupplier) {
        final Optional<File> fileToSaveOptional = getSourceCodeFile().or(fileSupplier);
        if (fileToSaveOptional.isPresent()) {
            final File fileToSave = fileToSaveOptional.get();
            saveChangesTo(fileToSave);
            tabChangeListener.tabContentSaved();
            return true;
        }
        return false;
    }

    boolean isBackedByFile(final File file) {
        final Optional<File> optionalFile = getSourceCodeFile();
        return optionalFile.isPresent() && optionalFile.get().equals(file);
    }

    @Override
    public void release() {
        getCodeEditorPane().release();
    }

    public boolean isChanged() {
        return changed;
    }

    SourceCode getSourceCode() {
        return new StringSourceCode(moduleName, getCodeEditorPane().getCodeLines());
    }

    void loadContentFrom(final File file) throws IOException {
        getCodeEditorPane().loadContentFrom(file);
        updateTabState(file);
    }

    boolean undoLastChange() {
        return getCodeEditorPane().undo();
    }

    boolean redoLastChange() {
        return getCodeEditorPane().redo();
    }

    boolean isRedoAvailable() {
        return getCodeEditorPane().isRedoAvailable();
    }

    boolean isUndoAvailable() {
        return getCodeEditorPane().isUndoAvailable();
    }

    private void saveChangesTo(final File fileToSave) {
        try {
            getCodeEditorPane().saveContentTo(fileToSave);
            updateTabState(fileToSave);
        } catch (final IOException e) {
            throw new SaveFileException(e.getMessage(), fileToSave);
        }
    }

    private Optional<File> getSourceCodeFile() {
        return getCodeEditorPane().getSourceCodeFile();
    }

    private void setCloseRequestHandler(final TabCloseConfirmationListener tabCloseConfirmationListener) {
        setOnCloseRequest(event -> {
            if (tabCloseConfirmationListener.isTabCloseEventCancelled(this)) {
                event.consume();
            } else {
                release();
            }
        });
    }

    private void setChanged() {
        changed = true;
        if (!getText().startsWith("*")) {
            setText("*" + moduleName);
        }
        // lastModifiedTime = Instant.now();
        tabChangeListener.tabContentChanged(true, getCodeEditorPane().isRedoAvailable());
    }

    private void updateTabState(final File file) {
        changed = false;
        setText(file.getName());
        moduleName = file.getName();
    }

    private CodeEditorPane getCodeEditorPane() {
        return (CodeEditorPane) getContent();
    }
}
