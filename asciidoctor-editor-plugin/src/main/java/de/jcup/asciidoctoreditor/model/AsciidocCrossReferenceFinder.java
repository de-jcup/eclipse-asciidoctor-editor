package de.jcup.asciidoctoreditor.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.jcup.asciidoctoreditor.LogAdapter;
import de.jcup.asciidoctoreditor.RootParentFinder;
import de.jcup.asciidoctoreditor.asciidoc.AsciiDocFileFilter;
import de.jcup.asciidoctoreditor.asciidoc.AsciiDocFileUtils;

public class AsciidocCrossReferenceFinder {

    private AsciiDocFileFilter fileFilter = new AsciiDocFileFilter(true);
    private LogAdapter logAdapter;
    private RootParentFinder rootParentFinder;

    public AsciidocCrossReferenceFinder(RootParentFinder rootParentFinder, LogAdapter logAdapter) {
        this.rootParentFinder = rootParentFinder;
        this.logAdapter = logAdapter;
    }

    /**
     * Tries to find a cross reference with given id. Why returning an array here?
     * Because, if we have a wrong asciidoc setup, there could be multiple documents
     * defined with same id (which is wrong, but it should be clear inside UI!)
     * 
     * @param id
     * @return references list, never <code>null</code>
     */
    public List<AsciidocCrossReference> findReferences(String crossReferenceId) {

        File rootParentFolder = rootParentFinder.findRootParent();
        if (rootParentFolder == null) {
            return new ArrayList<>();
        }

        SearchCrossReferenceContext context = new SearchCrossReferenceContext();
        context.crossReferenceId = crossReferenceId;
        context.toSearch = "[[" + crossReferenceId + "]]";

        collectReferences(rootParentFolder, context);

        return context.list;
    }

    private class SearchCrossReferenceContext {
        List<AsciidocCrossReference> list = new ArrayList<>();
        String crossReferenceId;
        String toSearch;
    }

    private void collectReferences(File parent, SearchCrossReferenceContext context) {
        File[] files = parent.listFiles(fileFilter);
        for (File file : files) {
            if (file.isDirectory()) {
                collectReferences(file, context);
            } else {
                try {
                    String content = AsciiDocFileUtils.readAsciidocfile(file);
                    addFoundReferencesInFile(context, content, file);
                } catch (IOException e) {
                    logAdapter.logError("Was not able to read file: " + file, e);
                }
            }
        }
    }

    private void addFoundReferencesInFile(SearchCrossReferenceContext context, String content, File file) {
        if (content == null || content.isEmpty()) {
            return;
        }
        int pos = content.indexOf(context.toSearch);
        if (pos == -1) {
            return;
        }
        AsciidocCrossReference ref = new AsciidocCrossReference();
        ref.file = file;
        ref.id = context.crossReferenceId;
        ref.positionStartIndex = pos;
        ref.length = context.toSearch.length();

        context.list.add(ref);

    }

}
