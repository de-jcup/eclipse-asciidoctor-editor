/*
 * Copyright 2021 Albert Tregnaghi
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
package de.jcup.asciidoctoreditor.toolbar;

public enum PlantUMLExampleDiagram {
    ACTIVITY("Activity", "activity-diagram.puml"),

    CLASS("Class", "class-diagram.puml"),

    COMPONENT("Component", "component-diagram.puml"),
    
    C4_COMPONENT_DIAGRAM("C4 component","c4-component-diagram-example.puml"),

    C4_CONTAINER_DIAGRAM("C4 container","c4-container-diagram-example.puml"),
    
    C4_DEPLOYMENT_DIAGRAM("C4 deployment","c4-deployment-diagram-example.puml"),
    
    C4_DYNAMIC_DIAGRAM("C4 dynamic diagram","c4-dynamic-diagram-example.puml"),
    
    C4_DYNAMIC_DIAGRAM_MESSAGE_BUS("C4 dynamic diagram (message bus)","c4-dynamic-diagram-messagebus-example.puml"),
    
    DEPLOYMENT("Deployment", "deployment-diagram.puml"), 
    
    GANTT("Gantt", "gantt-diagram.puml"), 
    
    JSON("JSON", "json-diagram.puml"),
    
    MIND_MAP("Mind Map", "mindmap-diagram.puml"), 
    
    NETWORK("Network", "network-diagram-openionic.puml"), 
    
    OBJECT_DIAMOND("Object (diamond)", "object-diagram-diamond.puml"),
    
    OBJECT_PERT("Object (pert)", "object-diagram-pert.puml"), 
    
    SEQUENCE_ALT("Sequence (alternatives)", "sequence-diagram-alternatives.puml"),
    
    SEQUENCE_LIFELINE("Sequence (lifeline)", "sequence-diagram-lifeline-auto.puml"), 
    
    STATE("State", "state-diagram.puml"), 
    
    TIME("Time", "time-diagram.puml"),
    
    USECASE("Usecase", "usecase-diagram.puml"), 
    
    WBS("WBS", "wbs-diagram.puml"), 
    
    WIREFRAME("Wireframe", "wireframe-diagram-salt.puml"), 
    
    YAML("YAML", "yaml-diagram.puml"),
    
    
    ;

    private String label;
    private String fileName;

    private PlantUMLExampleDiagram(String label, String fileName) {
        this.label = label;
        this.fileName = fileName;
    }

    public String getLabel() {
        return label;
    }

    public String getFileName() {
        return fileName;
    }

    public String getPath() {
        return "/examples/plantuml/" + fileName;
    }
    
    @Override
    public String toString() {
        return getLabel();
    }
}