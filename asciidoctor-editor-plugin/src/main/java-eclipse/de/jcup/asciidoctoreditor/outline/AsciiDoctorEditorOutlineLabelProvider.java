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

import static de.jcup.asciidoctoreditor.ui.AsciidoctorIconConstants.*;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.TextStyle;

import de.jcup.asciidoctoreditor.AsciiDoctorEditorActivator;
import de.jcup.asciidoctoreditor.ui.AsciiDoctorEditorColorConstants;
import de.jcup.asciidoctoreditor.ui.ColorManager;
import de.jcup.asciidoctoreditor.util.EclipseUtil;

public class AsciiDoctorEditorOutlineLabelProvider extends BaseLabelProvider implements IStyledLabelProvider, IColorProvider {

	private Styler outlineItemTypeStyler = new Styler() {

		@Override
		public void applyStyles(TextStyle textStyle) {
			textStyle.foreground = getColorManager().getColor(AsciiDoctorEditorColorConstants.OUTLINE_ITEM__TYPE);
		}
	};

	@Override
	public Color getBackground(Object element) {
		return null;
	}

	@Override
	public Color getForeground(Object element) {
		return null;
	}

	@Override
	public Image getImage(Object element) {
		if (element == null){
			return null;
		}
		if (element instanceof Item) {
			Item item = (Item) element;

			ItemType type = item.getItemType();
			
			
			if (type == null) {
				return null;
			}

			switch (type) {
			case HEADLINE:
				return getImage(PATH_OUTLINE_ICON_HEADLINE);
			case INLINE_ANCHOR:
				return getImage(PATH_OUTLINE_ICON_INLINE_ANCHOR);
			case INCLUDE:
				return getImage(PATH_OUTLINE_ICON_INCLUDE);
			case META_ERROR:
				return getImage(PATH_OUTLINE_ICON_ERROR);
			case META_INFO:
				return getImage(PATH_OUTLINE_ICON_INFO);
			default:
				return null;
			}
		}
		return null;
	}

	@Override
	public StyledString getStyledText(Object element) {
		StyledString styled = new StyledString();
		if (element == null) {
			styled.append("null");
		}
		if (element instanceof Item) {
			Item item = (Item) element;

			ItemType itemType = item.getItemType();
			if (itemType==ItemType.HEADLINE){
				
				StyledString typeString = new StyledString(item.getPrefix(), outlineItemTypeStyler);
				styled.append(typeString);
			}else if (itemType==ItemType.META_DEBUG){
				StyledString typeString = new StyledString(item.getOffset()+": ", outlineItemTypeStyler);
				styled.append(typeString);
			}else if (itemType==ItemType.INLINE_ANCHOR){
				/* no special handling */
			}
			String name = item.getName();
			if (name != null) {
				styled.append(name );//+" { ... }");
			}

		} else {
			return styled.append(element.toString());
		}

		return styled;
	}


	public ColorManager getColorManager() {
		AsciiDoctorEditorActivator editorActivator = AsciiDoctorEditorActivator.getDefault();
		if (editorActivator == null) {
			return ColorManager.getStandalone();
		}
		return editorActivator.getColorManager();
	}
	
	private Image getImage(String path) {
        return EclipseUtil.getImage(path, AsciiDoctorEditorActivator.PLUGIN_ID);
    }


}
