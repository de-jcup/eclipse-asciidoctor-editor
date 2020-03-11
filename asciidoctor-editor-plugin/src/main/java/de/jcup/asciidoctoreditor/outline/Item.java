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
package de.jcup.asciidoctoreditor.outline;

import java.util.ArrayList;
import java.util.List;

public class Item implements Comparable<Item>{

    ItemType type;
    String name;
    int offset;
    int length;
    int endOffset;
    String prefix;
    String filePath;

    public Item() {

    }

    List<Item> children = new ArrayList<>();
    Item parent;
    String fullString;
    String id;

    public List<Item> getChildren() {
        return children;
    }

    public Item getParent() {
        return parent;
    }
    
    /**
     * @return file path (if element references an asciidoc file). The file path starts from base dir of editor!
     */
    public String getFilePathOrNull() {
        return filePath;
    }

    /**
     * @return item blockType , or <code>null</code>
     */
    public ItemType getItemType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public int getOffset() {
        return offset;
    }

    public int getLength() {
        return length;
    }

    public int getEndOffset() {
        return endOffset;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Item:");
        sb.append("label:");
        sb.append(name);
        sb.append(",blockType:");
        sb.append(type);
        sb.append(",offset:");
        sb.append(offset);
        sb.append(",length:");
        sb.append(length);
        sb.append(",endOffset:");
        sb.append(endOffset);
        sb.append(",filePath:");
        sb.append(filePath);
        return sb.toString();
    }

    public String buildSearchString() {
        return name;
    }

    public String getFullString() {
        return fullString == null ? name : fullString;
    }

    public String getId() {
        return id;
    }

    @Override
    public int compareTo(Item o) {
        if (o==null) {
            return -1;
        }
        return getOffset()-o.getOffset();
    }
}
