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
package de.jcup.asciidoctoreditor.asciidoc;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;

import de.jcup.asciidoctoreditor.ASPSupport;
import de.jcup.asciidoctoreditor.AsciiDoctorEditorActivator;
import de.jcup.asciidoctoreditor.console.AsciiDoctorConsoleUtil;
import de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorPreferences;
import de.jcup.asciidoctoreditor.script.ASPMarker;
import de.jcup.asciidoctoreditor.script.AsciiDoctorMarker;
import de.jcup.asciidoctoreditor.util.AsciiDoctorEditorUtil;
import de.jcup.asciidoctoreditor.util.EclipseUtil;
import de.jcup.asp.api.Response;
import de.jcup.asp.api.ServerLog;
import de.jcup.asp.api.ServerLogEntry;
import de.jcup.asp.api.ServerLogSeverity;
import de.jcup.asp.api.asciidoc.AsciidocAttributes;
import de.jcup.asp.api.asciidoc.AsciidocOptions;
import de.jcup.asp.client.AspClient;
import de.jcup.asp.client.AspClientException;
import de.jcup.asp.client.AspClientProgressMonitor;
import de.jcup.eclipse.commons.EclipseResourceHelper;

public class AspAsciidoctorAdapter implements AsciidoctorAdapter {
	
    @Override
    public void convertFile(File editorFileOrNull, File asciiDocFile, AsciidocOptions options, AsciidocAttributes attributes, AspClientProgressMonitor monitor) {
        try {
            if (editorFileOrNull == null) {
                AsciiDoctorConsoleUtil.output("ASP: Processing content");
            } else {
                AsciiDoctorConsoleUtil.output("ASP: Processing file:" + editorFileOrNull.getAbsolutePath());
            }
            AspClient client = resolveClient();
            Response response = client.convertFile(asciiDocFile.toPath(), options, attributes, monitor);
            handleServerLog(response);
        } catch (AspClientException e) {
            Throwable rootCause = e;
            while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
                rootCause = rootCause.getCause();
            }
            String rootMessage = "";
            if (rootCause != null && rootCause != e) {
                rootMessage = rootCause.getMessage();
            }
            AsciiDoctorConsoleUtil.error(e.getMessage() + rootMessage);
            throw new ASPAsciidoctorException("Cannot convert file" + asciiDocFile, e);
        }
    }

    private AspClient resolveClient() {
        AsciiDoctorEditorActivator activator = AsciiDoctorEditorActivator.getDefault();
        ASPSupport aspSupport = activator.getAspSupport();
        return aspSupport.getAspClient();
    }

    private void handleServerLog(Response response) {
        ServerLog serverLog = response.getServerLog();
        EclipseUtil.safeAsyncExec(() -> {
            handleServerLogAsync(serverLog);
        });

    }

    private void handleServerLogAsync(ServerLog serverLog) {
        if (!AsciiDoctorEditorPreferences.getInstance().isShowingAspLogsAsMarkerInEditor()) {
            return;
        }
        for (ServerLogEntry entry : serverLog.getEntries()) {
            int eclipseSeverity = -1;
            ServerLogSeverity severity = entry.getSeverity();
            boolean ignore = false;
            switch (severity) {
            case ERROR:
            case FATAL:
                eclipseSeverity = IMarker.SEVERITY_ERROR;
                break;
            case INFO:
                eclipseSeverity = IMarker.SEVERITY_INFO;
                break;
            case WARN:
                eclipseSeverity = IMarker.SEVERITY_WARNING;
                break;
            case UNKNOWN:
            case DEBUG:
            default:
                ignore = true;
                break;
            }
            if (ignore) {
                continue;
            }
            String message = "ASP: " + severity + ": " + entry.getMessage();
            if (eclipseSeverity == IMarker.SEVERITY_ERROR) {
                AsciiDoctorConsoleUtil.error(message);
            } else {
                AsciiDoctorConsoleUtil.output(message);
            }
            if (eclipseSeverity == -1) {
                continue;
            }
            AsciiDoctorMarker marker = new ASPMarker(-1, -1, message);
            File file = entry.getFile();
            IFile resource = null;
            if (file != null) {
                resource = EclipseResourceHelper.DEFAULT.toIFile(file);
                if (resource != null) {
                    AsciiDoctorEditorUtil.addAsciiDoctorMarker(entry.getLineNumber(), marker, eclipseSeverity, resource);
                }
            }
            if (resource == null) {
                /* fallback, we need a resource to have markers */
                AsciiDoctorEditorUtil.addAsciiDoctorMarker(EclipseUtil.getActiveEditor(), entry.getLineNumber(), marker, eclipseSeverity);
            }
        }

    }

}
