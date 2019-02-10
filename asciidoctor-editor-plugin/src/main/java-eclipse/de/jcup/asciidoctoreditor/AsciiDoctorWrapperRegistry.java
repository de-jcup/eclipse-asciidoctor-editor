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
        AsciiDoctorWrapper wrapper = new AsciiDoctorWrapper(project, AsciiDoctorEclipseLogAdapter.INSTANCE);
        return wrapper;
    }
}
