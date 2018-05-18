package de.jcup.asciidoctoreditor;

import de.jcup.asciidoctoreditor.script.GraphvizCheckSupport;
import net.sourceforge.plantuml.cucadiagram.dot.GraphvizUtils;

public class CheckGraphviz implements GraphvizCheckSupport{

	public static CheckGraphviz INSTANCE = new CheckGraphviz();
	
	private CheckGraphviz(){
		
	}
	
	public boolean checkInstalled() {
		try {
			int version = GraphvizUtils.getDotVersion();
			return version != -1; // -1 only when no version 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
}
