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
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.console.IConsolePageParticipant;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import de.jcup.asciidoctoreditor.asciidoc.AsciiDocFileUtils;
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
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Proxy getProxy(URI uri) {
            BundleContext context = getBundle().getBundleContext();
            ServiceTracker<IProxyService, ?> proxyTracker;
            proxyTracker = new ServiceTracker(context, IProxyService.class, null);
            proxyTracker.open();
        
            IProxyService proxyService = (IProxyService) proxyTracker.getService();
            IProxyData[] proxyDataForHost = proxyService.select(uri);
            Set<String> propertyList = new LinkedHashSet<>();

            
        Proxy proxy = null;
        for (IProxyData data : proxyDataForHost) {
            String host = data.getHost();
            if (host == null) {
                continue;
            }
            String type = data.getType();

            if (type == null) {
                continue;
            }
            type=type.toLowerCase();
            int port = data.getPort();

            if (data.isRequiresAuthentication()) {
                String userid = data.getUserId();
                String pwd = data.getPassword();
            }
            Type proxyType = null;
            if (type.startsWith("http")) {
                proxyType=Type.HTTP;
            }else if (type.startsWith("socks")) {
                proxyType=Type.SOCKS;
            }else {
                proxyType=Type.DIRECT;
            }
            proxy = new Proxy(proxyType,InetSocketAddress.createUnresolved(checkHost(host), port));
            
            break;

        }
        proxyTracker.close();
        if (proxy==null) {
            proxy = Proxy.NO_PROXY;
        }
        return proxy;
    }
    
    private static String checkHost(String h) {
        if (h != null) {
            if (h.indexOf('\n') > -1) {
                throw new IllegalStateException("Illegal character in host");
            }
        }
        return h;
    }
    

    public void start(BundleContext context) throws Exception {
        super.start(context);

        cleanupTempFolder();
        
        getAspSupport().start();
        plugin = this;
        taskSupportProvider.getTodoTaskSupport().install();
        

    }

    public void stop(BundleContext context) throws Exception {
        plugin = null;
        getAspSupport().stop();
        taskSupportProvider.getTodoTaskSupport().uninstall();
        
        colorManager.dispose();
        
        cleanupTempFolder();
        
        super.stop(context);
    }

    private void cleanupTempFolder() throws IOException {
        
        AsciiDocFileUtils.deleteEmptyFoldersAndTempFilesOlderThanDaysAnd(AsciiDoctorEditorPreferences.getInstance().getDaysToKeepTempFiles());
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
