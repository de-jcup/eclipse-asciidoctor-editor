/*
 * Copyright 2022 Albert Tregnaghi
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