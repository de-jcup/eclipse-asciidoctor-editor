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

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import de.jcup.eclipse.commons.EclipseResourceHelper;

/**
 * This installer is used to install libraries (or server) into user home dir<br>
 * <br>
 * 
 * @author Albert Tregnaghi
 *
 */
public class PluginContentInstaller {
    private static final String CSS = "css";
    private static final String ADDONS = "addons";
    private static final String LIBS = "libs";
    private static final String CSS_PLUGIN_ID = "de.jcup.asciidoctoreditor.css";
	private static final String LIBS_PLUGIN_ID = "de.jcup.asciidoctoreditor.libs";
	public static final PluginContentInstaller INSTANCE = new PluginContentInstaller();
	
	private PluginContentInstaller() {
		
	}

	public File getLibsFolder(){
		String versionName = getLibVersionName();
		return ensureLibsAreAvailable(versionName);
	}
	
	public File getAddonsFolder(){
		return ensureEditorAddonsAreAvailable();
	}
	
	private String getEditorVersionName() {
		return getPluginVersion(AsciiDoctorEditorActivator.PLUGIN_ID);
	}
	private String getLibVersionName() {
		return getPluginVersion(LIBS_PLUGIN_ID);
	}
	
	private String getPluginVersion(String pluginId){
		Bundle bundle = Platform.getBundle(pluginId);
		String versionName = bundle.getVersion().toString();
		return versionName;
	}

	public File getCSSFolder(){
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
                copyFolderOrFail(targetVersionCSSfolder, CSS,CSS_PLUGIN_ID);
            } catch (IOException e) {
                throw new IllegalStateException("Not able to install CSS files from css plugin", e);
            }

        }
        return targetVersionCSSfolder;
}
	private File ensureLibsAreAvailable(String versionName) {
        File libsFolder = getHomeSubSubFolder(LIBS);

        File targetVersionCSSfolder = new File(libsFolder, versionName);
        if (!targetVersionCSSfolder.exists()) {
            targetVersionCSSfolder.mkdirs();

            try {
                copyFolderOrFail(targetVersionCSSfolder, LIBS,LIBS_PLUGIN_ID);
            } catch (IOException e) {
                throw new IllegalStateException("Not able to install Server files from libs plugin", e);
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
				copyFolderOrFail(targetVersionAddonsfolder, ADDONS,AsciiDoctorEditorActivator.PLUGIN_ID);
			} catch (IOException e) {
				throw new IllegalStateException("Not able to install addon files from editor plugin", e);
			}

		}
		return targetVersionAddonsfolder;
	}
	
	private void copyFolderOrFail(File targetFolder, String sourceFolder, String pluginID) throws IOException {
		File folderInPlugin = EclipseResourceHelper.DEFAULT.getFileInPlugin(sourceFolder, pluginID);
		if (folderInPlugin == null) {
            throw new IllegalStateException("sourceFolder:" + sourceFolder +" not found in plugin!");
        }
		if (!folderInPlugin.exists()) {
			throw new IllegalStateException("folder:" + folderInPlugin.getAbsolutePath() + " does not exist!");
		}

		FileUtils.copyDirectory(folderInPlugin,targetFolder);
	}

	private File getHomeSubSubFolder(String name){
		return new File(getHomeSubFolder(), name);
	}

	private File getHomeSubFolder() {
		File homeSubFolder = new File(System.getProperty("user.home"), ".eclipse-asciidoctor-editor");
		return homeSubFolder;
	}

}
