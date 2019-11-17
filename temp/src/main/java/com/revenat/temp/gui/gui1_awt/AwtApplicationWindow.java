
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

package com.revenat.temp.gui.gui1_awt;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.TextArea;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author Vitaliy Dragun
 *
 */
public class AwtApplicationWindow extends Frame {

    private static final long serialVersionUID = 1L;

    private static final int WIDTH = 600;

    private static final int HEIGHT = 400;

    private AwtApplicationWindow() {
        super("AWT");
        setResizable(false);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                dispose();
            }
        });

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        pack();
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());
        setMenuBar(createMenuBar());
        add(new TextArea(), BorderLayout.CENTER);
    }

    public static void main(final String[] args) {
        new AwtApplicationWindow().setVisible(true);
    }

    private MenuBar createMenuBar() {
        final MenuBar menuBar = new MenuBar();

        final Menu mFile = new Menu("File");
        menuBar.add(mFile);

        final MenuItem miExit = new MenuItem("Exit");
        miExit.addActionListener(e -> System.exit(0));
        mFile.add(miExit);

        return menuBar;
    }
}
