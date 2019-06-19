package de.jcup.asciidoctoreditor.asciidoc;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import de.jcup.asciidoctoreditor.LogAdapter;

/**
 * This a attribute resolver which will scan for asciidoc attribues in a given
 * folder. (we need this to fetch asciidoc variable information for documents
 * doing not include the settings because only main document does this and
 * include the child document...)
 *
 */
public class AsciiDocAttributeResolver {

    public static final AsciiDocAttributeResolver DEFAULT = new AsciiDocAttributeResolver();

    private LogAdapter logadapter;

    private AsciiDocFileFilter filter = new AsciiDocFileFilter(true);
    private AsciiDocDocumentAttributeScanner scanner = new AsciiDocDocumentAttributeScanner();

    public void setLogadapter(LogAdapter logadapter) {
        this.logadapter = logadapter;
    }

    public Map<String, Object> resolveAttributes(File baseDir) {
        Map<String, Object> map = new TreeMap<>();

        inspect(baseDir, map);

        return map;
    }

    private void inspect(File file, Map<String, Object> map) {
        if (file.isDirectory()) {
            File[] files = file.listFiles(filter);
            for (File child : files) {
                inspect(child, map);
            }
        } else {
            inspectFileContent(file, map);
        }
    }

    private void inspectFileContent(File file, Map<String, Object> map) {
        try {
            String doc = AsciiDocStringUtils.readUTF8FileToString(file);
            Map<String, Object> scanResult = scanner.scan(doc);
            map.putAll(scanResult);

        } catch (IOException e) {
            if (logadapter != null) {
                logadapter.logError("Cannot inspect file content for:" + file, e);
            } else {
                e.printStackTrace();
            }
        }
    }

}
