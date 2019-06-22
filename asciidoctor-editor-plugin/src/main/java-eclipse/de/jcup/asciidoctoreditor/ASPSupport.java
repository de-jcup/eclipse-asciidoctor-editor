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
        int count = 0;
        while (isServerNotInitialized()) {
            try {
                Thread.sleep(1000);
                count ++;
                if (count>10) {
                    throw new IllegalStateException("ASP server initialization timed out");
                }
            } catch (InterruptedException e) {
               Thread.currentThread().interrupt();
            }
        }
        return aspServerAdapter.getClient();
    }

    private boolean isServerNotInitialized() {
        return ! aspServerAdapter.isAlive();
    }

    /**
     * Initial start - is called by plugin activator
     */
    public void start() {
        updateASPServerStart();
    }
    
    /**
     * Called by preference page
     */
    public void configurationChanged() {
        updateASPServerStart();
        
    }
    /**
     * Shutdown - called by plugin activator on plugin stop, or also in preferences
     * @return <code>true</code> when server has been stopped, <code>false</code> when stop was not possible (e.g. when no server instance was running)
     */
    public boolean stop() {
        return aspServerAdapter.stopServer();
    }
    
    
    private void updateASPServerStart() {
        Thread t = new Thread(()->internalUpdateASPServerStart(),"Update ASP server start");
        t.start();
    }
    private void internalUpdateASPServerStart() {
        boolean usesInstalledAsciidoctor = AsciiDoctorEditorPreferences.getInstance().isUsingInstalledAsciidoctor();
        boolean showASPServerOutput = AsciiDoctorEditorPreferences.getInstance().isShowingASPServerOutput();
        boolean showServerOutputChanged = showASPServerOutput!=aspServerAdapter.isShowServerOutput();
        if (usesInstalledAsciidoctor) {
            if (aspServerAdapter.isServerStarted()) {
                AsciiDoctorConsoleUtil.output(">> Stopping ASP server because using now installed asciidoctor");
                aspServerAdapter.stopServer();
                return;
            }
        }else {
            File aspFolder = PluginContentInstaller.INSTANCE.getLibsFolder();
            File aspServer = new File(aspFolder,"asp-server-asciidoctorj.jar");
           
            String pathToJava= AsciiDoctorEditorPreferences.getInstance().getPathToJavaForASPLaunch();
            aspServerAdapter.setPathToJava(pathToJava);
            aspServerAdapter.setPathToServerJar(aspServer.getAbsolutePath());
            aspServerAdapter.setPort(AsciiDoctorEditorPreferences.getInstance().getAspServerPort());
            aspServerAdapter.setShowServerOutput(showASPServerOutput);
            aspServerAdapter.setConsoleAdapter(AsciiDoctorEclipseConsoleAdapter.INSTANCE);
            if (showServerOutputChanged) {
                AsciiDoctorConsoleUtil.output(">> ASP server output handling changed, so will stop and restart server instance");
                aspServerAdapter.stopServer(); // stop old processes
                aspServerAdapter.startServer();
            }else if (! aspServerAdapter.isAlive()) { // check if new setup is alive or server output has changed
                AsciiDoctorConsoleUtil.output(">> ASP server not alive at port "+aspServerAdapter.getPort()+", so starting new instance");
                aspServerAdapter.stopServer(); // stop old processes (if there is one)
                aspServerAdapter.startServer();
            }else {
                AsciiDoctorConsoleUtil.output(">> ASP server already alive at port "+aspServerAdapter.getPort()+", so reusing instance");
            }
        }

    }
   
}
