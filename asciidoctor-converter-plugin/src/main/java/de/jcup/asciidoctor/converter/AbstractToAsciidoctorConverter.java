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
        if (! file.isDirectory()) {
            throw new IOException("This is a directory:"+file);
        }
        String origin= readLinesAsString(file);
        return internalConvert(origin);
    }
    
    public void convertToFiles(File fileOrFolderToConvert) throws IOException {
        convertToFiles(fileOrFolderToConvert,null);
    }
    
    void convertToFiles(File fileOrFolderToConvert, File targetFolder) throws IOException {
        if (fileOrFolderToConvert==null) {
            return;
        }
        if (!fileOrFolderToConvert.exists()) {
            return;
        }
        if (targetFolder==null) {
            if (fileOrFolderToConvert.isDirectory()) {
                targetFolder=new File(fileOrFolderToConvert,CONVERT_DIRECTORY_NAME);
            }else {
                targetFolder=new File(fileOrFolderToConvert.getParentFile(),CONVERT_DIRECTORY_NAME);
            }
        }
        if (!targetFolder.exists()) {
            if (!targetFolder.mkdirs()) {
                throw new IOException("Was not able to create target folder:"+targetFolder.getAbsolutePath());
            }
        }
        if (fileOrFolderToConvert.isDirectory()) {
            File folder = fileOrFolderToConvert;
            if (folder.getName().contentEquals(CONVERT_DIRECTORY_NAME)) {
                /* ignore this*/
                return;
            }
            for (File child: folder.listFiles()) {
                convertToFiles(child, targetFolder);
            }
        }else {
            File file = fileOrFolderToConvert;
            String name =  file.getName();
            String acceptedFileEnding = getAcceptedFileEnding();
            if (!name.endsWith(acceptedFileEnding)){
                /* ignore this file */
                return;
            }
            String targetname = name.substring(0,name.length()-acceptedFileEnding.length())+".adoc";
            File targetFile = new File(targetFolder,targetname);

            String markdown = readLinesAsString(file);
            String asciidoc = internalConvert(markdown);
            
            try(FileWriter fw = new FileWriter(targetFile)){
                fw.write(asciidoc);
            }
            
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
