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
import com.revenat.javamm.ide.ui.listener.CodeTabChangeListener;
import com.revenat.javamm.ide.ui.listener.TabCloseConfirmationListener;
import javafx.scene.control.Tab;

import static java.util.Objects.requireNonNull;

/**
 * Represents named tab with {@linkplain CodeEditorPane code editor pane}
 *
 * @author Vitaliy Dragun
 */
public class CodeTab extends Tab implements Releasable {

    private final String moduleName;

    private final CodeTabChangeListener contentChangedListener;

    private boolean changed;

    CodeTab(final String moduleName,
            final CodeEditorPane content,
            final CodeTabChangeListener contentChangedListener,
            final TabCloseConfirmationListener tabCloseConfirmationListener) {
        super(requireNonNull(moduleName), requireNonNull(content));
        this.moduleName = moduleName;
        this.contentChangedListener = requireNonNull(contentChangedListener);

        content.setChangeListener((observable, oldValue, newValue) -> setChanged());

        setCloseRequestHandler(tabCloseConfirmationListener);
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

    public String getModuleName() {
        return moduleName;
    }

    SourceCode getSourceCode() {
        return new StringSourceCode(moduleName, getCodeEditorPane().getCodeLines());
    }

    public boolean isChanged() {
        return changed;
    }

    private void setChanged() {
        changed = true;
        if (!getText().startsWith("*")) {
            setText("*" + moduleName);
        }
//        lastModifiedTime = Instant.now();
//        actionStateManager.enableSaveAction();
        contentChangedListener.tabContentChanged();
    }

    @Override
    public void release() {
        getCodeEditorPane().release();
    }

    private CodeEditorPane getCodeEditorPane() {
        return (CodeEditorPane) getContent();
    }
}
