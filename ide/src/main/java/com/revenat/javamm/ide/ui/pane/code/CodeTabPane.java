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
import com.revenat.javamm.ide.ui.listener.CodeTabChangeListener;
import com.revenat.javamm.ide.ui.listener.TabCloseConfirmationListener;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

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

    private CodeTabChangeListener contentChangedListener;

    private TabCloseConfirmationListener tabCloseConfirmationListener;

    public CodeTabPane() {
        setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
    }

    public void setContentChangedListener(final CodeTabChangeListener contentChangedListener) {
        this.contentChangedListener = requireNonNull(contentChangedListener);
        setListenerForTabSelection(contentChangedListener);
    }

    public void setTabCloseConfirmationListener(final TabCloseConfirmationListener tabCloseConfirmationListener) {
        this.tabCloseConfirmationListener = requireNonNull(tabCloseConfirmationListener);
    }

    private void setListenerForTabSelection(final CodeTabChangeListener contentChangedListener) {
        getSelectionModel().selectedItemProperty().addListener(changeListenerWrapper(contentChangedListener));
    }

    private ChangeListener<Tab> changeListenerWrapper(CodeTabChangeListener codeTabChangeListener) {
        return (observable, oldValue, newValue) -> {
            if (newValue != null) {
                final CodeTab t = (CodeTab) newValue;
                if (t.isChanged()) {
                    codeTabChangeListener.tabContentChanged();
                } else {
                    codeTabChangeListener.tabContentUnchanged();
                }
            } else {
                codeTabChangeListener.allTabsClosed();
            }
        };
    }

    public void newCodeEditor() {
        final String tabTitle = generateNewTabName();
        final CodeEditorPane codeEditorPane = new CodeEditorPane();
        final Tab newCodeTab =
            new CodeTab(tabTitle, codeEditorPane, contentChangedListener, tabCloseConfirmationListener);

        addTab(newCodeTab);
        selectSpecifiedTab(newCodeTab);
        codeEditorPane.requestFocus(); // set focus on code editing area
    }

    public List<SourceCode> getAllSourceCode() {
        return collectSourceCodeFromAllTabs();
    }

    private void selectSpecifiedTab(final Tab tab) {
        getSelectionModel().select(tab);
    }

    private void addTab(final Tab tab) {
        getTabs().add(tab);
    }

    private String generateNewTabName() {
        return format("Untitled-%s.javamm", untitledCounter++);
    }

    private List<SourceCode> collectSourceCodeFromAllTabs() {
        return getTabs().stream().map(t -> ((CodeTab) t).getSourceCode()).collect(toList());
    }
}
