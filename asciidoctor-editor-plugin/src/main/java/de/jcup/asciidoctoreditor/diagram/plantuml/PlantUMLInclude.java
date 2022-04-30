/*
 * Copyright 2020 Albert Tregnaghi
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
package de.jcup.asciidoctoreditor.diagram.plantuml;

import java.net.URI;

public class PlantUMLInclude implements PlantUMLElement {

    private String location;
    private boolean local;
    private int lineNumber;
    private int position;
    private String line;

    public PlantUMLInclude(String location) {
        this.location = location;
        try {
            URI uri = URI.create(location);
            if (uri.getHost() != null) {
                local = false;
            } else {
                local = true;
            }
        } catch (Exception e) {
            local = true;
        }
    }

    public String getLocation() {
        return location;
    }

    public boolean isLocal() {
        return local;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setPosition(int pos) {
        this.position = pos;
    }

    public int getPosition() {
        return position;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getLine() {
        return line;
    }

    public int getLength() {
        if (line==null) {
            return 0;
        }
        return line.length();
    }
}
