/*
 * Copyright 2019 Albert Tregnaghi
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
package de.jcup.asciidoctor.converter;

import static de.jcup.asciidoctor.converter.ConverterConstants.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractToAsciidoctorConverter implements ToAsciidocConverter{


    public String convert(String markdown) {
        return internalConvert(markdown);
    }

    public String convert(File file) throws IOException {
        if (file==null) {
            return "";
        }
        if (file.isDirectory()) {
            throw new IOException("This is a directory:"+file);
        }
        String origin= readLinesAsString(file);
        return internalConvert(origin);
    }
    
    public void convertToFiles(File fileOrFolderToConvert) throws IOException {
        if (fileOrFolderToConvert==null) {
            return;
        }
        if (!fileOrFolderToConvert.exists()) {
            return;
        }
        if (fileOrFolderToConvert.isFile()) {
            File targetFolder = new File(fileOrFolderToConvert.getParentFile(),CONVERT_DIRECTORY_NAME);
            convertSingleFile(targetFolder, fileOrFolderToConvert);
        }else {
            File targetRootFolder=new File(fileOrFolderToConvert,CONVERT_DIRECTORY_NAME);
            File sourceRootFolder= fileOrFolderToConvert;
            convertMultipleFiles(sourceRootFolder, targetRootFolder, sourceRootFolder.list());
        }
    }
    
    void convertMultipleFiles(File sourceFolder, File targetFolder, String[] names) throws IOException {
        for (String name: names) {
            File file = new File(sourceFolder,name);
            if (!file.exists()) {
                continue;
            }
            if (file.isDirectory()) {
                File target = new File(targetFolder,name);
                convertMultipleFiles(file,target, file.list());
            }else {
                convertSingleFile(targetFolder, file);
            }
        }
            
    }

    private void convertSingleFile(File targetFolder, File file) throws IOException {
        String name =  file.getName();
        String acceptedFileEnding = getAcceptedFileEnding();
        if (!name.endsWith(acceptedFileEnding)){
            /* ignore this file */
            return;
        }
        String targetname = name.substring(0,name.length()-acceptedFileEnding.length())+".adoc";
        if (!targetFolder.exists()) {
            if (!targetFolder.mkdirs()){
                throw new IOException("Cannot create target folder:"+targetFolder.getAbsolutePath());
            }
        }
        File targetFile = new File(targetFolder,targetname);

        String markdown = readLinesAsString(file);
        String asciidoc = internalConvert(markdown);
        
        try(FileWriter fw = new FileWriter(targetFile)){
            fw.write(asciidoc);
        }
    }

    /**
     * Returns File ending which is treated to convert
     * @return file ending ".md"
     */
    protected abstract String getAcceptedFileEnding();
    
    private String internalConvert(String origin) {
        if (origin==null) {
            return "";
        }
        return convertImpl(origin);
    }

    protected abstract String convertImpl(String origin);

    private String readLinesAsString(File file) throws IOException {
        List<String> lines = Files.readAllLines(file.toPath());
        StringBuilder sb = new StringBuilder();
        for (Iterator<String> it = lines.iterator(); it.hasNext();) {
            sb.append(it.next());
            if (it.hasNext()) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }


}
