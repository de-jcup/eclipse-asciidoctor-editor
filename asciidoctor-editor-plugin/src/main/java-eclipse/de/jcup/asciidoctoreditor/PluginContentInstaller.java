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

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import de.jcup.asciidoctoreditor.asciidoc.AsciiDocFileUtils;
import de.jcup.eclipse.commons.EclipseResourceHelper;

/**
 * This installer is used to install libraries (or server) into user home
 * dir<br>
 * <br>
 * 
 * @author Albert Tregnaghi
 *
 */
public class PluginContentInstaller {
    private static final String CSS = "css";
    private static final String ADDONS = "addons";

    @Deprecated
    private static final String LIBS = "libs";
    private static final String ASP_SERVER_DISTRO_FOLDER = "asp-server";
    private static final String CSS_PLUGIN_ID = "de.jcup.asciidoctoreditor.css";
    private static final String ASP_SERVER_DIST_VERSION = "1.4.1";
    public static final PluginContentInstaller INSTANCE = new PluginContentInstaller();

    private PluginContentInstaller() {

    }

    public File getOrDownloadASPServerDistroFile(IProgressMonitor progressMonitor) throws Exception {
        String versionName = getASPServerDistributionVersionName();

        File aspServerDistroFolder = getHomeSubSubFolder(ASP_SERVER_DISTRO_FOLDER);
        String fileName = "asp-server-asciidoctorj-" + versionName + "-dist.jar";

        File aspServerFileName = new File(aspServerDistroFolder, fileName);
        if (aspServerFileName.exists()) {
            return aspServerFileName;
        }
        String urlAsString = "https://repo1.maven.org/maven2/de/jcup/asp/asp-server-asciidoctorj/" + versionName + "/" + fileName;
        
        runWithTimeout(()-> HttpDownloadUtility.downloadFile(urlAsString, aspServerFileName, progressMonitor),1);
        return aspServerFileName;
    }
    
    public static <T> T runWithTimeout(Callable<T> task, int seconds) throws Exception{
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<T> future = executor.submit(task);
        T result = future.get(seconds, TimeUnit.SECONDS);
        executor.shutdown();
        return result;
    }


    

    public File getAddonsFolder() {
        return ensureEditorAddonsAreAvailable();
    }

    private String getEditorVersionName() {
        return getPluginVersion(AsciiDoctorEditorActivator.PLUGIN_ID);
    }

    private String getASPServerDistributionVersionName() {
        return ASP_SERVER_DIST_VERSION;
    }

    private String getPluginVersion(String pluginId) {
        Bundle bundle = Platform.getBundle(pluginId);
        String versionName = bundle.getVersion().toString();
        return versionName;
    }

    public File getCSSFolder() {
        String versionName = getCSSVersionName();
        return ensureCSSArtefactsAreAvailable(versionName);
    }

    private String getCSSVersionName() {
        return getPluginVersion(CSS_PLUGIN_ID);
    }

    private File ensureCSSArtefactsAreAvailable(String versionName) {
        File cssFolder = getHomeSubSubFolder(CSS);

        File targetVersionCSSfolder = new File(cssFolder, versionName);
        if (!targetVersionCSSfolder.exists()) {
            targetVersionCSSfolder.mkdirs();

            try {
                copyFolderOrFail(targetVersionCSSfolder, CSS, CSS_PLUGIN_ID);
            } catch (IOException e) {
                throw new IllegalStateException("Not able to install CSS files from css plugin", e);
            }

        }
        return targetVersionCSSfolder;
    }

    private File ensureEditorAddonsAreAvailable() {
        String versionName = getEditorVersionName();
        File cssFolder = getHomeSubSubFolder(ADDONS);

        File targetVersionAddonsfolder = new File(cssFolder, versionName);
        if (!targetVersionAddonsfolder.exists()) {
            targetVersionAddonsfolder.mkdirs();

            try {
                copyFolderOrFail(targetVersionAddonsfolder, ADDONS, AsciiDoctorEditorActivator.PLUGIN_ID);
            } catch (IOException e) {
                throw new IllegalStateException("Not able to install addon files from editor plugin", e);
            }

        }
        return targetVersionAddonsfolder;
    }

    private void copyFolderOrFail(File targetFolder, String sourceFolder, String pluginID) throws IOException {
        File folderInPlugin = EclipseResourceHelper.DEFAULT.getFileInPlugin(sourceFolder, pluginID);
        if (folderInPlugin == null) {
            throw new IllegalStateException("sourceFolder:" + sourceFolder + " not found in plugin!");
        }
        if (!folderInPlugin.exists()) {
            throw new IllegalStateException("folder:" + folderInPlugin.getAbsolutePath() + " does not exist!");
        }

        FileUtils.copyDirectory(folderInPlugin, targetFolder);
    }

    private File getHomeSubSubFolder(String name) {
        return new File(getHomeSubFolder(), name);
    }

    private File getHomeSubFolder() {
        return AsciiDocFileUtils.getEditorHomeSubFolder();
    }

}
