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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.console.IConsolePageParticipant;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import de.jcup.asciidoctoreditor.document.keywords.TooltipTextSupport;

/**
 * The activator class controls the plug-in life cycle
 */
public class AsciiDoctorEditorActivator extends AbstractUIPlugin {

	// The plug-in COMMAND_ID
	public static final String PLUGIN_ID = "de.jcup.asciidoctoreditor"; //$NON-NLS-1$

	// The shared instance
	private static AsciiDoctorEditorActivator plugin;
	private ColorManager colorManager;

	private Map<StyledText, IConsolePageParticipant> viewers = new HashMap<StyledText, IConsolePageParticipant>();
	/**
	 * The constructor
	 */
	public AsciiDoctorEditorActivator() {
		colorManager = new ColorManager();
		TooltipTextSupport.setTooltipInputStreamProvider(new EclipseResourceInputStreamProvider());
	}

	public ColorManager getColorManager() {
		return colorManager;
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
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

	public void addViewer(StyledText viewer,IConsolePageParticipant participant) {
		viewers.put(viewer, participant);
	}

	public void removeViewerWithPageParticipant(IConsolePageParticipant participant) {
		Set<StyledText> toRemove = new HashSet<StyledText>();

		for (StyledText viewer : viewers.keySet()) {
			if (viewers.get(viewer) == participant){
				toRemove.add(viewer);
			}
		}

		for (StyledText viewer : toRemove){
			viewers.remove(viewer);
		}
		
	}

}
