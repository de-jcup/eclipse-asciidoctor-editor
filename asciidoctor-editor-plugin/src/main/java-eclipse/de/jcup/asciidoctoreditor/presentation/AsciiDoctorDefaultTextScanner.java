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
package de.jcup.asciidoctoreditor.presentation;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.WhitespaceRule;

import de.jcup.asciidoctoreditor.ui.ColorManager;

public class AsciiDoctorDefaultTextScanner extends RuleBasedScanner {

    public AsciiDoctorDefaultTextScanner(ColorManager manager) {
        IRule[] rules = new IRule[1];
        rules[0] = new WhitespaceRule(new AsciiDoctorWhitespaceDetector());

        setRules(rules);
    }

}
