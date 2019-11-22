
/*
 * Copyright (c) 2019. http://devonline.academy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.revenat.javamm.ide.util;

import com.revenat.javamm.ide.ui.pane.TitledPane;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.SplitPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * @author Vitaliy Dragun
 */
public final class UIUtils {

    private UIUtils() {
    }

    /**
     * Centers specified {@code stage} on user's screen according to provided {@code withPercentage} and {@code heightPercentage}
     *
     * @param stage            state to center on user's screen
     * @param withPercentage   how much percent from available screen width given {@code stage} should occupy
     * @param heightPercentage how much percent from available screen height given {@code stage} should occupy
     */
    public static void centerByScreen(final Stage stage, final double withPercentage, final double heightPercentage) {
        final Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        final double width = primaryScreenBounds.getWidth() * withPercentage;
        final double height = primaryScreenBounds.getHeight() * heightPercentage;

        stage.setWidth(width);
        stage.setHeight(height);

        stage.setX((primaryScreenBounds.getWidth() - width) / 2);
        stage.setY((primaryScreenBounds.getHeight() - height) / 2);
    }

    /**
     * Close titled pane defined by provided {@code pane} parameter
     *
     * @param pane pane to close
     */
    public static void closeTitledPane(final TitledPane pane) {
        findSplitPaneParent(pane).getItems().remove(pane);
    }

    private static SplitPane findSplitPaneParent(final TitledPane node) {
        Parent parent = node.getParent();
        while (parent != null) {
            if (parent instanceof SplitPane) {
                return (SplitPane) parent;
            }
            parent = parent.getParent();
        }
        throw new IllegalStateException("SplitPane parent not found");
    }
}
