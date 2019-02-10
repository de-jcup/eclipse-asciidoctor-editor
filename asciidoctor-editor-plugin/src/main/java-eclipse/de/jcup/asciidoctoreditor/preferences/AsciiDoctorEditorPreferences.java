package de.jcup.asciidoctoreditor.preferences;
/*
 * Copyright 2017 Albert Tregnaghi
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

import static de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorPreferenceConstants.*;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;
import de.jcup.asciidoctoreditor.AsciiDoctorEditorActivator;
import de.jcup.asciidoctoreditor.EclipseUtil;
import de.jcup.asciidoctoreditor.PreviewLayout;
import de.jcup.eclipse.commons.ui.ColorUtil;

public class AsciiDoctorEditorPreferences {

	private static AsciiDoctorEditorPreferences INSTANCE = new AsciiDoctorEditorPreferences();
	private IPreferenceStore store;

	private AsciiDoctorEditorPreferences() {
		store = new ScopedPreferenceStore(InstanceScope.INSTANCE, AsciiDoctorEditorActivator.PLUGIN_ID);
		store.addPropertyChangeListener(new IPropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (event == null) {
					return;
				}
				String property = event.getProperty();
				if (property == null) {
					return;
				}
				ChangeContext context = new ChangeContext();
				for (AsciiDoctorEditorSyntaxColorPreferenceConstants c : AsciiDoctorEditorSyntaxColorPreferenceConstants
						.values()) {
					if (property.equals(c.getId())) {
						context.colorChanged = true;
						break;
					}
				}
				for (AsciiDoctorEditorValidationPreferenceConstants c : AsciiDoctorEditorValidationPreferenceConstants
						.values()) {
					if (property.equals(c.getId())) {
						context.validationChanged = true;
						break;
					}
				}

				updateColorsInAsciiDoctorEditors(context);

			}

			private void updateColorsInAsciiDoctorEditors(ChangeContext context) {
				if (!context.hasChanges()) {
					return;
				}
				/* inform all AsciiDoctorWrapper editors about color changes */
				IWorkbenchPage activePage = EclipseUtil.getActivePage();
				if (activePage == null) {
					return;
				}
				IEditorReference[] references = activePage.getEditorReferences();
				for (IEditorReference ref : references) {
					IEditorPart editor = ref.getEditor(false);
					if (editor == null) {
						continue;
					}
					if (!(editor instanceof AsciiDoctorEditor)) {
						continue;
					}
					AsciiDoctorEditor geditor = (AsciiDoctorEditor) editor;
					if (context.colorChanged) {
						geditor.handleColorSettingsChanged();
					}
					if (context.validationChanged) {
						geditor.getOutlineSupport().rebuildOutline();
					}
				}
			}
		});

	}

	private class ChangeContext {
		private boolean colorChanged = false;
		private boolean validationChanged = false;

		private boolean hasChanges() {
			boolean changedAtAll = colorChanged;
			changedAtAll = changedAtAll || validationChanged;
			return changedAtAll;
		}
	}

	public String getStringPreference(AsciiDoctorEditorPreferenceConstants id) {
		String data = getPreferenceStore().getString(id.getId());
		if (data == null) {
			data = "";
		}
		return data;
	}

	public int getIntegerPreference(AsciiDoctorEditorPreferenceConstants id) {
		int data = getPreferenceStore().getInt(id.getId());
		return data;
	}

	public boolean getBooleanPreference(AsciiDoctorEditorPreferenceConstants id) {
		boolean data = getPreferenceStore().getBoolean(id.getId());
		return data;
	}

	public void setBooleanPreference(AsciiDoctorEditorPreferenceConstants id, boolean value) {
		getPreferenceStore().setValue(id.getId(), value);
	}
	

	public void setStringPreference(AsciiDoctorEditorPreferenceConstants id,
			String value) {
		getPreferenceStore().setValue(id.getId(), value);
	}

	public boolean isLinkOutlineWithEditorEnabled() {
		return getBooleanPreference(P_LINK_OUTLINE_WITH_EDITOR);
	}
	public boolean isLinkEditorWithPreviewEnabled() {
		return getBooleanPreference(P_LINK_EDITOR_WITH_PREVIEW);
	}

	public boolean isAutoBuildEnabledForExternalPreview() {
		return getBooleanPreference(P_EDITOR_AUTOBUILD_FOR_EXTERNAL_PREVIEW_ENABLED);
	}

	public IPreferenceStore getPreferenceStore() {
		return store;
	}

	public boolean getDefaultBooleanPreference(AsciiDoctorEditorPreferenceConstants id) {
		boolean data = getPreferenceStore().getDefaultBoolean(id.getId());
		return data;
	}

	public RGB getColor(PreferenceIdentifiable identifiable) {
		RGB color = PreferenceConverter.getColor(getPreferenceStore(), identifiable.getId());
		return color;
	}

	/**
	 * Returns color as a web color in format "#RRGGBB"
	 * 
	 * @param identifiable
	 * @return web color string
	 */
	public String getWebColor(PreferenceIdentifiable identifiable) {
		RGB color = getColor(identifiable);
		if (color == null) {
			return null;
		}
		String webColor = ColorUtil.convertToHexColor(color);
		return webColor;
	}

	public void setDefaultColor(PreferenceIdentifiable identifiable, RGB color) {
		PreferenceConverter.setDefault(getPreferenceStore(), identifiable.getId(), color);
	}

	public static AsciiDoctorEditorPreferences getInstance() {
		return INSTANCE;
	}

	public int getAutoRefreshInSecondsForExternalBrowser() {
		if (!isAutoBuildEnabledForExternalPreview()) {
			/*
			 * when disabled auto build a refresh does not make any sense... so
			 * always return 0 seconds
			 */
			return 0;
		}
		return getPreferenceStore().getInt(P_EDITOR_AUTOREFRESH_EXTERNAL_BROWSER_IN_SECONDS.getId());
	}

	public PreviewLayout getInitialLayoutModeForNewEditors() {
		String layoutMode = getStringPreference(AsciiDoctorEditorPreferenceConstants.P_EDITOR_NEWEDITOR_PREVIEW_LAYOUT);
		PreviewLayout layout = PreviewLayout.fromId(layoutMode);
		if (layout == null) {
			layout = PreviewLayout.VERTICAL;
		}
		return layout;
	}

	public boolean isUsingInstalledAsciidoctor() {
		return getBooleanPreference(P_USE_INSTALLED_ASCIIDOCTOR_ENABLED);
	}
	
	public String getArgumentsForInstalledAsciidoctor(){
		return getStringPreference(P_INSTALLED_ASCIICDOCTOR_ARGUMENTS);
	}

	public boolean isConsoleAlwaysShownOnError() {
		return getBooleanPreference(P_SHOW_ASCIIDOC_CONSOLE_ON_ERROR_OUTPUT);
	}

	public String getPathToInstalledAsciidoctor() {
		return getStringPreference(AsciiDoctorEditorPreferenceConstants.P_PATH_TO_INSTALLED_ASCIICDOCTOR);
	}

	public boolean isCopyImagesForPreview() {
		return getBooleanPreference(P_COPY_IMAGES_FOR_PREVIEW);
	}
}
