package de.jcup.asciidoctoreditor.asciidoc;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import de.jcup.asciidoctoreditor.LogHandler;
import de.jcup.asciidoctoreditor.PrintStreamLogHandler;

// see https://github.com/de-jcup/eclipse-asciidoctor-editor/issues/314 for details
public class AsiidocConfigFileSupport {
    private static final FindAsciidocFilenameFilter ASCIIDOC_CONFIG_FILENAME_FILTER = new FindAsciidocFilenameFilter();

    private static final String FILENAME_ASCIIDOCTORCONFIG = ".asciidoctorconfig";
    private static final String FILENAME_ASCIIDOCTORCONFIG_ADOC = FILENAME_ASCIIDOCTORCONFIG + ".adoc";
    private LogHandler logHandler;
    private Path rootFolder;

    public AsiidocConfigFileSupport(Path rootFolder) {
        this(null, rootFolder);
    }

    public AsiidocConfigFileSupport(LogHandler logHandler, Path rootFolder) {
        if (rootFolder == null) {
            throw new IllegalArgumentException("root folder may not be null!");
        }
        this.rootFolder = rootFolder;

        if (logHandler == null) {
            this.logHandler = new PrintStreamLogHandler();
        } else {
            this.logHandler = logHandler;
        }
    }

    public List<AsciidoctorConfigFile> collectConfigFiles(Path asciidocFile) {
        ReverseFileWalker walker = new ReverseFileWalker();
        walker.walk(asciidocFile);
        return walker.filesFound;
    }

    class ReverseFileWalker {
        private List<AsciidoctorConfigFile> filesFound;
        private File rootPathFile;

        private ReverseFileWalker() {
            filesFound = new ArrayList<>();
            rootPathFile = rootFolder.toFile();
        }

        public void walk(Path asciidocFile) {
            inspect(asciidocFile.toFile().getParentFile());
        }

        private void inspect(File folder) {
            if (folder == null || folder.equals(rootPathFile.getParentFile()) || !folder.exists()) {
                return;
            }

            File[] asciidocConfigFiles = folder.listFiles(ASCIIDOC_CONFIG_FILENAME_FILTER);
            for (File configFile : asciidocConfigFiles) {
                Path path = configFile.toPath();
                try {
                    filesFound.add(createAsciidocConfigFile(path));
                } catch (IOException e) {
                    logHandler.logError("Was not able to inspect:" + folder, e);
                }
            }
            File parent = folder.getParentFile();
            inspect(parent);
        }

    }

    public static boolean isAsciidocConfigFile(Path path) {
        if (path == null) {
            return false;
        }
        return isAsciidocConfigFile(path.toFile());

    }

    public static boolean isAsciidocConfigFile(File file) {
        if (file == null || !file.isFile()) {
            return false;
        }
        String name = file.getName();
        boolean isConfigFile = name.equals(FILENAME_ASCIIDOCTORCONFIG);
        isConfigFile = isConfigFile || name.equals(FILENAME_ASCIIDOCTORCONFIG_ADOC);

        return isConfigFile;
    }

    private AsciidoctorConfigFile createAsciidocConfigFile(Path file) throws IOException {

        String content = AsciiDocStringUtils.readUTF8FileToString(file.toFile());
        AsciidoctorConfigFile configFile = new AsciidoctorConfigFile(content, file);

        return configFile;
    }

    private static class FindAsciidocFilenameFilter implements FileFilter {

        @Override
        public boolean accept(File file) {
            return isAsciidocConfigFile(file);
        }

    }

}
