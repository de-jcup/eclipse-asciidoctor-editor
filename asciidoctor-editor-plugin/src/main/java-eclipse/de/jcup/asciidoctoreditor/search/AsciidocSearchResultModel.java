package de.jcup.asciidoctoreditor.search;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;

public class AsciidocSearchResultModel implements AsciidocSearchResultElement{
    private static final Object[] EMPTY_OBJ_ARRAY = new Object[] {};
    private List<ResourceElement> resourceElements = new ArrayList<>();
    
    public ResourceElement addResourceElement(IResource resource) {
        ResourceElement element = new ResourceElement(resource);
        resourceElements.add(element);
        return element;
    }
    
    public Object[] getChildren() {
        return resourceElements.toArray();
    }
    
    public class ResourceElement implements AsciidocSearchResultElement{
        private List<ResourceLineElement> lines = new ArrayList<>();
        private IResource resource;
        
        public ResourceElement(IResource resource) {
            this.resource=resource;
        }
        
        public String getLabel() {
            if (resource==null) {
                return "null";
            }
            return resource.getName();
        }
        
        public ResourceLineElement createNewLine(int lineNumber) {
            ResourceLineElement element = new ResourceLineElement(lineNumber);
            lines.add(element);
            return element;
        }
        
        public Object[] getChildren() {
            return lines.toArray();
        }
        
        
        public AsciidocSearchResultModel getParent() {
            return AsciidocSearchResultModel.this;
        }
        
        public class ResourceLineElement implements AsciidocSearchResultElement{
            private int lineNumber;
            private List<ResourceLineContentElement> contentData = new ArrayList<>();
            
            public ResourceLineElement(int lineNumber) {
                this.lineNumber=lineNumber;
            }
            
            public int getLineNumber() {
                return lineNumber;
            }

            public ResourceElement getParent() {
                return ResourceElement.this;
            }
            
            public ResourceLineContentElement addContent(String text, int offset) {
                ResourceLineContentElement element = new ResourceLineContentElement(text,offset);
                contentData.add(element);
                return element;
            }

            @Override
            public Object[] getChildren() {
                return contentData.toArray();
            }
            
            public class ResourceLineContentElement implements AsciidocSearchResultElement{
              
                private int offset;
                private String text;
                
                public ResourceLineContentElement(String text, int offset) {
                    this.text=text;
                    this.offset=offset;
                }
                public String getText() {
                    return text;
                }
                
                public int getOffset() {
                    return offset;
                }
                public ResourceLineElement getParent() {
                    return ResourceLineElement.this;
                }
                
                public Object[] getChildren() {
                    return EMPTY_OBJ_ARRAY;
                }
                
            }

            
            
        }

        public IResource getResource() {
            return resource;
        }
    }

    @Override
    public AsciidocSearchResultElement getParent() {
        return null;
    }
    
    
    
}
