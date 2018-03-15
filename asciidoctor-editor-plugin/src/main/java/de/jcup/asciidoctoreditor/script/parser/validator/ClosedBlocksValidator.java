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
 package de.jcup.asciidoctoreditor.script.parser.validator;

import java.util.List;

import de.jcup.asciidoctoreditor.script.AsciiDoctorError;
import de.jcup.asciidoctoreditor.script.ValidationResult;
import de.jcup.asciidoctoreditor.script.parser.ParseToken;

public class ClosedBlocksValidator extends AbstractParseTokenListValidator {

	@Override
	protected void doValidation(List<ParseToken> tokens, List<ValidationResult> result) {

		int amountOfOpened = 0;
		int amountOfClosed = 0;

		ParseToken lastWorkingOpen = null;
		ParseToken lastWorksBeforeClose = null;
		for (ParseToken token : tokens) {
			if (token.isOpenBlock()) {
				if (amountOfClosed == amountOfOpened) {
					lastWorkingOpen = token;
				}
				amountOfOpened++;
			} else if (token.isCloseBlock()) {
				if (amountOfClosed == amountOfOpened) {
					lastWorksBeforeClose = token;
				}
				amountOfClosed++;
			}
		}
		if (amountOfClosed==amountOfOpened){
			return;
		}
		if (amountOfClosed > amountOfOpened){
			if (lastWorksBeforeClose == null) {
				result.add(new AsciiDoctorError(0, 0, "More closing brackets than opened ones. It seems you got a '{' missing"));
			}else{
				result.add(new AsciiDoctorError(lastWorksBeforeClose.getStart(), lastWorksBeforeClose.getEnd(), "It seems this closing bracket is missing a opening one"));
			}
		}else{
			if (lastWorkingOpen == null) {
				result.add(new AsciiDoctorError(0, 0, "More opening brackets than closed ones. It seems you got a '}' missing"));
			}else{
				result.add(new AsciiDoctorError(lastWorkingOpen.getStart(), lastWorkingOpen.getEnd(), "It seems this opening bracket is missing a closing one."));
			}
		}
		
	}

}
