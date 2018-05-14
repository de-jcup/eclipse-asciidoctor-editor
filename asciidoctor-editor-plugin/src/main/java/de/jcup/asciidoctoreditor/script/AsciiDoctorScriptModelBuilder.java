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
package de.jcup.asciidoctoreditor.script;

import java.util.Collection;

import de.jcup.asciidoctoreditor.CheckGraphviz;
import de.jcup.asciidoctoreditor.script.parser.SimpleHeadlineParser;
import de.jcup.asciidoctoreditor.script.parser.SimpleIncludeParser;

/**
 * A asciidoc file model builder
 * 
 * @author Albert Tregnaghi
 *
 */
public class AsciiDoctorScriptModelBuilder {
	
	private boolean validateGraphviz;

	/**
	 * Parses given script and creates a asciidoc file model
	 * 
	 * @param asciidoctorScript
	 * @return a simple model with some information about asciidoc file
	 * @throws AsciiDoctorScriptModelException 
	 */
	public AsciiDoctorScriptModel build(String asciidoctorScript) throws AsciiDoctorScriptModelException{
		AsciiDoctorScriptModel model = new AsciiDoctorScriptModel();

		SimpleHeadlineParser parser = new SimpleHeadlineParser();
		Collection<AsciiDoctorHeadline> headlines=parser.parse(asciidoctorScript);

		SimpleIncludeParser includeParser = new SimpleIncludeParser();
		Collection<AsciiDoctorInclude> includes=includeParser.parse(asciidoctorScript);
		
		model.getHeadlines().addAll(headlines);
		model.getIncludes().addAll(includes);
		
		if (isGaphvizCheckNecessary(asciidoctorScript)){
			boolean graphvizAvailable= CheckGraphviz.checkInstalled();
			if (!graphvizAvailable){
				int index = getIndexWhereGraphvizBecomesNecessary(asciidoctorScript);
				
				AsciiDoctorError error = new AsciiDoctorError(index, index+9, "No GraphViz installation found but necessary.\n"
						+ "Please install GraphViz on your machine if\nyou want the diagramm correct generated!");
				model.getErrors().add(error);
			}
		}
		return model;
	}

	protected boolean isGaphvizCheckNecessary(String asciidoctorScript) {
		if (!validateGraphviz){
			return false;
		}
		return getIndexWhereGraphvizBecomesNecessary(asciidoctorScript)!=-1;
	}

	protected int getIndexWhereGraphvizBecomesNecessary(String asciidoctorScript) {
		int index = asciidoctorScript.indexOf("[plantuml");
		if (index==-1){
			index = asciidoctorScript.indexOf("[graphviz");
		}
		return index;
	}

	public void setValidateGraphviz(boolean validateGraphviz) {
		this.validateGraphviz=validateGraphviz;
	}

}
