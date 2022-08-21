package de.jcup.asciidoctoreditor.globalmodel;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class GlobalAsciidocModelRecursionFinder {

    private static final int MAX_DEPTH = 128;

    /**
     * Checks if given asciidoc file has ITSELF a recursion - means somewhere inside
     * the includes the files included again (so endless loop)
     * 
     * @param file
     * @return
     */
    public boolean isRecursive(AsciidocFile file) {
        Set<AsciidocFile> searchHistory = new LinkedHashSet<>();
        boolean isIncluded = checkIncluded(file, file, 0, searchHistory);

        return isIncluded;
    }

    private boolean checkIncluded(AsciidocFile toInspect, AsciidocFile toSearch, int depth, Set<AsciidocFile> searchHistory) {
        if (depth > MAX_DEPTH) {
            throw new IllegalStateException("Max depth " + MAX_DEPTH + " exceeded.");
        }
        if (depth > 1 && Objects.equals(toSearch, toInspect)) {
            return true;
        }
        searchHistory.add(toInspect);

        List<AsciidocFile> includedAsciidocFiles = toInspect.getIncludeNodes().stream().map(node -> node.getIncludedAsciidocFile()).collect(Collectors.toList());

        if (includedAsciidocFiles.contains(toSearch)) {
            return true;
        }
        for (AsciidocFile includedToInspect : includedAsciidocFiles) {
            if (searchHistory.contains(includedToInspect)) {
                /* already inspected - avoid endless loops */
                continue;
            }
            boolean isIncluded = checkIncluded(includedToInspect, toSearch, depth + 1, searchHistory);
            if (isIncluded) {
                return true;
            }
        }
        return false;
    }

}
