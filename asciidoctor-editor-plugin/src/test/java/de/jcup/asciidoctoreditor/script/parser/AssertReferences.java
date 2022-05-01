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
package de.jcup.asciidoctoreditor.script.parser;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;

import de.jcup.asciidoctoreditor.script.AsciiDoctorFileReference;

public class AssertReferences {

    private List<AsciiDoctorFileReference> includes;

    public AssertReferences(List<AsciiDoctorFileReference> headlines) {
        this.includes = headlines;
    }

    public AssertReferences hasReferences(int size) {
        assertEquals("Amount of includes differs", size, includes.size());
        return this;
    }

    public AssertReference hasReference(String target) {
        assertNotNull(target);
        Iterator<AsciiDoctorFileReference> it = includes.iterator();
        while (it.hasNext()) {

            AsciiDoctorFileReference headlineFound = it.next();
            if (target.equals(headlineFound.getTarget())) {
                return new AssertReference(headlineFound);
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("No reference found with target:\n -").append(target);
        sb.append("\nBut found:\n");
        it = includes.iterator();
        while (it.hasNext()) {
            sb.append(" -");
            sb.append(it.next().getTarget());
            sb.append("\n");
        }
        fail(sb.toString());
        return null;
    }

    public static AssertReferences assertReferences(List<AsciiDoctorFileReference> headlines) {
        assertNotNull(headlines);
        return new AssertReferences(headlines);
    }

    public class AssertReference {
        private AsciiDoctorFileReference reference;

        private AssertReference(AsciiDoctorFileReference reference) {
            assertNotNull(reference);
            this.reference = reference;
        }

        public AssertReference withPosition(int position) {
            assertEquals(reference.getLabel() + ":Position not as expected!", position, this.reference.getPosition());
            return this;
        }

        public AssertReference withEnd(int end) {
            assertEquals(reference.getLabel() + ":End not as expected!", end, this.reference.getEnd());
            return this;
        }

        public AssertReferences and() {
            return AssertReferences.this;
        }
    }

}