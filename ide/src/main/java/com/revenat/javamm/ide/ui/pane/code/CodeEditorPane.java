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

import com.revenat.javamm.ide.component.Releasable;
import com.revenat.javamm.ide.component.SyntaxHighlighter;
import javafx.scene.layout.StackPane;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.util.List;

import static com.revenat.javamm.ide.component.ComponentFactoryProvider.getComponentFactory;
import static com.revenat.javamm.ide.util.ResourceUtils.getClassPathResource;

/**
 * Pane with code area
 *
 * @author Vitaliy Dragun
 */
public final class CodeEditorPane extends StackPane implements Releasable {

    private final CodeArea codeArea = new CodeArea();

    private final SyntaxHighlighter syntaxHighlighter = getComponentFactory().createSyntaxHighlighter(codeArea);

    public CodeEditorPane() {
        enableLineNumeration();
        enableScrollingFacility();
        enableSyntaxHighlighting();
    }

    @Override
    public void requestFocus() {
        codeArea.requestFocus();
    }

    @Override
    public void release() {
        syntaxHighlighter.disable();
    }

    public List<String> getCodeLines() {
        return List.of(codeArea.getText().split("\n"));
    }

    private void enableLineNumeration() {
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
    }

    private void enableScrollingFacility() {
        getChildren().add(new VirtualizedScrollPane<>(codeArea));
    }

    private void enableSyntaxHighlighting() {
        loadSyntaxStyles();
        syntaxHighlighter.enable();
    }

    private void loadSyntaxStyles() {
        getStylesheets().add(getClassPathResource("/style/code-editor-pane.css").toExternalForm());
    }
}
