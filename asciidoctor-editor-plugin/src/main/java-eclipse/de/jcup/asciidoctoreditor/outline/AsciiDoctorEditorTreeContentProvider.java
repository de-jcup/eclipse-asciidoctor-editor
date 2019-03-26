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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ITreeContentProvider;

import de.jcup.asciidoctoreditor.SimpleStringUtils;
import de.jcup.asciidoctoreditor.script.AsciiDoctorHeadline;
import de.jcup.asciidoctoreditor.script.AsciiDoctorInclude;
import de.jcup.asciidoctoreditor.script.AsciiDoctorInlineAnchor;
import de.jcup.asciidoctoreditor.script.AsciiDoctorScriptModel;
import de.jcup.asciidoctoreditor.script.parser.ParseToken;

public class AsciiDoctorEditorTreeContentProvider implements ITreeContentProvider {

	private static final String ASCIIDOCTOR_SCRIPT_CONTAINS_ERRORS = "AsciiDoctorWrapper script contains errors.";
	private static final String ASCIIDOCTOR_SCRIPT_DOES_NOT_CONTAIN_OUTLINE_PARTS = "Your document has no includes or headlines";
	private static final Object[] RESULT_WHEN_EMPTY = new Object[] {
			ASCIIDOCTOR_SCRIPT_DOES_NOT_CONTAIN_OUTLINE_PARTS };
	private Object[] items;
	private Item cachedLastFoundItemByOffset;
	private Object monitor = new Object();

	AsciiDoctorEditorTreeContentProvider() {
		items = RESULT_WHEN_EMPTY;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		synchronized (monitor) {
			if (inputElement != null && !(inputElement instanceof AsciiDoctorScriptModel)) {
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
	
	private Item[] createItems(AsciiDoctorScriptModel model) {
		Map<Integer, Item> parents = new HashMap<Integer, Item>();
		List<Item> list = new ArrayList<>();
		addIncludes(model, list);
		addHeadlines(model, parents, list);
		addAnchors(model, list);
		if (list.isEmpty()) {
			Item item = new Item();
			item.name = ASCIIDOCTOR_SCRIPT_DOES_NOT_CONTAIN_OUTLINE_PARTS;
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
				item.name = ASCIIDOCTOR_SCRIPT_CONTAINS_ERRORS;
				item.type = ItemType.META_ERROR;
				item.offset = 0;
				item.length = 0;
				item.endOffset = 0;
				list.add(0, item);
			}
			for (ParseToken token : model.getDebugTokens()) {
				Item item = new Item();
				item.name = SimpleStringUtils.shortString(token.getText(), 40) + " :<- "
						+ token.createTypeDescription();
				item.type = ItemType.META_DEBUG;
				item.offset = token.getStart();
				item.length = token.getText().length();
				item.endOffset = token.getEnd();
				list.add(item);
			}
		}
		return list.toArray(new Item[list.size()]);

	}

	protected void addIncludes(AsciiDoctorScriptModel model, List<Item> list) {
		for (AsciiDoctorInclude include : model.getIncludes()) {
			Item item = new Item();
			item.name = include.getLabel();
			item.type = ItemType.INCLUDE;
			item.offset = include.getPosition();
			item.length = include.getLengthToNameEnd();
			item.endOffset = include.getEnd();
			item.fullString = include.getFullExpression();

			list.add(item);
		}
	}

	protected void addHeadlines(AsciiDoctorScriptModel model, Map<Integer, Item> parents, List<Item> list) {
		boolean alreadyOneDeep1 = false;
		for (AsciiDoctorHeadline headline : model.getHeadlines()) {
			Item item = new Item();
			item.name = headline.getName();
			int originDeep = headline.getDeep();
			if (!alreadyOneDeep1 && originDeep == 1) {
				alreadyOneDeep1 = true;
			} else {
				/* only when not first tile set id */
				item.id = headline.getId();
			}

			int deep = originDeep - 1;// = is level 0 so -1
			item.prefix = "H" + deep + ":";
			item.type = ItemType.HEADLINE;
			item.offset = headline.getPosition();
			item.length = headline.getLengthToNameEnd();
			item.endOffset = headline.getEnd();
			/* register as next parent at this deep */
			parents.put(deep, item);
			/* when not root deep, try to fetch parent */
			Item parent = parents.get(deep - 1);
			if (parent == null) {
				list.add(item);
			} else {
				parent.getChildren().add(item);
				item.parent = parent;
				/* no add to list */
			}
		}
	}

	protected void addAnchors(AsciiDoctorScriptModel model, List<Item> list) {
		for (AsciiDoctorInlineAnchor anchor : model.getInlineAnchors()) {
			Item item = new Item();
			item.name = anchor.getId();
			item.id = anchor.getId();

			item.prefix = "";
			item.type = ItemType.INLINE_ANCHOR;
			item.offset = anchor.getPosition();
			item.length = anchor.getLabel().length();
			item.endOffset = anchor.getEnd();
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
			items = createItems(model);
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
			 * check childern - endpos of parent contains not endpos of
			 * children!
			 */
			inspectUntilNextCachedItem(offset, item.getChildren());

		}
	}
}
