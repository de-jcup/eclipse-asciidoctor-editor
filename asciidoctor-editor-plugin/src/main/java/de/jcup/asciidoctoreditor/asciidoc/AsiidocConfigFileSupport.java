package de.jcup.asciidoctoreditor.asciidoc;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.jcup.asciidoctoreditor.LogHandler;
import de.jcup.asciidoctoreditor.PrintStreamLogHandler;
import de.jcup.asciidoctoreditor.util.AsciiDoctorEditorUtil;

public class AsiidocConfigFileSupport {

    private static final FindAsciidocFilenameFilter ASCIIDOC_CONFIG_FILENAME_FILTER = new FindAsciidocFilenameFilter();

    public static final String FILENAME_ASCIIDOCTORCONFIG = ".asciidoctorconfig";
    public static final String FILENAME_ASCIIDOCTORCONFIG_ADOC = FILENAME_ASCIIDOCTORCONFIG + ".adoc";
    private LogHandler logHandler;
    private Path rootFolder;

    private boolean autoCreateConfig;

    private Runnable autoCreateCallback;

    public AsiidocConfigFileSupport(Path rootFolder) {
        this(null, rootFolder);
    }

    public Path getRootFolder() {
        return rootFolder;
    }

    /**
     * When enabled, the support will automatically create a config file at root
     * location when no no config is found at all.
     * 
     * @param autoCreateConfig
     */
    public void setAutoCreateConfig(boolean autoCreateConfig) {
        this.autoCreateConfig = autoCreateConfig;
    }

    public void setAutoCreateConfigCallback(Runnable r) {
        this.autoCreateCallback = r;
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

    /**
     * Collects configuration files - when auto create is enabled, a configuraiton
     * at root level will be created when no other config has been found.
     * 
     * @param asciidocFile
     * @return configuration files - first one inside list the most far one (near
     *         parent), last one the nearest!
     */
    public List<AsciidoctorConfigFile> collectConfigFiles(Path asciidocFile) {
        ReverseFileWalker walker = new ReverseFileWalker();
        walker.walk(asciidocFile);
        /* we must reverse order */
        Collections.reverse(walker.filesFound);

        if (walker.filesFound.isEmpty()) {
            if (autoCreateConfig) {
                /* we shall create a config file at root level */
                /* @formatter:off */
                String content="// +++++++++++++++++++++++++++++++++++++++++++++++++++++++\n"+
                               "// +  Initial AsciiDoc editor configuration file - V1.0  +\n"+
                               "// ++++++++++++++++++++++++++++++++++++++++++++++++++++++\n"+
                               "// \n"+
                               "// Did not found any configuration files, so create this at project root level.\n" +
                               "// If you do not like those files to be generated - you can turn it off inside Asciidoctor Editor preferences.\n" +
                               "// \n" +
                               "// You can define editor specific parts here.\n" +
                               "// For example: with next line you could set imagesdir attribute to subfolder \"images\" relative to the folder where this config file is located.\n" +
                               "// :imagesdir: {asciidoctorconfigdir}/images\n"+
                               "// \n"+
                               "// For more information please take a look at https://github.com/de-jcup/eclipse-asciidoctor-editor/wiki/Asciidoctor-configfiles\n";
                        ;
                               /* @formatter:on */
                File file = new File(rootFolder.toFile(), FILENAME_ASCIIDOCTORCONFIG_ADOC);
                Path targetPath = file.toPath();

                try {
                    Files.write(targetPath, content.getBytes(Charset.forName("UTF-8")));
                    if (autoCreateCallback != null) {
                        autoCreateCallback.run();
                    }
                    return Arrays.asList(createAsciidocConfigFile(targetPath));
                } catch (IOException e) {
                    AsciiDoctorEditorUtil.logError("Was not able to auto create config file", e);
                }
            }
        }

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
        return createAsciidocConfigFile(file, file.toFile());
    }

    private AsciidoctorConfigFile createAsciidocConfigFile(Path file, File asFile) throws IOException {
        String content = AsciiDocStringUtils.readUTF8FileToString(asFile);
        AsciidoctorConfigFile configFile = new AsciidoctorConfigFile(content, file);

        return configFile;
    }

    private static class FindAsciidocFilenameFilter implements FileFilter {

        @Override
        public boolean accept(File file) {
            return isAsciidocConfigFile(file);
        }

    }

    private Map<String, String> buildMap(List<AsciidoctorConfigFile> configFiles) {
        // config files: last one inside list the most far one (near parent), first one
        // the nearest!
        // so we must reverse order here, because otherwise farest does overwrite all
        // others!
        Map<String, String> result = new LinkedHashMap<String, String>();
        for (AsciidoctorConfigFile configFile : configFiles) {
            result.putAll(configFile.toContentCustomizedMap());
        }

        /* now resolve/combine key values from config files as well */
        for (String key : result.keySet()) {
            String value = result.get(key);
            String newValue = calculate(value, result);
            result.put(key, newValue);
        }

        return result;
    }

    /**
     * Calculates resolved map
     * 
     * @param map
     * @param configFiles - first one inside list the most far one (near parent),
     *                    last one the nearest!
     * @return resolved map
     */
    public Map<String, Object> calculateResolvedMap(Map<String, Object> map, List<AsciidoctorConfigFile> configFiles) {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        Map<String, String> fromConfigFiles = buildMap(configFiles);
        result.putAll(fromConfigFiles);
        for (String mapKey : map.keySet()) {
            Object mapValue = map.get(mapKey);

            if (!(mapValue instanceof String)) {
                result.put(mapKey, mapValue);
                continue;
            }
            String mapValueString = mapValue.toString();
            String newValue = calculate(mapValueString, fromConfigFiles);
            result.put(mapKey, newValue);
        }
        return result;
    }

    String calculate(String mapValueString, Map<String, String> fromConfigFiles) {
        String result = mapValueString;
        for (String configKey : fromConfigFiles.keySet()) {
            String configValue = fromConfigFiles.get(configKey);
            result = result.replaceAll("\\{" + configKey + "\\}", configValue);
        }
        return result;
    }

}
