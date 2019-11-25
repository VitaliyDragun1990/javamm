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
import com.revenat.javamm.ide.ui.listener.TabChangeListener;
import com.revenat.javamm.ide.ui.listener.TabCloseConfirmationListener;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

/**
 * Container for {@linkplain CodeTab code tab} elements
 *
 * @author Vitaliy Dragun
 */
public final class CodeTabPane extends TabPane {

    private int untitledCounter = 1;

    private TabChangeListener tabChangeListener;

    private TabCloseConfirmationListener tabCloseConfirmationListener;

    public CodeTabPane() {
        setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
    }

    public void setTabChangeListener(final TabChangeListener tabChangeListener) {
        this.tabChangeListener = requireNonNull(tabChangeListener);
        setTabChangeListenerForSelectedCodeTab(tabChangeListener);
    }

    public void setTabCloseConfirmationListener(final TabCloseConfirmationListener tabCloseConfirmationListener) {
        this.tabCloseConfirmationListener = requireNonNull(tabCloseConfirmationListener);
    }

    private void setTabChangeListenerForSelectedCodeTab(final TabChangeListener changeListener) {
        getSelectionModel().selectedItemProperty().addListener(changeListenerWrapper(changeListener));
    }

    private ChangeListener<Tab> changeListenerWrapper(final TabChangeListener changeListener) {
        return (observable, oldValue, newValue) -> {
            if (newValue != null) {
                final CodeTab t = (CodeTab) newValue;
                if (t.isChanged()) {
                    changeListener.tabContentChanged();
                } else {
                    changeListener.tabContentUnchanged();
                }
            } else {
                changeListener.allTabsClosed();
            }
        };
    }

    public void newCodeEditor() {
        final CodeTab codeTab = createCodeTab(generateNewTabName());
        showCodeTab(codeTab);
    }

    public List<SourceCode> getAllSourceCode() {
        return collectSourceCodeFromAllTabs();
    }

    /**
     * Presents tab, whether new one or already existed, populated with content of the specified {@code file}.
     * If specified {@code file} is already opened in some of the existing tabs, such tab will be selected,
     * otherwise new tab will be opened and populated with file content
     *
     * @param file {@linkplain File file} with which content tab should be populated
     * @return {@code true} if new tab has been opened and populated with file content,
     * {@code false} if there is already a tab with such file content
     * @throws IOException if some error occurs while reading file content
     */
    public boolean presentTabWithFileContent(final File file) throws IOException {
        if (isFileAlreadyOpenedInTab(file)) {
            selectTabWithFile(file);
            return false;
        } else {
            loadFileContentToNewTab(file);
            return true;
        }
    }

    public CodeTab getSelectedTab() {
        return (CodeTab) getSelectionModel().getSelectedItem();
    }

    private boolean isFileAlreadyOpenedInTab(final File selectedFile) {
        return getCodeTabs().stream()
            .anyMatch(t -> t.isBackedByFile(selectedFile));
    }

    private void selectTabWithFile(final File selectedFile) {
        for (final CodeTab tab : getCodeTabs()) {
            if (tab.isBackedByFile(selectedFile)) {
                getSelectionModel().select(tab);
                return;
            }
        }
    }

    private void loadFileContentToNewTab(final File file) throws IOException {
        final CodeTab codeTab = createCodeTab(file.getName());
        codeTab.loadContentFrom(file);
        showCodeTab(codeTab);
    }

    private CodeTab createCodeTab(final String tabTitle) {
        return new CodeTab(tabTitle, new CodeEditorPane(), tabChangeListener, tabCloseConfirmationListener);
    }

    private void showCodeTab(final CodeTab codeTab) {
        addTab(codeTab);
        selectSpecifiedTab(codeTab);
        tabChangeListener.newTabCreated();
        codeTab.requestFocus();
    }

    private void addTab(final Tab tab) {
        getTabs().add(tab);
    }

    private void selectSpecifiedTab(final Tab tab) {
        getSelectionModel().select(tab);
    }

    private String generateNewTabName() {
        return format("Untitled-%s.javamm", untitledCounter++);
    }

    private List<SourceCode> collectSourceCodeFromAllTabs() {
        return getCodeTabs().stream().map(CodeTab::getSourceCode).collect(toList());
    }

    private List<CodeTab> getCodeTabs() {
        return getTabs().stream()
            .map(t -> (CodeTab) t)
            .collect(toList());
    }
}
