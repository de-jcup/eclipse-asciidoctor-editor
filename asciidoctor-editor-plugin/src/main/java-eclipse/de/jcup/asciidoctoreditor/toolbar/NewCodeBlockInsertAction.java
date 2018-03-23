package de.jcup.asciidoctoreditor.toolbar;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListDialog;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;
import de.jcup.asciidoctoreditor.EclipseUtil;

public class NewCodeBlockInsertAction extends InsertTextAction {

	private static final String JSON = "JSON";
	private static final String XML = "XML";
	private static final String JAVA = "Java";
	private static final String RUBY = "Ruby";
	private static ImageDescriptor IMG_NEW_CODE_BLOCK = createToolbarImageDescriptor("source.png");

	public NewCodeBlockInsertAction(AsciiDoctorEditor editor) {
		super(editor, "Insert a code block", IMG_NEW_CODE_BLOCK);
	}

	@Override
	protected void beforeInsert(InsertTextContext context) {
		Shell shell = EclipseUtil.getActiveWorkbenchShell();
		IStructuredContentProvider contentProvider = new ArrayContentProvider();
		ILabelProvider provider = new LabelProvider();

		ListDialog dlg = new ListDialog(shell);
		dlg.setContentProvider(contentProvider);
		dlg.setLabelProvider(provider);
		dlg.setInitialSelections(new String[] { JAVA });
		dlg.setInput(new String[] { JAVA, RUBY, XML, JSON });
		dlg.setTitle("Select source code type");

		int result = dlg.open();
		if (result == Window.CANCEL || dlg.getResult() == null || dlg.getResult().length == 0) {
			context.canceled = true;
			return;
		}
		context.data = dlg.getResult()[0];

	}

	@Override
	protected String getInsertText(InsertTextContext context) {
		StringBuilder sb = new StringBuilder();
		if (RUBY.equals(context.data)) {
			sb.append("\n[source,ruby]\n");
			sb.append("----\n");
			sb.append("require 'sinatra' // <1>\n");
			sb.append("\n");
			sb.append("get '/hi' do // <2>\n");
			sb.append("  \"Hello World!\" // <3>\n");
			sb.append("end\n");
			sb.append("----\n");
			sb.append("<1> Library import\n");
			sb.append("<2> URL mapping\n");
			sb.append("<3> HTTP response body\n");
		} else if (JAVA.equals(context.data)) {
			sb.append("[source,java]\n");
			sb.append("----\n");
			sb.append("package com.acme.example; // <1>\n");
			sb.append("\n");
			sb.append("public class HelloWorld {\n");
			sb.append("\n");
			sb.append("    public static void main(String[] args) { <2>\n");
			sb.append("\n");
			sb.append("         System.out.println(\"Hello World!\"); // <3>\n");
			sb.append("\n");
			sb.append("    }\n");
			sb.append("}\n");
			sb.append("----\n");
			sb.append("<1> Package definition\n");
			sb.append("<2> Main method with arguments\n");
			sb.append("<3> Print string to system output\n");
		} else if (XML.equals(context.data)) {
			sb.append("[source,xml]\n");
			sb.append("----\n");
			sb.append("<section>\n");
			sb.append("  <title>Section Title</title> <!--1-->\n");
			sb.append("</section>\n");
			sb.append("----\n");
			sb.append("<1> The section title is required.\n");
		} else if (JSON.equals(context.data)) {
			sb.append("[source,json]\n");
			sb.append("----\n");
			sb.append("{\n");
			sb.append("    \"apiVersion\" : \"1.0\",\n");
			sb.append("    \n");
			sb.append("    \"info\": { <1>\n");
			sb.append("        \"created\" : \"2018-03-23\"\n");
			sb.append("        \"state\"   : \"beta\"\n");
			sb.append("     }\n");
			sb.append("}\n");
			sb.append("----\n");
			sb.append("<1> Info object\n");
		}
		return sb.toString();
	}

}
