/*
 * Copyright 2018 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
package de.jcup.asciidoctoreditor.provider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import de.jcup.asciidoctoreditor.TemporaryOutputFileType;
import de.jcup.asciidoctoreditor.asciidoc.AsciiDocFileUtils;

public class AsciiDoctorTempFileProvider extends AbstractAsciiDoctorProvider {

    private static final String ID_HIDDEN_EDITORFILE_TEMP_FILE = "hidden-editorfile_";
    private static Path fallbackTempFolder;
    private static final Object MONITOR = new Object();
    
    AsciiDoctorTempFileProvider(AsciiDoctorProjectProviderContext context) {
        super(context);
    }

    @Override
    protected void reset() {
        
    }
    
    public File createHiddenEditorTempFile(File editorFile, long editorId) throws IOException{

        File rootDirectory = getContext().getRootDirectory();
        Path tempFolder = getContext().getTempFolder();
        
        File baseDiretory = fetchRelativeBaseDirectory(editorFile);
        
        String relativePathToParentFolder = AsciiDocFileUtils.calculateRelativePathToFileFromBase(editorFile.getParentFile(), baseDiretory);
        String relativePathToFile = AsciiDocFileUtils.calculateRelativePathToFileFromBase(editorFile, baseDiretory);
        String sourceFileBaseName = FilenameUtils.getBaseName(editorFile.getName());
        
        TempCreationContext creationContext = new TempCreationContext();
        creationContext.tempFolder=tempFolder;
        creationContext.relativePath=relativePathToParentFolder;
        creationContext.editorId=editorId;
        creationContext.prefix=ID_HIDDEN_EDITORFILE_TEMP_FILE;
        creationContext.sourceFileBaseName=sourceFileBaseName;
        creationContext.fileEnding=".adoc";
        
        File hiddenEditorFile = newTempFile(creationContext);
        
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("// origin file       :").append(editorFile.getAbsolutePath()).append("\n");
            sb.append("// editor id         :").append(editorId).append("\n");
            sb.append("// root directory    :").append(rootDirectory.getAbsolutePath()).append("\n");
            sb.append("// relativePathToFile:").append(relativePathToFile).append("\n");

            sb.append("include::").append(relativePathToFile ).append("[]\n");

            FileUtils.writeStringToFile(hiddenEditorFile, sb.toString(), "UTF-8", false);
            hiddenEditorFile.deleteOnExit();
        } catch (RuntimeException e) {
//            getContext().getLogAdapter().logWarn("File not in current base dir so copied origin as hidden file:" + editorFile.getAbsolutePath());
//            FileUtils.copyFile(editorFile, hiddenEditorFile);
            throw new IOException("Was not able to create hidden editor file!",e);
        }
        return hiddenEditorFile;
    }

    private File fetchRelativeBaseDirectory(File editorFile) {
        File rootDirectory = getContext().getRootDirectory();
        Path tempFolder = getContext().getTempFolder();
        File baseDiretory = rootDirectory;
        if (editorFile.toPath().startsWith(tempFolder)) {
            baseDiretory = tempFolder.toFile();
        }
        return baseDiretory;
    }
    
    public File createHTMLPreviewTempFile(File editorFileOrNull, Long editorId, TemporaryOutputFileType type) {
        Path tempFolder = getContext().getTempFolder();
        String sourceFileBaseName = null;
        if (editorFileOrNull==null) {
            /* as a fallback we create a non existing pseudo file... necessary for files not from file system */
            editorFileOrNull=new File(tempFolder.toFile(),editorId+"_"+ID_HIDDEN_EDITORFILE_TEMP_FILE+"unexisting_editorfile.adoc");
        }

        File baseDiretory = fetchRelativeBaseDirectory(editorFileOrNull);
        
        String editorFileName = editorFileOrNull.getName();
        
        int hiddenEditorTempFileNamePos = editorFileName.indexOf(ID_HIDDEN_EDITORFILE_TEMP_FILE);
        if (hiddenEditorTempFileNamePos != -1) {
            /* its a hidden editor file */
            String baseName = FilenameUtils.getBaseName(editorFileOrNull.getName());
            if (TemporaryOutputFileType.ORIGIN.equals(type)){
                /* keep as is */
                sourceFileBaseName= baseName;
                editorId=null;
            }else {
                sourceFileBaseName= baseName.substring(hiddenEditorTempFileNamePos+ID_HIDDEN_EDITORFILE_TEMP_FILE.length());
            }
        }else {
            /* its a normal file - so we use root directory */
            sourceFileBaseName = FilenameUtils.getBaseName(editorFileOrNull.getName());
        }
        
        
        String relativePathToParentFolder = AsciiDocFileUtils.calculateRelativePathToFileFromBase(editorFileOrNull.getParentFile(), baseDiretory);
        
        TempCreationContext creationContext = new TempCreationContext();
        creationContext.tempFolder=tempFolder;
        creationContext.relativePath=relativePathToParentFolder;
        creationContext.editorId=editorId;
        creationContext.prefix=type.getPrefix();
        creationContext.sourceFileBaseName=sourceFileBaseName;
        creationContext.fileEnding=".html";
        
        File previewTempFile = newTempFile(creationContext);
        return previewTempFile;
    }
    
    private class TempCreationContext{
        Path tempFolder;
        String relativePath;
        Long editorId;
        String prefix;
        String sourceFileBaseName;
        String fileEnding;
        
        private String createFileName() {
            StringBuilder sb = new StringBuilder();
            if (editorId != null) {
                sb.append(editorId);
                sb.append("_");
            }
            if (prefix!=null) {
                sb.append(prefix);
            }
            sb.append(sourceFileBaseName);
            if (fileEnding!=null) {
                sb.append(fileEnding);
            }
            return sb.toString();
        }
    }

    private File newTempFile(TempCreationContext creationContext) {
        Path path = Paths.get(tempFolderOrFallback(creationContext.tempFolder).toFile().getPath(), creationContext.relativePath);

        String name = creationContext.createFileName();
        File newTempFile = AsciiDocFileUtils.createEncodingSafeFile(path, name);

        return newTempFile;
    }
    
    
    private static Path getFallbackTempFolder() {
        synchronized (MONITOR) {
            if (fallbackTempFolder == null) {
                try {
                    fallbackTempFolder = Files.createTempDirectory("__fallback__");
                } catch (IOException e) {
                    throw new IllegalStateException("Cannot create fallback temp directory!", e);
                }
            }
            return fallbackTempFolder;

        }
    }
    private static Path tempFolderOrFallback(Path tempFolder) {
        if (tempFolder==null) {
            return getFallbackTempFolder();
        }
        return tempFolder;
    }

//    public File createImageOutDirTempFile() {
//        Path imagesDir = getContext().getImageDirProvider().getImagesDirAbsoluteFileOrNull();
//        if (imagesDir==null) {
//            File editorFileOrNull = getContext().gethgetEditorFileOrNull();
//            if (editorFileOrNull!=null) {
//                /* we just use the folder of the editor file*/ 
//                return editorFileOrNull.getParentFile();
//            }
//        }
//        
//        return null;
//    }

    

    
    
}
