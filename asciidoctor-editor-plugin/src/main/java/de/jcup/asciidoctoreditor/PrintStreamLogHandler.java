/*
 * Copyright 2021 Albert Tregnaghi
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
package de.jcup.asciidoctoreditor;

import java.io.PrintStream;

public class PrintStreamLogHandler implements LogHandler{

    @Override
    public void logError(String message, Throwable t) {
        log(System.err, "ERROR:",message,t);
    }

    @Override
    public void logInfo(String message) {
       log(System.out, "INFO:",message,null);
    }

    @Override
    public void logWarn(String message, Throwable t) {
        log(System.err, "WARN:",message,t);
    }
    
    private void log(PrintStream p, String prefix, String message, Throwable t) {
        p.print(prefix);
        if (message!=null) {
            p.println(message);
        }
        if (t!=null) {
            t.printStackTrace(p);
        }
    }

}
