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

    public AssertScriptModel hasNoHeadlines() {
        return hasHeadlines(0);
    }

    public AssertScriptModel hasHeadlines(int amount) {
        Collection<AsciiDoctorHeadline> headlines = getHeadlines();
        if (amount != headlines.size()) {
            assertEquals("asciidoc file model has not expected amount of headlines \nheadlines found:" + headlines, amount, headlines.size());
        }
        return this;
    }

    public AssertScriptModel hasNoHeadline(String headlineName) {
        return hasHeadline(SearchMode.BY_NAME, headlineName, false, -1);
    }

    public AssertScriptModel hasHeadline(String headlineName) {
        return hasHeadline(SearchMode.BY_NAME, headlineName, true, -1);
    }

    public AssertScriptModel hasHeadlineWithId(String id) {
        return hasHeadline(SearchMode.BY_ID, id, true, -1);
    }

    public AssertScriptModel hasHeadlineWithPosition(String headlineName, int expectedPosition) {
        return hasHeadline(SearchMode.BY_NAME, headlineName, true, expectedPosition);

    }

    private enum SearchMode {
        BY_NAME, BY_ID,
    }

    public AssertScriptModel hasHeadline(SearchMode mode, String text, boolean excpectedFunctionExists, int expectedPosition) {
        AsciiDoctorHeadline found = null;

        for (AsciiDoctorHeadline headline : getHeadlines()) {

            if (mode == SearchMode.BY_NAME && headline.getName().equals(text)) {
                found = headline;
            } else if (mode == SearchMode.BY_ID && headline.getId().equals(text)) {
                found = headline;
            }
            if (found != null) {
                break;
            }
        }
        /* assert headline available or not */
        if (found != null) {
            if (!excpectedFunctionExists) {
                fail("Did not expect, but script has headline with label:" + text);
            }

            /* assert start if wanted */
            assertFunctionHasPosition(found, expectedPosition);

        } else {
            if (excpectedFunctionExists) {
                fail(mode.toString() + " failed: Did not found with text:" + text + ". But it contains following headlines:" + createHeadlineStringList(mode));
            }
        }

        return this;
    }

    private StringBuilder createHeadlineStringList(SearchMode mode) {
        StringBuilder sb = new StringBuilder();
        for (AsciiDoctorHeadline headline : getHeadlines()) {
            sb.append('\'');
            if (mode == SearchMode.BY_NAME) {
                sb.append(headline.name);
            } else if (mode == SearchMode.BY_ID) {
                sb.append(headline.getId());
            }
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
        assertEquals("Position of headline is not as expected!", expectedPosition, found.position);

    }

    private Collection<AsciiDoctorHeadline> getHeadlines() {
        Collection<AsciiDoctorHeadline> headlines = model.getHeadlines();
        assertNotNull(headlines);
        return headlines;
    }

    private Collection<AsciiDoctorMarker> getErrors() {
        Collection<AsciiDoctorMarker> errors = model.getErrors();
        assertNotNull(errors);
        return errors;
    }

    public AssertScriptModel hasErrors(int expectedAmountOfErrors) {
        assertEquals("Script has not expected amount of errors!", expectedAmountOfErrors, getErrors().size());
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
        assertEquals("Amount of debug tokens not as expected", amount, model.getDebugTokens().size());
        return this;
    }

}
