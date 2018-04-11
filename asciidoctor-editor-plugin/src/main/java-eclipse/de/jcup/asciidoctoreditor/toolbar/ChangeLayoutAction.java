package de.jcup.asciidoctoreditor.toolbar;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;
import de.jcup.asciidoctoreditor.PreviewLayout;

public class ChangeLayoutAction extends ToolbarAction implements IMenuCreator {

	private static final String SWITCH_TO_VERTICAL_LAYOUT = "Vertical layout";
	private static final String SWITCH_TO_HORIZONTAL_LAYOUT = "Horizontal layout";
	private static final String SWITCH_TO_EXTERNAL_LAYOUT = "Hide preview panel and use external browser";
	private static ImageDescriptor IMG_LAYOUT_VERTICAL = createToolbarImageDescriptor("layout_vertical.png");
	private static ImageDescriptor IMG_LAYOUT_HORIZONTAL = createToolbarImageDescriptor("layout_horizontal.png");
	private static ImageDescriptor IMG_LAYOUT_EXTERNAL = createToolbarImageDescriptor("layout_external.png");
	private Menu menu;

	public ChangeLayoutAction(AsciiDoctorEditor asciiDoctorEditor) {
		super(asciiDoctorEditor);
		initUI();
	}

	@Override
	public void run() {
	}

	private void initUI() {
		setMenuCreator(this);
		initImageAndText();
	}

	private void initImageAndText() {
		if (asciiDoctorEditor.isPreviewVisible()) {
			setImageDescriptor(this.asciiDoctorEditor.isVerticalSplit() ? IMG_LAYOUT_VERTICAL : IMG_LAYOUT_HORIZONTAL);
			setText(this.asciiDoctorEditor.isVerticalSplit() ? SWITCH_TO_VERTICAL_LAYOUT: SWITCH_TO_HORIZONTAL_LAYOUT);
		} else {
			setImageDescriptor(IMG_LAYOUT_EXTERNAL);
			setText(SWITCH_TO_EXTERNAL_LAYOUT);
		}
	}

	public void dispose() {
		if (menu != null) {
			menu.dispose();
			menu = null;
		}
	}

	public Menu getMenu(Menu parent) {
		return null;
	}

	public Menu getMenu(Control parent) {
		if (menu != null) {
			menu.dispose();
		}

		menu = new Menu(parent);

		Action switchToHorizontal = new Action(SWITCH_TO_HORIZONTAL_LAYOUT, IMG_LAYOUT_HORIZONTAL) {
			public void run() {
				asciiDoctorEditor.setVerticalSplit(false);
				asciiDoctorEditor.setPreviewVisible(true);
				initImageAndText();
			}
		};
		Action switchToVertical = new Action(SWITCH_TO_VERTICAL_LAYOUT, IMG_LAYOUT_VERTICAL) {
			public void run() {
				asciiDoctorEditor.setVerticalSplit(true);
				asciiDoctorEditor.setPreviewVisible(true);
				initImageAndText();
			}
		};
		Action switchToExternal = new Action(SWITCH_TO_EXTERNAL_LAYOUT, IMG_LAYOUT_EXTERNAL) {
			public void run() {
				asciiDoctorEditor.setPreviewVisible(false);
				initImageAndText();
			}
		};

		addActionToMenu(menu, switchToHorizontal);
		addActionToMenu(menu, switchToVertical);
		new MenuItem(menu, SWT.SEPARATOR);
		addActionToMenu(menu, switchToExternal);

		return menu;
	}

	protected void addActionToMenu(Menu parent, Action action) {
		ActionContributionItem item = new ActionContributionItem(action);
		item.fill(parent, -1);
	}

	/**
	 * Get's rid of the menu, because the menu hangs on to the searches, etc.
	 */
	void clear() {
		dispose();
	}

}
