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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorPreferences;
import de.jcup.asp.client.AspClient;
import de.jcup.asp.client.AspClientException;

/**
 * Special variant of an Asciidoctor instance - uses native installation. But it
 * works ony with the editor, because not all parts are implemented...
 * 
 * @author Albert Tregnaghi
 *
 */
public class AspAsciidoctorAdapter implements AsciidoctorAdapter {
    
    private AspClient client = new  AspClient();
    
    @Override
    public Map<String, Object> resolveAttributes(File baseDir) {
        try {
            return client.resolveAttributes(baseDir);
        } catch (AspClientException e) {
           AsciiDoctorConsoleUtil.error(e.getMessage());
        }
        return new HashMap<String, Object>();
    }

    @Override
    public void convertFile(File filename, Map<String, Object> options) {
        try {
            client.convertFile(filename.toPath(), options);
        } catch (AspClientException e) {
           AsciiDoctorConsoleUtil.error(e.getMessage());
        }
    }

    protected String createCommandLineString(List<String> commands) {
        StringBuilder commandLine = new StringBuilder();
        for (String command : commands) {
            commandLine.append(command);
            commandLine.append(" ");
        }
        String commandLineString = commandLine.toString();
        return commandLineString;
    }

    protected List<String> buildCommands(File filename, Map<String, Object> options) {

        List<String> commands = new ArrayList<String>();
        if (OSUtil.isWindows()) {
            commands.add("cmd.exe");
            commands.add("/C");
        }
        String asciidoctorCall = createAsciidoctorCall();
        commands.add(asciidoctorCall);

        String outDir = null;

        @SuppressWarnings("unchecked")
        Map<String, String> attributes = (Map<String, String>) options.get("attributes");
        String baseDir = null;
        for (String key : attributes.keySet()) {
            Object value = attributes.get(key);
            if (value == null) {
                continue;
            }
            String v = value.toString();
            String attrib = key;
            if (v.isEmpty()) {
                continue;
            }
            if ("eclipse-editor-basedir".equals(attrib)) {
                baseDir = v;
                continue;
            }
            commands.add("-a");
            String safeValue = toWindowsSafeVariant(value);
            if (key.equals("outdir")) {
                outDir = safeValue;
            }
            attrib += "=" + safeValue;
            commands.add(attrib);
        }

        Object obj_backend = options.get("backend");
        if (obj_backend!=null) {
            commands.add("-b");
            commands.add(obj_backend.toString());
        }
        
        String argumentsForInstalledAsciidoctor = AsciiDoctorEditorPreferences.getInstance().getArgumentsForInstalledAsciidoctor();
        List<String> preferenceCLICommands = CLITextUtil.convertToList(argumentsForInstalledAsciidoctor);
        commands.addAll(preferenceCLICommands);
        if (baseDir!=null){
            commands.add("-B");
            commands.add(toWindowsSafeVariant(baseDir));
        }
        if (outDir != null) {
            commands.add("-D");
            commands.add(outDir);
        }

        commands.add(toWindowsSafeVariant(filename.getAbsolutePath()));
        return commands;
    }

    protected String createAsciidoctorCall() {
        StringBuilder sb = new StringBuilder();
        String path = AsciiDoctorEditorPreferences.getInstance().getPathToInstalledAsciidoctor();
        if (path != null && !path.trim().isEmpty()) {
            sb.append(path);
            if (!path.endsWith(File.separator)) {
                sb.append(File.separator);
            }
        }
        sb.append("asciidoctor");
        String callPath = sb.toString();
        return callPath;
    }

    private String toWindowsSafeVariant(Object obj) {
        String command = "" + obj;
        boolean windowsPath = command.indexOf('\\') != -1;
        if (!windowsPath) {
            return command;
        }
        return "\"" + command + "\"";
    }

   


}
