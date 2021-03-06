
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

package com.revenat.javamm.ide;

import com.revenat.javamm.ide.component.impl.ComponentFactoryProvider;
import com.revenat.javamm.ide.ui.listener.ActionListener;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static com.revenat.javamm.ide.util.UIUtils.centerByScreen;

/**
 * @author Vitaliy Dragun
 */
public class JavammIDELauncher extends Application {

    private static final double DEFAULT_WIDTH_PERCENTAGE = 0.8;

    private static final double DEFAULT_HEIGHT_PERCENTAGE = 0.9;

    public static void main(final String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {
        final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/javafx/main-window.fxml"));
        final Scene scene = new Scene(fxmlLoader.load());

        defineCloseRequestHandler(primaryStage, fxmlLoader.getController());

        primaryStage.setTitle("Javamm Simple IDE");
        primaryStage.setScene(scene);

        centerByScreen(primaryStage, DEFAULT_WIDTH_PERCENTAGE, DEFAULT_HEIGHT_PERCENTAGE);

        primaryStage.show();
    }

    private void defineCloseRequestHandler(final Stage state, final ActionListener controller) {
        state.setOnCloseRequest(we -> {
            if (!controller.onExitAction()) {
                we.consume();
            }
        });
    }

    @Override
    public void stop() {
        ComponentFactoryProvider.getComponentFactory().release();
    }
}
