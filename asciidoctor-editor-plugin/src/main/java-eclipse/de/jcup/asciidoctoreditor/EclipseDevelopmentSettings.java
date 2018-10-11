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

import static java.lang.Boolean.*;
import static java.lang.System.*;

public interface EclipseDevelopmentSettings {

	public static final boolean DEBUG_LOGGING_ENABLED = parseBoolean(getProperty("asciidoctor.editor.debug.logging"));
	
	public static final boolean DEBUG_TOOLBAR_ENABLED = parseBoolean(getProperty("asciidoctor.editor.debug.toolbar"));
	
	public static final boolean OLD_STUFF_ENABLED_DARK_PREFERENCE_DEFAULTS = parseBoolean(getProperty("asciidoctor.editor.oldstuff.darkdefaultpreferences"));

}