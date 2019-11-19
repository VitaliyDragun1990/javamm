
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

package com.revenat.temp.gui.gui3_swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Vitaliy Dragun
 *
 */
@SuppressWarnings("CheckStyle")
public class SwtApplicationWindow {

    private static final int WIDTH = 600;

    private static final int HEIGHT = 400;

    private SwtApplicationWindow() {
        final Display display = new Display();
        final Shell shell = new Shell(display, SWT.SHELL_TRIM & (~SWT.RESIZE));
        shell.setText("SWT");
        final Menu bar = createMenuBar(shell);
        shell.setMenuBar(bar);
        shell.setLayout(new FillLayout());
        new StyledText(shell, SWT.NONE);

        shell.setSize(WIDTH, HEIGHT);

        final Monitor primary = display.getPrimaryMonitor();
        final Rectangle bounds = primary.getBounds();
        final Rectangle rect = shell.getBounds();
        final int x = bounds.x + (bounds.width - rect.width) / 2;
        final int y = bounds.y + (bounds.height - rect.height) / 2;
        shell.setLocation(x, y);

        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();
    }

    public static void main(final String[] args) {
        new SwtApplicationWindow();
    }

    private Menu createMenuBar(final Shell shell) {
        final Menu bar = new Menu(shell, SWT.BAR);

        final MenuItem fileItem = new MenuItem(bar, SWT.CASCADE);
        fileItem.setText("File");

        final Menu submenu = new Menu(shell, SWT.DROP_DOWN);
        fileItem.setMenu(submenu);

        final MenuItem exitItem = new MenuItem(submenu, SWT.PUSH);
        exitItem.addListener(SWT.Selection, e -> System.exit(0));
        exitItem.setText("Exit");

        return bar;
    }
}
