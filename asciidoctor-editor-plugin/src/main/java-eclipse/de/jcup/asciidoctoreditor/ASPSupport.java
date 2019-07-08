package de.jcup.asciidoctoreditor;

import java.io.File;

import de.jcup.asciidoctoreditor.asciidoc.ASPServerAdapter;
import de.jcup.asciidoctoreditor.console.AsciiDoctorConsoleUtil;
import de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorPreferences;
import de.jcup.asp.client.AspClient;

public class ASPSupport {
    
    private ASPServerAdapter aspServerAdapter;

    public ASPSupport() {
        aspServerAdapter =  new ASPServerAdapter();
    }
    
    public AspClient getAspClient() {
        waitForServerAvailable(new FallbackRestartHandler());
        
        AspClient client = aspServerAdapter.getClient();
        return client;
    }
    
    private abstract class ServerNotAvailableHandler{
        protected abstract void handleNotAvailable();
    }
    
    private class FallbackRestartHandler extends ServerNotAvailableHandler{
        
        @Override
        protected void handleNotAvailable() {
            AsciiDoctorConsoleUtil.output("> ASP server not available at port " + aspServerAdapter.getPort()+", trigger server restart NOW");
            aspServerAdapter.startServer();
            
            /* wait until this server become available. If this fails, there must be something odd and we trigger a illegal state exception + do log*/
            waitForServerAvailable(new FallbackRestartNotPossibleSoFailHandler());
        }
        
    }
    
    private class FallbackRestartNotPossibleSoFailHandler extends ServerNotAvailableHandler{

        @Override
        protected void handleNotAvailable() {
            AsciiDoctorConsoleUtil.output("> ASP server restart failed! Maybe another application already running on port:"+aspServerAdapter.getPort());
            throw new IllegalStateException("ASP server initialization timed out and restart was not successful!");
        }
    }
        
    
    
    private void waitForServerAvailable(ServerNotAvailableHandler notAvailableHandler) {
        int count = 0;
        while (isServerNotInitialized()) {
            try {
                Thread.sleep(1000);
                count ++;
                if (count>10) {
                   notAvailableHandler.handleNotAvailable();
                }
            } catch (InterruptedException e) {
               Thread.currentThread().interrupt();
            }
        }
    }

    private boolean isServerNotInitialized() {
        return ! aspServerAdapter.isAlive();
    }

    /**
     * Initial start - is called by plugin activator
     */
    public void start() {
        startStopASPServerOnDemandInOwnThread();
    }
    
    /**
     * Called by preference page
     */
    public void configurationChanged() {
        startStopASPServerOnDemandInOwnThread();
        
    }
    /**
     * Shutdown - called by plugin activator on plugin stop, or also in preferences
     * @return <code>true</code> when server has been stopped, <code>false</code> when stop was not possible (e.g. when no server instance was running)
     */
    public boolean stop() {
        return aspServerAdapter.stopServer();
    }
    
    
    private void startStopASPServerOnDemandInOwnThread() {
        Thread t = new Thread(()->internalUpdateASPServerStart(),"Update ASP server start");
        t.start();
    }
    
    private void internalUpdateASPServerStart() {
        AsciiDoctorEditorPreferences preferences = AsciiDoctorEditorPreferences.getInstance();
        boolean usesInstalledAsciidoctor = preferences.isUsingInstalledAsciidoctor();
        if (usesInstalledAsciidoctor) {
            if (aspServerAdapter.isServerStarted()) {
                AsciiDoctorConsoleUtil.output(">> Stopping ASP server because using now installed asciidoctor");
                aspServerAdapter.stopServer();
                return;
            }
        }else {
            aspServerAdapter.setShowServerOutput(preferences.isShowingAspServerOutputInConsole());
            aspServerAdapter.setShowCommunication(AsciiDoctorEditorPreferences.getInstance().isShowingAspCommunicationInConsole());
        }
            if (aspServerAdapter.isAlive()) {
                return;
            }
            File aspFolder = PluginContentInstaller.INSTANCE.getLibsFolder();
            File aspServer = new File(aspFolder,"asp-server-asciidoctorj-dist.jar");
           
            String pathToJava= preferences.getPathToJavaForASPLaunch();
            aspServerAdapter.setPathToJava(pathToJava);
            aspServerAdapter.setPathToServerJar(aspServer.getAbsolutePath());
            aspServerAdapter.setMinPort(preferences.getAspServerMinPort());
            aspServerAdapter.setMaxPort(preferences.getAspServerMaxPort());
            aspServerAdapter.setConsoleAdapter(AsciiDoctorEclipseConsoleAdapter.INSTANCE);
            aspServerAdapter.setLogAdapter(AsciiDoctorEclipseLogAdapter.INSTANCE);
            aspServerAdapter.startServer();

    }
   
}
