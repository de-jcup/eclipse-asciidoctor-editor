package de.jcup.asciidoctoreditor.asciidoc.debug;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import de.jcup.asciidoctoreditor.asciidoc.AsciiDocFileUtils;

public class AsciidocFileDebugInfoCollector {

    private static final String INCLUDE_PREFIX = "include::";
    private static final int INCLUDE_PREFIX_LENGTH = INCLUDE_PREFIX.length();
    private Map<String, String> attributeMap = new LinkedHashMap<>();
    private StringBuilder dump = new StringBuilder();
    private Path baseDir;

    public void setBaseDir(Path baseDir) {
        this.baseDir = baseDir;
    }

    public String createDump() {
        return dump.toString();
    }

    public void collect(File fileToRender) {
        try {
            startCollectingIformation(fileToRender);
        } catch (IOException e) {
            dump.append("\nCOLLECTION FAILED: ").append(e);
        }
    }

    private void startCollectingIformation(File currentFile) throws IOException {
        markCurrentFile(currentFile);
        String text = AsciiDocFileUtils.readAsciidocFile(currentFile);
        int lineNr = -1;
        String[] lines = text.split("\n");
        for (String line : lines) {
            lineNr++;
            if (line.trim().isEmpty()) {
                // just empty line, ignore
                continue;
            }
            if (line.startsWith("//")) {
                // just a comment, so we do not show this inside debug
                continue;
            }
            int varStart = line.indexOf(':');
            if (varStart == 0) {
                int varEnd = line.indexOf(':', varStart + 1);
                if (varEnd != -1 /* && varEnd - varStart > 1 */) {
                    /* variable found */
                    String value = "";
                    String name = "";
                    if (varEnd < line.length() + 1) {
                        name = line.substring(varStart + 1, varEnd);
                        value = line.substring(varEnd + 1).trim();
                    }
                    attributeMap.put(name, value);
                    markAttributeSet(name, value);
                    continue;
                }

            } else {
                boolean lineShown=false;
                /* always replace ... */
                String replaced = createReplacedLine(line);
                if (!replaced.contentEquals(line)) {
                    markLineFoud(line, lineNr);
                    markChangedLine(replaced);
                    lineShown=true;
                }

                /* after replacement done */

                /* parse include */
                if (replaced.startsWith(INCLUDE_PREFIX)) {
                    if (!lineShown){
                        markLineFoud(line, lineNr);
                    }
                    int endIndex = replaced.indexOf("[");
                    if (endIndex != -1 && endIndex > INCLUDE_PREFIX_LENGTH) {
                        String path = replaced.substring(INCLUDE_PREFIX_LENGTH, endIndex);
                        markIncludePathFound(path);
                        
                        Path parentFolder = currentFile.toPath().getParent();
                        
                        Path includedFilePath = parentFolder.resolve(path);

                        if (!Files.exists(includedFilePath)) {
                            addDump("   INCLUDE file does not exist via parentfolder - try with base dir");
                            includedFilePath=baseDir.resolve(path);
                        } 
                        if (!Files.exists(includedFilePath)) {
                            markIncludePathNotExisting(path);
                        } else {
                            startCollectingIformation(includedFilePath.toFile());
                            // show we are back ...
                            addDump("   >> RETURN TO OLD FILE CONTEXT");
                            markCurrentFile(currentFile);
                        }
                    }
                }

            }
        }

    }

    private void markCurrentFile(File currentFile) {
        dump.append("\nFILE CONTEXT CHANGED: ").append(currentFile.getAbsolutePath());
    }

    private void markIncludePathNotExisting(String path) {
        dump.append("\n   INCLUDE: not found: " + path);
    }

    private void markIncludePathFound(String path) {
        dump.append("\n   INCLUDE PATH FOUND: " + path);
    }

    private void markChangedLine(String replaced) {
        dump.append("\n   LINE CHANGED TO: ").append(replaced);

    }

    private void addDump(String dumpInfo) {
        dump.append("\n").append(dumpInfo);
    }

    private String createReplacedLine(String origin) {
        String result = origin;
        for (String key : attributeMap.keySet()) {
            String val = attributeMap.get(key);
            String replacer = "\\{" + key + "\\}";
            result = result.replaceAll(replacer, val);
        }
        return result;
    }

    private void markLineFoud(String line, int lineNr) {
        dump.append("\n   LINE ").append(lineNr).append("=").append(line);
    }

    private void markAttributeSet(String name, String value) {
        dump.append("\n   SET ATTRIBUTE ").append(name).append('=').append(value);
    }
}
