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
 * This wrapper is used to have correct access to asciidoctor inside
 * OSGI/eclipse.<br>
 * <br>
 * <u>In a nutshell: </u><br>
 * <ol>
 * <li>Using the lib plugin</li>
 * </ol>
 * <br>
 * 
 * @author Albert Tregnaghi
 *
 */
public class PluginContentInstaller {
    private static final String CSS_PLUGIN_ID = "de.jcup.asciidoctoreditor.css";
	private static final String LIBS_PLUGIN_ID = "de.jcup.asciidoctoreditor.libs";
	public static final PluginContentInstaller INSTANCE = new PluginContentInstaller();
	
	private PluginContentInstaller() {
		
	}

	public File getASPFolder(){
		String versionName = getLibVersionName();
		return ensureServerArtefactsAreAvailable(versionName);
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
        File cssFolder = getHomeSubSubFolder("css");

        File targetVersionCSSfolder = new File(cssFolder, versionName);
        if (!targetVersionCSSfolder.exists()) {
            targetVersionCSSfolder.mkdirs();

            try {
                copyFolderOrFail(targetVersionCSSfolder, "css",CSS_PLUGIN_ID);
            } catch (IOException e) {
                throw new IllegalStateException("Not able to install CSS files from css plugin", e);
            }

        }
        return targetVersionCSSfolder;
}
	private File ensureServerArtefactsAreAvailable(String versionName) {
        File cssFolder = getHomeSubSubFolder("asp");

        File targetVersionCSSfolder = new File(cssFolder, versionName);
        if (!targetVersionCSSfolder.exists()) {
            targetVersionCSSfolder.mkdirs();

            try {
                copyFolderOrFail(targetVersionCSSfolder, "asp",LIBS_PLUGIN_ID);
            } catch (IOException e) {
                throw new IllegalStateException("Not able to install Server files from libs plugin", e);
            }

        }
        return targetVersionCSSfolder;
    }
	
	private File ensureEditorAddonsAreAvailable() {
		String versionName = getEditorVersionName();
		File cssFolder = getHomeSubSubFolder("addons");

		File targetVersionAddonsfolder = new File(cssFolder, versionName);
		if (!targetVersionAddonsfolder.exists()) {
			targetVersionAddonsfolder.mkdirs();

			try {
				copyFolderOrFail(targetVersionAddonsfolder, "addons",AsciiDoctorEditorActivator.PLUGIN_ID);
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
