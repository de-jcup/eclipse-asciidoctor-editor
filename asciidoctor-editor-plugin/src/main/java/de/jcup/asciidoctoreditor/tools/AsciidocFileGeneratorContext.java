package de.jcup.asciidoctoreditor.tools;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import de.jcup.asciidoctoreditor.globalmodel.AsciidocFile;

public class AsciidocFileGeneratorContext {
    
    private Map<AsciidocFile, AtomicInteger> generatedFiles = new HashMap<>();
    private StringBuilder sb = new StringBuilder();

    public void markAsGenerated(AsciidocFile file) {
        AtomicInteger atomic = generatedFiles.computeIfAbsent(file, (f)->new AtomicInteger());
        atomic.incrementAndGet();
    }

    public boolean isAlreadyGenerated(AsciidocFile file) {
        return generatedFiles.containsKey(file);
    }
    
    public int getAmountOfGenerations(AsciidocFile file) {
        if (!isAlreadyGenerated(file)) {
            return 0;
        }
        AtomicInteger atomic = generatedFiles.get(file);
        return atomic.get();
    }

    public void append(String string) {
        sb.append(string);
    }

    public String getContent() {
        return sb.toString();
    }
}