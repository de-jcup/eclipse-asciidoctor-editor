package de.jcup.asciidoctoreditor.asciidoc;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Objects;

import de.jcup.asciidoctoreditor.ConsoleAdapter;
import de.jcup.asciidoctoreditor.LogAdapter;
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
    private String pathToJava;
    private String pathToServerJar;
    private AspClient client;
    private ExternalProcessAsciidoctorJServerLauncher launcher;
    private boolean showServerOutput;
    private boolean showCommunication;

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

    public void setPathToJava(String pathToJava) {
        if (Objects.equals(pathToJava, this.pathToJava)) {
            return;
        }
        this.pathToJava = pathToJava;
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

    public void setShowCommunication(boolean showCommunication) {
        this.showCommunication = showCommunication;
        if (client!=null) {
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
        this.port = getFreePortToUse(minPort, maxPort);

        launcher = new ExternalProcessAsciidoctorJServerLauncher(pathToServerJar, port);
        launcher.setShowServerOutput(showServerOutput);
        launcher.setLogHandler(new LogHandler() {

            @Override
            public void error(String message, Throwable t) {
                if (logAdapter != null) {
                    logAdapter.logError(message, t);
                }

            }
        });
        OutputHandler outputHandler = new OutputHandler() {

            @Override
            public void output(String message) {
                if (consoleAdapter != null) {
                    consoleAdapter.output(message);
                }

            };
        };
        launcher.setOutputHandler(outputHandler);
        try {
            String key = launcher.launch(30);
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
            return launcher.stopServer();
        }
        return false;
    }

    public boolean isServerStarted() {
        return isAlive();
    }

}
