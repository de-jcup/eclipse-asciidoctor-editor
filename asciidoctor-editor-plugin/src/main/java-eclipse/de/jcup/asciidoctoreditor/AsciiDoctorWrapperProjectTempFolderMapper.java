package de.jcup.asciidoctoreditor;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

class AsciiDoctorWrapperProjectTempFolderMapper {

    Map<IProject, Path> map = new HashMap<>();

    /**
     * If already tempfolder created it will be reused, otherwise tempory
     * folder will be created. if creation is not possible an
     * {@link IllegalStateException} will be thrown.
     * 
     * @param project
     * @return path never <code>null</code>
     * @throws IllegalStateException
     *             when temp folder cannot be created
     */
    public Path getTempFolder(IProject project) {
        return map.computeIfAbsent(project, x -> createPath(x));
    }

    private Path createPath(IProject project) {
        String id = "fallback";
        if (project != null) {
            IProjectDescription description;
            try {
                description = project.getDescription();
                id = description.getName()+ project.hashCode();
            } catch (CoreException e) {
                id = ""+ project.hashCode();
            }
        }
        return AsciiDocFileUtils.createTempFolderForEditor(id);
    }
}