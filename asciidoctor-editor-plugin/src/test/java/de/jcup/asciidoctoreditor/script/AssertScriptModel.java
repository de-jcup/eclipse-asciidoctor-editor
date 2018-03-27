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

import static org.junit.Assert.*;

import java.util.Collection;

import de.jcup.asciidoctoreditor.script.AsciiDoctorError;
import de.jcup.asciidoctoreditor.script.AsciiDoctorHeadline;
import de.jcup.asciidoctoreditor.script.AsciiDoctorScriptModel;

public class AssertScriptModel {

	public static AssertScriptModel assertThat(AsciiDoctorScriptModel model) {
		if (model == null) {
			throw new IllegalArgumentException("model is null");
		}
		return new AssertScriptModel(model);
	}

	private AsciiDoctorScriptModel model;

	private AssertScriptModel(AsciiDoctorScriptModel model) {
		this.model = model;
	}

	public AssertScriptModel hasNoFunctions() {
		return hasFunctions(0);
	}
		
	public AssertScriptModel hasFunctions(int amount) {
		Collection<AsciiDoctorHeadline> functions = getFunctions();
		if (amount!=functions.size()){
			assertEquals("asciidoc file model has not expected amount of headlines \nfunctions found:"+functions,amount, functions.size());
		}
		return this;
	}

	public AssertScriptModel hasNoFunction(String functionName) {
		return hasFunction(functionName, false, -1);
	}

	public AssertScriptModel hasFunction(String functionName) {
		return hasFunction(functionName, true, -1);
	}

	public AssertScriptModel hasFunctionWithPosition(String functionName, int expectedPosition) {
		return hasFunction(functionName, true, expectedPosition);

	}

	private AssertScriptModel hasFunction(String functionName, boolean excpectedFunctionExists, int expectedPosition) {
		AsciiDoctorHeadline found = null;

		for (AsciiDoctorHeadline function : getFunctions()) {
			if (function.getName().equals(functionName)) {
				found = function;
			}
			if (found != null) {
				break;
			}
		}
		/* assert function available or not */
		if (found != null) {
			if (!excpectedFunctionExists) {
				fail("Did not expect, but script has function with label:" + functionName);
			}

			/* assert start if wanted */
			assertFunctionHasPosition(found, expectedPosition);

		} else {
			if (excpectedFunctionExists) {
				fail("This script has NO function with label:" + functionName+". But it contains following headlines:"+createFunctionStringList());
			}
		}

		return this;
	}

	private StringBuilder createFunctionStringList() {
		StringBuilder sb = new StringBuilder();
		for (AsciiDoctorHeadline function : getFunctions()){
			sb.append('\'');
			sb.append(function.name);
			sb.append('\'');
			sb.append(',');
		}
		return sb;
	}

	private void assertFunctionHasPosition(AsciiDoctorHeadline found, int expectedPosition) {
		if (found == null) {
			throw new IllegalArgumentException("wrong usage of this method, found may not be null here!");
		}
		if (expectedPosition == -1) {
			return;
		}
		assertEquals("Position of function is not as expected!", expectedPosition, found.position);

	}

	private Collection<AsciiDoctorHeadline> getFunctions() {
		Collection<AsciiDoctorHeadline> functions = model.getHeadlines();
		assertNotNull(functions);
		return functions;
	}
	
	private Collection<AsciiDoctorError> getErrors() {
		Collection<AsciiDoctorError> errors = model.getErrors();
		assertNotNull(errors);
		return errors;
	}

	public AssertScriptModel hasErrors(int expectedAmountOfErrors) {
		assertEquals("Script has not expected amount of errors!",expectedAmountOfErrors, getErrors().size());
		return this;
	}

	public AssertScriptModel hasNoErrors() {
		return hasErrors(0);
	}

	public AssertScriptModel hasNoDebugTokens() {
		assertFalse(model.hasDebugTokens());
		return this;
	}

	public AssertScriptModel hasDebugTokens(int amount) {
		assertTrue(model.hasDebugTokens());
		assertEquals("Amount of debug tokens not as expected", amount ,model.getDebugTokens().size());
		return this;
	}

}
