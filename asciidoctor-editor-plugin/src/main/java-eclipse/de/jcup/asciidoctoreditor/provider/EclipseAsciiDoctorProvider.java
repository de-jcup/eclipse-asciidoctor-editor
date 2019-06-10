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
package de.jcup.asciidoctoreditor.provider;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.asciidoctor.AsciiDocDirectoryWalker;
import org.asciidoctor.DirectoryWalker;
import org.asciidoctor.ast.DocumentHeader;

import de.jcup.asciidoctoreditor.AsciiDoctorOSGIWrapper;
import de.jcup.asciidoctoreditor.AsciidoctorAdapter;
import de.jcup.asciidoctoreditor.AspAsciidoctorAdapter;
import de.jcup.asciidoctoreditor.InstalledAsciidoctor;

/**
 * An instance providing ogsgi asccidoctor or installed variant
 * @author Albert Tregnaghi
 *
 */
public class EclipseAsciiDoctorProvider implements AsciiDoctorInstanceProvider{
    
    public static final EclipseAsciiDoctorProvider INSTANCE = new EclipseAsciiDoctorProvider();
    
    private static AsciidoctorAdapter asciidoctorInstalled;
    private static AsciidoctorAdapter asciidoctorEmbedded;
    private static AsciidoctorAdapter asciidoctorServerProtocollClient;
    
    EclipseAsciiDoctorProvider(){
        asciidoctorInstalled = new InstalledAsciidoctor();
//        asciidoctorEmbedded = new AsciidoctorAdapter() {
//            
//            @Override
//            public void convertFile(File filename, Map<String, Object> options) {
//                AsciiDoctorOSGIWrapper.INSTANCE.getAsciidoctor().convertFile(filename, options);
//            }
//
//            @Override
//            public Map<String, Object> resolveAttributes(File baseDir) {
//                
//                Map<String, Object> map = new HashMap<>();
//                Set<DocumentHeader> documentIndex = new HashSet<DocumentHeader>();
//                DirectoryWalker directoryWalker = new AsciiDocDirectoryWalker(baseDir.getAbsolutePath());
//
//                for (File file : directoryWalker.scan()) {
//                    documentIndex.add(AsciiDoctorOSGIWrapper.INSTANCE.getAsciidoctor().readDocumentHeader(file));
//                }
//                for (DocumentHeader header : documentIndex) {
//                    map.putAll(header.getAttributes());
//                }
//                return map;
//            }
//        };
        asciidoctorServerProtocollClient= new AspAsciidoctorAdapter();
    }
    
    @Override
    public AsciidoctorAdapter getAsciiDoctor(boolean installed){
        if (installed){
            return asciidoctorInstalled;
        }else{
            return asciidoctorServerProtocollClient;
        }
    }
}