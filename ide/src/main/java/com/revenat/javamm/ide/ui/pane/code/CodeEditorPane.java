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
import javafx.beans.value.ChangeListener;
import javafx.scene.layout.StackPane;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

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

    private File sourceCodeFile;

    CodeEditorPane() {
        enableLineNumeration();
        enableScrollingFacility();
        enableSyntaxHighlighting();
    }

    Optional<File> getSourceCodeFile() {
        return Optional.ofNullable(sourceCodeFile);
    }

    @Override
    public void requestFocus() {
        codeArea.requestFocus();
    }

    @Override
    public void release() {
        syntaxHighlighter.disable();
    }

    List<String> getCodeLines() {
        return List.of(codeArea.getText().split("\n"));
    }

    void setChangeListener(final ChangeListener<String> changeListener) {
        codeArea.textProperty().addListener(changeListener);
    }

    void loadContentFrom(final File file) throws IOException {
        this.sourceCodeFile = file;
        codeArea.replaceText(Files.readString(file.toPath()));
    }

    void saveContentTo(final File file) throws IOException {
        this.sourceCodeFile = file;
        Files.writeString(file.toPath(), codeArea.getText());
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
