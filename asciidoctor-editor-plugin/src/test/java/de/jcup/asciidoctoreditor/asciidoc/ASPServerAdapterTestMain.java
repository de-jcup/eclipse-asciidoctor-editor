package de.jcup.asciidoctoreditor.asciidoc;

public class ASPServerAdapterTestMain {

    public static void main(String[] args) throws InterruptedException {
       ASPServerAdapter adapter = new ASPServerAdapter();
       adapter.setPathToServerJar(System.getProperty("asp.server.location"));
       
       adapter.startServer();
       
       Thread.sleep(10000);
       adapter.stopServer();
       adapter.setPort(4444);
       adapter.startServer();
       Thread.sleep(10000);
       adapter.stopServer();
    
    }

}
