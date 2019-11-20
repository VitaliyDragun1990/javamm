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

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import static java.lang.String.format;

/**
 * Container for {@linkplain CodeTab code tab} elements
 *
 * @author Vitaliy Dragun
 */
public final class CodeTabPane extends TabPane {

    private int untitledCounter = 1;

    public CodeTabPane() {
        setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
    }

    public void newCodeEditor() {
        final String tabTitle = generateNewTabName();
        final CodeEditorPane codeEditorPane = new CodeEditorPane();
        final Tab tab = new CodeTab(tabTitle, codeEditorPane);

        getTabs().add(tab); // add new tab
        getSelectionModel().select(tab); // select new tab
        codeEditorPane.requestFocus(); // focus on new tab
    }

    private String generateNewTabName() {
        return format("Untitled-%s.javamm", untitledCounter++);
    }
}
