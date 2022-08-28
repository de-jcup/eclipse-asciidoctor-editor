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
package de.jcup.asciidoctoreditor.script;

public class AsciiDoctorInlineAnchor implements AsciidoctorTextSelectable {

    private int end;
    private int position;
    private String label;
    private String id;
    private int selectionLength;

    public AsciiDoctorInlineAnchor(String text, int position, int end) {
        if (text == null) {
            text = "";
        }
        this.label = text;
        this.end = end;
        this.position = position;
        this.id = createIDByLabel();
        this.selectionLength = id.length();
    }

    private String createIDByLabel() {
        String id = createIDByLabelNoCommataCheck();
        if (id == null) {
            return null;
        }
        int index = id.indexOf(',');
        if (index != -1) {
            id = id.substring(0, index);
        }
        return id.trim();
    }

    private String createIDByLabelNoCommataCheck() {
        if (label == null) {
            return null;
        }
        if (label.startsWith("[[")) {
            if (!label.endsWith("]]")) {
                return "illegal-noend-" + System.nanoTime();
            }
            return label.substring(2, label.length() - 2);
        } else if (label.startsWith("[#")) {
            if (label.endsWith("]]")) {
                return "illegal-noend-" + System.nanoTime();
            }
            if (!label.endsWith("]")) {
                return "illegal-noend-" + System.nanoTime();
            }
            return label.substring(2, label.length() - 1);
        }
        return "illegal-nostart-" + System.nanoTime();
    }

    public String getLabel() {
        return label;
    }

    @Override
    public int getPosition() {
        return position;
    }

    public int getEnd() {
        return end;
    }

    public String getId() {
        return id;
    }

    @Override
    public int getSelectionStart() {
        /* anker starts always with [[ so add 2 */
        return getPosition() + 2;
    }

    @Override
    public int getSelectionLength() {
        return selectionLength;
    }

}
