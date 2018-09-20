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

import de.jcup.asciidoctoreditor.script.parser.SimpleHeadlineParser;
import de.jcup.asciidoctoreditor.script.parser.SimpleIncludeParser;
import de.jcup.asciidoctoreditor.script.parser.SimpleInlineAnchorParser;

/**
 * A asciidoc file model builder
 * 
 * @author Albert Tregnaghi
 *
 */
public class AsciiDoctorScriptModelBuilder {

	private GraphvizCheckSupport graphVizCheckSupport;

	private SimpleHeadlineParser headlineParser = new SimpleHeadlineParser();
	private SimpleIncludeParser includeParser = new SimpleIncludeParser();
	private SimpleInlineAnchorParser inlineAnchorParser = new SimpleInlineAnchorParser();

	/**
	 * Parses given script and creates a asciidoc file model
	 * 
	 * @param asciidoctorScript
	 * @return a simple model with some information about asciidoc file
	 * @throws AsciiDoctorScriptModelException
	 */
	public AsciiDoctorScriptModel build(String asciidoctorScript) throws AsciiDoctorScriptModelException {
		AsciiDoctorScriptModel model = new AsciiDoctorScriptModel();

		Collection<AsciiDoctorHeadline> headlines = headlineParser.parse(asciidoctorScript);
		Collection<AsciiDoctorInclude> includes = includeParser.parse(asciidoctorScript);
		Collection<AsciiDoctorInlineAnchor> inlineAnchors = inlineAnchorParser.parse(asciidoctorScript);

		model.getHeadlines().addAll(headlines);
		model.getIncludes().addAll(includes);
		model.getInlineAnchors().addAll(inlineAnchors);

		handleHeadlinesWithAnchorsBefore(model);

		if (isGaphvizCheckNecessary(asciidoctorScript)) {
			boolean graphvizAvailable = graphVizCheckSupport.checkInstalled();
			if (!graphvizAvailable) {
				int index = getIndexWhereGraphvizBecomesNecessary(asciidoctorScript);

				AsciiDoctorError error = new AsciiDoctorError(index, index + 9,
						"No GraphViz installation found but necessary.\n"
								+ "Please install GraphViz on your machine if\nyou want the diagramm correct generated!");
				model.getErrors().add(error);
			}
		}
		return model;
	}

	protected void handleHeadlinesWithAnchorsBefore(AsciiDoctorScriptModel model) {
		/* headlines having an anchor befor do use the ID of the anker! */
		for (AsciiDoctorHeadline headline : model.getHeadlines()) {
			int headlinePosition = headline.getPosition();

			for (AsciiDoctorInlineAnchor anchor : model.getInlineAnchors()) {
				int anchorEnd = anchor.getEnd();
				if (anchorEnd+2==headlinePosition){ /* +2 necessary because of new line+one more*/
					headline.id=anchor.getId();
					break;
				}
			}
		}

	}

	protected boolean isGaphvizCheckNecessary(String asciidoctorScript) {
		if (graphVizCheckSupport == null) {
			return false;
		}
		return getIndexWhereGraphvizBecomesNecessary(asciidoctorScript) != -1;
	}

	protected int getIndexWhereGraphvizBecomesNecessary(String asciidoctorScript) {
		int index = asciidoctorScript.indexOf("[plantuml");
		if (index == -1) {
			index = asciidoctorScript.indexOf("[graphviz");
		}
		return index;
	}

	public void setGraphVizCheckSupport(GraphvizCheckSupport validateGraphviz) {
		this.graphVizCheckSupport = validateGraphviz;
	}

}
