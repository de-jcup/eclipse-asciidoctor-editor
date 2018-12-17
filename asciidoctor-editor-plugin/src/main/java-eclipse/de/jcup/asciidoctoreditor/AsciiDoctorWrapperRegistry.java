package de.jcup.asciidoctoreditor;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;

public class AsciiDoctorWrapperRegistry {
    public static AsciiDoctorWrapperRegistry INSTANCE = new AsciiDoctorWrapperRegistry();
    
    private Map<IProject, AsciiDoctorWrapper> map = new HashMap<>();

    private AsciiDoctorWrapperRegistry(){
        
    }
    
    public AsciiDoctorWrapper getWrapper(IProject project) {
        return map.computeIfAbsent(project, x -> createWrapper(x));
    }

    private AsciiDoctorWrapper createWrapper(IProject project) {
        return new AsciiDoctorWrapper(project, AsciiDoctorEclipseLogAdapter.INSTANCE);
    }
}
