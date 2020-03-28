package de.jcup.asciidoctoreditor.provider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import de.jcup.asciidoctoreditor.asciidoc.AsciiDocFileFilter;

public class AttributeSearch {
    private static final AsciiDocFileFilter ASCIIDOC_FILE_SCAN_FILTER = new AsciiDocFileFilter(true);

    public FileMatch resolveFirstAttributeFoundTopDown(AttributeSearchParameter attribute, File parent) throws IOException {
        if (parent.isFile()) {
            FileMatch match = scanFor(attribute, parent);
            if (match != null) {
                return match;
            }
            return null;
        }else {
            
            File[] result = parent.listFiles(ASCIIDOC_FILE_SCAN_FILTER);
            
            for (File file : result) {
                if (file.isFile()) {
                    FileMatch match = scanFor(attribute, file);
                    if (match != null) {
                        return match;
                    }
                }
            }
            
            /* after this do recursion */
            for (File file : result) {
                if (file.isDirectory()) {
                    FileMatch match = resolveFirstAttributeFoundTopDown(attribute, file);
                    if (match != null) {
                        return match;
                    }
                    
                }
            }
            return null;
        }
    }

    private FileMatch scanFor(AttributeSearchParameter attribute, File file) throws IOException {
        try(BufferedReader br = new BufferedReader(new FileReader(file))){
            String line = null;
            while ((line=br.readLine())!=null) {
                int index = line.indexOf(attribute.getSearchString());
                if (index!=-1) {
                    String value = line.substring(index+attribute.getSearchString().length()).trim();
                    FileMatch fileMatch = new FileMatch(file, value);
                    return fileMatch;
                }
            }
        }
        return null;
    }
}
