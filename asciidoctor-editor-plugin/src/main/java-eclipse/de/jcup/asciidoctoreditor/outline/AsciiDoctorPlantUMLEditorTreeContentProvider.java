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

import org.eclipse.jface.viewers.ITreeContentProvider;

import de.jcup.asciidoctoreditor.diagram.plantuml.PlantUMLInclude;
import de.jcup.asciidoctoreditor.diagram.plantuml.PlantUMLModel;
import de.jcup.asciidoctoreditor.diagram.plantuml.PlantUMLScriptModel;
import de.jcup.asciidoctoreditor.script.AsciiDoctorScriptModel;
import de.jcup.asciidoctoreditor.script.parser.ParseToken;
import de.jcup.eclipse.commons.SimpleStringUtils;

public class AsciiDoctorPlantUMLEditorTreeContentProvider implements ITreeContentProvider, ScriptItemTreeContentProvider {

    private static final String PLANTUML_SCRIPT_CONTAINS_ERRORS = "PlantUML contains errors.";
    private static final String PLANTPUML_SCRIPT_DOES_NOT_CONTAIN_OUTLINE_PARTS = "Your plantuml file has no includes";
    private static final Object[] RESULT_WHEN_EMPTY = new Object[] { PLANTPUML_SCRIPT_DOES_NOT_CONTAIN_OUTLINE_PARTS };
    private Object[] items;
    private Item cachedLastFoundItemByOffset;
    private Object monitor = new Object();

    AsciiDoctorPlantUMLEditorTreeContentProvider() {
        items = RESULT_WHEN_EMPTY;
    }

    @Override
    public Object[] getElements(Object inputElement) {
        synchronized (monitor) {
            if (inputElement != null && !(inputElement instanceof PlantUMLScriptModel)) {
                return new Object[] { "Unsupported input element:" + inputElement };
            }
            if (items != null && items.length > 0) {
                return items;
            }
        }
        return RESULT_WHEN_EMPTY;
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof Item) {
            Item item = (Item) parentElement;
            return item.getChildren().toArray();
        }
        return null;
    }

    @Override
    public Object getParent(Object element) {
        if (element instanceof Item) {
            Item item = (Item) element;
            return item.getParent();
        }
        return null;
    }

    @Override
    public boolean hasChildren(Object element) {
        if (element instanceof Item) {
            Item item = (Item) element;
            return !item.getChildren().isEmpty();
        }
        return false;
    }

    private Item[] createItems(PlantUMLScriptModel model) {
        List<Item> list = new ArrayList<>();
        addIncludes(model, list);
        if (list.isEmpty()) {
            Item item = new Item();
            item.name = PLANTPUML_SCRIPT_DOES_NOT_CONTAIN_OUTLINE_PARTS;
            item.type = ItemType.META_INFO;
            item.offset = 0;
            item.length = 0;
            item.endOffset = 0;
            list.add(item);
        }

        /* debug part */
        if (model.hasDebugTokens()) {
            if (model.hasErrors()) {
                Item item = new Item();
                item.name = PLANTUML_SCRIPT_CONTAINS_ERRORS;
                item.type = ItemType.META_ERROR;
                item.offset = 0;
                item.length = 0;
                item.endOffset = 0;
                list.add(0, item);
            }
            for (ParseToken token : model.getDebugTokens()) {
                Item item = new Item();
                item.name = SimpleStringUtils.shortString(token.getText(), 40) + " :<- " + token.createTypeDescription();
                item.type = ItemType.META_DEBUG;
                item.offset = token.getStart();
                item.length = token.getText().length();
                item.endOffset = token.getEnd();
                list.add(item);
            }
        }
        return list.toArray(new Item[list.size()]);

    }

    protected void addIncludes(PlantUMLScriptModel model, List<Item> list) {
        PlantUMLModel plantUMLModel = model.getPlantUMLModel();

        for (PlantUMLInclude include : plantUMLModel.getIncludes()) {
            Item item = new Item();
            item.name = include.getLocation();
            item.type = ItemType.INCLUDE;
            item.offset = include.getPosition();
            item.length = include.getLength();
            item.endOffset = item.offset + item.length;
            item.fullString = include.getLine();
            item.filePath = include.getLocation();
            list.add(item);
        }
    }

    public void rebuildTree(AsciiDoctorScriptModel model) {
        synchronized (monitor) {
            if (model == null) {
                items = null;
                cachedLastFoundItemByOffset = null;
                return;
            }
            items = createItems((PlantUMLScriptModel) model);
        }
    }

    public Item tryToFindByOffset(int offset) {
        synchronized (monitor) {
            if (items == null) {
                return null;
            }
            if (cachedLastFoundItemByOffset != null) {
                if (cachedLastFoundItemByOffset.offset == offset) {
                    return cachedLastFoundItemByOffset;

                }
            }
            /* not existing or offset differernt, so reset always to null */
            cachedLastFoundItemByOffset = null;

            List<Item> list = new ArrayList<>();
            for (Object oItem : items) {
                if (oItem instanceof Item) {
                    list.add((Item) oItem);
                }
            }
            inspectUntilNextCachedItem(offset, list);

            return cachedLastFoundItemByOffset;
        }
    }

    protected void inspectUntilNextCachedItem(int offset, List<Item> itemsToSearch) {
        if (cachedLastFoundItemByOffset != null) {
            return;
        }
        for (Item item : itemsToSearch) {
            int itemStart = item.getOffset();
            int itemEnd = item.getEndOffset();// old:
                                              // itemStart+item.getLength();
            if (offset >= itemStart && offset <= itemEnd) {
                cachedLastFoundItemByOffset = item;
                return;
            }
            /*
             * check childern - endpos of parent contains not endpos of children!
             */
            inspectUntilNextCachedItem(offset, item.getChildren());

        }
    }

}
