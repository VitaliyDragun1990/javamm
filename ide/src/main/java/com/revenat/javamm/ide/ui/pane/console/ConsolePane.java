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

package com.revenat.javamm.ide.ui.pane.console;

import com.revenat.javamm.code.component.Console;
import com.revenat.javamm.ide.component.VirtualMachineRunner.CompletionStatus;
import com.revenat.javamm.ide.ui.pane.TitledPane;
import javafx.application.Platform;
import javafx.fxml.FXML;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.Collection;
import java.util.List;

import static com.revenat.javamm.ide.component.VirtualMachineRunner.CompletionStatus.SUCCESS;
import static com.revenat.javamm.ide.component.VirtualMachineRunner.CompletionStatus.TERMINATED;
import static com.revenat.javamm.ide.util.ResourceUtils.getClassPathResource;

/**
 * @author Vitaliy Dragun
 */
@SuppressWarnings("unused")
public final class ConsolePane extends TitledPane {

    private static final String STYLE_OUT = "out";

    private static final String STYLE_ERROR = "err";

    private static final String STYLE_COMPLETED = "completed";

    private static final String STYLE_OVERFLOW = "overflow";

    private final int maxConsoleSize = 1000;

    private final CodeAreaConsoleAdapter console = new CodeAreaConsoleAdapter();

    @FXML
    private CodeArea caConsole;

    public ConsolePane() {
        super("/javafx/console-pane.fxml");
        loadStyles();
    }

    public Console getNewConsole() {
        caConsole.setEditable(false);
        caConsole.replaceText("");
        console.clearOverflowStatus();

        return console;
    }

    public void displayMessage(final CompletionStatus status) {
        if (status == SUCCESS) {
            displayMessage("\nProcess finished successfully", STYLE_COMPLETED);
        } else if (status == TERMINATED) {
            displayMessage("\nProcess was terminated", STYLE_ERROR);
        }
    }

    private void displayMessage(final String message, final String styleClass) {
        final StyleSpans<Collection<String>> styleSpans = buildStyleSpans(styleClass, message.length());
        final int initialSize = caConsole.getLength();
        caConsole.appendText(message);
        caConsole.setStyleSpans(initialSize, styleSpans);
    }

    private StyleSpans<Collection<String>> buildStyleSpans(final String styleClass, final int length) {
        return new StyleSpansBuilder<Collection<String>>(1)
            .add(List.of(styleClass), length)
            .create();
    }

    private void loadStyles() {
        caConsole.getStylesheets()
            .add(getClassPathResource("/style/console-pane.css").toExternalForm());
    }

    private class CodeAreaConsoleAdapter implements Console {

        static final String CONSOLE_OVERFLOWED = "<CONSOLE OVERFLOWED...>";

        private boolean consoleIsOverflown;

        @Override
        public void outPrintln(final Object value) {
            postAppendText(value + "\n", STYLE_OUT);
        }

        @Override
        public void errPrintln(final String message) {
            postAppendText(message + "\n", STYLE_ERROR);
        }

        void clearOverflowStatus() {
            consoleIsOverflown = false;
        }

        private boolean isOverflown() {
            return consoleIsOverflown;
        }

        private void postAppendText(final String message, final String styleClass) {
            if (!isOverflown()) {
                final String normalizedMessage = message.replace("\r", "");

                if (isMessageCauseOverflow(normalizedMessage)) {
                    Platform.runLater(() -> displayMessage(CONSOLE_OVERFLOWED, STYLE_OVERFLOW));
                } else {
                    Platform.runLater(() -> displayMessage(normalizedMessage, styleClass));
                }
            }
        }

        private boolean isMessageCauseOverflow(final String message) {
            final int currentConsoleSize = caConsole.getLength();
            consoleIsOverflown = currentConsoleSize + message.length() > maxConsoleSize;
            return consoleIsOverflown;
        }
    }
}
