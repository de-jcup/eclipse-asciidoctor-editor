package de.jcup.asciidoctoreditor.globalmodel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AsciidocFile {

    private List<AsciidocIncludeNode> includeNodes = new ArrayList<>();
    private List<AsciidocFile> asciiDocFilesWhichIncludesThisFile = new ArrayList<>();
    
    private List<AsciidocImageNode> imageNodes = new ArrayList<>();
    private List<AsciidocDiagramNode> diagramNodes = new ArrayList<>();
    
    File file;
    private boolean fallback;

    public File getFile() {
        return file;
    }
    
    public List<AsciidocImageNode> getImageNodes() {
        return imageNodes;
    }
    
    public List<AsciidocDiagramNode> getDiagramNodes() {
        return diagramNodes;
    }

    public List<AsciidocIncludeNode> getIncludeNodes() {
        return includeNodes;
    }

    public List<AsciidocFile> getAsciidocFilesWhichIncludeThisFile() {
        return asciiDocFilesWhichIncludesThisFile;
    }

    @Override
    public String toString() {
        return "AsciidocFile:" + Objects.toString(file);
    }

    void markAsFallback() {
        fallback = true;
    }

    /**
     * @return <code>true</code> when the model creation has created the file
     *         because referenced, but not really resolvable/existing.
     */
    public boolean isFallback() {
        return fallback;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(file);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof AsciidocFile)) {
            return false;
        }
        AsciidocFile other = (AsciidocFile) obj;
        return Objects.equals(file, other.file);
    }

    void addInclude(AsciidocFile includedFile, int position, int length) {
        AsciidocIncludeNode includeNode = new AsciidocIncludeNode();
        includeNode.asciidocFile=this;
        includeNode.includedAsciidocFile=includedFile;
        includeNode.positionStartIndex=position;
        includeNode.length=length;
        
        includeNodes.add(includeNode);
        
        includedFile.asciiDocFilesWhichIncludesThisFile.add(this);
    }
    
    void addImage(File imageFile,int position, int length) {
        AsciidocImageNode imageNode = new AsciidocImageNode();
        imageNode.asciidocFile=this;
        imageNode.imageFile=imageFile;
        imageNode.positionStartIndex=position;
        imageNode.length=length;
        
        imageNodes.add(imageNode);
    }

    public void addDiagram(File diagramFile, int position, int length) {
        AsciidocDiagramNode imageNode = new AsciidocDiagramNode();
        imageNode.asciidocFile=this;
        imageNode.diagramFile=diagramFile;
        imageNode.positionStartIndex=position;
        imageNode.length=length;
        
        diagramNodes.add(imageNode);
        
    }

    
}
