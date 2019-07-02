package de.jcup.asciidoctoreditor.asciidoc;

import java.util.Objects;

import de.jcup.asciidoctoreditor.ConsoleAdapter;
import de.jcup.asciidoctoreditor.LogAdapter;
import de.jcup.asp.client.AspClient;
import de.jcup.asp.core.LaunchException;
import de.jcup.asp.core.LogHandler;
import de.jcup.asp.core.OutputHandler;
import de.jcup.asp.server.asciidoctorj.launcher.ExternalProcessAsciidoctorJServerLauncher;

public class ASPServerAdapter {

    private ConsoleAdapter consoleAdapter;
    private LogAdapter logAdapter;
    private int port;
    private String pathToJava;
    private String pathToServerJar;
    private AspClient client;
    private boolean started;
    private boolean showServerOutput;
    private ExternalProcessAsciidoctorJServerLauncher launcher;

    public ASPServerAdapter() {
    }

    public AspClient getClient() {
        return client;
    }

    public void setPort(int port) {
        if (this.port == port) {
            return;
        }
        this.port = port;
    }

    public boolean isShowServerOutput() {
        return showServerOutput;
    }

    public void setShowServerOutput(boolean showServerOutput) {
        this.showServerOutput = showServerOutput;
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

    public boolean isAlive() {
        if (client==null) {
            return false;
        }
        return client.isServerAlive(null);
    }

    public void setLogAdapter(LogAdapter logAdapter) {
        this.logAdapter = logAdapter;
    }

    public void startServer() {
        if (launcher != null) {
            launcher.stopServer();
        }
        launcher = new ExternalProcessAsciidoctorJServerLauncher(pathToServerJar, port);
        launcher.setLogHandler(new LogHandler() {

            @Override
            public void error(String message, Throwable t) {
                if (logAdapter != null) {
                    logAdapter.logError(message, t);
                }

            }
        });
        launcher.setOutputHandler(new OutputHandler() {

            @Override
            public void output(String message) {
                if (consoleAdapter != null) {

                    consoleAdapter.output(message);
                }

            }
        });
        try {
            String key = launcher.launch(30);
            this.client=new AspClient(key);
            this.client.setPortNumber(port);
        } catch (LaunchException e) {
            logAdapter.logError("Was not able to launch asp server", e);
        }
        
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
        return started;
    }
}
