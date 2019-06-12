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
import org.eclipse.jface.window.Window;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;
import de.jcup.asciidoctoreditor.util.EclipseUtil;

public class NewTableInsertAction extends InsertTextAction {

	private static final String GAP = "   ";
	private static ImageDescriptor IMG_NEW_TABLE = createToolbarImageDescriptor("table.gif");

	public NewTableInsertAction(AsciiDoctorEditor editor) {
		super(editor, "Insert a table", IMG_NEW_TABLE);
	}

	private class TableData {
		int rows;
		int columns;
	}

	@Override
	protected void beforeInsert(InsertTextContext context) {
		NewTableDialog dialog = new NewTableDialog(EclipseUtil.getActiveWorkbenchShell());
		int result = dialog.open();
		if (result == Window.CANCEL) {
			context.canceled = true;
			return;
		}
		TableData data = new TableData();
		data.rows = dialog.getRows();
		data.columns = dialog.getColumns();

		context.data = data;
	}

	@Override
	protected String getInsertText(InsertTextContext context) {
		TableData data = (TableData) context.data;
		StringBuilder sb = new StringBuilder();
		sb.append("[options=\"header\",cols=\"");
		for (int i = 0;i<data.columns;i++){
			sb.append("1");
			if (i<data.columns-1){
				sb.append(",");
			}
		}
		sb.append("\"]\n");

		sb.append("|===");
		sb.append("\n");

		appendColumns(data, sb, 0, "Head");
		sb.append("//");
		int charsNeededForHeadline = data.columns*(GAP.length()+6)-5;
		for (int i=0;i<charsNeededForHeadline;i++){
			sb.append("-");
		}
		sb.append("\n");
		for (int row = 1; row <= data.rows; row++) {
			appendColumns(data, sb, row, "Row");
		}
		sb.append("|===");
		sb.append("\n");
		return sb.toString();
	}

	private void appendColumns(TableData data, StringBuilder sb, int row, String text) {
		for (int column = 1; column <= data.columns; column++) {
			sb.append("|");
			sb.append(text);
			if (row>0){
				sb.append(row);
			}
			sb.append(toColumnName(column));
			sb.append(GAP);
		}
		sb.append("\n");
	}
	
	private String toColumnName(int column){
		if (column==0){
			return "";
		}
		if (column>26 ){
			return "-";
		}
		return ""+(char) (column+64); // A=65, column is at least 1..
	}

}
