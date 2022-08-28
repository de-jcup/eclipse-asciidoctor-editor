/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
package de.jcup.asciidoctoreditor.ui;

import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import de.jcup.asciidoctoreditor.AdaptedFromEGradle;
import de.jcup.asciidoctoreditor.util.EclipseUtil;

@AdaptedFromEGradle
abstract class AbstractQuickDialog extends PopupDialog {

    protected static final boolean GRAB_FOCUS = true;
    protected static final boolean PERSIST_NO_SIZE = false;
    protected static final boolean PERSIST_SIZE = true;

    protected static final boolean PERSIST_NO_BOUNDS = false;
    protected static final boolean PERSIST_BOUNDS = true;

    protected static final boolean SHOW_DIALOG_MENU = true;
    protected static final boolean SHOW_NO_DIALOG_MENU = false;

    protected static final boolean SHOW_NO_PERSIST_ACTIONS = false;
    protected static final boolean SHOW_PERSIST_ACTIONS = true;

    AbstractQuickDialog(Shell parent, int shellStyle, boolean takeFocusOnOpen, boolean persistSize, boolean persistLocation, boolean showDialogMenu, boolean showPersistActions, String titleText,
            String infoText) {
        super(parent, shellStyle, takeFocusOnOpen, persistSize, persistLocation, showDialogMenu, showPersistActions, titleText, infoText);
    }

    @Override
    public final int open() {
        int value = super.open();
        beforeRunEventLoop();
        runEventLoop(getShell());
        return value;
    }

    protected void beforeRunEventLoop() {

    }

    private void runEventLoop(Shell loopShell) {
        Display display;
        if (getShell() == null) {
            display = Display.getCurrent();
        } else {
            display = loopShell.getDisplay();
        }

        while (loopShell != null && !loopShell.isDisposed()) {
            try {
                if (!display.readAndDispatch()) {
                    display.sleep();
                }
            } catch (Throwable e) {
                EclipseUtil.logError("UI problems on dispatch", e);
            }
        }
        if (!display.isDisposed()) {
            display.update();
        }
    }

    @Override
    protected boolean canHandleShellCloseEvent() {
        return true;
    }

}