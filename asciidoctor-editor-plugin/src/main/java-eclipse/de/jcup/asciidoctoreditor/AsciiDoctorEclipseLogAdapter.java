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

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class AsciiDoctorEclipseLogAdapter implements LogAdapter {

    public static final LogAdapter INSTANCE = new AsciiDoctorEclipseLogAdapter();

    private AsciiDoctorEclipseLogAdapter() {
    }

    public void logInfo(String info) {
        getLog().log(new Status(IStatus.INFO, AsciiDoctorEditorActivator.PLUGIN_ID, info));
    }

    public void logWarn(String warning) {
        getLog().log(new Status(IStatus.WARNING, AsciiDoctorEditorActivator.PLUGIN_ID, warning));
    }

    public void logError(String error, Throwable t) {
        getLog().log(new Status(IStatus.ERROR, AsciiDoctorEditorActivator.PLUGIN_ID, error, t));
    }

    private static ILog getLog() {
        ILog log = AsciiDoctorEditorActivator.getDefault().getLog();
        return log;
    }

    private long lastTimeLog;

    @Override
    public void logTimeDiff(String info) {
        long current = System.currentTimeMillis();
        if (EclipseDevelopmentSettings.DEBUG_LOGGING_ENABLED) {
            long time = current - lastTimeLog;
            logInfo("elapsed " + time + " ms:" + info);
        }
        lastTimeLog = current;
    }

    @Override
    public void resetTimeDiff() {
        lastTimeLog = System.currentTimeMillis();
    }

}
