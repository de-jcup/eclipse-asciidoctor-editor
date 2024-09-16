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
import de.jcup.asciidoctoreditor.PreviewLayout;
import de.jcup.asciidoctoreditor.util.EclipseUtil;
import de.jcup.eclipse.commons.ui.ColorUtil;

public class AsciiDoctorEditorPreferences {

    private static AsciiDoctorEditorPreferences INSTANCE = new AsciiDoctorEditorPreferences();
    private IPreferenceStore store;

    public static AsciiDoctorEditorPreferences getInstance() {
        return INSTANCE;
    }

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
                for (AsciiDoctorEditorSyntaxColorPreferenceConstants c : AsciiDoctorEditorSyntaxColorPreferenceConstants.values()) {
                    if (property.equals(c.getId())) {
                        context.colorChanged = true;
                        break;
                    }
                }
                for (AsciiDoctorEditorValidationPreferenceConstants c : AsciiDoctorEditorValidationPreferenceConstants.values()) {
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
                        geditor.validate();
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

    public String getStringPreference(PreferenceIdentifiable id) {
        String data = getPreferenceStore().getString(id.getId());
        if (data == null) {
            data = "";
        }
        return data;
    }

    public int getIntegerPreference(PreferenceIdentifiable id) {
        int data = getPreferenceStore().getInt(id.getId());
        return data;
    }

    public boolean getBooleanPreference(PreferenceIdentifiable id) {
        boolean data = getPreferenceStore().getBoolean(id.getId());
        return data;
    }

    public void setBooleanPreference(PreferenceIdentifiable id, boolean value) {
        getPreferenceStore().setValue(id.getId(), value);
    }

    public void setStringPreference(PreferenceIdentifiable id, String value) {
        getPreferenceStore().setValue(id.getId(), value);
    }

    public boolean getDefaultBooleanPreference(PreferenceIdentifiable id) {
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

    /*
     * -----------------------------------------------------------------------------
     * ------------
     */
    /*
     * - ....................... Dedicated getter/setter
     * ..................................... -
     */
    /*
     * -----------------------------------------------------------------------------
     * ------------
     */
    public boolean isLinkOutlineWithEditorEnabled() {
        return getBooleanPreference(P_LINK_OUTLINE_WITH_EDITOR);
    }

    public boolean isLinkEditorWithPreviewEnabled() {
        return getBooleanPreference(P_LINK_EDITOR_WITH_PREVIEW);
    }
    public boolean isLinkEditorWithPreviewUsingTextSelectionAsFallback() {
        return getBooleanPreference(P_LINK_BETWEEN_EDITOR_AND_PREVIEW_USES_TEXT_SELECTION_AS_FALLBACK);
    }

    public boolean isAutoBuildEnabledForExternalPreview() {
        return getBooleanPreference(P_EDITOR_AUTOBUILD_FOR_EXTERNAL_PREVIEW_ENABLED);
    }
    
    public boolean isAutoRefreshEnabledForExternalPreview() {
        return getBooleanPreference(P_EDITOR_EXTERNAL_PREVIEW_AUTOREFRESH_ENABLED);
    }

    public int getAutoRefreshInSecondsForExternalBrowser() {
        return getPreferenceStore().getInt(P_EDITOR_EXTERNAL_PREVIEW_AUTOREFRESH_IN_SECONDS.getId());
    }

    public IPreferenceStore getPreferenceStore() {
        return store;
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

    public String getArgumentsForInstalledAsciidoctor() {
        return getStringPreference(P_INSTALLED_ASCIICDOCTOR_ARGUMENTS);
    }

    public boolean isConsoleAlwaysShownOnError() {
        return getBooleanPreference(P_SHOW_ASCIIDOC_CONSOLE_ON_ERROR_OUTPUT);
    }
    
    public String getPathToInstalledAsciidoctor() {
        return getStringPreference(AsciiDoctorEditorPreferenceConstants.P_PATH_TO_INSTALLED_ASCIICDOCTOR);
    }

    public boolean isUsingPreviewImageDirectory() {
        return true; // we do now always use the preview image directory see
                     // https://github.com/de-jcup/eclipse-asciidoctor-editor/issues/314
//        return getBooleanPreference(P_USE_PREVIEW_IMAGEDIRECTORY);
    }

    public int getAspServerMinPort() {
        return getPreferenceStore().getInt(P_ASP_SERVER_MIN_PORT.getId());
    }

    public int getAspServerMaxPort() {
        return getPreferenceStore().getInt(P_ASP_SERVER_MAX_PORT.getId());
    }

    public boolean isURLValidationEnabled() {
        return getBooleanPreference(AsciiDoctorEditorValidationPreferenceConstants.VALIDATE_URLS);
    }

    public boolean isIncludeValidationEnabled() {
        return getBooleanPreference(AsciiDoctorEditorValidationPreferenceConstants.VALIDATE_INCLUDES);
    }

    public boolean isImageValidationEnabled() {
        return getBooleanPreference(AsciiDoctorEditorValidationPreferenceConstants.VALIDATE_IMAGES);
    }

    public boolean isDiagramValidationEnabled() {
        return getBooleanPreference(AsciiDoctorEditorValidationPreferenceConstants.VALIDATE_DIAGRAMS);
    }

    public boolean isShowingAspLogsAsMarkerInEditor() {
        return getBooleanPreference(AsciiDoctorEditorPreferenceConstants.P_ASP_SERVER_LOGS_SHOWN_AS_MARKER_IN_EDITOR);
    }

    public boolean isShowingAspServerOutputInConsole() {
        return getBooleanPreference(AsciiDoctorEditorPreferenceConstants.P_ASP_SERVER_OUTPUT_SHOWN_IN_CONSOLE);
    }

    public boolean isShowingAspCommunicationInConsole() {
        return getBooleanPreference(AsciiDoctorEditorPreferenceConstants.P_ASP_COMMUNICATION_SHOWN_IN_CONSOLE);
    }

    public boolean isDynamicCodeAssistForIncludesEnabled() {
        return getBooleanPreference(AsciiDoctorEditorPreferenceConstants.P_CODE_ASSIST_DYNAMIC_FOR_INCLUDES);
    }

    public boolean isDynamicCodeAssistForImagesEnabled() {
        return getBooleanPreference(AsciiDoctorEditorPreferenceConstants.P_CODE_ASSIST_DYNAMIC_FOR_IMAGES);
    }

    public boolean isDynamicCodeAssistForPlantumlEnabled() {
        return getBooleanPreference(AsciiDoctorEditorPreferenceConstants.P_CODE_ASSIST_DYNAMIC_FOR_PLANTUML_MACRO);
    }

    public boolean isDynamicCodeAssistForDitaaEnabled() {
        return getBooleanPreference(AsciiDoctorEditorPreferenceConstants.P_CODE_ASSIST_DYNAMIC_FOR_DITAA_MACRO);
    }

    public String getPathToJavaBinaryForASPLaunch() {
        return getStringPreference(AsciiDoctorEditorPreferenceConstants.P_PATH_TO_JAVA_BINARY_FOR_ASP_LAUNCH);
    }

    public boolean isGroupingInOutlineEnabledPerDefault() {
        return getBooleanPreference(AsciiDoctorEditorPreferenceConstants.P_OUTLINE_GROUPING_ENABLED_PER_DEFAULT);
    }

    /* ------------------------------------ */
    /* - plantuml parts */ // maybe own class in future?
    /* ------------------------------------ */

    public boolean isAutoCreateConfigEnabled() {
        return getBooleanPreference(AsciiDoctorEditorPreferenceConstants.P_AUTOCREATE_INITIAL_CONFIGFILE);
    }

    public int getDaysToKeepTempFiles() {
        return getIntegerPreference(AsciiDoctorEditorPreferenceConstants.P_DAYS_TO_KEEP_TEMPFILES);
    }

    public String getPlantUMLDefaultZoomLevelAsText() {
        return getStringPreference(AsciiDoctorPlantUMLEditorPreferenceConstants.P_DEFAULT_ZOOM_LEVEL);
    }

    public boolean getTocVisibleOnNewEditorsPerDefault() {
        return getBooleanPreference(AsciiDoctorEditorPreferenceConstants.P_TOC_VISIBLE_ON_NEW_EDITORS_PER_DEFAULT);
    }

}
