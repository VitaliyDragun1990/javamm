
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

package com.revenat.temp.gui.gui4_javafx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * @author Vitaliy Dragun
 *
 */
@SuppressWarnings("CheckStyle")
public class JavafxApplicationWindow extends Application {

    private static final int WIDTH = 600;

    private static final int HEIGHT = 400;

    public static void main(final String[] args) {
        launch(args);
    }

    @SuppressWarnings("exports")
    @Override
    public void start(final Stage primaryStage) throws Exception {
        primaryStage.setTitle("JavaFx");
        primaryStage.setResizable(false);

        final BorderPane borderPane = new BorderPane();
        final MenuBar menuBar = createMenuBar();
        borderPane.setTop(menuBar);
        borderPane.setCenter(new TextArea());

        final Scene stage = new Scene(borderPane, WIDTH, HEIGHT);
        primaryStage.setScene(stage);

        primaryStage.show();
    }

    private MenuBar createMenuBar() {
        final MenuBar menuBar = new MenuBar();

        final Menu fileMenu = new Menu("File");
        menuBar.getMenus().add(fileMenu);

        final MenuItem exitMenuItem = new MenuItem("Exit");
        exitMenuItem.setOnAction(event -> System.exit(0));
        fileMenu.getItems().add(exitMenuItem);

        return menuBar;
    }
}
