
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

/**
 * @author Vitaliy Dragun
 */
module javamm.ide {
    requires javamm.code;
    requires javamm.compiler;
    requires javamm.interpreter;
    requires javamm.vm;

    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;

    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.devicons;
    requires org.kordamp.ikonli.typicons;

    requires richtextfx;
    requires flowless;
    requires reactfx;
    requires wellbehavedfx;
    requires undofx;

    exports com.revenat.javamm.ide to javafx.graphics;
    exports com.revenat.javamm.ide.controller to javafx.fxml;
    exports com.revenat.javamm.ide.component to javafx.fxml;
    exports com.revenat.javamm.ide.ui.pane.action to javafx.fxml;
    exports com.revenat.javamm.ide.ui.listener to javafx.fxml;

    opens com.revenat.javamm.ide.controller to javafx.fxml;
    opens com.revenat.javamm.ide.ui.pane to javafx.fxml;
    opens com.revenat.javamm.ide.ui.pane.action to javafx.fxml;
    opens com.revenat.javamm.ide.ui.pane.code to javafx.fxml;
    opens com.revenat.javamm.ide.ui.pane.console to javafx.fxml;
}