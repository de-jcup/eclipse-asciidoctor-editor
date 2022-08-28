/*
 * Copyright 2017 Albert Tregnaghi
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

import java.util.regex.Pattern;

public class AsciiDoctorHeadline implements AsciidoctorTextSelectable {

    private static final String REGEXP_STRING = "\\W";
    private static final Pattern REGEXP = Pattern.compile(REGEXP_STRING, Pattern.UNICODE_CHARACTER_CLASS);

    String name;
    int position;
    int lengthToNameEnd;
    int end;
    int deep;
    private String calculatedId;
    private String id;

    public AsciiDoctorHeadline(int deep, String name, int position, int end, int lengthTonNameEnd) {
        this.deep = deep;
        this.name = name;

        this.position = position;
        this.end = end;
        this.lengthToNameEnd = lengthTonNameEnd;
        this.calculatedId = calculateId(name);
    }

    void setId(String id) {
        this.id = id;
    }

    boolean isIdSet() {
        return id != null;
    }

    public String getCalculatedId() {
        return calculatedId;
    }

    static String calculateId(String name) {
        if (name == null) {
            return "";
        }
        String id = REGEXP.matcher(name).replaceAll("_");
        id = "_" + id.toLowerCase();
        /* remove ending _ */
        while (id.length() > 1 && id.endsWith("_")) {
            id = id.substring(0, id.length() - 1);
        }
        /* remove double _ */
        while (id.indexOf("__") != -1) {
            id = id.replaceAll("__", "_");
        }
        return id;

    }

    public int getLengthToNameEnd() {
        return lengthToNameEnd;
    }

    @Override
    public int getSelectionStart() {
        return getPosition();
    }

    @Override
    public int getSelectionLength() {
        return getLengthToNameEnd();
    }

    public int getDeep() {
        return deep;
    }

    public String getName() {
        return name;
    }

    @Override
    public int getPosition() {
        return position;
    }

    public int getEnd() {
        return end;
    }

    public String getId() {
        if (id == null) {
            return calculatedId;
        }
        return id;
    }

    @Override
    public String toString() {
        return "h" + deep + ":" + name + "[pos:" + position + ",end:" + end + ",lengthToNameEnd:" + lengthToNameEnd + "]";
    }

}
