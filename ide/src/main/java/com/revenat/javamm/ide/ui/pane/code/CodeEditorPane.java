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

import com.revenat.javamm.ide.component.CodeFormatter;
import com.revenat.javamm.ide.component.CodeTemplateHelper;
import com.revenat.javamm.ide.component.NewLineHelper;
import com.revenat.javamm.ide.component.PairedTokensHelper;
import com.revenat.javamm.ide.component.Releasable;
import com.revenat.javamm.ide.component.SyntaxHighlighter;
import javafx.beans.value.ChangeListener;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.util.UndoUtils;
import org.fxmisc.undo.UndoManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

import static com.revenat.javamm.ide.component.impl.ComponentFactoryProvider.getComponentFactory;
import static com.revenat.javamm.ide.util.ResourceUtils.getClassPathResource;
import static com.revenat.javamm.ide.util.TabReplaceUtils.initCodeAreaTabFixer;
import static com.revenat.javamm.ide.util.TabReplaceUtils.replaceTabulations;
import static org.fxmisc.undo.UndoManagerFactory.fixedSizeHistoryFactory;

/**
 * Pane with code area
 *
 * @author Vitaliy Dragun
 */
public final class CodeEditorPane extends StackPane implements Releasable {

    private static final int UNDO_HISTORY_SIZE = 100;

    private final CodeArea codeArea = new CodeArea();

    private final CodeTemplateHelper codeTemplateHelper = getComponentFactory().getCodeTemplateHelper();

    private final NewLineHelper newLineHelper = getComponentFactory().getNewLineHelper();

    private final SyntaxHighlighter syntaxHighlighter = getComponentFactory().createSyntaxHighlighter(codeArea);

    private final PairedTokensHelper pairedTokensHelper = getComponentFactory().getPairedTokensHelper();

    private final CodeFormatter codeFormatter = getComponentFactory().getCodeFormatter();

    private File sourceCodeFile;

    CodeEditorPane() {
        setUndoHistorySize();
        enableLineNumeration();
        enableScrollingFacility();
        applyCustomLengthTabFix();
        enableCodeAutocompletionAndNewLineIndentation();
        enablePairedTokensAutoInsertion();
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

    void setCodeChangeListener(final ChangeListener<String> changeListener) {
        codeArea.textProperty().addListener(changeListener);
    }

    void loadContentFrom(final File file) throws IOException {
        this.sourceCodeFile = file;
        final String content = replaceTabulations(Files.readString(file.toPath()));
        codeArea.replaceText(content);
        codeArea.getUndoManager().forgetHistory();
    }

    void saveContentTo(final File file) throws IOException {
        this.sourceCodeFile = file;
        Files.writeString(file.toPath(), codeArea.getText());
    }

    boolean undo() {
        final UndoManager undoManager = codeArea.getUndoManager();
        undoManager.undo();
        return undoManager.isUndoAvailable();
    }

    boolean redo() {
        final UndoManager undoManager = codeArea.getUndoManager();
        undoManager.redo();
        return undoManager.isRedoAvailable();
    }

    boolean isRedoAvailable() {
        return codeArea.isRedoAvailable();
    }

    boolean isUndoAvailable() {
        return codeArea.isUndoAvailable();
    }

    void format() {
        codeArea.replaceText(String.join("\n", codeFormatter.format(getCodeLines())));
        syntaxHighlighter.enable();
    }

    private void setUndoHistorySize() {
        codeArea.setUndoManager(UndoUtils.plainTextUndoManager(codeArea, fixedSizeHistoryFactory(UNDO_HISTORY_SIZE)));
    }

    private void enableLineNumeration() {
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
    }

    private void enableScrollingFacility() {
        getChildren().add(new VirtualizedScrollPane<>(codeArea));
    }

    private void applyCustomLengthTabFix() {
        initCodeAreaTabFixer(codeArea);
    }

    private void enableCodeAutocompletionAndNewLineIndentation() {
        codeArea.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && !codeTemplateHelper.insertCodeTemplateAtCaretPosition(codeArea)) {
                newLineHelper.insertNewLine(codeArea);
            }
        });
    }

    private void enablePairedTokensAutoInsertion() {
        codeArea.setOnKeyTyped(event -> pairedTokensHelper.insertPairedTokenIfAny(codeArea, event.getCharacter()));
    }

    private void enableSyntaxHighlighting() {
        loadSyntaxStyles();
        syntaxHighlighter.enable();
    }

    private void loadSyntaxStyles() {
        getStylesheets().add(getClassPathResource("/style/code-editor-pane.css").toExternalForm());
    }
}
