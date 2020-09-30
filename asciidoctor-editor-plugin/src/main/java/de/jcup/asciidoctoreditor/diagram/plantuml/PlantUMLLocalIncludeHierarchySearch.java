package de.jcup.asciidoctoreditor.diagram.plantuml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class PlantUMLLocalIncludeHierarchySearch {

    private SimplePlantUMLParser parser = new SimplePlantUMLParser();
    private File baseFolder = new File(".");

    public List<File> searchLocalIncludes(String text) throws IOException {
        List<File> files = new ArrayList<>();
        searchLocalIncludes(files, text, 0);
        return files;
    }

    private void searchLocalIncludes(List<File> files, String text, int level) throws IOException {
        if (level>100) {
            throw new IOException("Maximium recursive include scan level reached:"+level);
        }
        PlantUMLModel model = parser.parse(text);
        List<PlantUMLInclude> includes = model.getIncludes();
        for (PlantUMLInclude include : includes) {
            if (!include.isLocal()) {
                continue;
            }
            String location = include.getLocation();
            File file = new File(baseFolder, location);
            if (!file.exists()) {
                throw new FileNotFoundException("Include not found:" + file.getAbsolutePath());
            }
            files.add(file);
            try(FileInputStream fis = new FileInputStream(file)){
                String loadedText = IOUtils.toString(fis,"UTF-8");
                searchLocalIncludes(files, loadedText, level+1);
            }
        }
    }

    public void setBaseFolder(File baseFolder) {
        this.baseFolder=baseFolder;
    }
}
