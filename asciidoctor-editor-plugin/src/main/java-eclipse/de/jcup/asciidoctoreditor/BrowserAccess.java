package de.jcup.asciidoctoreditor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ICoreRunnable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;

/**
 * All browser access must be done over this class. So its easier to handle toggle off when external mode etc.
 * @author Albert Tregnaghi
 *
 */
public class BrowserAccess {
	private Browser browser;
	private Object monitor = new Object();
	private Composite sashForm;
	private boolean enabled;

	public interface BrowserContentInitializer {

		public void initialize(Browser browser);

	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		safeSetBrowserVisible(enabled);
	}

	public boolean isEnabled() {
		return enabled;
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
		boolean isVisible = browser.isVisible();
		if (isVisible == visible) {
			return;
		}
		browser.setVisible(visible);
	}

	public void navgigateToTopOfView() {
		if (isBrowserNotAvailable()) {
			return;
		}
		browser.evaluate("scroll(0,0)");
	}

	public void dispose() {
		if (browser == null) {
			return;
		}
		if (!browser.isDisposed()) {
			browser.dispose();
		}
	}

	public Browser ensureBrowser(BrowserContentInitializer initializer) {
		synchronized (monitor) {
			if (browser == null) {
				browser = new Browser(sashForm, SWT.CENTER);
				safeBrowserSetText("Initializing...");
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

	public void safeBrowserSetText(String html) {
		if (isBrowserNotAvailable()) {
			return;
		}
		if (!isEnabled()) {
			return;
		}
		browser.setText(html);
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

}
