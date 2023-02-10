/*
 * Copyright 2019 Albert Tregnaghi
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
import java.util.Map;

import de.jcup.asciidoctoreditor.asciidoc.ASPServerAdapter;
import de.jcup.asciidoctoreditor.console.AsciiDoctorConsoleUtil;
import de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorPreferences;
import de.jcup.asciidoctoreditor.preferences.CustomEnvironmentEntrySupport;
import de.jcup.asp.client.AspClient;

public class ASPSupport {

    private InstalledJavaBinaryPathResolver installedBinaryPathResolver;

    private ASPServerAdapter aspServerAdapter;

    private boolean aspServerStarted;

    public ASPSupport() {
        aspServerAdapter = new ASPServerAdapter();
        installedBinaryPathResolver = new InstalledJavaBinaryPathResolver(new SystemAccess());
    }

    /**
     * Initial start - is called by plugin activator
     */
    public void start() {
        Runtime.getRuntime().addShutdownHook(new Thread("asp-shutdown-hook") {
            public void run() {
                // stop ASP - without UI output (already disposed...)
                ASPSupport.this.stop(false);
            }
        });
    }

    /**
     * Shutdown - called by JMV shutdown or also in preferences
     * 
     * @return <code>true</code> when server has been stopped, <code>false</code>
     *         when stop was not possible (e.g. when no server instance was running)
     */
    public boolean stop(boolean withOutput) {
        boolean stopped = aspServerAdapter.stopServer(withOutput);
        aspServerStarted = false;
        return stopped;
    }

    public AspClient getAspClient() {
        if (!aspServerStarted) {
            startStopASPServerOnDemandInOwnThread();
        }
        waitForServerAvailable(new FallbackRestartHandler());

        AspClient client = aspServerAdapter.getClient();
        return client;
    }

    /**
     * Called by preference page
     */
    public void configurationChanged() {
        startStopASPServerOnDemandInOwnThread();

    }

    private void internalUpdateASPServerStart() {
        AsciiDoctorEditorPreferences preferences = AsciiDoctorEditorPreferences.getInstance();

        boolean usesInstalledAsciidoctor = preferences.isUsingInstalledAsciidoctor();

        if (usesInstalledAsciidoctor) {
            if (aspServerAdapter.isServerStarted()) {
                /* shutdown running instance */
                AsciiDoctorConsoleUtil.output(">> Stopping ASP server because using now installed asciidoctor");
                aspServerAdapter.stopServer(true);
            }
            /*
             * we are done - using installed Asciidoctor instance we have do not need to
             * start ASP...
             */
            return;
        }

        /* ASP wanted */
        aspServerAdapter.setShowServerOutput(preferences.isShowingAspServerOutputInConsole());
        aspServerAdapter.setShowCommunication(AsciiDoctorEditorPreferences.getInstance().isShowingAspCommunicationInConsole());

        if (aspServerAdapter.isAlive()) {
            AsciiDoctorConsoleUtil.output(">> Stop running ASP server at port " + aspServerAdapter.getPort());
            aspServerAdapter.stopServer(true);
        }

        File aspFolder = PluginContentInstaller.INSTANCE.getLibsFolder();
        File aspServer = new File(aspFolder, "asp-server-asciidoctorj-dist.jar");

        String pathToJavaBinary = preferences.getPathToJavaBinaryForASPLaunch();

        if (pathToJavaBinary == null || pathToJavaBinary.isEmpty()) {
            pathToJavaBinary = installedBinaryPathResolver.resolvePathToJavaBinary();
        }
        aspServerAdapter.setPathToJavaBinary(pathToJavaBinary);
        aspServerAdapter.setPathToServerJar(aspServer.getAbsolutePath());
        aspServerAdapter.setMinPort(preferences.getAspServerMinPort());
        aspServerAdapter.setMaxPort(preferences.getAspServerMaxPort());
        aspServerAdapter.setConsoleAdapter(AsciiDoctorEclipseConsoleAdapter.INSTANCE);
        aspServerAdapter.setLogAdapter(AsciiDoctorEclipseLogAdapter.INSTANCE);

        Map<String, String> customEnvEntries = null;
        if (CustomEnvironmentEntrySupport.DEFAULT.areCustomEntriesEnabled()) {
            customEnvEntries = CustomEnvironmentEntrySupport.DEFAULT.fetchConfiguredEntriesAsMap();
        }
        aspServerAdapter.setCustomEnvironmentEntries(customEnvEntries);

        aspServerAdapter.startServer();

    }

    private boolean isServerNotInitialized() {
        return !aspServerAdapter.isAlive();
    }

    private void startStopASPServerOnDemandInOwnThread() {
        aspServerStarted = true;

        Thread t = new Thread(() -> internalUpdateASPServerStart(), "Update ASP server start");
        t.start();
    }

    private void waitForServerAvailable(ServerNotAvailableHandler notAvailableHandler) {
        int count = 0;
        while (isServerNotInitialized()) {
            try {
                count++;
                if (count % 2 == 0) { // we just show only every 2 seconds the wait, but check is done for every
                                      // second...
                    AsciiDoctorConsoleUtil.output("...wait for server (" + count + " seconds)");
                }
                Thread.sleep(1000);
                if (count > 15) {
                    notAvailableHandler.handleNotAvailable();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private class FallbackRestartHandler extends ServerNotAvailableHandler {

        @Override
        protected void handleNotAvailable() {
            AsciiDoctorConsoleUtil.output("> ASP server not available at port " + aspServerAdapter.getPort() + ", trigger server restart NOW");
            aspServerAdapter.startServer();

            /*
             * wait until this server become available. If this fails, there must be
             * something odd and we trigger a illegal state exception + do log
             */
            waitForServerAvailable(new FallbackRestartNotPossibleSoFailHandler());
        }

    }

    private class FallbackRestartNotPossibleSoFailHandler extends ServerNotAvailableHandler {

        @Override
        protected void handleNotAvailable() {
            AsciiDoctorConsoleUtil.output("> ASP server restart failed! Maybe another application already running on port:" + aspServerAdapter.getPort());
            throw new IllegalStateException("ASP server initialization timed out and restart was not successful!");
        }
    }

    private abstract class ServerNotAvailableHandler {
        protected abstract void handleNotAvailable();
    }

}
