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
package de.jcup.asciidoctoreditor.asciidoc;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;
import java.util.Objects;

import de.jcup.asciidoctoreditor.ConsoleAdapter;
import de.jcup.asciidoctoreditor.LogAdapter;
import de.jcup.asciidoctoreditor.console.AsciiDoctorConsoleUtil;
import de.jcup.asp.client.AspClient;
import de.jcup.asp.core.LaunchException;
import de.jcup.asp.core.LogHandler;
import de.jcup.asp.core.OutputHandler;
import de.jcup.asp.server.asciidoctorj.launcher.ExternalProcessAsciidoctorJServerLauncher;

public class ASPServerAdapter {

    public static final int DEFAULT_MIN_PORT = 4444;
    public static final int DEFAULT_MAX_PORT = 4484;
    private ConsoleAdapter consoleAdapter;
    private LogAdapter logAdapter;

    private int minPort = DEFAULT_MIN_PORT;
    private int maxPort = DEFAULT_MAX_PORT;

    private int port;
    private String pathToJavaBinary;
    private String pathToServerJar;
    private AspClient client;
    private ExternalProcessAsciidoctorJServerLauncher launcher;
    private boolean showServerOutput;
    private boolean showCommunication;
    private Map<String, String> customEnvironmentEntries;

    public ASPServerAdapter() {
    }

    public AspClient getClient() {
        return client;
    }

    public void setMinPort(int minPort) {
        this.minPort = minPort;
    }

    public void setMaxPort(int maxPort) {
        this.maxPort = maxPort;
    }

    public int getPort() {
        return port;
    }

    public void setPathToJavaBinary(String pathToJavaBinary) {
        if (Objects.equals(pathToJavaBinary, this.pathToJavaBinary)) {
            return;
        }
        this.pathToJavaBinary = pathToJavaBinary;
    }

    public void setPathToServerJar(String pathToServerJar) {
        this.pathToServerJar = pathToServerJar;
    }

    public void setConsoleAdapter(ConsoleAdapter consoleAdapter) {
        this.consoleAdapter = consoleAdapter;
    }

    public void setShowServerOutput(boolean showServerOutput) {
        this.showServerOutput = showServerOutput;
        if (launcher != null) {
            launcher.setShowServerOutput(showServerOutput);
        }
    }

    public void setCustomEnvironmentEntries(Map<String, String> map) {
        this.customEnvironmentEntries = map;
    }

    public void setShowCommunication(boolean showCommunication) {
        this.showCommunication = showCommunication;
        if (client != null) {
            client.setShowCommunication(showCommunication);
        }
    }

    public void setLogAdapter(LogAdapter logAdapter) {
        this.logAdapter = logAdapter;
    }

    public boolean isAlive() {
        if (client == null) {
            return false;
        }
        return client.isServerAlive(null);
    }

    public void startServer() {
        if (launcher != null) {
            launcher.stopServer();
        }
        LogHandler logHandler = new LogHandler() {

            @Override
            public void error(String message, Throwable t) {
                if (logAdapter != null) {
                    logAdapter.logError(message, t);
                }

            }
        };
        OutputHandler outputHandler = new OutputHandler() {

            @Override
            public void output(String message) {
                if (consoleAdapter != null) {
                    consoleAdapter.output(message);
                }

            };
        };
        this.port = getFreePortToUse(minPort, maxPort);

        launcher = new ExternalProcessAsciidoctorJServerLauncher(pathToServerJar, port);
        launcher.setPathToJavaBinary(pathToJavaBinary);
        launcher.setShowServerOutput(showServerOutput);
        launcher.setLogHandler(logHandler);
        launcher.setOutputHandler(outputHandler);

        if (customEnvironmentEntries != null) {
            for (String key : customEnvironmentEntries.keySet()) {
                launcher.setEnvironment(key, customEnvironmentEntries.get(key));
            }
        }

        try {
            String key = launcher.launch(30);
            /*
             * Next line is a temporary workaround until fixed in ASP itself - on windows we
             * got \r\n so having \r inside key... we must trim to avoid the problem
             */
            key = key.trim();

            outputHandler.output(">> ASP Server has been started successfully");
            this.client = new AspClient(key);
            this.client.setPortNumber(port);
            this.client.setOutputHandler(outputHandler);
            this.client.setShowCommunication(showCommunication);
        } catch (LaunchException e) {
            if (logAdapter != null) {
                logAdapter.logError("Was not able to launch asp server", e);
            }
        }

    }

    private int getFreePortToUse(int minPort, int maxPort) {
        for (int p = minPort; p <= maxPort; p++) {
            try {
                ServerSocket socket = new ServerSocket(p);
                socket.close();
                // able to open a server socket on port p, so it will be possible for ASP server
                // as well
                // we use this port;
                return p;
            } catch (IOException e) {
                /* ignore */
            }
        }
        throw new IllegalStateException("No port free between " + minPort + " - " + maxPort + " for usage of ASP Server!");
    }

    /**
     * Stop server
     * 
     * @return <code>true</code> when server shutdown was successful,
     *         <code>false</code> when server was already not running
     */
    public boolean stopServer() {
        if (launcher != null) {
            boolean stopDone = launcher.stopServer();
            if (stopDone) {
                AsciiDoctorConsoleUtil.output(">> ASP Server stop triggered.");
                waitForServerNoLongerAlive();
                AsciiDoctorConsoleUtil.output(">> ASP Server stop done.");
            }
        }
        return false;
    }

    private void waitForServerNoLongerAlive() {
        for (int i = 0; i < 10; i++) {
            if (!client.isServerAlive(null)) {
                return;
            }
            AsciiDoctorConsoleUtil.output(">>> Wait for server no longer alive...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

        }
    }

    public boolean isServerStarted() {
        return isAlive();
    }

}
