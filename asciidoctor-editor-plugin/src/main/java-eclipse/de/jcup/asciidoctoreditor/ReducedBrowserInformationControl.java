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
 package de.jcup.asciidoctoreditor;

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.text.AbstractInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;

import de.jcup.asciidoctoreditor.AdaptedFromEGradle;

@AdaptedFromEGradle// from SimpleBrowserInformationControl - but changed strong - reduced features.
public class ReducedBrowserInformationControl extends AbstractInformationControl {

	private static boolean browserAvailabilityChecked;
	private static boolean swtBrowserCanBeUsed;
	private static Point cachedScrollBarSize;
	private String currentHTML;
	private Browser browser;

	/**
	 * Creates an simple browser information control being resizable, providing
	 * a toolbar and uses hyperlink listener
	 * 
	 * @param parentShell
	 */
	public ReducedBrowserInformationControl(Shell parentShell) {
		super(parentShell, new ToolBarManager());
		create();
	}

	@Override
	public void setBackgroundColor(Color background) {
		super.setBackgroundColor(background);
		if (isBrowserNotDisposed()) {
			browser.setBackground(background);
		}
	}

	@Override
	public void setForegroundColor(Color foreground) {
		super.setForegroundColor(foreground);
		if (isBrowserNotDisposed()) {
			browser.setForeground(foreground);
		}

	}

	@Override
	public void setInformation(String information) {
		if (isBrowserNotDisposed()) {
			boolean hasHtmlElementInside = information.startsWith("<html");
			StringBuilder htmlSb = new StringBuilder();
			if (!hasHtmlElementInside) {
				htmlSb.append("<html><body>");
			}
			htmlSb.append(information);
			if (!hasHtmlElementInside) {
				htmlSb.append("</body></html>");
			}
			this.currentHTML = htmlSb.toString();
			browser.setText(information);
		}
	}

	public void redraw() {
		if (isBrowserNotDisposed()) {
			browser.redraw();
		}
	}

	/**
	 * Tells whether this control is available for given parent composite
	 * 
	 * @param parent
	 *            the parent component used for checking or <code>null</code> if
	 *            none
	 * @return <code>true</code> if this control is available
	 */
	public static boolean isAvailableFor(Composite parent) {
		if (!browserAvailabilityChecked) {
			try {
				Browser browser = new Browser(parent, SWT.NONE);
				browser.dispose();
				swtBrowserCanBeUsed = true;

				/* compute scrollbar size */
				Slider sliderV = new Slider(parent, SWT.VERTICAL);
				Slider sliderH = new Slider(parent, SWT.HORIZONTAL);

				int width = sliderV.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
				int height = sliderH.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;

				cachedScrollBarSize = new Point(width, height);

				sliderV.dispose();
				sliderH.dispose();

			} catch (SWTError er) {
				swtBrowserCanBeUsed = false;
			} finally {
				browserAvailabilityChecked = true;
			}
		}

		return swtBrowserCanBeUsed;
	}

	@Override
	public boolean hasContents() {
		return currentHTML != null && currentHTML.length() > 0;
	}

	int getToolbarWidth() {
		assertAvailable();
		return cachedScrollBarSize.x;
	}

	int getToolbarHeight() {
		assertAvailable();
		return cachedScrollBarSize.x;
	}

	@Override
	protected void createContent(Composite parent) {
		assertAvailable();
		browser = new Browser(parent, SWT.FILL);
		browser.setJavascriptEnabled(false);

		/* disable browser menues */
		browser.setMenu(new Menu(getShell(), SWT.NONE));

	}

	@Override
	public void dispose() {
		super.dispose();
		if (isBrowserNotDisposed()) {

			browser.dispose();
		}
	}

	private boolean isBrowserNotDisposed() {
		return browser != null && !browser.isDisposed();
	}

	private void assertAvailable() {
		if (!browserAvailabilityChecked) {
			throw new IllegalStateException("Availability not checked before!");
		}
		if (!swtBrowserCanBeUsed) {
			throw new IllegalStateException("Availibility was checked but SWT browser cannot be used!");
		}
	}

	@Override
	public IInformationControlCreator getInformationPresenterControlCreator() {
		return new IInformationControlCreator() {
			@Override
			public IInformationControl createInformationControl(Shell parent) {
				ReducedBrowserInformationControl newControl = new ReducedBrowserInformationControl(parent);
				return newControl;
			}
		};
	}
}
