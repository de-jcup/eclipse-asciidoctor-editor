/*
 * Copyright 2019 Albert Tregnaghi
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
package de.jcup.asciidoctoreditor.codeassist;

import java.util.Objects;

public class AsciidocReferenceProposalData implements Comparable<AsciidocReferenceProposalData>{
    private String proposedCode;
    private String label;
    
    AsciidocReferenceProposalData(String proposedCode, String label){
        this.proposedCode=proposedCode;
        this.label=label;
    }
    
    public String getProposedCode() {
        return proposedCode;
    }
    
    public String getLabel() {
        return label;
    }
    @Override
    public int hashCode() {
        return Objects.hash(proposedCode, label);
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof AsciidocReferenceProposalData)) {
            return false;
        }
        AsciidocReferenceProposalData other = (AsciidocReferenceProposalData) obj;
        return Objects.equals(proposedCode, other.proposedCode) && Objects.equals(label, other.label);
    }
    
    @Override
    public int compareTo(AsciidocReferenceProposalData o) {
        if (o==null) {
            return 1;
        }
        if (proposedCode==null) {
            return -1;
        }
        return proposedCode.compareTo(o.proposedCode);
    }

    @Override
    public String toString() {
        return "AsciidocReferenceProposalData [proposedCode=" + proposedCode + "]";
    }
    
    
    
    
}