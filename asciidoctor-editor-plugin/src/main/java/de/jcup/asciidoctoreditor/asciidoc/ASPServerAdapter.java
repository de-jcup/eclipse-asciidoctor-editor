package de.jcup.asciidoctoreditor.asciidoc;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.jcup.asciidoctoreditor.LogAdapter;
import de.jcup.asp.client.AspClient;


public class ASPServerAdapter {

    private LogAdapter logAdapter;
    private int port;
    private String pathToJava;
    private String pathToServerJar;
    private Process process;
    private AspClient client;
    private boolean started;
    
    public ASPServerAdapter(){
        this.client= new AspClient();
    }
    
    public void setPort(int port) {
        if (this.port==port) {
            return;
        }
        this.port = port;
        client.setPortNumber(port);
    }
    
    public int getPort() {
        return port;
    }
    
    public void setPathToJava(String pathToJava) {
        if (Objects.equals(pathToJava,this.pathToJava)) {
            return;
        }
        this.pathToJava = pathToJava;
    }
    
    public void setPathToServerJar(String pathToServerJar) {
        this.pathToServerJar = pathToServerJar;
    }
    
    public void setLogAdapter(LogAdapter logAdapter) {
        this.logAdapter = logAdapter;
    }
    
    public boolean isAlive() {
        return client.isServerAlive();
    }
    
    /**
     * Start server
     * @return <code>true</code> when server has been started
     */
    public boolean startServer() {
        /* check if there is not already a running server instance */
        if (isAlive()) {
            return false;
        }
        
        if (process!=null && process.isAlive()) {
            /* already a process running*/
            return false;
        }
        started=true;
        Thread thread = new Thread(new ServerStartRunnable(),"ASP Server at port:"+port);
        thread.setDaemon(true);
        thread.start();
        return true;
    }

    public void stopServer() {
        started=false;
        if (process==null) {
            return;
        }
        if (! process.isAlive()) {
            return;
        }
        if (logAdapter!=null) {
            logAdapter.logInfo(">> Stopping ASP server");
        }
        process.destroyForcibly();
        process=null;
    }
    
    private class ServerStartRunnable implements Runnable {

        public void run() {
            if (pathToJava==null) {
                pathToJava="java";
            }
            
            List<String> commands = new ArrayList<String>();
            commands.add(pathToJava);
            commands.add("-Dasp.server.port="+port);
            commands.add("-jar");
            commands.add(pathToServerJar);
            
            if (logAdapter!=null) {
                logAdapter.logInfo(">> Starting ASP server at port:"+port);
            }
            ProcessBuilder pb = new ProcessBuilder(commands);
            pb.inheritIO();
            try {
                process = pb.start();
                int exitCode = process.waitFor();
                if (logAdapter!=null) {
                    logAdapter.logInfo(">> ASP Server exited with:"+exitCode);
                }
            } catch (Exception e) {
                if (logAdapter!=null) {
                    logAdapter.logError(">> FATAL ASP server connection failure ",e);
                }else {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean isServerStarted() {
        return started;
    }
}
