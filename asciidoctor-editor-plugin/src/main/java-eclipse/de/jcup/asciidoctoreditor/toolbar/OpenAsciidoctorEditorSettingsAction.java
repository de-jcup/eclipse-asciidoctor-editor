/*
 * Copyright 2023 Albert Tregnaghi
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
package de.jcup.asciidoctoreditor.toolbar;

import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.resource.ImageDescriptor;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;
import de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorAttributesPreferencePage;
import de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorEnvironmentPreferencePage;
import de.jcup.eclipse.commons.ui.EclipseUtil;

public class OpenAsciidoctorEditorSettingsAction extends ToolbarAction {

    private static ImageDescriptor IMG_SETTINGS = createToolbarImageDescriptor("settings_obj.png");

    public OpenAsciidoctorEditorSettingsAction(AsciiDoctorEditor editor) {
        super(editor);
        initUI();
    }

    @Override
    public void run() {
        // see https://wiki.eclipse.org/FAQ_How_do_I_launch_the_preference_page_that_belongs_to_my_plug-in%3F
        // be aware that titles must be dei
        PreferenceManager preferenceManager = new PreferenceManager();
        
        IPreferencePage page1 = new AsciiDoctorEditorAttributesPreferencePage();
        page1.setTitle("Asciidoc attributes");
        IPreferenceNode node1 = new PreferenceNode("page1", page1);
        
        IPreferencePage page2 = new AsciiDoctorEditorEnvironmentPreferencePage();
        page2.setTitle("Asciidoc environment");
        IPreferenceNode node2 = new PreferenceNode("page2", page2);
        
        preferenceManager.addToRoot(node1);
        preferenceManager.addToRoot(node2);
        
        PreferenceDialog dialog = new PreferenceDialog(EclipseUtil.getActiveWorkbenchShell(), preferenceManager);
        dialog.create();
        dialog.setSelectedNode("page1");
        dialog.setMessage(page1.getTitle());
        dialog.open();
    }

    private void initUI() {
        initImage();
        initText();
    }

    private void initImage() {
        setImageDescriptor(IMG_SETTINGS);
    }

    private void initText() {
        StringBuilder sb = new StringBuilder();
        sb.append("Open asciidoc editor preferences for rendering");
        setText(sb.toString());
    }

}