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
package de.jcup.asciidoctoreditor.toolbar;

import org.eclipse.jface.resource.ImageDescriptor;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;

public abstract class FormatTextAction extends InsertTextAction{

	protected FormatTextAction(AsciiDoctorEditor editor, String text, ImageDescriptor descriptor) {
		super(editor, text, descriptor);
	}

	protected abstract String formatPrefix();
	
	protected abstract String formatPostfix();
	
	@Override
	protected String getInsertText(InsertTextContext context) {
		StringBuilder sb = new StringBuilder();
		String formatPrefix = formatPrefix();
		if (formatPrefix==null){
			formatPrefix="";
		}
		String formatPostfix = formatPostfix();
		if (formatPostfix==null){
			formatPostfix="";
		}

		if (context.selectedLength>0){
			if (context.selectedText.startsWith(formatPostfix)){
				/* already formatted, so ignore */
				context.canceled=true;
				return null;
			}
			/* not already formatted, so format selected text */
			sb.append(formatPrefix);
			sb.append(context.selectedText);
			context.nextOffset=context.selectedOffset+sb.length();
			sb.append(formatPostfix);
		}else{
			/* complete new text, so just do format and set cursor between */
			sb.append(formatPrefix);
			sb.append(formatPostfix);
			context.nextOffset=context.selectedOffset+formatPrefix.length();
		}
		return sb.toString();
	}

}
