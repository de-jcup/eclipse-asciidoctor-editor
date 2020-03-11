package de.jcup.asciidoctoreditor.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

import de.jcup.asciidoctoreditor.search.AsciidocSearchResultModel.ResourceElement.ResourceLineElement;
import de.jcup.asciidoctoreditor.search.AsciidocSearchResultModel.ResourceElement.ResourceLineElement.ResourceLineContentElement;

public class AsciidocSearchResultModel implements AsciidocSearchResultElement {
    private static final Object[] EMPTY_OBJ_ARRAY = new Object[] {};

    private ProjectElement FALLBACK_PROJECT = new ProjectElement("[NO-Project]");
    private Map<String, ProjectElement> projectElements = new TreeMap<>();
    
    public ResourceElement addResourceElement(IResource resource) {
        IProject project = resource.getProject();
        String projectNameToSearch = FALLBACK_PROJECT.getProjectName();
        if (project!=null) {
            projectNameToSearch= project.getName();
        }
        ProjectElement pe = projectElements.computeIfAbsent(projectNameToSearch, projectName-> createProjectElement(projectName));
        
        ResourceElement element = new ResourceElement(resource);
        pe.resourceElements.add(element);
        return element;
    }

    private ProjectElement createProjectElement(String projectName) {
        return new ProjectElement(projectName);
    }

    public Object[] getChildren() {
        return projectElements.values().toArray();
    }

    public class ProjectElement implements AsciidocSearchResultElement {

        private List<ResourceElement> resourceElements = new ArrayList<>();
        private String name;

        public ProjectElement(String name) {
            this.name=name;
        }

        public String getProjectName() {
            return name;
        }

        @Override
        public AsciidocSearchResultElement getParent() {
            return AsciidocSearchResultModel.this;
        }

        public Object[] getChildren() {
            return resourceElements.toArray();
        }

    }

    public class ResourceElement implements AsciidocSearchResultElement {
        private List<ResourceLineElement> lines = new ArrayList<>();
        private IResource resource;

        public ResourceElement(IResource resource) {
            this.resource = resource;
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

        public class ResourceLineElement implements AsciidocSearchResultElement {
            private int lineNumber;
            private List<ResourceLineContentElement> contentData = new ArrayList<>();

            public ResourceLineElement(int lineNumber) {
                this.lineNumber = lineNumber;
            }

            public int getLineNumber() {
                return lineNumber;
            }

            public ResourceElement getParent() {
                return ResourceElement.this;
            }

            public ResourceLineContentElement addContent(String text, int offset) {
                ResourceLineContentElement element = new ResourceLineContentElement(text, offset);
                contentData.add(element);
                return element;
            }

            @Override
            public Object[] getChildren() {
                return contentData.toArray();
            }

            public class ResourceLineContentElement implements AsciidocSearchResultElement {

                private int offset;
                private String text;

                public ResourceLineContentElement(String text, int offset) {
                    this.text = text;
                    this.offset = offset;
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

    public Object[] getFlatResourceElements() {
        List<ResourceLineContentElement> list = new ArrayList<>();
        for (ProjectElement pe: projectElements.values()) {
            for (ResourceElement element: pe.resourceElements) {
                for (ResourceLineElement le: element.lines) {
                    for (ResourceLineContentElement lce : le.contentData) {
                        list.add(lce);
                    }
                }
            }
        }
        return list.toArray();
    }

}
