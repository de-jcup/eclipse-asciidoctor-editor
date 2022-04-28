/*
 * Copyright 2018 Albert Tregnaghi
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
package de.jcup.asciidoctoreditor.preview;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ICoreRunnable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Composite;

import de.jcup.asciidoctoreditor.AsciiDoctorEclipseLogAdapter;
import de.jcup.asciidoctoreditor.EclipseDevelopmentSettings;
import de.jcup.asciidoctoreditor.util.EclipseUtil;

/**
 * All browser access must be done over this class. So its easier to handle
 * toggle off when external mode etc.
 * 
 * @author Albert Tregnaghi
 *
 */
public class BrowserAccess {
    private Browser browser;
    private Object monitor = new Object();
    private Composite sashForm;
    private MouseListener listener;

    /**
     * In newer eclipse versions we have a newer SWT which does support EDGE.. As long as we
     * support old eclipse versions as well, we must handle old SWT also and make sure it can be compiled
     * and also run with the old version. So we just copied the new SWT.EDGE constant here into our own code
     * and use it only when EDGE environment variable is set by eclipse (so new one).
     */
    private static int SWT_COMPATIBILITY_ADOPT_EDGE = 1 << 18;

    /*
     * FIXME ATR, 26.04.2018: the initializer parts are no longer used - check if
     * this could be removed
     */
    public interface BrowserContentInitializer {

        public void initialize(Browser browser);

    }

    public void setEnabled(boolean enabled) {
        safeSetBrowserVisible(enabled);
    }

    public BrowserAccess(Composite parent) {
        this.sashForm = parent;
    }

    public void refresh() {
        if (isBrowserNotAvailable()) {
            return;
        }
        browser.refresh();

    }

    private void safeSetBrowserVisible(boolean visible) {
        if (isBrowserNotAvailable()) {
            return;
        }
        browser.setVisible(visible);
    }

    public void navgigateToTopOfView() {
        safeBrowserExecuteJavascript("scroll(0,0)");
    }

    public void dispose() {
        if (browser == null) {
            return;
        }
        if (this.listener != null && !browser.isDisposed()) {
            browser.removeMouseListener(this.listener);
        }
        if (!browser.isDisposed()) {
            browser.dispose();
        }
    }

    public Browser ensureBrowser(BrowserContentInitializer initializer) {
        synchronized (monitor) {
            if (browser == null) {
                // Use edge renderer for SWT browser if available
                String edgeVersion = System.getProperty("org.eclipse.swt.browser.EdgeVersion");
                int browserStyle = edgeVersion != null && !edgeVersion.isEmpty() ? SWT.CENTER | SWT_COMPATIBILITY_ADOPT_EDGE: SWT.CENTER;
                browser = new Browser(sashForm, browserStyle);
                /*
                 * FIXME ATR, 26.04.2018: the initializer parts are no longer used - check if
                 * this could be removed
                 */
                Job job = Job.create("Init browser", new ICoreRunnable() {

                    @Override
                    public void run(IProgressMonitor monitor) throws CoreException {
                        monitor.beginTask("Initializing browser", IProgressMonitor.UNKNOWN);
                        initializer.initialize(browser);
                        monitor.done();
                    }
                });
                job.schedule();
            }
            return browser;
        }
    }

    public void safeBrowserSetText(final String html) {
        if (isBrowserNotAvailable()) {
            return;
        }
        EclipseUtil.safeAsyncExec(new Runnable() {

            @Override
            public void run() {
                if (isBrowserNotAvailable()) {
                    return;
                }
                browser.setText(html);
            }
        });
    }

    /**
     * Installs a mouse listener - we do only suppport ONE mouse listener at same
     * time. Calling this method multiple times will uninstall former one!
     * 
     * @param mouseListener
     */
    public void install(MouseListener mouseListener) {
        if (isBrowserNotAvailable()) {
            return;
        }
        if (this.listener != null) {
            browser.removeMouseListener(this.listener);
        }
        this.listener = mouseListener;
        browser.addMouseListener(listener);
    }

    public void safeBrowserExecuteJavascript(final String javascript) {
        if (isBrowserNotAvailable()) {
            return;
        }
        EclipseUtil.safeAsyncExec(new Runnable() {

            @Override
            public void run() {
                if (isBrowserNotAvailable()) {
                    return;
                }
                if (EclipseDevelopmentSettings.DEBUG_LOGGING_ENABLED) {
                    AsciiDoctorEclipseLogAdapter.INSTANCE.logInfo("safeBrowserExecuteJavascript, sending javascript:" + javascript);
                }
                try {
                    browser.evaluate(javascript);
                } catch (RuntimeException e) {
                    AsciiDoctorEclipseLogAdapter.INSTANCE.logError("Was not able to execute javascript:" + javascript, e);
                }
            }
        });
    }

    public String getUrl() {
        if (isBrowserNotAvailable()) {
            return "";
        }
        return browser.getUrl();
    }

    protected boolean isBrowserNotAvailable() {
        return browser == null || browser.isDisposed();
    }

    public void setUrl(String url) {
        if (isBrowserNotAvailable()) {
            return;
        }
        browser.setUrl(url);
    }

    @SuppressWarnings("unchecked")
    public <T> T safeBrowserEvaluateJavascript(String javascript) {
        if (isBrowserNotAvailable()) {
            return null;
        }
        Object result = null;
        try {
            result = browser.evaluate(javascript);
        } catch (RuntimeException e) {
            AsciiDoctorEclipseLogAdapter.INSTANCE.logError("Was not able to execute javascript:" + javascript, e);
        }
        return (T) result;
    }

}
