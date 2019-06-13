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
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;

import de.jcup.asciidoctoreditor.console.AsciiDoctorConsoleUtil;
import de.jcup.asciidoctoreditor.script.AsciiDoctorMarker;
import de.jcup.asciidoctoreditor.util.AsciiDoctorEditorUtil;
import de.jcup.asciidoctoreditor.util.EclipseUtil;
import de.jcup.asp.api.Response;
import de.jcup.asp.api.ServerLog;
import de.jcup.asp.api.ServerLogEntry;
import de.jcup.asp.api.ServerLogSeverity;
import de.jcup.asp.client.AspClient;
import de.jcup.asp.client.AspClientException;
import de.jcup.eclipse.commons.EclipseResourceHelper;

public class AspAsciidoctorAdapter implements AsciidoctorAdapter {
    
    private AspClient client = new  AspClient();
    
    @Override
    public Map<String, Object> resolveAttributes(File baseDir) {
        try {
            Response response = client.resolveAttributes(baseDir);
            Map<String, Object> attributes = response.getAttributes();
            handleServerLog(response);
            return attributes;
        } catch (AspClientException e) {
           AsciiDoctorConsoleUtil.error(e.getMessage());
        }
        return new HashMap<String, Object>();
    }

    @Override
    public void convertFile(File filename, Map<String, Object> options) {
        try {
            Response response = client.convertFile(filename.toPath(), options);
            handleServerLog(response);
        } catch (AspClientException e) {
           AsciiDoctorConsoleUtil.error(e.getMessage());
        }
    }
    
    private void handleServerLog(Response response) {
        ServerLog serverLog = response.getServerLog();
        EclipseUtil.safeAsyncExec(()->{
            handleServerLogAsync(serverLog);
        });
        
    }

    private void handleServerLogAsync(ServerLog serverLog) {
        for (ServerLogEntry entry: serverLog.getEntries()) {
            int eclipseSeverity = -1 ;
            ServerLogSeverity sever = entry.getSeverity();
            switch(sever) {
            case ERROR:
            case FATAL:
                eclipseSeverity=IMarker.SEVERITY_ERROR;
                break;
            case INFO:
                eclipseSeverity=IMarker.SEVERITY_INFO;
                break;
            case WARN:
                eclipseSeverity=IMarker.SEVERITY_WARNING;
                break;
            case UNKNOWN:
            case DEBUG:
            default:
                break;
            }
            if (eclipseSeverity==-1) {
                continue;
            }
                
            AsciiDoctorMarker error = new AsciiDoctorMarker(-1, -1, entry.getMessage());
            File file = entry.getFile();
            IFile resource = null;
            if (file!=null) {
                resource = EclipseResourceHelper.DEFAULT.toIFile(file);
                AsciiDoctorEditorUtil.addAsciiDoctorMarker(entry.getLineNumber(), error, eclipseSeverity,resource);
            }else {
                /* fallback, we need a resource to have markers */
                AsciiDoctorEditorUtil.addAsciiDoctorMarker(EclipseUtil.getActiveEditor(),entry.getLineNumber(),error,eclipseSeverity);
            }
        }
        
    }


}
