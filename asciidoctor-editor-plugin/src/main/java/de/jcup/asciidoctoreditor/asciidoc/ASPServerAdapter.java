package de.jcup.asciidoctoreditor.asciidoc;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.jcup.asciidoctoreditor.ConsoleAdapter;
import de.jcup.asciidoctoreditor.LogAdapter;
import de.jcup.asp.client.AspClient;

public class ASPServerAdapter {

    private ConsoleAdapter consoleAdapter;
    private LogAdapter logAdapter;
    private int port;
    private String pathToJava;
    private String pathToServerJar;
    private Process process;
    private AspClient client;
    private boolean started;
    private boolean showServerOutput;

    public ASPServerAdapter() {
        this.client = new AspClient();
    }

    public AspClient getClient() {
        return client;
    }

    public void setPort(int port) {
        if (this.port == port) {
            return;
        }
        this.port = port;
        client.setPortNumber(port);
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
        return client.isServerAlive();
    }

    public void setLogAdapter(LogAdapter logAdapter) {
        this.logAdapter = logAdapter;
    }

    /**
     * Start server in own thread
     * 
     * @return <code>true</code> when server has been started
     */
    public boolean startServer() {
        /* check if there is not already a running server instance */
        if (isAlive()) {
            return false;
        }

        if (process != null && process.isAlive()) {
            /* already a process running */
            return false;
        }
        started = true;
        Thread thread = new Thread(new ServerStartRunnable(port), "ASP Server at port:" + port);
        thread.setDaemon(true);
        thread.start();
        return true;
    }

    /**
     * Stop server
     * @return <code>true</code> when server shutdown was successful, <code>false</code> when server was already not running 
     */
    public boolean stopServer() {
        started = false;
        if (process == null) {
            return false;

        }
        if (!process.isAlive()) {
            return false;
        }
        process.destroyForcibly();
        return true;
    }

    private class ServerStartRunnable implements Runnable {
        int port;

        private ServerStartRunnable(int port) {
            this.port = port;
        }

        public void run() {
            String javaCommand = null;
            if (pathToJava == null || pathToJava.trim().isEmpty()) {
                javaCommand = "java";
            } else {
                javaCommand = pathToJava + "/java";
                File test = new File(javaCommand);
                if (!test.exists()) {
                    if (consoleAdapter != null) {
                        consoleAdapter.output(">> Not able to start java because not found on defined location:" + javaCommand);
                    }
                    return;
                }
                if (!test.canExecute()) {
                    if (consoleAdapter != null) {
                        consoleAdapter.output(">> Not able to start java because existing but not executable: " + javaCommand);
                    }
                    return;
                }
            }

            List<String> commands = new ArrayList<String>();
            commands.add(javaCommand);
            commands.add("-Dasp.server.port=" + port);
            commands.add("-jar");
            commands.add(pathToServerJar);

            ProcessBuilder pb = new ProcessBuilder(commands);
            StringBuffer lineStringBuffer = new StringBuffer();
            try {
                if (!showServerOutput) {
                    // Next line is strange, but important for initial startup when no library files area available and must be copied.
                    // when missing, it happens that the generation (api client call) will block infinite.
                    // Doing the pb.inheritIO() seems to solve it - but I have currently no idea why...
                    // Showing server output does also solve the problem.
                    // --> so do this as a workaround...
                    pb.inheritIO();
                }
                process = pb.start();
                if (consoleAdapter != null) {
                    consoleAdapter.output(">> Triggered ASP server start at port:" + port);
                }
                if (showServerOutput) { // only fetch input stream when configured, so faster and process termination works also faster
                    try (InputStream is = process.getInputStream()) {
                        int c;
                        while ((c = is.read()) != -1) {
                            if (c == '\n') {
                                if (consoleAdapter != null) {
                                    consoleAdapter.output(lineStringBuffer.toString());
                                }
                                lineStringBuffer = new StringBuffer();
                            } else {
                                lineStringBuffer.append((char) c);
                            }
                        }
                    }
                    if (consoleAdapter != null) {
                        consoleAdapter.output(lineStringBuffer.toString());
                    }
                }
                int exitCode = process.waitFor();
                if (consoleAdapter != null) {
                    consoleAdapter.output(">> Former running ASP Server at port " + port + " stopped, exit code was:" + exitCode);
                }
            } catch (Exception e) {
                String message = ">> FATAL ASP server connection failure :" + e.getMessage();
                if (consoleAdapter != null) {
                    consoleAdapter.output(message);
                } else {
                    System.err.println(message);
                }
                if (logAdapter != null) {
                    logAdapter.logError(message, e);
                } else {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean isServerStarted() {
        return started;
    }
}
