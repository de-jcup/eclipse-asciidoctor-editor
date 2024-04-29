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
package de.jcup.asciidoctoreditor;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.console.IConsolePageParticipant;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import de.jcup.asciidoctoreditor.asciidoc.AsciiDocFileUtils;
import de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorPreferences;
import de.jcup.asciidoctoreditor.template.AsciidoctorEditorTemplateSupportConfig;
import de.jcup.asciidoctoreditor.ui.ColorManager;
import de.jcup.asciidoctoreditor.util.AsciiDoctorEditorUtil;
import de.jcup.eclipse.commons.PluginContextProvider;
import de.jcup.eclipse.commons.keyword.TooltipTextSupport;
import de.jcup.eclipse.commons.resource.EclipseResourceInputStreamProvider;
import de.jcup.eclipse.commons.tasktags.AbstractConfigurableTaskTagsSupportProvider;
import de.jcup.eclipse.commons.templates.TemplateSupportProvider;

/**
 * The activator class controls the plug-in life cycle
 */
public class AsciiDoctorEditorActivator extends AbstractUIPlugin implements PluginContextProvider {

    // The plug-in COMMAND_ID
    public static final String PLUGIN_ID = "de.jcup.asciidoctoreditor"; //$NON-NLS-1$

    // The shared instance
    private static AsciiDoctorEditorActivator plugin;
    private ColorManager colorManager;
    private TemplateSupportProvider templateSupportProvider;

    private Map<StyledText, IConsolePageParticipant> viewers = new HashMap<StyledText, IConsolePageParticipant>();

    private AsciiDoctorEditorTaskTagsSupportProvider taskSupportProvider;

    private ASPSupport aspSupport;

    public AsciiDoctorEditorActivator() {
        colorManager = new ColorManager();
        aspSupport = new ASPSupport();
        templateSupportProvider = new TemplateSupportProvider(new AsciidoctorEditorTemplateSupportConfig(), this);
        taskSupportProvider = new AsciiDoctorEditorTaskTagsSupportProvider(this);
        TooltipTextSupport.setTooltipInputStreamProvider(new EclipseResourceInputStreamProvider(PLUGIN_ID));
    }

    public ColorManager getColorManager() {
        return colorManager;
    }

    public ASPSupport getAspSupport() {
        return aspSupport;
    }

    public void start(BundleContext context) throws Exception {
        super.start(context);

        getAspSupport().start();
        plugin = this;
        cleanupTempFolder();
        taskSupportProvider.getTodoTaskSupport().install();

    }

    public void stop(BundleContext context) throws Exception {
        cleanupTempFolder();
        plugin = null;
//        getAspSupport().stop();
        taskSupportProvider.getTodoTaskSupport().uninstall();
        colorManager.dispose();

        

        super.stop(context);
    }

    private void cleanupTempFolder() {
        try {
            int daysToKeepTempFiles = AsciiDoctorEditorPreferences.getInstance().getDaysToKeepTempFiles();

            AsciiDocFileUtils.deleteEmptyFoldersAndTempFilesOlderThanDaysAnd(daysToKeepTempFiles);
            
        } catch (IOException e) {
            AsciiDoctorEditorUtil.logError("Was not able to cleanup temp folder", e);
        }
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static AsciiDoctorEditorActivator getDefault() {
        return plugin;
    }

    public void addViewer(StyledText viewer, IConsolePageParticipant participant) {
        viewers.put(viewer, participant);
    }

    public void removeViewerWithPageParticipant(IConsolePageParticipant participant) {
        Set<StyledText> toRemove = new HashSet<StyledText>();

        for (StyledText viewer : viewers.keySet()) {
            if (viewers.get(viewer) == participant) {
                toRemove.add(viewer);
            }
        }

        for (StyledText viewer : toRemove) {
            viewers.remove(viewer);
        }

    }

    public TemplateSupportProvider getTemplateSupportProvider() {
        return templateSupportProvider;
    }

    @Override
    public AbstractUIPlugin getActivator() {
        return this;
    }

    @Override
    public String getPluginID() {
        return PLUGIN_ID;
    }

    public AbstractConfigurableTaskTagsSupportProvider getTaskSupportProvider() {
        return taskSupportProvider;
    }

}
