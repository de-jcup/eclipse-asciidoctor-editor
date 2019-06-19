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

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.console.IConsolePageParticipant;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import de.jcup.asciidoctoreditor.asciidoc.ASPServerAdapter;
import de.jcup.asciidoctoreditor.console.AsciiDoctorConsoleUtil;
import de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorPreferences;
import de.jcup.asciidoctoreditor.template.AsciidoctorEditorTemplateSupportConfig;
import de.jcup.asciidoctoreditor.ui.ColorManager;
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
    private ASPServerAdapter aspServerAdapter = new ASPServerAdapter();

    public AsciiDoctorEditorActivator() {
        colorManager = new ColorManager();
        templateSupportProvider = new TemplateSupportProvider(new AsciidoctorEditorTemplateSupportConfig(), this);
        taskSupportProvider = new AsciiDoctorEditorTaskTagsSupportProvider(this);
        TooltipTextSupport.setTooltipInputStreamProvider(new EclipseResourceInputStreamProvider(PLUGIN_ID));
    }

    public ColorManager getColorManager() {
        return colorManager;
    }

    public void start(BundleContext context) throws Exception {
        super.start(context);
        updateASPServerStart();
        plugin = this;

        taskSupportProvider.getTodoTaskSupport().install();
    }
    public void updateASPServerStart() {
        Thread t = new Thread(()->internalUpdateASPServerStart(),"Update ASP server start");
        t.start();
    }
    private void internalUpdateASPServerStart() {
        boolean usesInstalledAsciidoctor = AsciiDoctorEditorPreferences.getInstance().isUsingInstalledAsciidoctor();
        if (usesInstalledAsciidoctor) {
            if (aspServerAdapter.isServerStarted()) {
                AsciiDoctorConsoleUtil.output(">> Stopping ASP server because using now installed asciidoctor");
                aspServerAdapter.stopServer();
                return;
            }
        }else {
            File aspFolder = PluginContentInstaller.INSTANCE.getLibsFolder();
            File aspServer = new File(aspFolder,"asp-server-asciidoctorj.jar");
           
            String pathToJava= AsciiDoctorEditorPreferences.getInstance().getPathToJavaForASPLaunch();
            aspServerAdapter.setPathToJava(pathToJava.trim());
            aspServerAdapter.setPathToServerJar(aspServer.getAbsolutePath());
            aspServerAdapter.setPort(AsciiDoctorEditorPreferences.getInstance().getAspServerPort());
            aspServerAdapter.setConsoleAdapter(AsciiDoctorEclipseConsoleAdapter.INSTANCE);
            if (! aspServerAdapter.isAlive()) { // check if new setup is alive
                aspServerAdapter.stopServer(); // stop old processes
                AsciiDoctorConsoleUtil.output(">> ASP server not alive at port "+aspServerAdapter.getPort()+", so starting new instance");
                aspServerAdapter.startServer();
                return;
            }else {
                AsciiDoctorConsoleUtil.output(">> ASP server already alive at port "+aspServerAdapter.getPort()+", so reusing instance");
            }
        }

    }

    public void stop(BundleContext context) throws Exception {
        plugin = null;
        aspServerAdapter.stopServer();
        taskSupportProvider.getTodoTaskSupport().uninstall();
        colorManager.dispose();
        super.stop(context);
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
