package de.jcup.asciidoctoreditor.globalmodel;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import de.jcup.asciidoctoreditor.LogAdapter;
import de.jcup.asciidoctoreditor.NullLogAdapter;
import de.jcup.asciidoctoreditor.asciidoc.AsciiDocFileFilter;
import de.jcup.asciidoctoreditor.asciidoc.AsciiDocFileUtils;
import de.jcup.asciidoctoreditor.script.AsciiDoctorFileReference;
import de.jcup.asciidoctoreditor.script.parser.SimpleReferenceParser;

public class GlobalAsciidocModelBuilder {

    private LogAdapter logAdapter;
    private File baseFolder;
    private boolean withImages;
    private File imageDir;
    private boolean withDiagrams;
    
    GlobalAsciidocModelBuilder() {
    }
    
    public GlobalAsciidocModelBuilder logWith(LogAdapter logAdapter) {
        this.logAdapter = logAdapter;
        return this;
    }
    
    public GlobalAsciidocModelBuilder from(File baseFolder) {
        this.baseFolder = baseFolder;
        return this;
    }
    
    public GlobalAsciidocModelBuilder withImages(boolean withImages, File imageDir) {
        this.withImages=withImages;
        this.imageDir = imageDir;
        return this;
    }
    
    public GlobalAsciidocModelBuilder withDiagrams(boolean withDiagrams) {
        this.withDiagrams=withDiagrams;
        return this;
    }

    public GlobalAsciidocModel build() {
        if (baseFolder == null) {
            throw new IllegalStateException("base folde must be defined!");
        }
        if (!baseFolder.exists()) {
            throw new IllegalStateException("base folde must exist:" + baseFolder.getAbsolutePath());
        }
        if (!baseFolder.isDirectory()) {
            throw new IllegalStateException("base folde must be a directory:" + baseFolder.getAbsolutePath());
        }
        if (logAdapter == null) {
            logAdapter = new NullLogAdapter();
        }

        GlobalAsciidocModel model = new GlobalAsciidocModel();
        try {
            collectAllAsciidocFilesIntiallyIntoModel(model, baseFolder);
            connectAsciidocFiles(model);
        } catch (IOException e) {
            logAdapter.logError("Was not able to build model. Will return empty model.", e);
        }
        return model;
    }


    private void connectAsciidocFiles(GlobalAsciidocModel model) throws IOException {
        Collection<AsciidocFile> allAsciidocFiles = model.getAllAsciidocFiles();
        for (AsciidocFile asciidocFile : allAsciidocFiles) {
            if (asciidocFile.file == null) {
                continue;
            }
            connectAsciidocFile(model, asciidocFile);
        }
    }

    private void connectAsciidocFile(GlobalAsciidocModel model, AsciidocFile asciidocFile) throws IOException {
        String content = AsciiDocFileUtils.readAsciidocFile(asciidocFile.file);
        
        connectIncludes(model, asciidocFile, content);
        if (withImages) {
            connectImages(model, asciidocFile, content);
        }
        if (withDiagrams) {
            connectDiagrams(model, asciidocFile, content);
        }
    }

    private void connectDiagrams(GlobalAsciidocModel model, AsciidocFile asciidocFile, String content) throws IOException {
        List<AsciiDoctorFileReference> plantUMLReferences = SimpleReferenceParser.PLANTUML_PARSER.parse(content);
        for (AsciiDoctorFileReference plantUMLReference : plantUMLReferences) {
            File relativeFile = new File(baseFolder, plantUMLReference.getFilePath());
            asciidocFile.addDiagram(relativeFile, plantUMLReference.getPosition(),plantUMLReference.getLengthToNameEnd());
        }
        List<AsciiDoctorFileReference> ditaaReferences = SimpleReferenceParser.DITAA_PARSER.parse(content);
        for (AsciiDoctorFileReference ditaaReference : ditaaReferences) {
            File relativeFile = new File(baseFolder, ditaaReference.getFilePath());
            asciidocFile.addDiagram(relativeFile, ditaaReference.getPosition(),ditaaReference.getLengthToNameEnd());
        }
    }
    private void connectImages(GlobalAsciidocModel model, AsciidocFile asciidocFile, String content) throws IOException {
        List<AsciiDoctorFileReference> images = SimpleReferenceParser.IMAGE_PARSER.parse(content);
        for (AsciiDoctorFileReference image : images) {
            File relativeFile = new File(imageDir, image.getFilePath());
            asciidocFile.addImage(relativeFile, image.getPosition(),image.getLengthToNameEnd());
        }
    }
    
    private void connectIncludes(GlobalAsciidocModel model, AsciidocFile asciidocFile, String content) throws IOException {
        List<AsciiDoctorFileReference> includes = SimpleReferenceParser.INCLUDE_PARSER.parse(content);
        for (AsciiDoctorFileReference include : includes) {
            File relativeFile = new File(asciidocFile.file.getParentFile(), include.getFilePath());
            if (!AsciiDocFileFilter.ASCIIDOC_FILES_ONLY.accept(relativeFile)) {
                // other include... e.g. a script/code example like test.json etc.
                continue;
            }
            AsciidocFile includedAsciidocFile = model.getAsciidocFileOrNull(relativeFile, logAdapter);
            if (includedAsciidocFile == null) {
                logAdapter.logWarn("Not found in model:" + asciidocFile.file.getName() + "#" + include.getTarget() + " - will create fallback");
                
                AsciidocFile fallbackAsciidocFile = initAsciidocFileAndAddToModel(model, relativeFile);
                fallbackAsciidocFile.markAsFallback();
                
                includedAsciidocFile = fallbackAsciidocFile;
            }
            asciidocFile.addInclude(includedAsciidocFile,include.getPosition(),include.getLengthToNameEnd());
           
        }
    }

    private void collectAllAsciidocFilesIntiallyIntoModel(GlobalAsciidocModel model, File parent) throws IOException {
        File[] files = parent.listFiles(AsciiDocFileFilter.ASCIIDOC_FILES_AND_FOLDERS);
        for (File file : files) {
            if (file.isDirectory()) {
                collectAllAsciidocFilesIntiallyIntoModel(model, file);
            } else {
                initAsciidocFileAndAddToModel(model, file);
            }
        }
    }

    AsciidocFile initAsciidocFileAndAddToModel(GlobalAsciidocModel model, File relativeFile) throws IOException {
        return model.registerNewAsciidocFile(relativeFile);
    }
}
