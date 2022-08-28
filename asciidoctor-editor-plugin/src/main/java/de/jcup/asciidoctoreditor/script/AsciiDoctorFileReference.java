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

public class AsciiDoctorFileReference {

    String target;
    int position;
    int lengthToNameEnd;
    public int end;
    private String targetPrefix;
    private String fullExpression;
    private String filePath;

    public AsciiDoctorFileReference(String fullExpression, int position, int end, int lengthTonNameEnd) {
        this.fullExpression = fullExpression;
        this.target = calculateTarget(fullExpression);
        this.position = position;
        this.end = end;
        this.lengthToNameEnd = lengthTonNameEnd;

        this.targetPrefix = resolveTargetPrefix();
        this.filePath = resolveFilePath();
    }

    private String calculateTarget(String fullExpression) {
        if (fullExpression == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (char charAt : fullExpression.toCharArray()) {
            if (!isPartOfTarget(charAt)) {
                break;
            }
            sb.append(charAt);
        }
        return sb.toString().trim();
    }

    private boolean isPartOfTarget(char charAt) {
        if (Character.isSpaceChar(charAt)) {
            /* we allow spaces */
            return true;
        }
        /* we do not allow [ becaus it terminates target part */
        if (charAt == '[') {
            return false;
        }
        if (Character.isWhitespace(charAt)) {
            /* any other whitespace than space is not accepted */
            return false;
        }
        /* all other characters are accepted */
        return true;
    }

    private String resolveTargetPrefix() {
        if (target == null) {
            return "";
        }
        int prefixIndex = target.indexOf("::");
        if (prefixIndex == -1) {
            return "";
        }
        return target.substring(0, prefixIndex + 2);
    }

    private String resolveFilePath() {
        if (target == null) {
            return "null";
        }
        if (targetPrefix == null || targetPrefix.isEmpty()) {
            return target;
        }
        String filePath = target;
        if (filePath.startsWith(targetPrefix)) {
            filePath = filePath.substring(targetPrefix.length());
        }
        return filePath;
    }

    public int getLengthToNameEnd() {
        return lengthToNameEnd;
    }

    public String getTarget() {
        return target;
    }

    public String getTargetPrefix() {
        return targetPrefix;
    }

    public String getLabel() {
        return getTarget();
    }

    public int getPosition() {
        return position;
    }

    public int getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "=" + target + "[pos:" + position + ",end:" + end + ",lengthToNameEnd:" + lengthToNameEnd + "]";
    }

    public String getFullExpression() {
        return fullExpression;
    }

    /**
     * @return file path (e.g. for an include::abc/xyz/mydoc.adoc file path is
     *         abc/xyz/mydoc.adoc)
     */
    public String getFilePath() {
        return filePath;
    }

}
