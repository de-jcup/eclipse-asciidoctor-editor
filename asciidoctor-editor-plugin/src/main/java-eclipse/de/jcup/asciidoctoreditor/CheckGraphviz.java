package de.jcup.asciidoctoreditor;

import net.sourceforge.plantuml.cucadiagram.dot.GraphvizUtils;

public class CheckGraphviz {

	public static boolean checkInstalled() {
		try {
			int version = GraphvizUtils.getDotVersion();
			return version != -1; // -1 only when no version 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
}
