
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

package com.revenat.temp.gui.gui2_swing;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * @author Vitaliy Dragun
 *
 */
public class SwingApplicationWindow extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final int WIDTH = 600;

    private static final int HEIGHT = 400;

    static {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    private SwingApplicationWindow() {
        super("Swing");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        pack();
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());
        final JMenuBar menuBar = createMenuBar();
        add(menuBar, BorderLayout.NORTH);
        add(new JTextArea(), BorderLayout.CENTER);
    }

    public static void main(final String[] args) {
        SwingUtilities.invokeLater(() -> new SwingApplicationWindow().setVisible(true));
    }

    private JMenuBar createMenuBar() {
        final JMenuBar menuBar = new JMenuBar();
        final JMenu menuFile = new JMenu("File");
        menuBar.add(menuFile);

        final JMenuItem menuItemExit = new JMenuItem("Exit");
        menuItemExit.addActionListener(e -> System.exit(0));
        menuFile.add(menuItemExit);

        return menuBar;
    }
}
