package de.jcup.asciidoctoreditor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.runtime.IProgressMonitor;

public class HttpDownloadUtility {
    private static final int BUFFER_SIZE = 4096;

    public static boolean downloadFile(String fileURL, File targetFile, IProgressMonitor monitor) throws IOException {

        targetFile.getParentFile().mkdirs();
        
        URL url = new URL(fileURL);
        Proxy proxy;
        try {
            proxy = AsciiDoctorEditorActivator.getDefault().getProxy(url.toURI());
        } catch (URISyntaxException e) {
          throw new IllegalStateException("URI must be correct!", e);
        }
        HttpURLConnection httpConn = null;
        if (proxy == null) {
            httpConn = (HttpURLConnection) url.openConnection();
        } else {
            httpConn = (HttpURLConnection) url.openConnection(proxy);
        }
        
        int responseCode = httpConn.getResponseCode();

        boolean downloadDone = false;

        try {

            if (responseCode == HttpURLConnection.HTTP_OK) {
                int contentLength = httpConn.getContentLength();

                int steps = contentLength / BUFFER_SIZE;
                monitor.beginTask("Start download: " + fileURL, steps);

                int worked = 0;
                try (InputStream inputStream = httpConn.getInputStream(); FileOutputStream outputStream = new FileOutputStream(targetFile);) {
                    int bytesRead = -1;
                    byte[] buffer = new byte[BUFFER_SIZE];
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        if (monitor.isCanceled()) {
                            break;
                        }
                        outputStream.write(buffer, 0, bytesRead);
                        monitor.worked(1);
                        worked++;
                        monitor.subTask(BUFFER_SIZE * worked + "/" + contentLength);
                    }
                }

                downloadDone = true;

            } else {
                throw new IOException("Unexpected server repsonse code:" + responseCode);
            }
            httpConn.disconnect();
        } finally {
            if (!downloadDone) {
                if (targetFile.exists()) {
                    targetFile.delete();
                }
            }
        }
        return downloadDone;
    }
}