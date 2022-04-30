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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.jcup.asciidoctoreditor.AsciiDoctorEclipseLogAdapter;
import de.jcup.asciidoctoreditor.CLITextUtil;
import de.jcup.asciidoctoreditor.EclipseDevelopmentSettings;
import de.jcup.asciidoctoreditor.OSUtil;
import de.jcup.asciidoctoreditor.console.AsciiDoctorConsoleUtil;
import de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorPreferences;
import de.jcup.asciidoctoreditor.preferences.CustomEnvironmentEntrySupport;
import de.jcup.asciidoctoreditor.util.AsciiDoctorEditorUtil;
import de.jcup.asp.api.asciidoc.AsciidocAttributes;
import de.jcup.asp.api.asciidoc.AsciidocOptions;
import de.jcup.asp.client.AspClientProgressMonitor;

public class InstalledAsciidoctorAdapter implements AsciidoctorAdapter {
    
    @Override
    public void convertFile(File editorFileOrNull, File asciiDocFile, AsciidocOptions options, AsciidocAttributes attributes, AspClientProgressMonitor monitor) {
        if (editorFileOrNull==null) {
            AsciiDoctorConsoleUtil.output( "Installed asciidoctor: Processing content");
        }else {
            AsciiDoctorConsoleUtil.output( "Installed asciidoctor: Processing file:"+editorFileOrNull.getAbsolutePath());
        }
        List<String> commands = buildCommands(asciiDocFile, options,attributes);
        String commandLineString = createCommandLineString(commands);
        if (monitor.isCanceled()) {
            return;
        }
        ProcessBuilder pb = new ProcessBuilder(commands);
        try {
            CustomEnvironmentEntrySupport customEnvironmentEntrySupport = getCustomEnvEntrySupport();
            if (customEnvironmentEntrySupport.areCustomEnvironmentEntriesEnabled()) {
                pb.environment().putAll(customEnvironmentEntrySupport.fetchConfiguredEnvironmentEntriesAsMap());
            }
            StringBuffer lineStringBuffer = null;
            Process process = pb.start();
            try (InputStream is = process.getErrorStream();) {
                int c;
                lineStringBuffer = new StringBuffer();
                while ((c = is.read()) != -1) {
                    lineStringBuffer.append((char) c);
                }
                String line = lineStringBuffer.toString();
                if (line.isEmpty()) {
                    AsciiDoctorConsoleUtil.output(line);
                } else {
                    AsciiDoctorConsoleUtil.error(line);
                }
            }
            boolean exitdone = process.waitFor(2, TimeUnit.MINUTES);
            int exitCode = -1;
            if (exitdone) {
                exitCode = process.exitValue();
            }
            if (EclipseDevelopmentSettings.DEBUG_LOGGING_ENABLED) {
                AsciiDoctorConsoleUtil.output("Called:" + commandLineString);
                AsciiDoctorConsoleUtil.output("Exitcode:" + exitCode);
            }
            if (exitCode > 0) {
                AsciiDoctorEclipseLogAdapter.INSTANCE
                        .logWarn("Installed Asciidoctor rendering failed for '" + asciiDocFile.getName() + "'\n\nCommandLine was:\n" + commandLineString
                                + "\n\nResulted in exitcode:" + exitCode + ", \nLast output:" + lineStringBuffer);
                throw new InstalledAsciidoctorException("FAILED - Asciidoctor exitcode:" + exitCode + " - last output:" + lineStringBuffer);

            }
        } catch (Exception e) {
            if (e instanceof InstalledAsciidoctorException) {
                InstalledAsciidoctorException iae = (InstalledAsciidoctorException) e;
                throw iae; // already an exception from installed asciidoctor so
                           // just re-throw
            } else {
                AsciiDoctorEditorUtil.logError("Cannot execute installed asciidoctor\n\nCommandline was:\n" + commandLineString, e);
                throw new InstalledAsciidoctorException("FAILED - Installed Asciidoctor instance was not executable, reason:" + e.getMessage());
            }
        }

    }

    private CustomEnvironmentEntrySupport getCustomEnvEntrySupport() {
        return CustomEnvironmentEntrySupport.DEFAULT;
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

    private List<String> buildCommands(File asciiDocFile, AsciidocOptions options, AsciidocAttributes attributes2) {

        List<String> commands = new ArrayList<String>();
        if (OSUtil.isWindows()) {
            commands.add("cmd.exe");
            commands.add("/C");
        }
        String asciidoctorCall = createAsciidoctorCall();
        commands.add(asciidoctorCall);

        String outDir = null;

        
        Map<String, Object> attributes = attributes2.toMap();
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

        Map<String, Object> optionsMap = options.toMap();
        Object obj_backend = optionsMap.get("backend");
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

        commands.add(toWindowsSafeVariant(asciiDocFile.getAbsolutePath()));
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
