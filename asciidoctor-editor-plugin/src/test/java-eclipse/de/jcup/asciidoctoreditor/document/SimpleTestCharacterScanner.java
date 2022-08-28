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
package de.jcup.asciidoctoreditor.document;

import org.eclipse.jface.text.rules.ICharacterScanner;

class SimpleTestCharacterScanner implements ICharacterScanner {
    int column;
    private String text;

    public SimpleTestCharacterScanner(String text) {
        this.text = text;
    }

    @Override
    public char[][] getLegalLineDelimiters() {
        char[][] chars = new char[1][];
        chars[0] = "\n".toCharArray();
        return chars;
    }

    @Override
    public int getColumn() {
        return column;
    }

    @Override
    public int read() {
        if (column >= text.length()) {
            return EOF;
        }
        char c = text.substring(column, column + 1).toCharArray()[0];
        column++;
        return c;
    }

    @Override
    public void unread() {
        column--;

    }

}