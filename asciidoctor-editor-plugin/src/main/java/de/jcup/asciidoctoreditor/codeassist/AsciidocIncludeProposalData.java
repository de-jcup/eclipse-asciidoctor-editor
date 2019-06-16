package de.jcup.asciidoctoreditor.codeassist;

import java.util.Objects;

public class AsciidocIncludeProposalData implements Comparable<AsciidocIncludeProposalData>{
    private String include;
    private String label;
    
    AsciidocIncludeProposalData(String include, String label){
        this.include=include;
        this.label=label;
    }
    
    public String getInclude() {
        return include;
    }
    @Deprecated// do we really need here a extra label - normaly always same as include..
    public String getLabel() {
        return label;
    }
    @Override
    public int hashCode() {
        return Objects.hash(include, label);
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof AsciidocIncludeProposalData)) {
            return false;
        }
        AsciidocIncludeProposalData other = (AsciidocIncludeProposalData) obj;
        return Objects.equals(include, other.include) && Objects.equals(label, other.label);
    }
    
    @Override
    public int compareTo(AsciidocIncludeProposalData o) {
        if (o==null) {
            return 1;
        }
        if (include==null) {
            return -1;
        }
        return include.compareTo(o.include);
    }

    @Override
    public String toString() {
        return "AsciidocIncludeProposalData [include=" + include + "]";
    }
    
    
    
    
}